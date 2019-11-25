package org.jggn.testelastic.repository;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.ClearScrollRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetMappingsRequest;
import org.elasticsearch.client.indices.GetMappingsResponse;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.Scroll;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.jggn.testelastic.configuration.ProcessException;
import org.jggn.testelastic.models.EnumType;
import org.jggn.testelastic.models.Pony;
import org.jggn.testelastic.utils.ElasticMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.HttpClientErrorException.TooManyRequests;

import com.fasterxml.jackson.databind.ObjectMapper;

@Repository
public class PonyRepository{

	@Autowired
	RestHighLevelClient client;
	@Autowired
	ObjectMapper objectMapper;
	@Value("${spring.data.elasticsearch.index-name}")
	String index;
	@Autowired
	ElasticMapper mapper;
	@Value("${spring.data.elasticsearch.fetchSize}")
	int fetchSize;
	@Autowired
	Map<String,MappingMetaData> mappings;
	
	@Async
	public CompletableFuture<Long> countByType(EnumType type)
	{
		return null;
	}



	public Slice<Pony> findAllByType(Pageable p,EnumType type) throws IOException
	{
		Scroll scroll = new Scroll(TimeValue.timeValueSeconds(10L));
		SearchSourceBuilder builder = new SearchSourceBuilder().trackTotalHits(true);
		long offset = p.getOffset();
		long localOffset=offset%fetchSize;
		int localFetchSize=(int) (fetchSize>offset?offset+p.getPageSize():fetchSize);
		int pageNumber=(int) (offset/fetchSize);
		//Requete
		QueryBuilder query = QueryBuilders.termQuery("type.keyword", type.name());
		builder.query(query);
		builder.size(localFetchSize);
		SearchResponse response = client.search(new SearchRequest("ponies").source(builder).scroll(scroll),RequestOptions.DEFAULT);
		int currentPage=0;
		String scrollId=response.getScrollId();
		while(pageNumber>currentPage || response.getHits().getHits()==null)
		{
			SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId); 
		    scrollRequest.scroll(scroll);
		    response = client.scroll(scrollRequest, RequestOptions.DEFAULT);
		    scrollId = response.getScrollId();
		    currentPage++;
		}
		Page<Pony> pagePonies= new PageImpl<>(StreamSupport.stream(response.getHits().spliterator(),false).skip(localOffset).limit(p.getPageSize()).map(t-> new Pony(t)).collect(Collectors.toList()), p, response.getHits().getTotalHits().value);
		ClearScrollRequest clearScrollRequest = new ClearScrollRequest(); 
		clearScrollRequest.addScrollId(scrollId);
		client.clearScroll(clearScrollRequest, RequestOptions.DEFAULT);
		return pagePonies;
	}

	public void saveAll(List<Pony> ponies) throws IOException, ProcessException {
		MappingMetaData ponyMapping = mappings.get("ponies");
		BulkRequest batchRunBP = new BulkRequest();
		boolean backPressureHandling = false;
		BulkRequest batchRun = new BulkRequest();
		for (Pony pony : ponies) {
			Map<String,Object> obj=mapper.mapObject(pony.generateMap(),ponyMapping);
			String id = pony.getPonyId().toString();			
			IndexRequest request = new IndexRequest(index);
			request.id(id);		
			request.source(objectMapper.writeValueAsString(obj),XContentType.JSON);
			batchRun.add(request);
		}
		try
		{
			BulkResponse response = client.bulk(batchRun,RequestOptions.DEFAULT.toBuilder().build());
			
			for (BulkItemResponse bulkItemResponse : response.getItems()) {
				if(bulkItemResponse.status().ordinal() == 429)
				{
					//Gestion du backPressure : sleep + rejeu
					backPressureHandling =true;
					//on recupere l'objet
					batchRunBP.add(batchRun.requests().get(bulkItemResponse.getItemId()));
				}
			}
		}catch(TooManyRequests exc)
		{
			backPressureHandling=true;
			batchRunBP.add(batchRun.requests());
		}
		
		if(backPressureHandling)
		{
			
			try {
				//On marque une courte temporisation pour laisser le cluster respirer (10ms par requete présentes dans le bulk)
				Thread.sleep(10*batchRunBP.numberOfActions());
				BulkResponse response = client.bulk(batchRunBP,RequestOptions.DEFAULT.toBuilder().build());
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
			
		}
	}
}

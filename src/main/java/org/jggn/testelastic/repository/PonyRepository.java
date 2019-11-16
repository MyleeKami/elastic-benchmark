package org.jggn.testelastic.repository;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetMappingsRequest;
import org.elasticsearch.client.indices.GetMappingsResponse;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.elasticsearch.common.xcontent.XContentType;
import org.jggn.testelastic.configuration.ProcessException;
import org.jggn.testelastic.models.EnumType;
import org.jggn.testelastic.models.Pony;
import org.jggn.testelastic.utils.ElasticMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;

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
	
	@Async
	public CompletableFuture<Long> countByType(EnumType type)
	{
		return null;
	}

	public Slice<Pony> findAllByType(Pageable p,EnumType type)
	{
		return null;
	}
	
	public MappingMetaData getMapping() throws IOException
	{
		GetMappingsRequest map = new GetMappingsRequest();
		GetMappingsResponse res =client.indices().getMapping(map,RequestOptions.DEFAULT);
		System.out.println();
		MappingMetaData mapDta =res.mappings().get("ponies");
		return mapDta;
	}

	public void saveAll(List<Pony> ponies) throws IOException, ProcessException {
		MappingMetaData ponyMapping = getMapping();
		for (Pony pony : ponies) {
			Map<String,Object> obj=mapper.mapObject(pony.generateMap(),ponyMapping);
			String id = pony.getPonyId().toString();
			IndexRequest request = new IndexRequest(index);
			request.id(id);		
			request.source(objectMapper.writeValueAsString(obj),XContentType.JSON);
			client.index(request,RequestOptions.DEFAULT.toBuilder().build());
		}
		
	}
}

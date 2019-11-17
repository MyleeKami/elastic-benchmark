package org.jggn.testelastic.repository;

import java.io.IOException;

import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetMappingsRequest;
import org.elasticsearch.client.indices.GetMappingsResponse;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
@Repository
public class MappingRepository {
	
	@Autowired
	RestHighLevelClient client;
	
	public MappingMetaData getMapping(String mappingName) throws IOException
	{
		GetMappingsRequest map = new GetMappingsRequest();
		GetMappingsResponse res =client.indices().getMapping(map,RequestOptions.DEFAULT);
		System.out.println();
		MappingMetaData mapDta =res.mappings().get(mappingName);
		return mapDta;
	}

}

package org.jggn.testelastic.configuration;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.jggn.testelastic.repository.MappingRepository;
import org.jggn.testelastic.repository.PonyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
@Configuration
public class ConfigurationMappings {
	@Autowired
	RestHighLevelClient client;
	@Autowired
	MappingRepository repository;
	@Value("${application.mapped.types}")
	String mappedTypes;
	
	@Bean
	public Map<String,MappingMetaData> mappings() throws IOException
	{
		Map<String,MappingMetaData> ret = new HashMap<>();
		for (String string : mappedTypes.split(",")) {
			MappingMetaData metadata = repository.getMapping(string);
			ret.put(string,metadata);
		}
		return ret;
	}

}

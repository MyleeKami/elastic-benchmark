package org.jggn.testelastic.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.jggn.testelastic.configuration.ProcessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
@Component
public class ElasticMapper {
	@Autowired
	FieldConverter converter;
	public Map<String,Object> mapObject(Map<String,String> data,MappingMetaData metadata) throws ProcessException
	{
		Map<String,Object> retVal = new HashMap<>();
		Map<String,Object> typeMap=(Map<String, Object>) metadata.getSourceAsMap().get("properties");
		for (Entry<String, Object> subType : typeMap.entrySet()) {
			String val = data.get(subType.getKey());
			String type = (String)((Map<String, Object>) subType.getValue()).get("type");
			Object newVal = converter.convert(val,type);
			retVal.put(subType.getKey(), newVal);
		}
		return retVal;
	}

	

}

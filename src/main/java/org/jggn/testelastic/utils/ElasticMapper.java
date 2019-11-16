package org.jggn.testelastic.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.lucene.analysis.CharArrayMap.EntrySet;
import org.elasticsearch.ElasticsearchTimeoutException;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.springframework.stereotype.Component;
@Component
public class ElasticMapper {
	
	public Map<String,Object> mapObject(Map<String,String> data,MappingMetaData metadata)
	{
		Map<String,Object> retVal = new HashMap<>();
		Map<String,Object> typeMap=(Map<String, Object>) metadata.getSourceAsMap().get("properties");
		for (Entry<String, Object> subType : typeMap.entrySet()) {
			String val = data.get(subType.getKey());
			String type = (String)((Map<String, Object>) subType.getValue()).get("type");
			Object newVal = convertStringToType(val,type);
			retVal.put(subType.getKey(), newVal);
		}
		return retVal;
	}

	private Object convertStringToType(String val, String type) {
		
		System.out.println();
		//CF : la doc
//		Each field has a data type which can be:
//		    a simple type like text, keyword, date, long, double, boolean or ip.
		if(type.equalsIgnoreCase("text") || type.equalsIgnoreCase("keyword") )
		{
			return val;
		}
		if(type.equalsIgnoreCase("date"))
		{
			return Instant.parse(val);
		}
		if(type.equalsIgnoreCase("integer") )
		{
			return Integer.parseInt(val);
		}
		if(type.equalsIgnoreCase("long") )
		{
			return Long.parseLong(val);
		}
		if(type.equalsIgnoreCase("double") )
		{
			return Double.parseDouble(val);
		}
		if(type.equalsIgnoreCase("boolean") )
		{
			return Boolean.parseBoolean(val);
		}
		if(type.equalsIgnoreCase("ip") )
		{
			try {
				return InetAddress.getByName(val);
			} catch (UnknownHostException e) {
				return null;
			}
		}
		
//		    a type which supports the hierarchical nature of JSON such as object or nested.
//		    or a specialised type like geo_point, geo_shape, or completion.
		return null;
	}

}

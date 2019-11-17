package org.jggn.testelastic.models;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.elasticsearch.search.SearchHit;

import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
public class Pony {

	UUID ponyId;
	String name;
	String birthPlace;
	EnumGenre genre;	
	Instant dateOfBirth;
	Integer weigh;
	InetAddress networkLocation;
	EnumType type;
	
	public Map<String,String> generateMap()
	{
		Map<String,String> val=new HashMap<>();
		val.put("ponyId", ponyId.toString());
		val.put("name", name.toString());
		val.put("birthPlace", birthPlace.toString());
		val.put("genre", genre.toString());
		val.put("dateOfBirth", dateOfBirth.toString());
		val.put("weigh", weigh.toString());
		val.put("networkLocation", networkLocation.getHostAddress());
		val.put("type", type.toString());
		return val;
		
	}
	
	
	public Pony(SearchHit hit)
	{
		Map<String,Object> val=hit.getSourceAsMap();
		ponyId=UUID.fromString(val.get("ponyId").toString());
		name=val.get("name").toString();
		birthPlace=(String) val.get("birthPlace").toString();
		genre=EnumGenre.valueOf(val.get("genre").toString());
		dateOfBirth=Instant.parse(val.get("dateOfBirth").toString());
		weigh=Integer.parseInt(val.get("weigh").toString());
		try {
			networkLocation=InetAddress.getByName(val.get("networkLocation").toString());
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		type=EnumType.valueOf(val.get("type").toString());
	}

}

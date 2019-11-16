package org.jggn.testelastic.models;

import java.net.InetAddress;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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

}

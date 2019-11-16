package org.jggn.testelastic.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Instant;
import java.util.Arrays;
import java.util.NoSuchElementException;

import javax.xml.bind.DatatypeConverter;

import org.jggn.testelastic.configuration.ProcessException;
import org.springframework.stereotype.Component;
@Component
public class FieldConverter {
	
	public Object convert(String data,String fieldType) throws ProcessException
	{
		try
		{
			FieldType type = Arrays.stream(FieldType.values()).filter(t -> t.name().equalsIgnoreCase(fieldType)).findFirst().get();
			switch(type)
			{
			case auto:
			case Keyword:
			case Nested:
			case Object:
			case Flattened:
			case Text: return data;
			case Binary:return DatatypeConverter.parseHexBinary(data);
			case Boolean:return Boolean.parseBoolean(data);
			case Byte:return Byte.parseByte(data);
			case Date:return Instant.parse(data);
			case Date_Nanos: return Instant.ofEpochMilli(Long.parseLong(data)/1000);
			case Double: return Double.parseDouble(data);
			case Float:
			case Half_Float:return Float.parseFloat(data);
			case Short:
			case Integer: return Integer.parseInt(data);
			case Long: return Long.parseLong(data);
			case Ip: return InetAddress.getByName(data);
			case Date_Range:
			case Double_Range:
			case Float_Range:
			case Integer_Range:
			case TokenCount:
			case Ip_Range:
			case Percolator:
			case Long_Range:
			case Scaled_Float:throw new ProcessException(type+ " n'est utilisable");
			default:throw new ProcessException(type+ " inconnu");
			
			}
		}
		catch(NoSuchElementException e)
		{
			throw new ProcessException(fieldType + " n'est pas un type elastic reconnu");
		} catch (UnknownHostException e) {
			throw new ProcessException(data + " n'est pas une adresse IP");
		}
	}

}

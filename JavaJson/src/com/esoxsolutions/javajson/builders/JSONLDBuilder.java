package com.esoxsolutions.javajson.builders;

import java.lang.reflect.Field;
import java.util.ArrayList;

import org.codehaus.jettison.json.JSONObject;

import com.esoxsolutions.javajson.annotations.JsonSerializationType;

public class JSONLDBuilder extends AbstractBuilder {

	@Override
	public String build(Object o) throws Exception {

		String schemaName = getSchemaName(o);
		String typeName=getTypeName(o);

		ArrayList<String> jsonElements=new ArrayList<String>();
		jsonElements.add(createContext(schemaName));
		jsonElements.add(createType(typeName));
		jsonElements.addAll(ConvertToArray(o));
		
		return arrayListToJson(jsonElements);
	}

	private String createType(String typeName) {
		StringBuilder sb=new StringBuilder("\"@type\":\"");
		sb.append(typeName);
		sb.append("\"");
		return sb.toString();
	}

	private String createContext(String schemaName) {
		StringBuilder sb=new StringBuilder("\"@context\":\"");
		sb.append(schemaName);
		sb.append("\"");
		return sb.toString();
	}


	
	private String getTypeName(Object o) throws Exception {
		if (o!=null) {
			JsonSerializationType serializationType=(JsonSerializationType)o.getClass().getAnnotation(JsonSerializationType.class);
			if (serializationType==null) {
				throw new Exception("No jsonld annotation");
			}
			return serializationType.Type();
		} else {
			throw new Exception("Null objects do not have annotations");
		}
	}
	
	private String getSchemaName(Object o) throws Exception {
		if (o != null) {
			JsonSerializationType serializationType = (JsonSerializationType) o.getClass()
					.getAnnotation(JsonSerializationType.class);
			if (serializationType == null) {
				throw new Exception("No jsonld annotation");
			}
			return serializationType.Schema();
		} else {
			throw new Exception("Null objects have no annotations");
		}
	}



}

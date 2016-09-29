package com.esoxsolutions.javajson.conversion.engine;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.codehaus.jettison.json.JSONObject;

import com.esoxsolutions.javajson.annotations.JsonSerializable;
import com.esoxsolutions.javajson.annotations.JsonSerializationType;
import com.esoxsolutions.javajson.annotations.JsonType;
import com.esoxsolutions.javajson.builders.AbstractBuilder;
import com.esoxsolutions.javajson.builders.JSONBuilder;
import com.esoxsolutions.javajson.builders.JSONLDBuilder;

public class Converter {

	public static String START_ARRAY = "[";
	public static String END_ARRAY = "]";
	public static String START_JSON = "{";
	public static String END_JSON = "}";
	public static String EMPTY_JSON = "{}";
	private static Converter _instance;

	private HashMap<JsonType, AbstractBuilder> builders;

	public static Converter getInstance() {
		if (_instance == null) {
			_instance = new Converter();
		}

		return _instance;
	}

	private Converter() {
		builders = new HashMap<>();
		builders.put(JsonType.JSON, new JSONBuilder());
		builders.put(JsonType.JSON_LD, new JSONLDBuilder());
	}

	private static String prepareString(String aValue) {
		aValue = aValue.replace("[", "");
		aValue = aValue.replace("]", "");
		aValue = aValue.replace("\"", "\\\"");
		return aValue;
	}

	public String arrayListToJson(ArrayList<String> jsonElements) {
		StringBuilder sb = new StringBuilder(AbstractBuilder.START_JSON);
		sb.append(String.join(",", jsonElements));
		sb.append(AbstractBuilder.END_JSON);
		return sb.toString();
	}

	public ArrayList<String> ConvertToArray(Object o) throws Exception {

		if (o != null) {
			JsonType type = getJsonTypeForObject(o);
			if (builders.containsKey(type)) {
				return builders.get(type).ConvertToArray(o);
			} else {
				throw new Exception("Builder was not found");
			}

		} else {
			throw new Exception("Null objects have no json");
		}
	}

	public ArrayList<String> ConvertToArray(Object o, String schema) throws Exception {
		if (o != null) {
			JsonType type = getJsonTypeForObject(o);
			if (builders.containsKey(type)) {
				return builders.get(type).ConvertToArray(o);
			} else {
				throw new Exception("Builder was not found");
			}

		} else {
			throw new Exception("Null objects have no json");
		}

	}

	public ArrayList<String> Convert(Object o, JsonType jsonType) throws Exception {

		if (o != null) {

			if (builders.containsKey(jsonType)) {
				return builders.get(jsonType).ConvertToArray(o);
			} else {
				throw new Exception("Builder was not found");
			}

		} else {
			throw new Exception("Null objects have no json");
		}
	}

	public String Convert(Object o) throws Exception {

		if (o != null) {
			JsonType type = getJsonTypeForObject(o);
			if (builders.containsKey(type)) {
				return builders.get(type).build(o);
			} else {
				throw new Exception("Builder was not found");
			}

		}

		return EMPTY_JSON;
	}
	
	public String ConvertWithId(Object o,String id) throws Exception {
		if (o!=null) {
			JsonType type=getJsonTypeForObject(o);
			if (builders.containsKey(type)) {
				return builders.get(type).buildWithId(o, id);
			} else {
				throw new Exception("Builder was not found");
			}
		}
		
		return EMPTY_JSON;
	}

	public String Convert(Object o,String schemaType) throws Exception {

		if (o != null) {
			JsonType type = getJsonTypeForObject(o);
			if (builders.containsKey(type)) {
				return builders.get(type).build(o,schemaType);
			} else {
				throw new Exception("Builder was not found");
			}

		}

		return EMPTY_JSON;
	}
	private JsonType getJsonTypeForObject(Object o) throws Exception {
		JsonType result = JsonType.JSON;
		if (o != null) {
			JsonSerializationType jsonType = o.getClass().getAnnotation(JsonSerializationType.class);
			if (jsonType != null) {
				result = jsonType.JsonType();
			}
		} else {
			throw new Exception("Null referenced object does not have annotations");
		}

		return result;
	}

}

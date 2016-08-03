package com.esoxsolutions.javajson.conversion.engine;

import java.lang.reflect.Field;
import java.util.ArrayList;

import com.esoxsolutions.javajson.annotations.JsonSerializable;

public class Converter {

	public static String START_ARRAY="[";
	public static String END_ARRAY="]";
	public static String START_JSON="{";
	public static String END_JSON="}";
	public static String EMPTY_JSON="{}";
	private static Converter _instance;
	
	public static Converter getInstance() {
		if (_instance==null) {
			_instance=new Converter();
		}
		
		return _instance;
	}
	
	private Converter() {
		
	}
	
	public String Convert(Object o) throws Exception {
		
		if (o!=null) {
			ArrayList<String> jsonElements=new ArrayList<>();
			Field[] fields=o.getClass().getFields();
			for (Field f:fields) {
				JsonSerializable attribute=(JsonSerializable)f.getAnnotation(JsonSerializable.class);
				if (attribute==null) {
					continue;
				}
				try {
					String jsonElement=buildElement(attribute.JsonFieldName(),f,o);
					jsonElements.add(jsonElement);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					throw e;
				}
				
			}
			StringBuilder sb=new StringBuilder(START_JSON);
			sb.append(String.join(",", jsonElements));
			sb.append(END_JSON);
			return sb.toString();
		}
		
		return EMPTY_JSON;
	}

	private String buildElement(String jsonFieldName, Field f,Object o) throws IllegalArgumentException, IllegalAccessException {
		StringBuilder result=new StringBuilder();
		result.append("\"");
		result.append(jsonFieldName);
		result.append("\":");
		Object field=f.get(o);
		Class<?> fieldClass=field.getClass();
		if (fieldClass.isArray()) {
			result.append(START_ARRAY);
			ArrayList<String> arrayElements=new ArrayList<>();
			Object[] values=(Object[]) f.get(o);
			if (values!=null) {
				for (Object obj:values) {
					arrayElements.add("\""+obj.toString()+"\"");
				}
			}
			result.append(String.join(",", arrayElements));
			result.append(END_ARRAY);
		} else {
			try {
				ArrayList<?> list=(ArrayList<?>)o;
				if (list!=null) {
					Object[] array=list.toArray(new Object[list.size()]);
					ArrayList<String> arrayElements=new ArrayList<>();
					result.append(START_ARRAY);
					for (Object ob:array) {
						arrayElements.add("\""+ob.toString()+"\"");
					}
					result.append(String.join(",", arrayElements));
					result.append(END_ARRAY);
				}
			} catch(Exception ee) {
			
				result.append("\"");
				result.append(f.get(o).toString());
				result.append("\"");
			}
			
		}
		
		return result.toString(); 
	}
}

package com.esoxsolutions.javajson.conversion.engine;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
	
	public ArrayList<String> ConvertToArray(Object o) throws Exception {

		ArrayList<String> jsonElements=new ArrayList<>();	
		if (o!=null) {
			ArrayList<Field> fields=(ArrayList<Field>) getAllFields(new ArrayList<Field>(),o.getClass());
			for (Field f:fields) {
				f.setAccessible(true);
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
			
		}
		
		return jsonElements;

	}
	
	public String Convert(Object o) throws Exception {
		
		if (o!=null) {
			ArrayList<String> jsonElements=new ArrayList<>();
			ArrayList<Field> fields=(ArrayList<Field>) getAllFields(new ArrayList<Field>(),o.getClass());
			for (Field f:fields) {
				f.setAccessible(true);
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

	private static List<Field> getAllFields(List<Field> fields, Class<?> type) {
	    fields.addAll(Arrays.asList(type.getDeclaredFields()));

	    if (type.getSuperclass() != null) {
	        fields = getAllFields(fields, type.getSuperclass());
	    }

	    return fields;
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
				ArrayList<?> list=(ArrayList<?>)f.get(o);
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

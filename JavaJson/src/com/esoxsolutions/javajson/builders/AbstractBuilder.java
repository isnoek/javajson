package com.esoxsolutions.javajson.builders;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.codehaus.jettison.json.JSONObject;

import com.esoxsolutions.javajson.annotations.JsonSerializable;
import com.esoxsolutions.javajson.annotations.JsonType;

public abstract class AbstractBuilder {

	public static String START_ARRAY = "[";
	public static String END_ARRAY = "]";
	public static String START_JSON = "{";
	public static String END_JSON = "}";
	public static String EMPTY_JSON = "{}";

	public abstract String build(Object o) throws Exception;

	public abstract String build(Object o, String schemaType) throws Exception;

	public abstract JSONObject buildJson(Object o) throws Exception;
	
	protected static String prepareString(String aValue) {
		aValue = aValue.replace("[", "");
		aValue = aValue.replace("]", "");
		aValue = aValue.replace("\"", "\\\"");
		return aValue;
	}

	public String arrayListToJson(ArrayList<String> jsonElements) {
		StringBuilder sb = new StringBuilder(START_JSON);
		sb.append(String.join(",", jsonElements));
		sb.append(END_JSON);
		return sb.toString();
	}

	protected static List<Field> getAllFields(List<Field> fields, Class<?> type) {
		fields.addAll(Arrays.asList(type.getDeclaredFields()));

		if (type.getSuperclass() != null) {
			fields = getAllFields(fields, type.getSuperclass());
		}

		return fields;
	}

	public ArrayList<String> ConvertToArray(Object o) throws Exception {

		ArrayList<String> jsonElements = new ArrayList<>();
		if (o != null) {

			ArrayList<Field> fields = (ArrayList<Field>) getAllFields(new ArrayList<Field>(), o.getClass());
			for (Field f : fields) {
				f.setAccessible(true);
				JsonSerializable attribute = (JsonSerializable) f.getAnnotation(JsonSerializable.class);
				if (attribute == null) {
					continue;
				}
				try {
					String jsonElement = buildElement(attribute.JsonFieldName(), f, o);
					if (!jsonElement.equals("")) {
						jsonElements.add(jsonElement);
					}
				} catch (IllegalArgumentException | IllegalAccessException e) {
					throw e;
				}

			}

		}

		return jsonElements;

	}

	protected static String buildElement(String jsonFieldName, Field f, Object o)
			throws IllegalArgumentException, IllegalAccessException {
		StringBuilder result = new StringBuilder();
		result.append("\"");
		result.append(jsonFieldName);
		result.append("\":");
		Object field = f.get(o);
		if ((field == null) || (field.toString().equals("{}") || field.toString().equals(""))) {
			return "";
		}
		Class<?> fieldClass = field.getClass();
		if (field instanceof JSONObject) {
			JSONObject j = (JSONObject) field;
			return "\"" + jsonFieldName + "\":" + j.toString().trim();
		}

		if (fieldClass.isArray()) {
			result.append(START_ARRAY);
			ArrayList<String> arrayElements = new ArrayList<>();
			Object[] values = (Object[]) f.get(o);
			if (values != null) {
				for (Object obj : values) {
					if (obj instanceof JSONObject) {
						arrayElements.add(obj.toString().trim());
					} else {
						arrayElements.add(JSONObject.quote(obj.toString().trim()));
					}
				}
			}

			result.append(String.join(",", arrayElements));
			result.append(END_ARRAY);
		} else {
			try {
				ArrayList<?> list = (ArrayList<?>) f.get(o);
				if (list != null && list.size() > 0) {
					Object[] array = list.toArray(new Object[list.size()]);
					ArrayList<String> arrayElements = new ArrayList<>();
					result.append(START_ARRAY);
					for (Object ob : array) {
						if (ob instanceof JSONObject) {
							arrayElements.add(ob.toString().trim());
						} else {
							arrayElements.add(JSONObject.quote(ob.toString().trim()));
						}
					}
					result.append(String.join(",", arrayElements));
					result.append(END_ARRAY);
					return result.toString();
				} else {
					return "";
				}

			} catch (Exception ee) {

			}

		}

		// result.append("\"");
		result.append(JSONObject.quote(f.get(o).toString().trim()));
		// result.append("\"");
		return result.toString();
	}

	



	protected boolean IsSimpleType(Object o) {
		if (o == null) {
			return false;
		}
		return o instanceof String || o instanceof Integer;
	}
	
	protected boolean IsArraySimple(Object[] array) {
		if (array==null) {
			return true;
		}
		if (array.length>0) {
			return IsSimpleType(array[0]);
		} 
		return true;
	}
	
	
	protected boolean IsCollectionSimple(Collection<?> collection) {
		if (collection==null) {
			return true;
		}
		if (collection.size()>0) {
			return IsSimpleType(collection.toArray()[0]);
		}
		return true;
	}
}

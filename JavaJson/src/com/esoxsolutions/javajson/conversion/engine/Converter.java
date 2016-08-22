package com.esoxsolutions.javajson.conversion.engine;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.codehaus.jettison.json.JSONObject;

import com.esoxsolutions.javajson.annotations.JsonSerializable;

public class Converter {

	public static String START_ARRAY = "[";
	public static String END_ARRAY = "]";
	public static String START_JSON = "{";
	public static String END_JSON = "}";
	public static String EMPTY_JSON = "{}";
	private static Converter _instance;

	public static Converter getInstance() {
		if (_instance == null) {
			_instance = new Converter();
		}

		return _instance;
	}

	private Converter() {

	}

	public static String prepareString(String aValue) {
		aValue = aValue.replace("[", "");
		aValue = aValue.replace("]", "");
		aValue = aValue.replace("\"", "\\\"");
		return aValue;
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

	public ArrayList<String> ConvertArray(Object[] array) throws Exception {
		ArrayList<String> result = new ArrayList<>();
		if (array != null) {
			for (Object element : array) {
				ArrayList<String> jsonElements = new ArrayList<>();
				ArrayList<Field> fields = (ArrayList<Field>) getAllFields(new ArrayList<Field>(), element.getClass());
				for (Field f : fields) {
					f.setAccessible(true);
					JsonSerializable attribute = (JsonSerializable) f.getAnnotation(JsonSerializable.class);
					if (attribute == null) {
						continue;
					}
					try {
						String jsonElement = buildElement(attribute.JsonFieldName(), f, element);
						result.add(jsonElement);
					} catch (IllegalArgumentException | IllegalAccessException e) {
						throw e;
					}
				}
			}
		}

		return result;
	}

	public String Convert(Object o) throws Exception {

		if (o != null) {
			ArrayList<String> jsonElements = new ArrayList<>();
			ArrayList<Field> fields = (ArrayList<Field>) getAllFields(new ArrayList<Field>(), o.getClass());
			for (Field f : fields) {
				f.setAccessible(true);
				JsonSerializable attribute = (JsonSerializable) f.getAnnotation(JsonSerializable.class);
				if (attribute == null) {
					continue;
				}
				try {
					String jsonElement = buildElement(attribute.JsonFieldName(), f, o);
					jsonElements.add(jsonElement);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					throw e;
				}

			}
			return arrayListToJson(jsonElements);
		}

		return EMPTY_JSON;
	}

	public String arrayListToJson(ArrayList<String> jsonElements) {
		StringBuilder sb = new StringBuilder(START_JSON);
		sb.append(String.join(",", jsonElements));
		sb.append(END_JSON);
		return sb.toString();
	}

	private static List<Field> getAllFields(List<Field> fields, Class<?> type) {
		fields.addAll(Arrays.asList(type.getDeclaredFields()));

		if (type.getSuperclass() != null) {
			fields = getAllFields(fields, type.getSuperclass());
		}

		return fields;
	}

	private static String quote(String string) {
		if (string == null || string.length() == 0) {
			return "\"\"";
		}

		char c = 0;
		int i;
		int len = string.length();
		StringBuilder sb = new StringBuilder(len + 4);
		String t;

		sb.append('"');
		for (i = 0; i < len; i += 1) {
			c = string.charAt(i);
			switch (c) {
			case '\\':
			case '"':
				sb.append('\\');
				sb.append(c);
				break;
			case '/':
				// if (b == '<') {
				sb.append('\\');
				// }
				sb.append(c);
				break;
			case '\b':
				sb.append("\\b");
				break;
			case '\t':
				sb.append("\\t");
				break;
			case '\n':
				sb.append("\\n");
				break;
			case '\f':
				sb.append("\\f");
				break;
			case '\r':
				sb.append("\\r");
				break;
			default:
				if (c < ' ') {
					t = "000" + Integer.toHexString(c);
					sb.append("\\u" + t.substring(t.length() - 4));
				} else {
					sb.append(c);
				}
			}
		}
		sb.append('"');
		return sb.toString();
	}

	private String buildElement(String jsonFieldName, Field f, Object o)
			throws IllegalArgumentException, IllegalAccessException {
		StringBuilder result = new StringBuilder();
		result.append("\"");
		result.append(jsonFieldName);
		result.append("\":");
		Object field = f.get(o);
		if (field.toString().equals("{}") || field.toString().equals("")) {
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
						arrayElements.add("\"" + prepareString(obj.toString().trim()) + "\"");
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
						arrayElements.add("\"" + prepareString(ob.toString().trim()) + "\"");
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

		result.append("\"");
		result.append(prepareString(f.get(o).toString().trim()));
		result.append("\"");
		return result.toString();
	}
}

package com.esoxsolutions.javajson.builders;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;

import org.codehaus.jettison.json.JSONObject;

import com.esoxsolutions.javajson.annotations.JsonSerializable;

public class JSONBuilder extends AbstractBuilder {

	@Override
	public String build(Object o) throws Exception {
		if (o != null) {
			ArrayList<String> jsonElements = ConvertToArray(o);
			return arrayListToJson(jsonElements);
		}

		return EMPTY_JSON;
	}

	@Override
	public String build(Object o, String schemaType) throws Exception {
		return build(o);
	}

	@Override
	public JSONObject buildJson(Object o) throws Exception {
		if (o == null) {
			throw new Exception("Cannot build json on null object");
		}
		JSONObject result = new JSONObject();
		ArrayList<Field> fields = (ArrayList<Field>) getAllFields(new ArrayList<Field>(), o.getClass());
		for (Field f : fields) {
			f.setAccessible(true);
			JsonSerializable attribute = (JsonSerializable) f.getAnnotation(JsonSerializable.class);
			if (attribute == null) {
				continue;
			}
			Object obj = f.get(o);
			if (IsSimpleType(obj)) {
				result.put(attribute.JsonFieldName(), obj.toString());
			} else {
				Class<?> fieldClass = obj.getClass();
				if (fieldClass.isArray()) {
					Object[] array = (Object[]) obj;

					boolean shouldConvert = !IsArraySimple(array);
					if (shouldConvert) {
						ArrayList<JSONObject> converted = new ArrayList<>();
						for (Object element : array) {
							converted.add(buildJson(element));

						}
						result.put(attribute.JsonFieldName(), converted);
					} else {
						ArrayList<String> converted = new ArrayList<>();
						for (Object element : array) {
							converted.add(element.toString());
						}
						result.put(attribute.JsonFieldName(), converted);
					}

				} else {
					try {
						Collection<?> elements = (Collection<?>) obj;
						if (elements != null) {
							System.out.println("Elements is " + elements);
							boolean shouldConvert = !IsCollectionSimple(elements);

							if (shouldConvert) {
								ArrayList<JSONObject> converted = new ArrayList<>();

								for (Object element : elements) {
									converted.add(buildJson(element));
								}
								result.put(attribute.JsonFieldName(), converted);
							} else {
								ArrayList<String> converted=new ArrayList<>();
								for (Object element:elements) {
									converted.add(element.toString());
								}
								result.put(attribute.JsonFieldName(), converted);
							}
						}
					} catch (Exception e) {
						result.put(attribute.JsonFieldName(), buildJson(obj));
					}
				}
			}
		}
		return result;
	}

	
}

package com.esoxsolutions.javajson.builders;

import java.lang.reflect.Field;
import java.util.ArrayList;

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


	

}

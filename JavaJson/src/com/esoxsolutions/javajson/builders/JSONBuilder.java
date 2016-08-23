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

	

	

}

package com.esoxsolutions.javajson.builders;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.esoxsolutions.javajson.annotations.JsonContext;
import com.esoxsolutions.javajson.annotations.JsonContexts;
import com.esoxsolutions.javajson.annotations.JsonSerializable;
import com.esoxsolutions.javajson.annotations.JsonSerializationType;

public class JSONLDBuilder extends AbstractBuilder {

	@Override
	public String build(Object o) throws Exception {

		return buildJson(o).toString();
	}

	@Override
	public String build(Object o, String schemaType) throws Exception {
		
		JSONObject result=buildJson(o);
		result.put("@context", schemaType);
		
		return result.toString();
	}



	
	private static String getTypeName(Object o) throws Exception {
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
	
	
	private static String getSchemaName(Object o) throws Exception {
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

	@Override
	public JSONObject buildJson(Object o) throws Exception {
		if (o == null) {
			throw new Exception("Cannot build json on null object");
		}
		JSONObject result = new JSONObject();
		JSONObject context=getContextFromObject(o);
		result.put("@context", context);
		//String schemaName = getSchemaName(o);
		//String typeName=getTypeName(o);
		
		//if ((schemaName!=null) && (!schemaName.equals(""))) {
		//result.put("@context",schemaName);
		//}
		//result.put("@type", typeName);
		
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
							//System.out.println("Elements is " + elements);
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

	private JSONObject getContextFromObject(Object o) throws JSONException {
		JSONObject result=new JSONObject();
		HashMap<String,String> namespaces=getNamespacesFromObject(o);
		for (String k:namespaces.keySet()) {
			result.put(k, namespaces.get(k));
		}
		ArrayList<Field> fields = (ArrayList<Field>) getAllFields(new ArrayList<Field>(), o.getClass());
		for(Field f : fields) {
			f.setAccessible(true);
			JsonSerializable annotation=(JsonSerializable)f.getAnnotation(JsonSerializable.class);
			if (annotation!=null) {
				JSONObject fieldObject=new JSONObject();
				String id=annotation.Id();
				String type=annotation.JsonType();
				
				if ((id!=null) && (!id.equals(""))) {
					fieldObject.put("@id", id);
				}
				
				if ((type!=null) && (!type.equals(""))) {
					fieldObject.put("@type", type);
				}
				
				result.put(annotation.JsonFieldName(), fieldObject);
				
			}
			
		}
		return result;
	}

	private HashMap<String, String> getNamespacesFromObject(Object o) {
		HashMap<String,String> result=new HashMap<>();
		JsonContext[] contexts = o.getClass().getAnnotationsByType(JsonContext.class);
		Annotation[] test=o.getClass().getDeclaredAnnotations();
		for (JsonContext jc:contexts) {
			result.put(jc.Name(), jc.URL());
		}
		
		return result;
	}

	

	



}

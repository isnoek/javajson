# javajson
a very small library for converting java objects to json.

In your objects (which can be any kind of object), make the fields you want to serialize public
like and give them the JsonSerializable attribute (with the compulsory JsonFieldName to make sure it gets the right name)

	@JsonSerializable(JsonFieldName="name")
	public String Name;
	

in order to generate the json

Converter.getInstance().Convert(object); 

which returns the json string

Limitations

At this moment generic types are not supported
	


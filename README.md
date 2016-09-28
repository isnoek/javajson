# javajson
a very small library for converting java objects to json.

In your objects (which can be any kind of object), make the fields you want to serialize public
like and give them the JsonSerializable attribute (with the compulsory JsonFieldName to make sure it gets the right name)

	@JsonSerializable(JsonFieldName="name")
	public String Name;
	

in order to generate the json

Converter.getInstance().Convert(object); 

which returns the json string

Update 03-08-2016
Generic types are now supported as are fields of inherited classes with this annotation 

Update 04-09-2016

The generation of JSON has been improved, there is even a switch for preliminary JSON-LD support (The class annotation JsonSerializationType). Furthermore, the json serialization of embedded objects has been added.
	


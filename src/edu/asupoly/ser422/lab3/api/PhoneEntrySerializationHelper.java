package edu.asupoly.ser422.lab3.api;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import edu.asupoly.ser422.lab3.model.PhoneEntry;

import java.io.IOException;

/*
 * This is an example of a simple custom serializer (converts a Java object to a JSON string).
 * Note all this one does is rename the property keys for Author, but what you do in the serialize
 * method is entirely up to you, so you may generalize to as rich a hypermedia format as you like.
 * 
 * Note that you would typically write a deserializer as well when you customize the JSON. A
 * deserializer goes in the opposite direction, converting JSON to a Java object. So, in this case
 */
public final class PhoneEntrySerializationHelper {
	// some locally used constant naming our phone entry field names
	private static final String __PHONENUMBER = "phoneNumber";
	private static final String __PHONEBOOKID = "phoneBookID";
	private static final String __LASTNAME = "lName";
	private static final String __FIRSTNAME = "fName";
	
	private final static PhoneEntrySerializationHelper __me = new PhoneEntrySerializationHelper();
	private ObjectMapper mapper = new ObjectMapper();
	private SimpleModule module = new SimpleModule();
	
	// Singleton
	private PhoneEntrySerializationHelper() {
		module.addSerializer(PhoneEntry.class, new AuthorJSON());
		module.addDeserializer(PhoneEntry.class, new JSONAuthor());
		mapper.registerModule(module);
	}
	
	public static PhoneEntrySerializationHelper getHelper() {
		return __me;
	}
	
	public String generateJSON(PhoneEntry phoneEntry) throws JsonProcessingException {
		// Since a custom serializer was added to the mapper via registerModule,
		// internally it will invoke the serialize method in the inner class below
		return mapper.writeValueAsString(phoneEntry);
	}
	
	public PhoneEntry consumeJSON(String json) throws IOException, JsonProcessingException {
		// A deserializer goes from JSON to the Object using the inverse process
		System.out.println("consumeJSON: " + json);
		return mapper.readValue(json, PhoneEntry.class);
	}
	
	// Inner class for custom Author deserialization.
	// Loosely based on http://tutorials.jenkov.com/java-json/jackson-objectmapper.html
    final private class JSONAuthor extends JsonDeserializer<PhoneEntry>  {
		@Override
		public PhoneEntry deserialize(JsonParser parser, DeserializationContext context)
				throws IOException, JsonProcessingException {
			PhoneEntry phoneEntry = new PhoneEntry();
			JsonToken token = parser.nextToken();
			while (!parser.isClosed()) {
				System.out.print("Deserializer processing token: " + token.asString());
				if (token != null && JsonToken.FIELD_NAME.equals(token)) {
					// we have a JSON Field, get it and see which one we have
					String fieldName = parser.getCurrentName();
					System.out.println(", field name: " + fieldName);
					// Check for which of our 4 fields comes next and set the next token in there
					token = parser.nextToken();
					if (fieldName.equals(__PHONENUMBER))
						phoneEntry.setPhoneNumber(parser.getValueAsString());
					else if (fieldName.equals(__LASTNAME))
						phoneEntry.setLastName(parser.getValueAsString());
					else if (fieldName.equals(__FIRSTNAME))
						phoneEntry.setFirstName(parser.getValueAsString());
					else if(fieldName.equals(__PHONEBOOKID))
						phoneEntry.setPhoneBookID(parser.getValueAsString());
				}
				token = parser.nextToken();
			}
			System.out.println("Deserializer returning PhoneEntry: " + phoneEntry);
			return phoneEntry;
		}
    }
    
	// Inner class for custom Author serialization.
    final private class AuthorJSON extends JsonSerializer<PhoneEntry>  {
       @Override
       public void serialize(PhoneEntry phoneEntry, JsonGenerator jgen, SerializerProvider provider)
               throws IOException, JsonProcessingException {
           jgen.writeStartObject();	
           jgen.writeNumberField(__PHONENUMBER, Integer.parseInt(phoneEntry.getPhoneNumber()));
		   jgen.writeNumberField(__PHONEBOOKID, Integer.parseInt(phoneEntry.getPhoneBookID()));
           jgen.writeStringField(__LASTNAME, phoneEntry.getLastName());
           jgen.writeStringField(__FIRSTNAME, phoneEntry.getFirstName());
           jgen.writeEndObject();
       }
   }
}

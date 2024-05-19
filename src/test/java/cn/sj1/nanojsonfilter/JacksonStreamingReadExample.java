package cn.sj1.nanojsonfilter;


import com.fasterxml.jackson.core.*;

import java.io.IOException;
import java.io.StringWriter;

public class JacksonStreamingReadExample {

	public static void main(String[] args) throws JsonParseException, IOException {

//
//		//loop through the tokens
//		Employee emp = new Employee();
//		Address address = new Address();
//		emp.setAddress(address);
//		emp.setCities(new ArrayList<String>());
//		emp.setProperties(new HashMap<String, String>());
//		List<Long> phoneNums = new ArrayList<Long>();
//		boolean insidePropertiesObj=false;
//
//		parseJSON(jsonParser, emp, phoneNums, insidePropertiesObj);
//
//		long[] nums = new long[phoneNums.size()];
//		int index = 0;
//		for(Long l :phoneNums){
//			nums[index++] = l;
//		}
//		emp.setPhoneNumbers(nums);
//
//		jsonParser.close();
//		//print employee object
//		System.out.println("Employee Object\n\n"+emp);
	}
	static JsonFactory jsonFactoryByBuilder =JsonFactory.builder().build();
public	static String filter(String jsonString) throws JsonParseException, IOException {
		try(JsonParser jsonParser = new JsonFactory().createParser(jsonString)){
			StringWriter stringWriter = new StringWriter();
			try(JsonGenerator jsonGenerator =jsonFactoryByBuilder.createGenerator(stringWriter)){
				while(jsonParser.nextToken() != null) {
					JsonToken currentToken = jsonParser.currentToken();


					switch (currentToken) {
						case START_OBJECT:
							jsonGenerator.writeStartObject();
							break;
						case END_OBJECT:
							jsonGenerator.writeEndObject();
							break;
						case START_ARRAY:
							jsonGenerator.writeStartArray();
							break;
						case END_ARRAY:
							jsonGenerator.writeEndArray();
							break;
						case FIELD_NAME:
							jsonGenerator.writeFieldName(jsonParser.currentName());
							break;
						case VALUE_STRING:
							jsonGenerator.writeString(jsonParser.getValueAsString());
							break;
						case VALUE_NUMBER_INT:
							jsonGenerator.writeNumber(jsonParser.getValueAsInt());
							break;
						case VALUE_NUMBER_FLOAT:
							jsonGenerator.writeNumber(jsonParser.getValueAsDouble());
							break;
						case VALUE_TRUE:
							jsonGenerator.writeBoolean(true);
							break;
						case VALUE_FALSE:
							jsonGenerator.writeBoolean(false);
							break;
						case VALUE_NULL:
							jsonGenerator.writeNull();
							break;
					}
				}
			}
			return stringWriter.toString();
		}
	}

}
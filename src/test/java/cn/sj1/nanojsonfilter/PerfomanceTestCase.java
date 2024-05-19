package cn.sj1.nanojsonfilter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static cn.sj1.nanojsonfilter.JsonFilterStatic.filter;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PerfomanceTestCase {
	String jsonString;

	int MAX = 1000;

	@BeforeEach
	public void setup() throws IOException {
		jsonString = Files.readString(Path.of("src/test/resources/sample.json"));
	}

	@Test
	public void test_filter_function() throws JsonProcessingException {

		String filteredJsonString = "{\"meta\":{\"tenantId\":\"tenantId\",\"moduleName\":\"moduleName\"},\"payload\":{\"key\":\"value\"}}";
		String result = null;
		long start = System.currentTimeMillis();
		for (int i = 0; i < MAX; i++) {
			result = filter(jsonString);
		}
		long end = System.currentTimeMillis();
		System.out.println("filter \t\ttime: " + (end - start));
//		assertEquals(filteredJsonString, result);

	}

	@Test
	public void test_filter_jackson() throws JsonProcessingException {

		String filteredJsonString = "{\"meta\":{\"tenantId\":\"tenantId\",\"moduleName\":\"moduleName\"},\"payload\":{\"key\":\"value\"}}";
		String result = null;
		long start = System.currentTimeMillis();
		for (int i = 0; i < MAX; i++) {
			result = filterWithJackson(jsonString);
		}
		long end = System.currentTimeMillis();
		System.out.println("jackson \t\ttime: " + (end - start));
//		assertEquals(filteredJsonString, result);

	}

	@Test
	public void test_filter_jaskson_streaming() throws IOException {

		String filteredJsonString = "{\"meta\":{\"tenantId\":\"tenantId\",\"moduleName\":\"moduleName\"},\"payload\":{\"key\":\"value\"}}";
		String result = null;
		long start = System.currentTimeMillis();
		for (int i = 0; i < MAX; i++) {
			result = JacksonStreamingReadExample.filter(jsonString);
		}
		long end = System.currentTimeMillis();
		System.out.println("jackson streaming\ttime: " + (end - start));
//		assertEquals(filteredJsonString, result);
	}

	@Test
	public void test_filter_jaskson_streaming_check() throws IOException {
		String filteredJsonString = "{\"meta\":{\"tenantId\":\"tenantId\",\"moduleName\":\"moduleName\"},\"payload\":{\"key\":\"value\"}}";
		String result = null;
		result = JacksonStreamingReadExample.filter(filteredJsonString);
		assertEquals(filteredJsonString, result);
	}

	static String filterWithJackson(String payload) throws JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode jsonNode = objectMapper.readTree(payload);
//		if(jsonNode instanceof ObjectNode objectNode){
//			objectNode.remove("meta");
//		}
		payload = objectMapper.writeValueAsString(jsonNode);
		payload = payload.trim();
		return payload;
	}
}

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

public class JsonFilterStaticTestCase {
	@Test
	public void testArray() throws JsonProcessingException {
		assertEquals("[123]", filter("[ 123 ]"));
		assertEquals("[true]", filter("[ true ]"));
		assertEquals("[false]", filter("[ false ]"));
		assertEquals("[null]", filter("[ null ]"));
		assertEquals("[\"abc\"]", filter("[ \"abc\" ]"));
		assertEquals("[null]", filter("""
						[
						null ]"""));

		assertEquals("[123,null]", filter("""
						[
							123,
							null
						]
						"""));
		assertEquals("[123,\"abc\",true,false,null]", filter("""
						[
							123,
							"abc",
							true,
							false,
							null
						]
						"""));

	}

	@Test
	public void testObject() throws JsonProcessingException {

		assertEquals("{\"key\":\"value\"}", filter("""
						{
							"key":"value"
						}
						"""));

		assertEquals("{\"key\":123}", filter("""
						{
							"key": 123
						}
						"""));

		assertEquals("{\"key\":123.456}", filter("""
						{
							"key": 123.456
						}
						"""));

		assertEquals("{\"key\":-123}", filter("""
						{
							"key": -123
						}
						"""));

		assertEquals("{\"key\":-123.456e-10}", filter("""
						{
							"key": -123.456e-10
						}
						"""));

		assertEquals("{\"key\":true}", filter("""
						{
							"key": true
						}
						"""));

		assertEquals("{\"key\":false}", filter("""
						{
							"key": false
						}
						"""));

		assertEquals("{\"key\":null}", filter("""
						{
							"key": null
						}
						"""));


		assertEquals("{\"key\":\"abc\",\"key2\":123,\"key3\":123.456,\"key4\":-123.456,\"key5\":true,\"key6\":false,\"key7\":null}", filter("""
						{
							"key": "abc",
							"key2": 123,
							"key3": 123.456,
							"key4": -123.456,
							"key5":  true,
							"key6": false,
							"key7": null
						}
						"""));

	}


}

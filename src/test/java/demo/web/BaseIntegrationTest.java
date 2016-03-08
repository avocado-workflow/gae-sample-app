package demo.web;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.test.TestRestTemplate;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import demo.WebMvcConfiguration;

public class BaseIntegrationTest {
	
	protected TestRestTemplate testRestTemplate = new TestRestTemplate();
	
	public BaseIntegrationTest() {
	    List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
	    MappingJackson2HttpMessageConverter messageConverter = new WebMvcConfiguration().customJackson2HttpMessageConverter();
	    messageConverters.add(messageConverter);
	    testRestTemplate.setMessageConverters(messageConverters); 
	}
}

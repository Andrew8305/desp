package org.apel.desp.console.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableScheduling
public class ConsoleConfig {

	@Bean
	public RestTemplate restTemplate(){
		RestTemplate restTemplate = new RestTemplate();
		SimpleClientHttpRequestFactory requestFactory = (SimpleClientHttpRequestFactory )restTemplate.getRequestFactory();
		requestFactory.setReadTimeout(1500);
		requestFactory.setConnectTimeout(1500);
		return restTemplate;
	}
	
}

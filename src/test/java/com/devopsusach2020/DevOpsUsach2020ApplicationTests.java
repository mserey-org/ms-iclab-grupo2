package com.devopsusach2020;

import java.util.logging.Logger;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@SpringBootTest(classes = DevOpsUsach2020Application.class, webEnvironment = WebEnvironment.RANDOM_PORT)
class DevOpsUsach2020ApplicationTests {
	
	private static final Logger LOGGER = Logger.getLogger("com.devopsusach2020.DevOpsUsach2020ApplicationTests");
	
	@LocalServerPort
	private int port;
	
	@Test
	void validaApiCovid() {
		RestTemplate restTemplate = new RestTemplate();
		String url = "https://api.covid19api.com/";
		try {
			ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
			LOGGER.info("response: " + response);
			Assertions.assertThat(response.getStatusCodeValue()).isEqualTo(200);
		} catch (HttpClientErrorException e) {
			Assertions.assertThat(e.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		}
	}
	
	@Test
	void getEstadoPais() throws InterruptedException {
		RestTemplate restTemplate = new RestTemplate();
		String url = "http://localhost:" + port + "/rest/mscovid/estadoPais?pais=ecuador";
		try {
			ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
			LOGGER.info("response: " + response);
			Assertions.assertThat(response.getStatusCodeValue()).isEqualTo(200);
		} catch (HttpClientErrorException e) {
			Assertions.assertThat(e.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		}
	}

}
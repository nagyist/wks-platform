package com.wks.caseengine.loader.runner;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Component
@ConditionalOnProperty("camunda.data.import.enabled")
@Order(3)
@Slf4j
public class CamundaDataImportCommandRunner implements CommandLineRunner {

	@Value("${camunda.data.import.folder}")
	private String importDir;

	@Value("${camunda.data.import.url}")
	private String baseUrl;

	@Value("${camunda.data.import.tenant}")
	private String tenantId;

	@Value("${camunda.data.import.username}")
	private String username;
	
	@Value("${camunda.data.import.password}")
	private String password;

	@Override
	public void run(String... args) throws Exception {
		log.info("Starting upload model to camunda....");

		RestTemplate restTemplate = createRestOperation();
		importData(restTemplate);
		createTenant(restTemplate);

		log.info("Finish upload model to camunda");
	}

	private void createTenant(RestTemplate restTemplate) {

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBasicAuth(username, password);

		String body = String.format("{\"id\":\"%s\", \"name\":\"Tenant Platform\"}", tenantId);

		HttpEntity<String> entity = new HttpEntity<String>(body, headers);

		try {
			restTemplate.getForEntity(String.format("%s/tenant/%s", baseUrl, tenantId), String.class);
		} catch (RestClientException e) {
			ResponseEntity<String> responseEntity = restTemplate.exchange(String.format("%s/tenant/create", baseUrl),
					HttpMethod.POST, entity, String.class);

			if (responseEntity.getStatusCode().is2xxSuccessful()) {
				log.info("Camunda created tenant {}", tenantId);
			} else if (responseEntity.getStatusCode().is5xxServerError()) {
				log.error("Error to create tenant {}. Error: {}", tenantId, responseEntity.toString());
			}
		}
	}

	private void importData(RestTemplate restTemplate) throws Exception {
		if (importDir != null && !importDir.isEmpty()) {
			listFiles(importDir).forEach(fileName -> {
				File file = new File(fileName);

				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType.MULTIPART_FORM_DATA);
				headers.setBasicAuth(username, password);

				MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
				multipartBodyBuilder.part("upload", new FileSystemResource(file));

				MultiValueMap<String, HttpEntity<?>> multipartBody = multipartBodyBuilder.build();

				HttpEntity<MultiValueMap<String, HttpEntity<?>>> httpEntity = new HttpEntity<>(multipartBody, headers);

				String action = String.format("%s/deployment/create", baseUrl);

				ResponseEntity<String> responseEntity = restTemplate.postForEntity(action, httpEntity, String.class);

				if (responseEntity.getStatusCode().is2xxSuccessful()) {
					log.info("Camunda file imported {}", file.getName());
				}
			});
		}
	}

	private Set<String> listFiles(String dir) throws IOException {
		try (Stream<Path> stream = Files.list(Paths.get(dir))) {
			return stream.filter(file -> !Files.isDirectory(file)).filter(f -> f.toFile().getName().endsWith(".bpmn"))
					.map(Path::toAbsolutePath).map(Path::toString).collect(Collectors.toSet());
		}
	}
	
	private RestTemplate createRestOperation() {
	 	    return new RestTemplate();
	}

}
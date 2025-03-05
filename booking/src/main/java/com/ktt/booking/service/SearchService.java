package com.ktt.booking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import reactor.core.publisher.Flux;

@Service
public class SearchService {

	private final WebClient.Builder webClientBuilder;

	@Autowired
	public SearchService(WebClient.Builder webClientBuilder) {
		this.webClientBuilder = webClientBuilder;
	}

	public Flux<String> streamSearchResponse(String soapRequestXml) {
		WebClient webClient = webClientBuilder.build();

		return webClient.post()
				       .uri("https://apac.universal-api.pp.travelport.com/B2BGateway/connect/uAPI/AirService")
				       .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_XML_VALUE)
				       .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_XML_VALUE)
				       .bodyValue(soapRequestXml)
				       .retrieve()
				       .bodyToFlux(String.class)
				       .doOnNext(chunk -> System.out.println("Received chunk: " + chunk));
	}
}

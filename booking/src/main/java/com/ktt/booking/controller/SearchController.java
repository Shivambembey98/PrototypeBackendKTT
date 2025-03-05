package com.ktt.booking.controller;

import com.ktt.booking.dto.SearchDto;
import com.ktt.booking.requestBuilder.LfsRequestBuilder;
import com.ktt.booking.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
public class SearchController {

	private final SearchService searchService;

	private final LfsRequestBuilder lfsRequestBuilder;

	@Autowired
		public SearchController(SearchService searchService, LfsRequestBuilder lfsRequestBuilder) {
			this.searchService = searchService;
			this.lfsRequestBuilder = lfsRequestBuilder;
	}

	@CrossOrigin()
	@PostMapping("/low-fare-search")
	public Flux<String> getAirData(@RequestBody SearchDto searchDto) {
		String soapRequestXml = lfsRequestBuilder.buildSoapRequest(searchDto);
		return searchService.streamSearchResponse(soapRequestXml);
	}
}


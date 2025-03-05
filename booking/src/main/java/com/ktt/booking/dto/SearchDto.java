package com.ktt.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchDto {

	private String origin;
	private String destination;
	private String fromDate;
	private String toDate;
	private int adults;
	private int infants;
	private int children;
}

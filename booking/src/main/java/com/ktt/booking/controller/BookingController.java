package com.ktt.booking.controller;

import com.ktt.booking.entities.AirportList;
import com.ktt.booking.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/booking")
public class BookingController {

    @Autowired
    BookingService bookingService;
    @GetMapping("/search")
    public ResponseEntity<List<AirportList>> searchAirports(@RequestParam(required = false) String query) {
        List<AirportList> results = bookingService.searchAirports(query);
        return ResponseEntity.ok(results);
    }

}

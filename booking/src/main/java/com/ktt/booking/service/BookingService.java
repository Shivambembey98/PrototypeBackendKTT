package com.ktt.booking.service;

import com.ktt.booking.entities.AirportList;
import com.ktt.booking.repository.AirportListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingService {

    private final AirportListRepository airportListRepo;

    public BookingService(AirportListRepository airportListRepo) {
        this.airportListRepo = airportListRepo;
    }

    public List<AirportList> getAllAirports() {
        return airportListRepo.findAll();
    }

    public List<AirportList> searchAirports(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getAllAirports();
        }

        String searchTermLower = searchTerm.toLowerCase().trim();

        return airportListRepo.findAll().stream()
                .filter(airport -> matchesSearchCriteria(airport, searchTermLower))
                .collect(Collectors.toList());
    }

    private boolean matchesSearchCriteria(AirportList airport, String searchTerm) {
        return (airport.getIataCode() != null && airport.getIataCode().toLowerCase().contains(searchTerm)) ||
                (airport.getAirportName() != null && airport.getAirportName().toLowerCase().contains(searchTerm)) ||
                (airport.getAirportCity() != null && airport.getAirportCity().toLowerCase().contains(searchTerm));
    }
}

package com.ktt.booking.repository;

import com.ktt.booking.entities.AirportList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AirportListRepository extends JpaRepository<AirportList,Long> {

}

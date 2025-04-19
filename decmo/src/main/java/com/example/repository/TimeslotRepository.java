package com.example.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.entity.Timeslot;

@Repository
public interface TimeslotRepository extends JpaRepository<Timeslot, Integer> {
    List<Timeslot> findByHourrange(String hourrange); 
    
    @Query("SELECT t FROM Timeslot t LEFT JOIN FETCH t.stop")
    List<Timeslot> findAllWithStop();
}

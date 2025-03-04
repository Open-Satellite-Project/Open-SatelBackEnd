package com.example.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.entity.Timeslot;

public interface TimeslotRepository extends JpaRepository<Timeslot, Integer> {
    List<Timeslot> findByHourrange(String hourrange); 
}

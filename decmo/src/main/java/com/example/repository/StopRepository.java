package com.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.entity.Stop;

public interface StopRepository extends JpaRepository <Stop,Integer> {
    
}

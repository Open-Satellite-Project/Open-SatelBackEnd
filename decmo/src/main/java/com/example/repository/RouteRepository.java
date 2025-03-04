package com.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.entity.Route;

@Repository
public interface RouteRepository extends JpaRepository<Route,Integer> {

}

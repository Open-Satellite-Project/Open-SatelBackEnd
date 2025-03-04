package com.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.entity.Operationtime;

@Repository
public interface OperationtimeRepository extends JpaRepository<Operationtime,Integer>{

}

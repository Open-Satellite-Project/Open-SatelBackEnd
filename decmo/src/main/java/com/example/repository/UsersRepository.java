package com.example.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.entity.Role;
import com.example.entity.Users;

public interface UsersRepository extends JpaRepository<Users, Integer> {
    Optional<Users> findByEmail(String email);

    List<Users> findTop10ByOrderByCreatedAtDesc();

    Optional<Users> findByEmployeeNumber(String employeeNumber);

    List<Users> findByRole(Role role);

    Optional<Users> findByName(String name);

    List<Users> findByIsApprovedTrue();

    
}

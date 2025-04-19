package com.example.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor

public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "email")
    private String email;

    @Column(name = "employee_number")
    private String employeeNumber;

    @Column(name = "name")
    private String name;

    @Column(name = "is_approved")
    private Boolean isApproved = false;

    @Column(name = "password")
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // 회원가입 승인 시간
    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    // 회원가입 승인자
    @Column(name = "approved_by")
    private Integer approvedBy;

    // 승인 코드
    @Column(name = "approved_code")
    private String approvedCode;

    // 휴직・탈퇴등 비활성화
    @Column(name = "is_active")
    private Boolean isActive = true;

    // 일정 기간 지난후 회원(관리자,직원) 삭제
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // 시스템 계정 보호
    @Column(name = "is_system-account")
    private Boolean isSystemAccount = false;

}

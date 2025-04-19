package com.example.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.cglib.core.Local;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.entity.Role;
import com.example.entity.Users;
import com.example.repository.UsersRepository;

import lombok.Getter;
import lombok.Setter;;

@Service
@Getter
@Setter

public class UsersService {

    final UsersRepository usersRepository;
    final BCryptPasswordEncoder passwordEncoder;
    // BCrypt는 보통 Service 계층에 적용
    // BCrypt varchar (60) 이상 필요

    public UsersService(
            UsersRepository usersRepository, BCryptPasswordEncoder passwordEncoder) {
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<Users> findByEmail(String email) {
        return usersRepository.findByEmail(email);
    }

    // 회원가입
    public Users registerUser(String email, String password, String name, String role, String employeeNumber) {
        // 이메일 중복 검사
        Optional<Users> existingUser = usersRepository.findByEmail(email);
        if (existingUser.isPresent()) {
            throw new RuntimeException("이미 등록된 이메일입니다.");
        }

        // 직원번호 중복 검사
        Optional<Users> empUser = usersRepository.findByEmployeeNumber(employeeNumber);
        if (existingUser.isPresent()) {
            throw new RuntimeException("이미 등록된 직원번호입니다.");
        }

        // PW
        String encodedPassword = passwordEncoder.encode(password);

        // Entity 생성
        Users newUser = new Users();
        newUser.setEmail(email);
        newUser.setPassword(encodedPassword);
        newUser.setName(name);
        newUser.setEmployeeNumber(employeeNumber);

        // Administrator 승인 전 상태
        newUser.setRole(Role.CUSTOMER);
        newUser.setIsApproved(false);

        // Role 인자 값이 Null이면 기본 값을 CUSTOMER로 지정

        if (role == null || role.isEmpty()) {
            newUser.setRole(Role.CUSTOMER);
        } else {
            // 전달된 role 값을 enum으로 변경
            try {
                newUser.setRole(Role.valueOf(role.toUpperCase()));
            } catch (IllegalArgumentException e) {
                newUser.setRole(Role.CUSTOMER);
            }
        }

        return usersRepository.save(newUser);
    }

    public List<Users> getAllMembers() {
        return usersRepository.findAll();
    }

    public List<Users> getMembersByRole(String rolestr) {
        try {
            Role role = Role.valueOf(rolestr.toUpperCase());
            return usersRepository.findByRole(role);
        } catch (IllegalArgumentException e) {
            return new ArrayList<>();
        }
    }

    // 회원가입 승인
    public void approveMember(int id) {
        Users user = usersRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        user.setIsApproved(true);
        usersRepository.save(user);
    }

    // 회원가입 취소
    public void deleteMember(int id) {
        Users user = usersRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        usersRepository.delete(user);
    }

    public void approverMember(int targetUserId, int approverUserId) {
        Users user = usersRepository.findById(targetUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setIsApproved(true);
        user.setApprovedAt(LocalDateTime.now());
        user.setApprovedBy(approverUserId); // ← user_id로 저장 (INT)
        user.setApprovedCode(UUID.randomUUID().toString()); // 고유 코드 자동 생성
        usersRepository.save(user);
    }

    // 회원 역할 변경
    public void updateRole(int userId, String roleStr) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Role newRole = Role.valueOf(roleStr.toUpperCase());

        // 시스템 계정 보호
        if (user.getIsSystemAccount() != null && user.getIsSystemAccount()) {
            throw new RuntimeException("시스템 계정은 보호되어 있어 수정할 수 없습니다.");
        }

        // ROOT_ADMIN은 스스로 다운 불가
        if (user.getRole() == Role.ROOT_ADMIN && newRole != Role.ROOT_ADMIN) {
            throw new RuntimeException("ROOT_ADMIN은 역할을 변경할 수 없습니다.");
        }

        user.setRole(newRole);
        user.setUpdatedAt(LocalDateTime.now());
        usersRepository.save(user);
    }

    // 휴가 및 퇴사 - 계정 비활성화
    public void deactivateUser(int userId) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getIsSystemAccount() != null && user.getIsSystemAccount()) {
            throw new RuntimeException("시스템 계정은 보호되어 있어 수정할 수 없습니다.");
        }
        user.setIsActive(false);
        user.setUpdatedAt(LocalDateTime.now());
        usersRepository.save(user);
    }

    // 회원탈퇴 처리 (3~5년 주기 삭제제)
    public void markUserAsDeleted(int userId) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getIsSystemAccount() != null && user.getIsSystemAccount()) {
            throw new RuntimeException("시스템 계정은 보호되어 있어 수정할 수 없습니다.");
        }

        user.setIsActive(false);
        user.setDeletedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        usersRepository.save(user);
    }
}

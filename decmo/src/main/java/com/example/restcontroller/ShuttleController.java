package com.example.restcontroller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.entity.Timeslot;
import com.example.entity.Token;
import com.example.entity.Users;
import com.example.repository.TokenRepository;
import com.example.repository.UsersRepository;
import com.example.service.ShuttleService;
import com.example.service.UsersService;
import com.example.token.JWTUtil;

import io.jsonwebtoken.Claims;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class ShuttleController {

    private final TokenRepository tokenRepository;

    final ShuttleService shuttleService;
    final JWTUtil jwtUtil;
    final UsersService usersService;
    final BCryptPasswordEncoder passwordEncoder;
    final UsersRepository usersRepository;

    public ShuttleController(
            ShuttleService shuttleservice,
            JWTUtil jwtUtil,
            UsersService usersService,
            BCryptPasswordEncoder passwordEncoder, TokenRepository tokenRepository, UsersRepository usersRepository) {
        this.shuttleService = shuttleservice;
        this.jwtUtil = jwtUtil;
        this.usersService = usersService;
        this.passwordEncoder = passwordEncoder;
        this.tokenRepository = tokenRepository;
        this.usersRepository = usersRepository;
    }

    // 노선 정보 조회
    @GetMapping("/route")
    public Map<String, Object> getRoute() {
        Map<String, Object> response = new HashMap<>();
        response.put("start", "대학교병원캠퍼스역");
        response.put("end", "대학교병");
        response.put("status", "success");

        return response;
    }

    // 정류장 위치 정보 조회 (카카오맵 API)
    @GetMapping("/stations")
    public Map<String, Object> getStation() {
        Map<String, Object> response = new HashMap<>();
        List<Map<String, Object>> stations = new ArrayList<>();

        Map<String, Object> station1 = new HashMap<>();
        station1.put("name", "대학교병원캠퍼스역");
        station1.put("lat", 35.31695856613882);
        station1.put("lng", 129.01319228841916);

        Map<String, Object> station2 = new HashMap<>();
        station2.put("name", "대학교병원");
        station2.put("lat", 35.32812167724669);
        station2.put("lng", 129.00654705719649);

        stations.add(station1);
        stations.add(station2);

        response.put("stations", stations);
        response.put("status", "success");
        return response;
    }

    // 시간표 조회
    @GetMapping("/admin/timetable")
    public Map<String, Object> getTimetable() {
        return shuttleService.getTimetable();
    }

    // 시간표 추가
    @PostMapping("/admin/timetable")
    public Map<String, Object> addTimetable(@RequestBody Timeslot timeslot) {
        return shuttleService.addTimetable(timeslot);
    }

    // 시간표 수정
    @PutMapping("/admin/timetable/{slotid}")
    public Map<String, Object> updateTimetable(@PathVariable int slotid, @RequestBody Timeslot timeslot) {
        timeslot.setSlotid(slotid);
        return shuttleService.updateTimetable(timeslot);
    }

    // 시간표 삭제
    @DeleteMapping("/admin/timetable/{slotid}")
    public Map<String, Object> deleteTimetable(@PathVariable int slotid) {
        return shuttleService.deleteTimetable(slotid);
    }

    // 현재 운행 정보 조회 (주말 이용불가)
    @GetMapping("/current")
    public Map<String, Object> getCurrentOperation() {
        return shuttleService.getCurrentOperation();
    }

    // 관리자 로그인 & token
    @PostMapping("/admin/login")
    public Map<String, Object> adminLogin(@RequestBody Map<String, Object> loginData) {
        Map<String, Object> result = new HashMap<>();
        try {
            String email = (String) loginData.get("adminEmail");
            String password = (String) loginData.get("adminPassword");

            Users adminUser = usersService.findByEmail(email).orElse(null);
            if (adminUser == null) {
                result.put("status", "error");
                result.put("message", "로그인 정보가 일치하지 않습니다.");
                return result;
            }
            // 3) 비밀번호 검증 (BCrypt)
            if (!passwordEncoder.matches(password, adminUser.getPassword())) {
                result.put("status", "error");
                result.put("message", "로그인 정보가 일치하지 않습니다.");
                return result;
            }

            if (!adminUser.getIsActive()) {
                result.put("status","error");
                result.put("message","비활성화된 계정입니다. 관리자에게 문의해주세요.");
                return result;
            }

            String role = adminUser.getRole().name();
            if (!role.equals("SUPER_ADMIN") && !role.equals("ROOT_ADMIN") && !role.equals("EMP")) {
                result.put("status", "error");
                result.put("message", "해당 계정은 로그인 권한이 없습니다. 관리자의 승인을 기다려주세요.");
                return result;
            }

            // 토큰 생성
            String token = jwtUtil.createTokenForAdmin(adminUser.getEmail(), adminUser.getRole().name());

            // 기존 토큰 삭제후 새로 저장 (optional)
            tokenRepository.findByAdminEmail(email).ifPresent(tokenRepository::delete);

            Token tokenEntity = new Token();
            tokenEntity.setAdminEmail(email);
            tokenEntity.setToken(token);
            tokenEntity.setExpiretime(LocalDateTime.now().plusHours(4));
            tokenRepository.save(tokenEntity);

            Map<String, Object> userData = new HashMap<>();
            userData.put("userId", adminUser.getUserId());
            userData.put("email", adminUser.getEmail());
            userData.put("name", adminUser.getName());
            userData.put("role", adminUser.getRole());

            result.put("status", "success");
            result.put("token", token);
            result.put("user", userData);
            return result;

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            result.put("status", "error");
            result.put("message", "로그인 실패: " + e.getMessage());
            return result;
        }
    }

    // 로그아웃
    @PostMapping("/admin/logout")
    public Map<String, Object> adminLogout(
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        Map<String, Object> result = new HashMap();
        try {
            if (authorization == null || !authorization.startsWith("Bearer ")) {
                result.put("status", "error");
                result.put("message", "인증 토큰이 없습니다.");
                return result;
            }

            String token = authorization.replace("Bearer", "").trim();
            Claims claims = jwtUtil.validate(token);
            String adminEmail = claims.get("adminEmail", String.class);

            tokenRepository.findByAdminEmail(adminEmail).ifPresent(tokenRepository::delete);

            result.put("status", "success");
            result.put("message", "로그아웃 되었습니다.");

        } catch (

        Exception e) {
            result.put("status", "error");
            result.put("message", "로그아웃 실패" + e.getMessage());
        }
        return result;
    }

    // 회원가입
    @PostMapping("/admin/register")
    public Map<String, Object> adminRegister(@RequestBody Map<String, Object> registerData) {
        Map<String, Object> result = new HashMap<>();
        try {
            String email = (String) registerData.get("adminEmail");
            String password = (String) registerData.get("adminPassword");
            String name = (String) registerData.get("name");
            String role = (String) registerData.get("role");
            String employeeNumber = (String) registerData.get("employeeNumber");
            if (role == null) {
                role = "CUSTOMER";
            }

            Users newAdmin = usersService.registerUser(email, password, name, role, employeeNumber);

            result.put("status", "success");
            result.put("message", "회원가입 성공");
            result.put("user", newAdmin);
        } catch (Exception e) {
            result.put("status", "error");
            result.put("message", e.getMessage());
        }
        return result;
    }

    @GetMapping("/admin/members")
    public ResponseEntity<List<Users>> getAllMembers(@RequestParam(required = false) String role) {
        List<Users> members;

        if ("CUSTOMER".equals(role)) {
            members = usersService.getMembersByRole("CUSTOMER");
        } else {
            // 모든 회원 조회 (필요에 따라 처리)
            members = usersService.getAllMembers();
        }
        return ResponseEntity.ok(members);
    }

    // 회원 가입 승인
    @PutMapping("/admin/members/{id}/approve")
    public ResponseEntity<?> approveMember(@PathVariable int id,
            @RequestHeader("Authorization") String authorization,
            @RequestBody Map<String, Object> requestBody) {
        try {
            boolean approved = (boolean) requestBody.get("approved");

            if (!approved) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("승인 거절");
            }

            String token = authorization.replace("Bearer ", "").trim();
            Claims claims = jwtUtil.validate(token);
            String approverEmail = claims.get("adminEmail", String.class);

            Users approver = usersService.findByEmail(approverEmail)
                    .orElseThrow(() -> new RuntimeException("승인자 찾을 수 없음"));
            int approverUserId = approver.getUserId();

            usersService.approverMember(id, approverUserId);
            usersService.updateRole(id, "EMP");

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // 회원 가입 취소
    @DeleteMapping("/admin/members/{id}")
    public ResponseEntity<?> deleteMember(@PathVariable int id) {
        try {
            usersService.deleteMember(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // 회원 승인 이력
    @GetMapping("/admin/approval-log")
    public ResponseEntity<?> getApprovalLog() {
        List<Users> approvedUsers = usersRepository.findByIsApprovedTrue();

        List<Map<String, Object>> result = new ArrayList<>();

        for (Users user : approvedUsers) {
            Map<String, Object> data = new HashMap<>();
            data.put("name", user.getName());
            data.put("employeeNumber", user.getEmployeeNumber());
            data.put("approvedAt", user.getApprovedAt());
            data.put("approvedBy", user.getApprovedBy());
            data.put("approvedCode", user.getApprovedCode());

            if (user.getApprovedBy() != null) {
                usersRepository.findById(user.getApprovedBy())
                        .ifPresent(approver -> data.put("approvedByName", approver.getName()));
            } else {
                data.put("approvedByName", "미확인");
            }
            result.add(data);
        }
        return ResponseEntity.ok(result);
    }

    // 권한 변경
    @PutMapping("/admin/users/{id}/role")
    public ResponseEntity<?> updateUserRole(@PathVariable int id, @RequestBody Map<String, String> req) {
        try {
            String newRole = req.get("role");
            if (newRole == null) {
                return ResponseEntity.badRequest().body("변경할 역할이 지정되지 않았습니다.");
            }
            usersService.updateRole(id, newRole);
            return ResponseEntity.ok("역할이 변경 되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("유효하지 않은 역할입니다.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("오류발생:" + e.getMessage());
        }
    }

    // 권한 비활성화
    @PutMapping("/admin/users/{id}/deactivate")
    public ResponseEntity<?> deactivateUser(@PathVariable int id) {
        try {
            usersService.deactivateUser(id); // is_active = false 처리
            return ResponseEntity.ok("계정이 비활성화되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("오류발생" + e.getMessage());
        }
    }

    // 회원 탈퇴 (3~5년 주기 삭제)
    @PutMapping("/admin/users/{id}/delete")
    public ResponseEntity<?> deleteUser(@PathVariable int id) {
        try {
            usersService.markUserAsDeleted(id);
            return ResponseEntity.ok("탈퇴 처리 완료.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("오류 발생: " + e.getMessage());
        }
    }
}
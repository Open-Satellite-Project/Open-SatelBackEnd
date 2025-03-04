package com.example.restcontroller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.entity.Timeslot;
import com.example.service.ShuttleService;
import com.example.token.JWTUtil;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class ShuttleController {

    final ShuttleService shuttleService;
    final JWTUtil jwtUtil;

    public ShuttleController(ShuttleService shuttleservice, JWTUtil jwtUtil) {
        this.shuttleService = shuttleservice;
        this.jwtUtil = jwtUtil;
    }

    // 노선 정보 조회
    @GetMapping("/route")
    public Map<String, Object> getRoute() {
        Map<String, Object> response = new HashMap<>();
        response.put("start", "양산부산대학교캠퍼스역");
        response.put("end", "양산부산대학교병원");
        response.put("status", "success");

        return response;
    }

    // 정류장 위치 정보 조회 (카카오맵 API)
    @GetMapping("/stations")
    public Map<String, Object> getStation() {
        Map<String, Object> response = new HashMap<>();
        List<Map<String, Object>> stations = new ArrayList<>();

        Map<String, Object> station1 = new HashMap<>();
        station1.put("name", "양산부산대학교캠퍼스역");
        station1.put("lat", 35.31695856613882);
        station1.put("lng", 129.01319228841916);

        Map<String, Object> station2 = new HashMap<>();
        station2.put("name", "양산부산대학교병원");
        station2.put("lat", 35.32812167724669);
        station2.put("lng", 129.00654705719649);

        stations.add(station1);
        stations.add(station2);

        response.put("stations", stations);
        response.put("status", "success");
        return response;
    }

    // 시간표 조회
    @GetMapping("/timetable")
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
    public Map<String, Object> adminLogin(@RequestBody Map<String, Object> loginData, HttpServletResponse response) {
        Map<String, Object> result = new HashMap<>();
        try {
            System.out.println("Received login request");
            System.out.println("Login data: " + loginData);

            String adminEmail = (String) loginData.get("adminEmail");
            System.out.println("Admin email: " + adminEmail);

            String token = jwtUtil.createTokenForAdmin(1);
            System.out.println("Generated token: " + token);

            // HttpOnly
            Cookie cookie = new Cookie("token", token);
            cookie.setHttpOnly(true); // JavaScript 접근 차단
            cookie.setSecure(false); // HTTPS 사용시 Ture로 변경 (개발 단계인 현재는 False로 설정) 
            cookie.setPath("/");
            cookie.setMaxAge(60*60*4);
            response.addCookie(cookie);

            result.put("status", "success");
            result.put("token", "로그인 성공");
            return result;
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            result.put("status", "error");
            result.put("message", "로그인 실패: " + e.getMessage());
            return result;
        }
    }
    
    
}
package com.example.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.example.entity.Timeslot;
import com.example.entity.Users;
import com.example.repository.DepartuetimeRepository;
import com.example.repository.StopRepository;
import com.example.repository.TimeslotRepository;
import com.example.repository.UsersRepository;

@Service
public class ShuttleService {

    final TimeslotRepository timeslotRepository;
    final DepartuetimeRepository departuetimeRepository;
    final StopRepository stopRepository;
    final UsersRepository usersRepository;

    public ShuttleService(
            TimeslotRepository timeslotRepository,
            DepartuetimeRepository departuetimeRepository,
            StopRepository stopRepository, UsersRepository usersRepository) {
        this.timeslotRepository = timeslotRepository;
        this.departuetimeRepository = departuetimeRepository;
        this.stopRepository = stopRepository;
        this.usersRepository = usersRepository;
    }

    // 고객용 스케줄 리스트
    public Map<String, Object> getcustTimetable() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Timeslot> timeSlots = timeslotRepository.findAll();

            // 시간대 그룹화
            Map<String, List<String>> groupTimes = new HashMap<>();
            for (Timeslot t : timeSlots) {
                if (t.getHourrange() != null && t.getHourrange().contains(":")) {
                    String[] parts = t.getHourrange().split(":");
                    String hour = parts[0];
                    String minute = parts[1] + "분";
                    groupTimes.computeIfAbsent(hour, k -> new ArrayList<>()).add(minute);
                }
            }

            response.put("groupTimes", groupTimes);
            response.put("status", "success");
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
        }
        return response;
    }

    // 시간표 조회
    public Map<String, Object> getTimetable() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Timeslot> timeSlots = timeslotRepository.findAllWithStop();
            System.out.println("조회된 시간표 데이터: " + timeSlots);
            response.put("timeSlots", timeSlots);
            response.put("status", "success");
        } catch (Exception e) {
            System.out.println("시간표 조회 중 오류 발생: " + e.getMessage());
            e.printStackTrace(); // 스택 트레이스 출력
            response.put("status", "error");
            response.put("message", e.getMessage());
        }
        return response;
    }

    // 시간표 추가
    public Map<String, Object> addTimetable(Timeslot timeslot) {
        Map<String, Object> response = new HashMap<>();
        try {
            // DB에 저장된 최종 엔티티(자동 생성된 slotid 포함)를 받기 위해 save() 결과를 변수에 담음
            Timeslot saved = timeslotRepository.save(timeslot);

            response.put("status", "success");

            // data라는 키 아래에, 새로 생성된 slotid를 포함한 정보를 담아 반환
            Map<String, Object> data = new HashMap<>();
            data.put("slotid", saved.getSlotid());
            data.put("hourrange", saved.getHourrange());
            data.put("type", saved.getType());
            data.put("isactive", saved.getIsactive());

            response.put("data", data);

        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
        }
        return response;
    }

    // 시간표 수정
    public Map<String, Object> updateTimetable(Timeslot timeslot) {
        Map<String, Object> response = new HashMap<>();
        try {
            // 기존 slotid에 해당하는 데이터를 조회
            Timeslot existingSlot = timeslotRepository.findById(timeslot.getSlotid()).orElse(null);

            if (existingSlot == null) {
                response.put("status", "error");
                response.put("message", "시간표를 찾을 수 없습니다.");
                return response;
            }

            // 값이 있을 경우만 업데이트
            if (timeslot.getHourrange() != null) {
                existingSlot.setHourrange(timeslot.getHourrange());
            }

            existingSlot.setIsactive(timeslot.getIsactive());

            // 수정된 데이터 저장
            timeslotRepository.save(existingSlot);

            response.put("status", "success");
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
        }
        return response;
    }

    // 시간표 삭제
    public Map<String, Object> deleteTimetable(int slotid) {
        Map<String, Object> response = new HashMap<>();
        try {
            timeslotRepository.deleteById(slotid);
            response.put("status", "success");
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
        }
        return response;
    }

    // 현재 운행 정보 조회
    public Map<String, Object> getCurrentOperation() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Timeslot> timeSlots = timeslotRepository.findAll();
            response.put("timeSlots", timeSlots);
            response.put("status", "success");
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
        }
        return response;
    }

    public List<Users> getAllMembers(){
        return usersRepository.findAll();
    }
}
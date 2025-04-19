package com.example.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.entity.Users;
import com.example.repository.UsersRepository;

@Service
public class DashBoardService {

    final UsersRepository usersRepository;

    public DashBoardService(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    // 전체 사용자수 조회
    public long getTotalUserCount() {
        return usersRepository.count();
    }

    // 최근 가입자 목록 ()
    public List<String> getRecentRegisterations() {
        return usersRepository.findTop10ByOrderByCreatedAtDesc()
                .stream()
                .map(Users::getEmail)
                .collect(Collectors.toList());
    }

    public Map<String, Object> getDashBoardDataForUser(Integer userId) {
        Map<String, Object> result = new HashMap<>();

        result.put("totalUserCount", getTotalUserCount());
        result.put("RecentRegisterations", getRecentRegisterations());
        return result;
    }
}

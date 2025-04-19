package com.example.restcontroller;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.service.DashBoardService;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/admin")
public class DashBoardController {

    private final DashBoardService dashboardService;

    public DashBoardController(DashBoardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashBoard(HttpServletRequest request) {
        Integer currentUserId = (Integer) request.getAttribute("user_id");
        Map<String, Object> dashboardData = dashboardService.getDashBoardDataForUser(currentUserId);
        return ResponseEntity.ok(dashboardData);
    }
}

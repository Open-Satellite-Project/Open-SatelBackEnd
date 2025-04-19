package com.example.token;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.repository.TokenRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class JwtFilter extends OncePerRequestFilter {

    final TokenRepository tokenRepository;
    final JWTUtil jwtUtil;
    final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 검증 패스
        String path = request.getServletPath();
        if (path.equals("/api/admin/register") || path.equals("/api/admin/login")) {
            filterChain.doFilter(request, response);
            return;
        }

        // jsp에서 json으로 변경
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        Map<String, Object> map = new HashMap<>();

        try {
            System.out.println("=============Filter=============");
            System.out.println(request.getRequestURI());
            System.out.println("=============Filter=============");

            String authHeader = request.getHeader("Authorization");

            if (authHeader == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
                map.put("status", 0);
                map.put("result", "토큰 키가 없습니다");
                String json = objectMapper.writeValueAsString(map);
                response.getWriter().write(json);
                return;
            }

            if (authHeader.length() <= 0) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
                map.put("status", 0);
                map.put("result", "토큰 값이 없습니다");
                String json = objectMapper.writeValueAsString(map);
                response.getWriter().write(json);
                return;
            }

            if (!authHeader.startsWith("Bearer")) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
                map.put("status", 0);
                map.put("result", "토큰 구조가 다릅니다");
                String json = objectMapper.writeValueAsString(map);
                response.getWriter().write(json);
                return;
            }

            // 실제 토큰
            String token = authHeader.substring(7);

            // 토큰 검증이 유효하지 않으면 Exception 발생
            Claims claims = jwtUtil.validate(token);
            response.setStatus(HttpServletResponse.SC_OK); // 200

            String role = claims.get("role", String.class);
            SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role);

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
             claims.getSubject(), null, Collections.singletonList(authority));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            request.setAttribute("adminEmail", claims.get("adminEmail", String.class));

            filterChain.doFilter(request, response);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
            System.out.println(e.getMessage());
            map.put("status", -1);
            map.put("result", "토큰 값이 유효하지 않습니다.");

            // map을 json으로 변경
            String json = objectMapper.writeValueAsString(map);
            response.getWriter().write(json);
        }
        
    }

}

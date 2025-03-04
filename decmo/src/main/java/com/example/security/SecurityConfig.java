package com.example.security;

import java.security.Security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.example.token.*;;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authorize -> authorize
                        // GET 요청 모두허용 고객 접근
                        .requestMatchers(HttpMethod.GET, "/api/timetable").permitAll()
                        // 관리자 , 직원
                        .requestMatchers(HttpMethod.POST, "/api/timetable").hasAnyRole("SUPER_ADMIN", "EMP")
                        .requestMatchers(HttpMethod.PUT, "/api/timetable/**").hasAnyRole("SUPER_ADMIN", "EMP")
                        .requestMatchers(HttpMethod.DELETE, "/api/timetable/**").hasAnyRole("SUPER_ADMIN", "EMP")
                        // 그 외
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable());

        return http.build();
    }
}
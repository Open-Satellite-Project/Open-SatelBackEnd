package com.example.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.example.token.JwtFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // React 개발 서버에서 오는 요청 허용 
        configuration.addAllowedOrigin("http://localhost:3000");
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        configuration.setAllowCredentials(true); // 쿠키 전달 허용
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors().and()  // CORS 활성화
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers(HttpMethod.GET, "/api/admin/timetable").hasAnyRole("ROOT_ADMIN","SUPER_ADMIN", "EMP")
                .requestMatchers(HttpMethod.POST, "/api/admin/timetable").hasAnyRole("ROOT_ADMIN","SUPER_ADMIN", "EMP")
                .requestMatchers(HttpMethod.PUT, "/api/admin/timetable/**").hasAnyRole("ROOT_ADMIN","SUPER_ADMIN", "EMP")
                .requestMatchers(HttpMethod.DELETE, "/api/admin/timetable/**").hasAnyRole("ROOT_ADMIN","SUPER_ADMIN", "EMP")
                
                // 컨텍스트 경로는 서블릿 경로에서 제거되므로, permitAll 대상은 "/api/admin/register", "/api/admin/login"으로 지정
                .requestMatchers("/api/admin/register", "/api/admin/login").permitAll()
                .requestMatchers("/api/admin/**", "/admin/**").hasAnyRole("ROOT_ADMIN","SUPER_ADMIN", "EMP")
                .anyRequest().authenticated())
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable());

        return http.build();
    }   
}

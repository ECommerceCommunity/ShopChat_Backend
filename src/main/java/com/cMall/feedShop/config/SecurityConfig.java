package com.cMall.feedShop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                // CSRF 보호 비활성화 (REST API의 경우 일반적으로 토큰 기반 인증 사용 시 비활성화)
                .csrf(csrf -> csrf.disable())

                // 예외 처리 설정 (JWT 도입 시 인증/인가 실패 처리)
                // .exceptionHandling(exceptionHandling -> exceptionHandling
                //     .authenticationEntryPoint(jwtAuthenticationEntryPoint) // JWT 인증 실패 시
                //     .accessDeniedHandler(jwtAccessDeniedHandler) // JWT 인가 실패 시
                // )

                // 세션 관리 비활성화 (JWT 사용 시 무상태(stateless)로 설정)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // HTTP 요청에 대한 접근 권한 설정
                .authorizeHttpRequests(auth -> auth
                                // 특정 경로에 대한 접근 허용 (회원가입, 로그인 등 인증이 필요 없는 경로)
                                // .requestMatchers("/api/auth/**", "/api/users/signup").permitAll()
                                // 현재는 모든 요청을 허용 (테스트용)
                                .anyRequest().permitAll()
                        // 그 외 모든 요청은 인증 필요
                        // .anyRequest().authenticated()
                )

                // HTTP Basic 인증 비활성화 또는 제거 (JWT 사용 시)
                .httpBasic(withDefaults()); // 현재는 withDefaults()로 설정되어 있으나, JWT 도입 시 제거 또는 .disable()로 변경

        // 폼 로그인 비활성화 (JWT 사용 시)
        // .formLogin(formLogin -> formLogin.disable());

        // JWT 필터 추가 (JWT 도입 시 여기에 JwtAuthenticationFilter 등을 추가)
        // .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);


        return httpSecurity.build();
    }
}

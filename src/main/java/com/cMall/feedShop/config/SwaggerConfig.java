package com.cMall.feedShop.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        String jwtSchemeName = "jwtAuth"; // JWT 인증 스키마 이름 정의
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwtSchemeName); // API 요청 시 JWT가 필요함을 명시

        Components components = new Components()
                .addSecuritySchemes(jwtSchemeName, new SecurityScheme()
                        .name(jwtSchemeName)
                        .type(SecurityScheme.Type.HTTP) // HTTP 인증 타입 (Bearer)
                        .scheme("bearer") // Bearer 토큰 방식 사용
                        .bearerFormat("JWT")); // JWT 포맷임을 명시

        return new OpenAPI()
                .components(components) // 컴포넌트 추가 (인증 스키마 포함)
                .info(apiInfo()) // API 정보 추가 (제목, 설명, 버전 등)
                .addSecurityItem(securityRequirement); // 모든 API에 JWT 보안 요구사항 적용
    }

    private Info apiInfo() {
        return new Info()
                .title("FeedShop API Documentation") // API 문서 제목
                .description("FeedShop 쇼핑몰 API 문서입니다.") // API 문서 설명
                .version("1.0.0"); // API 버전
    }
}
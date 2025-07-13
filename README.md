# FeedShop 백엔드 프로젝트

[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=ECommerceCommunity_FeedShop_Backend&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=ECommerceCommunity_FeedShop_Backend)

신발 전문 쇼핑몰 FeedShop의 백엔드 API 서버입니다.

## 📜 프로젝트 개요

본 프로젝트는 신발을 판매하는 이커머스 플랫폼의 서버를 구축하는 것을 목표로 합니다. 사용자 관리, 상품, 주문, 결제, 커뮤니티(리뷰, 채팅) 등 쇼핑몰의 핵심 기능을 제공합니다.

프론트엔드 프로젝트는 React로 별도 관리되고 있습니다.

## ✨ 주요 기능

-   **사용자 관리**: 회원가입, 로그인(JWT 기반 인증), 소셜 로그인, 마이페이지, 회원 정보 수정
-   **장바구니**: 상품 담기, 수량 변경, 선택 삭제 기능
-   **주문 및 결제**: 상품 주문, 주문 내역 조회, 결제 연동
-   **리뷰**: 사용자가 구매한 상품에 대한 리뷰 작성, 조회, 수정, 삭제
-   **포인트 및 쿠폰**: 활동에 따른 포인트 적립/사용, 쿠폰 발급 및 관리
-   **관리자**: 사용자 및 상품 관리를 위한 관리자 기능

## 🛠️ 기술 스택

### Backend
-   **Framework**: Spring Boot 3.3.12
-   **Language**: Java 17
-   **Build Tool**: Gradle
-   **Database**: MySQL, H2 (for tests)
-   **ORM**: Spring Data JPA
-   **Security**: Spring Security, JWT (JSON Web Token)
-   **API Documentation**: SpringDoc (Swagger UI)

### Frontend
-   **Framework**: React (별도 레포지토리에서 관리)

### DevOps & Code Quality
-   **CI/CD**: GitHub Actions
-   **Code Analysis**: SonarCloud
-   **Test Coverage**: Jacoco

## 🚀 시작하기

### 사전 요구사항

-   Java 17
-   Gradle
-   MySQL

### 백엔드 실행

1.  **레포지토리 클론:**
    ```bash
    git clone https://github.com/{your-github-id}/FeedShop_Backend.git
    cd FeedShop_Backend
    ```

2.  **application.yml 설정:**
    `src/main/resources/` 경로의 `application.yml` 파일에서 본인 환경에 맞게 DB 및 JWT 설정을 수정합니다.

3.  **애플리케이션 실행:**
    ```bash
    ./gradlew bootRun
    ```

### 프론트엔드 실행

프론트엔드 레포지토리의 `README.md` 파일을 참고하여 실행해 주세요. 일반적으로 아래와 같은 명령어를 사용합니다.

```bash
npm install
npm start
```

## 📖 API 문서

애플리케이션 실행 후, 아래 URL로 접속하여 API 문서를 확인할 수 있습니다.

-   [https://localhost:8443/swagger-ui.html](https://localhost:8443/swagger-ui.html)
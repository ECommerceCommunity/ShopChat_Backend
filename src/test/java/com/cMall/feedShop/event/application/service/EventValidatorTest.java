package com.cMall.feedShop.event.application.service;

import com.cMall.feedShop.event.application.dto.request.EventCreateRequestDto;
import com.cMall.feedShop.event.application.exception.EventException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("EventValidator 테스트")
class EventValidatorTest {

    private EventValidator eventValidator;
    private EventCreateRequestDto validRequestDto;

    @BeforeEach
    void setUp() {
        eventValidator = new EventValidator();
        
        validRequestDto = EventCreateRequestDto.builder()
                .type(com.cMall.feedShop.event.domain.enums.EventType.BATTLE)
                .title("테스트 이벤트")
                .description("테스트 이벤트 설명")
                .eventStartDate(LocalDate.now())
                .eventEndDate(LocalDate.now().plusDays(7))
                .purchaseStartDate(LocalDate.now())
                .purchaseEndDate(LocalDate.now().plusDays(5))
                .build();
    }

    @Test
    @DisplayName("유효한 요청 검증 성공")
    void validateEventCreateRequest_Success() {
        // When & Then
        eventValidator.validateEventCreateRequest(validRequestDto);
        // 예외가 발생하지 않으면 성공
    }

    @Test
    @DisplayName("이벤트 타입이 null인 경우 예외 발생")
    void validateEventCreateRequest_NullType() {
        // Given
        validRequestDto = EventCreateRequestDto.builder()
                .title("테스트 이벤트")
                .description("테스트 이벤트 설명")
                .build();

        // When & Then
        assertThatThrownBy(() -> eventValidator.validateEventCreateRequest(validRequestDto))
                .isInstanceOf(EventException.InvalidEventTypeException.class);
    }

    @Test
    @DisplayName("제목이 null인 경우 예외 발생")
    void validateEventCreateRequest_NullTitle() {
        // Given
        validRequestDto = EventCreateRequestDto.builder()
                .type(com.cMall.feedShop.event.domain.enums.EventType.BATTLE)
                .description("테스트 이벤트 설명")
                .build();

        // When & Then
        assertThatThrownBy(() -> eventValidator.validateEventCreateRequest(validRequestDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이벤트 제목은 필수입니다.");
    }

    @Test
    @DisplayName("제목이 빈 문자열인 경우 예외 발생")
    void validateEventCreateRequest_EmptyTitle() {
        // Given
        validRequestDto = EventCreateRequestDto.builder()
                .type(com.cMall.feedShop.event.domain.enums.EventType.BATTLE)
                .title("")
                .description("테스트 이벤트 설명")
                .build();

        // When & Then
        assertThatThrownBy(() -> eventValidator.validateEventCreateRequest(validRequestDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이벤트 제목은 필수입니다.");
    }

    @Test
    @DisplayName("설명이 null인 경우 예외 발생")
    void validateEventCreateRequest_NullDescription() {
        // Given
        validRequestDto = EventCreateRequestDto.builder()
                .type(com.cMall.feedShop.event.domain.enums.EventType.BATTLE)
                .title("테스트 이벤트")
                .build();

        // When & Then
        assertThatThrownBy(() -> eventValidator.validateEventCreateRequest(validRequestDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이벤트 설명은 필수입니다.");
    }

    @Test
    @DisplayName("설명이 빈 문자열인 경우 예외 발생")
    void validateEventCreateRequest_EmptyDescription() {
        // Given
        validRequestDto = EventCreateRequestDto.builder()
                .type(com.cMall.feedShop.event.domain.enums.EventType.BATTLE)
                .title("테스트 이벤트")
                .description("")
                .build();

        // When & Then
        assertThatThrownBy(() -> eventValidator.validateEventCreateRequest(validRequestDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이벤트 설명은 필수입니다.");
    }

    @Test
    @DisplayName("이벤트 시작일이 종료일보다 늦은 경우 예외 발생")
    void validateEventCreateRequest_InvalidEventDateRange() {
        // Given
        validRequestDto = EventCreateRequestDto.builder()
                .type(com.cMall.feedShop.event.domain.enums.EventType.BATTLE)
                .title("테스트 이벤트")
                .description("테스트 이벤트 설명")
                .eventStartDate(LocalDate.now().plusDays(7))
                .eventEndDate(LocalDate.now())
                .build();

        // When & Then
        assertThatThrownBy(() -> eventValidator.validateEventCreateRequest(validRequestDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이벤트 시작일은 종료일보다 이전이어야 합니다.");
    }

    @Test
    @DisplayName("구매 시작일이 종료일보다 늦은 경우 예외 발생")
    void validateEventCreateRequest_InvalidPurchaseDateRange() {
        // Given
        validRequestDto = EventCreateRequestDto.builder()
                .type(com.cMall.feedShop.event.domain.enums.EventType.BATTLE)
                .title("테스트 이벤트")
                .description("테스트 이벤트 설명")
                .purchaseStartDate(LocalDate.now().plusDays(5))
                .purchaseEndDate(LocalDate.now())
                .build();

        // When & Then
        assertThatThrownBy(() -> eventValidator.validateEventCreateRequest(validRequestDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("구매 시작일은 종료일보다 이전이어야 합니다.");
    }
} 
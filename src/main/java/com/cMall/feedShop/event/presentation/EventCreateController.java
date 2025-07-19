package com.cMall.feedShop.event.presentation;

import com.cMall.feedShop.common.dto.ApiResponse;
import com.cMall.feedShop.event.application.dto.request.EventCreateRequestDto;
import com.cMall.feedShop.event.application.dto.response.EventCreateResponseDto;
import com.cMall.feedShop.event.application.service.EventCreateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventCreateController {
    private final EventCreateService eventCreateService;

    /**
     * 이벤트 생성
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<EventCreateResponseDto>> createEvent(
        @ModelAttribute EventCreateRequestDto requestDto,
        @RequestPart(value = "image", required = false) MultipartFile image,
        @RequestPart(value = "rewards", required = false) String rewardsJson
    ) {
        // rewards 파싱 (프론트에서 JSON.stringify로 보낸 경우)
        if (rewardsJson != null && !rewardsJson.isBlank()) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                List<EventCreateRequestDto.EventRewardRequestDto> rewards =
                    objectMapper.readValue(rewardsJson,
                        objectMapper.getTypeFactory().constructCollectionType(List.class, EventCreateRequestDto.EventRewardRequestDto.class));
                requestDto.setRewards(rewards);
            } catch (Exception e) {
                throw new IllegalArgumentException("이벤트 보상 정보 파싱에 실패했습니다.", e);
            }
        }
        // 이미지 파일 처리 필요시 requestDto.setImageUrl(...) 등으로 활용
        EventCreateResponseDto responseDto = eventCreateService.createEvent(requestDto);
        ApiResponse<EventCreateResponseDto> response = ApiResponse.<EventCreateResponseDto>builder()
                .success(true)
                .message("이벤트가 성공적으로 생성되었습니다.")
                .data(responseDto)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
} 
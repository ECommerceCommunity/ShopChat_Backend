package com.cMall.feedShop.stores.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.cMall.feedShop.stores.application.dto.request.StoreCreateRequest;
import com.cMall.feedShop.stores.application.dto.response.StoreResponse;
import com.cMall.feedShop.stores.domain.Store;
import com.cMall.feedShop.stores.domain.repository.StoreRepository;
import com.cMall.feedShop.user.domain.model.User;
import com.cMall.feedShop.user.domain.enums.UserRole;
import com.cMall.feedShop.user.domain.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;
    private final UserRepository userRepository;

    public StoreResponse createStore(StoreCreateRequest request) {
        // ✅ 1. userId로 유저 조회
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        // ✅ 2. ROLE_SELLER인지 검증
        if (!user.getRole().equals(UserRole.ROLE_SELLER)) {
            throw new IllegalArgumentException("해당 유저는 판매자 계정(ROLE_SELLER)이 아닙니다.");
        }

        // ✅ 3. Store 생성 시 User 설정
        Store store = new Store(
                request.getDescription(),
                request.getLogo(),
                request.getName(),
                user // 생성자 또는 setter로 설정
        );

        // ✅ 4. 저장
        Store saved = storeRepository.save(store);

        // ✅ 5. 응답 반환
        return new StoreResponse(
                saved.getId(),
                saved.getName(),
                saved.getDescription(),
                saved.getLogo(),
                saved.getNumbersLikes()
        );
    }
}

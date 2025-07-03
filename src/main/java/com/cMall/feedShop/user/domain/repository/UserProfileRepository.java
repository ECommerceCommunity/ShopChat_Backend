package com.cMall.feedShop.user.domain.repository;
import com.cMall.feedShop.user.domain.model.User;
import com.cMall.feedShop.user.domain.model.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    // User 엔티티를 이용하여 UserProfile을 찾는 쿼리 메서드 추가
    Optional<UserProfile> findByUser(User user);
}

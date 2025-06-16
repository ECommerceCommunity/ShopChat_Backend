package com.cMall.shopChat.user.infrastructure.repository;

import org.springframework.stereotype.Repository;

@Repository
public interface JpaUserRepository extends JpaRepository<User, Long>, UserRepository {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
}
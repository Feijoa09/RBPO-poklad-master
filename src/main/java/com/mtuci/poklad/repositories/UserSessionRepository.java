package com.mtuci.poklad.repositories;

import com.mtuci.poklad.models.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserSessionRepository extends JpaRepository<UserSession, Long> {
    List<UserSession> findByEmail(String email);
    Optional<UserSession> findByEmailAndDeviceId(String email, String deviceId);

    Optional<UserSession> findByAccessToken(String accessToken);

    Optional<UserSession> findByRefreshToken(String refreshToken);
}

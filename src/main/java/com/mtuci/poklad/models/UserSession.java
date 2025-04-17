package com.mtuci.poklad.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "user_sessions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String deviceId;

    @Column(nullable = false)
    private String accessToken;

    @Column(nullable = false)
    private String refreshToken;

    @Column(nullable = false)
    private Date accessTokenExpiration;

    @Column(nullable = false)
    private Date refreshTokenExpiration;

    @Column(nullable = false)
    private String status; // Статус сессии (например, активна, завершена и т.д.)

    @Version
    private Integer version; // Версия для обработки конкурентных изменений

    // Конструктор с параметрами
    public UserSession(String email, String deviceId, String accessToken, String refreshToken,
                       Date accessTokenExpiration, Date refreshTokenExpiration, String status) {
        this.email = email;
        this.deviceId = deviceId;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
        this.status = status;
    }
}

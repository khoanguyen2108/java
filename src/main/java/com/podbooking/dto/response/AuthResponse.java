package com.podbooking.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String accessToken;
    private String tokenType = "Bearer";
    private Long expiresIn;

    private UserInfo user;
    private Instant loginTime;
    private String sessionId;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo {
        private Integer id;
        private String name;
        private String email;
        private String phone;
        private String role;
        private Integer points;
        private Boolean isVip;
        private String vipTier;
        private Boolean isActive;
        private Boolean emailVerified;
        private Instant createdAt;
    }
}
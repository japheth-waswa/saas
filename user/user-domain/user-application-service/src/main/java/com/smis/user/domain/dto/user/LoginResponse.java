package com.smis.user.domain.dto.user;

public record LoginResponse(String accessToken, long expiry) {
}

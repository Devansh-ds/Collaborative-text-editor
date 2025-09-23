package com.devansh.response;

import lombok.Builder;

@Builder
public class AuthenticationResponse {
    private String accessToken;
    private String refreshToken;
    private String displayName;

    public AuthenticationResponse(String accessToken, String refreshToken, String displayName) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.displayName = displayName;
    }

    public AuthenticationResponse() {}

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}

package com.playwright.tests.api.qasandbox.auth;

public class TokenResponse {
    private String token;
    private String type;
    private long expiresIn;

    public TokenResponse() {}

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getBearerToken() {
        return (type != null ? type : "Bearer") + " " + token;
    }
}
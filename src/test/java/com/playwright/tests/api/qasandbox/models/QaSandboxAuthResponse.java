package com.playwright.tests.api.qasandbox.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class QaSandboxAuthResponse {
    private String token;
    @JsonProperty("user_id")
    private int userId;
    private String email;
    private String role;

    public QaSandboxAuthResponse() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "QaSandboxAuthResponse{" +
                "token='" + token + '\'' +
                ", userId=" + userId +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}
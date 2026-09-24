package com.playwright.tests.services;

import com.playwright.tests.config.TestConfig;
import com.playwright.tests.api.qasandbox.auth.TokenRequest;
import com.playwright.tests.api.qasandbox.auth.TokenResponse;
import io.restassured.RestAssured;
import io.restassured.response.Response;

public class AuthService {
    private static String cachedToken;
    private static long tokenExpiryTime;

    public static synchronized String getBearerToken() {
        // Проверяем, не истёк ли токен (с запасом 7 дней)
        if (cachedToken != null && System.currentTimeMillis() < tokenExpiryTime - 604800000) {
            return cachedToken;
        }

        // Получаем новый токен
        TokenRequest request = new TokenRequest(
                TestConfig.getQaSandboxUsername(),
                TestConfig.getQaSandboxPassword()
        );

        Response response = RestAssured.given()
                .baseUri(TestConfig.getQaSandboxUrl())
                .header("Content-Type", "application/json")
                .body(request)
                .when()
                .post(TestConfig.getQaSandboxAuthEndpoint());

        if (response.getStatusCode() != 200) {
            throw new RuntimeException("Failed to obtain token. Status: " + response.getStatusCode() +
                    ", Body: " + response.getBody().asString());
        }

        TokenResponse tokenResponse = response.as(TokenResponse.class);
        cachedToken = tokenResponse.getBearerToken();
        tokenExpiryTime = System.currentTimeMillis() + (tokenResponse.getExpiresIn() * 1000);

        return cachedToken;
    }
}
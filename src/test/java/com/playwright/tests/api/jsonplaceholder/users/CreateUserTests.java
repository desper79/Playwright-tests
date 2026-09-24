package com.playwright.tests.api.jsonplaceholder.users;

import com.playwright.tests.fixtures.ApiFixture;
import com.playwright.tests.utils.AllureUtils;
import io.qameta.allure.*;
import io.restassured.common.mapper.TypeRef;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import com.playwright.tests.annotations.*;


import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(ApiFixture.class)
@Tag("API")
@Tag("JSONPlaceholder")
@Layer("API")
@Epic("JSONPlaceholder API Tests")
@Feature("POST /users")
public class CreateUserTests {

    private static final String ENDPOINT = "/users";

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Description("Create new user with valid data and verify response")
    @Story("Create User")
    @DisplayName("POST /users - Should create new user")
    void createUser_withValidData_shouldReturnCreatedUser() {
        // Arrange
        Map<String, String> newUser = new HashMap<>();
        newUser.put("name", "John Doe");
        newUser.put("username", "johndoe");
        newUser.put("email", "john@example.com");

        AllureUtils.attachJson("Request Body",
                String.format("{\"name\":\"%s\",\"username\":\"%s\",\"email\":\"%s\"}",
                        newUser.get("name"), newUser.get("username"), newUser.get("email")));

        // Act
        Map<String, Object> response = ApiFixture.jsonPlaceholder()
                .body(newUser)
                .when()
                .post(ENDPOINT)
                .then()
                .statusCode(201)
                .extract()
                .as(new TypeRef<Map<String, Object>>() {});

        // Assert
        AllureUtils.attachJson("Response Body",
                String.format("{\"id\":%s,\"name\":\"%s\",\"email\":\"%s\"}",
                        response.get("id"), response.get("name"), response.get("email")));

        assertThat(response.get("id")).as("User ID should be generated").isNotNull();
        assertThat(response.get("name")).as("User name should match").isEqualTo("John Doe");
        assertThat(response.get("username")).as("Username should match").isEqualTo("johndoe");
        assertThat(response.get("email")).as("Email should match").isEqualTo("john@example.com");

        AllureUtils.attachText("Creation Result",
                String.format("User created successfully with ID: %s", response.get("id")));
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @Description("Create user with missing email should return validation error")
    @Story("Create User - Validation")
    @DisplayName("POST /users - Should return error when email is missing")
    void createUser_withoutEmail_shouldReturnError() {
        // Arrange
        Map<String, String> newUser = new HashMap<>();
        newUser.put("name", "John Doe");
        newUser.put("username", "johndoe");
        // email is missing intentionally

        AllureUtils.attachJson("Request Body (Invalid)",
                String.format("{\"name\":\"%s\",\"username\":\"%s\"}",
                        newUser.get("name"), newUser.get("username")));

        // Act & Assert
        ApiFixture.jsonPlaceholder()
                .body(newUser)
                .when()
                .post(ENDPOINT)
                .then()
                .statusCode(201); // JSONPlaceholder doesn't validate, always returns 201

        AllureUtils.attachText("Validation Note",
                "JSONPlaceholder API doesn't perform validation - always returns 201 even for incomplete data");
    }
}
package com.playwright.tests.api.jsonplaceholder.users;

import com.playwright.tests.annotations.Layer;
import com.playwright.tests.fixtures.ApiFixture;
import com.playwright.tests.utils.AllureUtils;
import io.qameta.allure.*;
import io.restassured.common.mapper.TypeRef;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(ApiFixture.class)
@Tag("API")
@Tag("JSONPlaceholder")
@Layer("API")
@Epic("JSONPlaceholder API Tests")
@Feature("GET /users")
public class GetUserTests {

    private static final String ENDPOINT = "/users";

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Description("Get list of all users and verify structure")
    @Story("Get Users List")
    @DisplayName("GET /users - Should return non-empty list of users")
    void getUsers_shouldReturnNonEmptyList() {
        // Act
        List<User> users = ApiFixture.jsonPlaceholder()
                .when()
                .get(ENDPOINT)
                .then()
                .statusCode(200)
                .extract()
                .as(new TypeRef<List<User>>() {});

        // Assert
        assertThat(users).as("Users list should not be empty").isNotEmpty();

        User firstUser = users.get(0);
        assertThat(firstUser.getId()).as("First user ID should be 1").isEqualTo(1);
        assertThat(firstUser.getName()).as("First user name should not be empty").isNotBlank();
        assertThat(firstUser.getEmail()).as("First user email should contain @").contains("@");
        assertThat(firstUser.getUsername()).as("First user username should not be empty").isNotBlank();

        AllureUtils.attachText("Users Count", String.format("Total users: %d", users.size()));
        AllureUtils.attachJson("First User Sample",
                String.format("{\"id\":%d,\"name\":\"%s\",\"username\":\"%s\",\"email\":\"%s\"}",
                        firstUser.getId(), firstUser.getName(), firstUser.getUsername(), firstUser.getEmail()));
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Description("Get user by valid ID and verify response data")
    @Story("Get Single User")
    @DisplayName("GET /users/{id} - Should return user with matching ID")
    void getUserById_withValidId_shouldReturnUser() {
        // Arrange
        int userId = 1;

        // Act
        User user = ApiFixture.jsonPlaceholder()
                .when()
                .get(ENDPOINT + "/" + userId)
                .then()
                .statusCode(200)
                .extract()
                .as(User.class);

        // Assert
        assertThat(user.getId()).as("User ID should match").isEqualTo(userId);
        assertThat(user.getName()).as("Name should not be empty").isNotBlank();
        assertThat(user.getUsername()).as("Username should not be empty").isNotBlank();
        assertThat(user.getEmail()).as("Email should contain @").contains("@");
        assertThat(user.getPhone()).as("Phone should not be empty").isNotBlank();
        assertThat(user.getWebsite()).as("Website should not be empty").isNotBlank();

        AllureUtils.attachJson("User Data",
                String.format("{\"id\":%d,\"name\":\"%s\",\"username\":\"%s\",\"email\":\"%s\",\"phone\":\"%s\",\"website\":\"%s\"}",
                        user.getId(), user.getName(), user.getUsername(), user.getEmail(), user.getPhone(), user.getWebsite()));
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @Description("Get user with non-existent ID returns 404")
    @Story("Get Single User - Error Handling")
    @DisplayName("GET /users/{invalidId} - Should return 404")
    void getUserById_withInvalidId_shouldReturn404() {
        // Act & Assert
        ApiFixture.jsonPlaceholder()
                .when()
                .get(ENDPOINT + "/99999")
                .then()
                .statusCode(404);

        AllureUtils.attachText("Error Handling", "Correctly returned 404 for non-existent user ID");
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @Description("Get user with invalid ID format returns error")
    @Story("Get Single User - Error Handling")
    @DisplayName("GET /users/{invalidFormat} - Should return error")
    void getUserById_withInvalidFormat_shouldReturnError() {
        // Act & Assert
        ApiFixture.jsonPlaceholder()
                .when()
                .get(ENDPOINT + "/abc")
                .then()
                .statusCode(404);

        AllureUtils.attachText("Error Handling", "Correctly handled invalid ID format");
    }
}
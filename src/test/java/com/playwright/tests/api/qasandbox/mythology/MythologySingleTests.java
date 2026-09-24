package com.playwright.tests.api.qasandbox.mythology;

import com.playwright.tests.fixtures.ApiFixture;
import com.playwright.tests.utils.AllureUtils;
import io.qameta.allure.Description;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import java.awt.*;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(ApiFixture.class)
public class MythologySingleTests {

    @Test
    @Tag("High")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that a specific mythology figure (ID 1) returns the expected data")
    @Story("Get Specific Figure")
    @DisplayName("GET /api/mythology/1 - Should return the exact figure data")
    void getMythologyFigureById_shouldReturnExpectedData() {
        // Arrange: Ожидаемые значения
        int expectedId = 1;
        String expectedName = "Геракл";
        String expectedCategory = "heroes";
        String expectedDesc = "Сын Зевса, прославившийся своими двенадцатью подвигами.";
        String expectedImg = "https://raw.githubusercontent.com/artichokeee/gifs/main/Греция/Геракл.png"; // Ожидаемое значение из URL

        // Act: Выполняем GET-запрос для конкретного ID
        MythologyFigure figure = ApiFixture.qaSandbox()
                .when()
                .get("/api/mythology/1")
                .then()
                .statusCode(200)
                .extract()
                .as(MythologyFigure.class);

        // Assert: Проверяем, что все поля соответствуют ожидаемым значениям
        AllureUtils.attachJson("Retrieved Figure",
                String.format("{\"id\":%d,\"name\":\"%s\",\"category\":\"%s\",\"desc\":\"%s\",\"img\":\"%s\"}",
                        figure.getId(), figure.getName(), figure.getCategory(), figure.getDesc(), figure.getImg()));

        // Создаём SoftAssertions для сбора всех ошибок
        SoftAssertions softAssertions = new SoftAssertions();

        // Счётчик проблемных элементов
        int errorCount = 0;


        if (figure.getId() != expectedId) {
            errorCount++;
            softAssertions.fail("Figure with ID '%d' has invalid ID: '%s' (expected non-blank)",
                    figure.getId(), expectedId);
        }

        if (!figure.getName().equalsIgnoreCase(expectedName)) {
            errorCount++;
            softAssertions.fail("Figure with ID '%s' has invalid name: '%s' (expected non-blank)",
                    figure.getName(), expectedName);
        }

        // ✅ Проверка category (обязательное поле)
        if (!figure.getCategory().equalsIgnoreCase(expectedCategory)) {
            errorCount++;
            softAssertions.fail("Figure with ID '%s' has invalid category: '%s' (expected non-blank)",
                    figure.getCategory(), expectedCategory);
        }

        if (!figure.getDesc().equalsIgnoreCase(expectedDesc)) {
            errorCount++;
            softAssertions.fail("Figure with ID '%s' has invalid Description: '%s' (expected non-blank)",
                    figure.getDesc(), expectedDesc);
        }

        if (!figure.getImg().equalsIgnoreCase(expectedImg)) {
            errorCount++;
            softAssertions.fail("Figure with ID '%s' has invalid Image URL: '%s' (expected non-blank)",
                    figure.getImg(), expectedImg);
        }

        // В конце выполняем все проверки — если были ошибки, тест упадёт
        softAssertions.assertAll();
    }

    @Test
    @Tag("High")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that a specific mythology figure (ID 1) returns the expected data")
    @Story("Get Specific Figure")
    @DisplayName("GET /api/mythology/1000 - Should be error")
    void getMythologyFigureById_shouldReturnError() {
        // Arrange: Ожидаемые значения
        String expectedError = "Персонаж не найден";
        String id = "1000";

        // Act: Выполняем GET-запрос для конкретного ID
        MythologyFigure figure = ApiFixture.qaSandbox()
                .when()
                .get("/api/mythology/" + id)
                .then()
                .statusCode(404)
                .extract()
                .as(MythologyFigure.class);

        // Assert: Проверяем, что все поля соответствуют ожидаемым значениям
        AllureUtils.attachJson("Retrieved Figure",
                String.format("{\"error\":\"%s\"}",
                        figure.getError()));

        // Создаём SoftAssertions для сбора всех ошибок
        SoftAssertions softAssertions = new SoftAssertions();

        // Счётчик проблемных элементов
        int errorCount = 0;


        if (!figure.getError().equalsIgnoreCase(expectedError)) {
            errorCount++;
            softAssertions.fail("Wrong error message: '%s'",
                    figure.getError(), expectedError);
        }

        // В конце выполняем все проверки — если были ошибки, тест упадёт
        softAssertions.assertAll();
    }
}
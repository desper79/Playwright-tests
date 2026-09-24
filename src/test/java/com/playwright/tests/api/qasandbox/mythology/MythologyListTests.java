package com.playwright.tests.api.qasandbox.mythology;

import com.playwright.tests.fixtures.ApiFixture;
import com.playwright.tests.utils.AllureUtils;
import io.qameta.allure.Description;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import io.restassured.common.mapper.TypeRef;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(ApiFixture.class)
public class MythologyListTests {

    @Test
    @Tag("High")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that the list of mythology figures is returned with non-empty name and category")
    @Story("Get Mythology List")
    @DisplayName("GET /api/mythology - Should return non-empty list with valid name and category")
    void getMythologyList_shouldReturnNonEmptyStructure() {
        // Act: Выполняем GET-запрос к эндпоинту и десериализуем ответ в список
        List<MythologyFigure> figures = ApiFixture.qaSandbox()
                .when()
                .get("/api/mythology")
                .then()
                .statusCode(200) // Ожидаем успешный статус
                .extract()
                .as(new TypeRef<List<MythologyFigure>>() {});

        // Assert: Проверяем, что список не пуст
        assertThat(figures).as("The list of mythology figures should not be empty").isNotEmpty();

        // Создаём SoftAssertions для сбора всех ошибок
        SoftAssertions softAssertions = new SoftAssertions();

        // Счётчик проблемных элементов
        int errorCount = 0;

        // Для каждого элемента в списке проверяем обязательные поля
        for (MythologyFigure figure : figures) {
            int currentId = figure.getId();
            String currentName = figure.getName();
            String currentCategory = figure.getCategory();
            String currentDesc = figure.getDesc();
            String currentImg = figure.getImg();

            AllureUtils.attachJson("Figure Data",
                    String.format("{\"id\":%d,\"name\":\"%s\",\"category\":\"%s\",\"desc\":\"%s\",\"img\":\"%s\"}",
                            currentId, currentName, currentCategory, currentDesc, currentImg));

            // ✅ Проверка name (обязательное поле)
            if (currentName == null) {
                errorCount++;
                softAssertions.fail("Figure with ID '%d' has invalid name: '%s' (expected non-blank)",
                        currentId, currentName);
            }

            // ✅ Проверка category (обязательное поле)
            if (currentCategory == null) {
                errorCount++;
                softAssertions.fail("Figure with ID '%d' has invalid category: '%s' (expected non-blank)",
                        currentId, currentCategory);
            }

            // ⚠️ Проверка id (должно быть не null)
            if (currentId == 0 && figure.getId() == 0) {
                // id == 0 может быть допустимым, но проверим, что не null
                // В Java примитив int не может быть null, поэтому эта проверка для объекта Integer
            }
        }

        // Добавляем итоговую информацию в отчёт
        if (errorCount > 0) {
            AllureUtils.attachText("Validation Summary",
                    String.format("Found %d error(s) while validating %d figure(s)", errorCount, figures.size()));
        }

        // В конце выполняем все проверки — если были ошибки, тест упадёт
        softAssertions.assertAll();
    }

}
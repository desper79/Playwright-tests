package com.playwright.tests.ui;

import com.playwright.tests.annotations.Layer;
import com.playwright.tests.config.TestConfig;
import com.playwright.tests.fixtures.PlaywrightFixture;
import com.playwright.tests.ui.pages.LoginPage;
import com.playwright.tests.ui.pages.ProductsPage;
import com.playwright.tests.utils.AllureUtils;
import io.qameta.allure.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(PlaywrightFixture.class)
@Tag("UI")
@Layer("UI")
@Epic("UI Testing")
@Feature("SauceDemo Tests")
public class UiTests {
    private LoginPage loginPage;
    private ProductsPage productsPage;

    @BeforeEach
    void setUp() {
        // Используем @BeforeEach вместо @BeforeAll
        // PlaywrightFixture.getPage() уже доступна, так как beforeEach в фикстуре выполняется раньше
        loginPage = new LoginPage(PlaywrightFixture.getPage());
        productsPage = new ProductsPage(PlaywrightFixture.getPage());
    }

    @Test
    @Tag("High")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Verify that user can login with valid credentials")
    @Story("Login Functionality")
    @DisplayName("Login with valid credentials - Should redirect to products page")
    void login_withValidCredentials_shouldRedirectToProductsPage() {
        loginPage.navigate();
        loginPage.assertLoginPageLoaded();
        loginPage.login(TestConfig.getStandardUsername(), TestConfig.getStandardPassword());
        productsPage.assertProductsPageLoaded();
        AllureUtils.attachScreenshot(PlaywrightFixture.getPage(), "products_page");
    }

    @Test
    @Tag("Medium")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that locked out user sees error message")
    @Story("Login Functionality")
    @DisplayName("Login with locked out user - Should show error message")
    void login_withLockedOutUser_shouldShowErrorMessage() {
        loginPage.navigate();
        loginPage.login(TestConfig.getLockedUsername(), TestConfig.getLockedPassword());
        loginPage.assertErrorMessage("locked out");
        AllureUtils.attachScreenshot(PlaywrightFixture.getPage(), "error_message");
    }

    @Test
    @Tag("High")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that adding product to cart increases cart badge")
    @Story("Cart Functionality")
    @DisplayName("Add product to cart - Should increase cart badge")
    void addProductToCart_shouldIncreaseCartBadge() {
        loginPage.navigate();
        loginPage.login(TestConfig.getStandardUsername(), TestConfig.getStandardPassword());
        productsPage.assertProductsPageLoaded();

        int initialCount = productsPage.getCartItemsCount();

        productsPage.addProductToCart("Sauce Labs Backpack");
        int newCount = productsPage.getCartItemsCount();

        assertThat(newCount).isEqualTo(initialCount + 1);

        AllureUtils.attachText("Cart Count", String.format("Initial: %d, New: %d", initialCount, newCount));
    }
}
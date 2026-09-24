package com.playwright.tests.ui.tests;

import com.playwright.tests.annotations.Layer;
import com.playwright.tests.fixtures.PlaywrightFixture;
import com.playwright.tests.ui.pages.LoginPage;
import com.playwright.tests.ui.pages.ProductsPage;
import com.playwright.tests.config.TestConfig;
import com.playwright.tests.utils.AllureUtils;
import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(PlaywrightFixture.class)
@Tag("UI")
@Tag("Login")
@Layer("UI")
@Epic("UI Testing")
@Feature("Login Functionality")
public class LoginTests {

    private LoginPage loginPage;
    private ProductsPage productsPage;

    @BeforeEach
    void setUp() {
        loginPage = new LoginPage(PlaywrightFixture.getPage());
        productsPage = new ProductsPage(PlaywrightFixture.getPage());
    }

    @Test
    @Severity(SeverityLevel.BLOCKER)
    @Description("User can login with valid credentials")
    @Story("Login")
    @DisplayName("TC-001: Valid login redirects to products page")
    void validLogin_shouldRedirectToProductsPage() {
        loginPage.navigate();
        loginPage.assertLoginPageLoaded();
        loginPage.login(TestConfig.getStandardUsername(), TestConfig.getStandardPassword());
        productsPage.assertProductsPageLoaded();
        AllureUtils.attachScreenshot(PlaywrightFixture.getPage(), "products_page");
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Description("Locked out user sees error message")
    @Story("Login - Error Handling")
    @DisplayName("TC-002: Locked out user sees error message")
    void lockedOutUser_shouldSeeErrorMessage() {
        loginPage.navigate();
        loginPage.login(TestConfig.getLockedUsername(), TestConfig.getLockedPassword());
        loginPage.assertErrorMessage("locked out");
        AllureUtils.attachScreenshot(PlaywrightFixture.getPage(), "error_message");
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @Description("Empty credentials show validation error")
    @Story("Login - Validation")
    @DisplayName("TC-003: Empty credentials show error")
    void emptyCredentials_shouldShowError() {
        loginPage.navigate();
        loginPage.login("", "");
        loginPage.assertErrorMessage("Username is required");
    }
}
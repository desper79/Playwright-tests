package com.playwright.tests.ui.pages;

import com.microsoft.playwright.Page;
import org.assertj.core.api.Assertions;

public class LoginPage extends BasePage {
    private final String usernameInput = "[data-test='username']";
    private final String passwordInput = "[data-test='password']";
    private final String loginButton = "[data-test='login-button']";
    private final String errorMessage = "[data-test='error']";

    public LoginPage(Page page) {
        super(page);
    }

    public void navigate() {
        page.navigate(getBaseUrl());
        waitForLoadState();
    }

    public void login(String username, String password) {
        page.fill(usernameInput, username);
        page.fill(passwordInput, password);
        page.click(loginButton);
        waitForLoadState();
    }

    public void assertLoginPageLoaded() {
        Assertions.assertThat(page.locator(usernameInput).isVisible()).isTrue();
        Assertions.assertThat(page.locator(passwordInput).isVisible()).isTrue();
        Assertions.assertThat(page.locator(loginButton).isVisible()).isTrue();
    }

    public void assertErrorMessage(String expectedMessage) {
        Assertions.assertThat(page.locator(errorMessage).textContent())
                .contains(expectedMessage);
    }
}
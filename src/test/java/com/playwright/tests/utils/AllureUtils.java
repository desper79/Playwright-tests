package com.playwright.tests.utils;

import com.microsoft.playwright.Page;
import io.qameta.allure.Allure;
import org.assertj.core.api.Assertions;

import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class AllureUtils {

    private AllureUtils() {
        // Private constructor to prevent instantiation
    }

    @io.qameta.allure.Step("Open URL: {url}")
    public static void stepOpenUrl(String url) {
        Allure.addAttachment("URL", "text/plain", url);
    }

    @io.qameta.allure.Step("Login with username: {username}")
    public static void stepLogin(String username) {
        Allure.addAttachment("Username", "text/plain", username);
    }

    @io.qameta.allure.Step("Verify that element is displayed")
    public static void stepVerifyDisplayed() {
        // Step for verification
    }

    @io.qameta.allure.Step("API Request: {method} {endpoint}")
    public static void stepApiRequest(String method, String endpoint) {
        Allure.addAttachment("Request", "text/plain", method + " " + endpoint);
    }

    @io.qameta.allure.Step("API Response Status: {statusCode}")
    public static void stepApiResponse(int statusCode) {
        Allure.addAttachment("Response Status", "text/plain", String.valueOf(statusCode));
    }

    public static void attachScreenshot(Page page, String name) {
        try {
            byte[] screenshot = page.screenshot(new Page.ScreenshotOptions()
                    .setFullPage(true));
            Allure.addAttachment(name, "image/png", new ByteArrayInputStream(screenshot), "png");
        } catch (Exception e) {
            Allure.addAttachment("Screenshot failed", "text/plain", e.getMessage());
        }
    }

    public static void attachText(String name, String content) {
        Allure.addAttachment(name, "text/plain", content);
    }

    public static void attachJson(String name, String json) {
        Allure.addAttachment(name, "application/json", json);
    }

    public static void attachHtml(String name, String html) {
        Allure.addAttachment(name, "text/html", html);
    }

    public static void assertWithAttachment(String message, boolean condition) {
        Allure.addAttachment("Assertion", "text/plain", message + ": " + condition);
        Assertions.assertThat(condition).as(message).isTrue();
    }
}
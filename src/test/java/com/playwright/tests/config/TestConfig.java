package com.playwright.tests.config;

public class TestConfig {
    private static final ConfigManager config = ConfigManager.getInstance();

    // ========== UI Configuration ==========
    public static String getUiBaseUrl() {
        return config.getProperty("ui.base.url");
    }

    // ========== API Configuration ==========
    public static String getJsonPlaceholderUrl() {
        return config.getProperty("api.jsonplaceholder.url");
    }

    public static String getQaSandboxUrl() {
        return config.getProperty("api.qasandbox.url");
    }

    public static String getQaSandboxAuthEndpoint() {
        return config.getProperty("api.qasandbox.auth.endpoint", "/api/login");
    }

    public static String getQaSandboxUsername() {
        return config.getProperty("api.qasandbox.username");
    }

    public static String getQaSandboxPassword() {
        return config.getProperty("api.qasandbox.password");
    }

    // ========== Playwright Configuration ==========
    public static boolean isPlaywrightHeadless() {
        return Boolean.parseBoolean(config.getProperty("playwright.headless"));
    }

    public static int getPlaywrightTimeout() {
        return Integer.parseInt(config.getProperty("playwright.timeout", "30000"));
    }

    public static int getPlaywrightRetries() {
        return Integer.parseInt(config.getProperty("playwright.retries", "0"));
    }

    public static int getPlaywrightSlowMo() {
        return Integer.parseInt(config.getProperty("playwright.slowMo", "0"));
    }

    public static boolean isPlaywrightVideoEnabled() {
        return Boolean.parseBoolean(config.getProperty("playwright.video.enabled", "false"));
    }

    public static boolean isPlaywrightTraceEnabled() {
        return Boolean.parseBoolean(config.getProperty("playwright.trace.enabled", "false"));
    }

    public static BrowserType getPlaywrightBrowser() {
        // Приоритет: системное свойство > конфиг
        String browserFromProp = System.getProperty("browser");
        if (browserFromProp != null) {
            return BrowserType.fromString(browserFromProp);
        }

        String browserFromConfig = config.getProperty("playwright.browser", "chromium");
        return BrowserType.fromString(browserFromConfig);
    }

    // ========== Test Users ==========
    public static String getStandardUsername() {
        return config.getProperty("user.standard.username");
    }

    public static String getStandardPassword() {
        return config.getProperty("user.standard.password");
    }

    public static String getLockedUsername() {
        return config.getProperty("user.locked.username");
    }

    public static String getLockedPassword() {
        return config.getProperty("user.locked.password");
    }


    // ========== Allure Configuration ==========
    public static String getAllureResultsDirectory() {
        return config.getProperty("allure.results.directory", "target/allure-results");
    }

    // ========== Environment ==========
    public static Environment getCurrentEnvironment() {
        return config.getCurrentEnvironment();
    }
}
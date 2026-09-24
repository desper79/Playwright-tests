package com.playwright.tests.config;

public enum BrowserType {
    CHROMIUM("chromium"),
    FIREFOX("firefox"),
    WEBKIT("webkit"),
    CHROME("chrome"),
    MSEDGE("msedge");

    private final String playwrightName;

    BrowserType(String playwrightName) {
        this.playwrightName = playwrightName;
    }

    public String getPlaywrightName() {
        return playwrightName;
    }

    public static BrowserType fromString(String value) {
        if (value == null) return CHROMIUM;

        switch (value.toLowerCase()) {
            case "chromium":
                return CHROMIUM;
            case "firefox":
                return FIREFOX;
            case "webkit":
                return WEBKIT;
            case "chrome":
                return CHROME;
            case "msedge":
                return MSEDGE;
            default:
                System.err.println("Unknown browser: " + value + ". Using CHROMIUM as default.");
                return CHROMIUM;
        }
    }
}
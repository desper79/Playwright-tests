package com.playwright.tests.config;

public enum Environment {
    DEV("dev"),
    TEST("test"),
    PROD("prod");

    private final String value;

    Environment(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static Environment fromString(String value) {
        for (Environment env : Environment.values()) {
            if (env.value.equalsIgnoreCase(value)) {
                return env;
            }
        }
        return TEST; // default
    }
}
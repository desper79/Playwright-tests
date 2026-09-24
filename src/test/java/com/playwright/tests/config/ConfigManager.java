package com.playwright.tests.config;

import java.io.InputStream;
import java.util.Properties;

public class ConfigManager {
    private static ConfigManager instance;
    private Properties properties;
    private Environment currentEnvironment;

    private ConfigManager() {
        properties = new Properties();
        loadConfiguration();
    }

    public static synchronized ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }

    private void loadConfiguration() {
        // 1. Загружаем общие параметры (база)
        loadFile("/config.common.properties", true);

        // 2. Определяем окружение
        String envValue = getEnvironment();
        currentEnvironment = Environment.fromString(envValue);
        System.out.println("Running tests on environment: " + currentEnvironment.name());

        // 3. Загружаем параметры окружения (переопределяют общие)
        String envFile = String.format("/config.%s.properties", currentEnvironment.getValue());
        loadFile(envFile, false);

        // 4. Загружаем локальные настройки (переопределяют всё)
        loadFile("/config.local.properties", false);

        // 5. Подставляем переменные окружения (если есть)
        resolveEnvironmentVariables();
    }

    private void loadFile(String resourcePath, boolean optional) {
        try (InputStream input = getClass().getResourceAsStream(resourcePath)) {
            if (input != null) {
                properties.load(input);
                System.out.println("Loaded: " + resourcePath);
            } else if (!optional) {
                throw new RuntimeException("Required configuration file not found: " + resourcePath);
            }
        } catch (Exception e) {
            if (!optional) {
                throw new RuntimeException("Failed to load configuration: " + resourcePath, e);
            }
        }
    }

    private String getEnvironment() {
        // Приоритет: системное свойство > переменная окружения > значение по умолчанию
        String envFromSystemProp = System.getProperty("env");
        if (envFromSystemProp != null) {
            return envFromSystemProp;
        }

        String envFromEnvVar = System.getenv("TEST_ENV");
        if (envFromEnvVar != null) {
            return envFromEnvVar;
        }

        return "test"; // default
    }

    private void resolveEnvironmentVariables() {
        // Подстановка переменных окружения вида ${VAR_NAME}
        for (String key : properties.stringPropertyNames()) {
            String value = properties.getProperty(key);
            if (value != null && value.startsWith("${") && value.endsWith("}")) {
                String envVar = value.substring(2, value.length() - 1);
                String envValue = System.getenv(envVar);
                if (envValue != null) {
                    properties.setProperty(key, envValue);
                } else {
                    System.err.println("Warning: Environment variable '" + envVar + "' not found for key '" + key + "'");
                }
            }
        }
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    public Environment getCurrentEnvironment() {
        return currentEnvironment;
    }
}
package com.playwright.tests.fixtures;

import com.playwright.tests.config.TestConfig;
import com.playwright.tests.services.AuthService;
import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.util.HashMap;
import java.util.Map;

public class ApiFixture implements BeforeEachCallback {

    private static final ThreadLocal<Map<String, RequestSpecification>> specs = new ThreadLocal<>();

    public enum Service {
        JSONPLACEHOLDER("jsonplaceholder"),
        QASANDBOX("qasandbox");

        private final String name;

        Service(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    @Override
    public void beforeEach(ExtensionContext context) {
        Map<String, RequestSpecification> serviceSpecs = new HashMap<>();

        // JSONPlaceholder (без авторизации)
        serviceSpecs.put(Service.JSONPLACEHOLDER.getName(), RestAssured.given()
                .baseUri(TestConfig.getJsonPlaceholderUrl())
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .log().ifValidationFails());

        // QA Sandbox (с авторизацией через Bearer Token)
        String bearerToken = AuthService.getBearerToken();
        serviceSpecs.put(Service.QASANDBOX.getName(), RestAssured.given()
                .baseUri(TestConfig.getQaSandboxUrl())
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Authorization", bearerToken)
                .log().ifValidationFails());

        specs.set(serviceSpecs);
    }

    public static RequestSpecification spec(String serviceName) {
        Map<String, RequestSpecification> serviceSpecs = specs.get();
        if (serviceSpecs == null) {
            throw new IllegalStateException(
                    "ApiFixture not initialized. Make sure @ExtendWith(ApiFixture.class) is applied to the test class."
            );
        }
        RequestSpecification spec = serviceSpecs.get(serviceName);
        if (spec == null) {
            throw new IllegalArgumentException("Unknown service: " + serviceName +
                    ". Available services: jsonplaceholder, qasandbox");
        }
        return spec;
    }

    public static RequestSpecification jsonPlaceholder() {
        return spec(Service.JSONPLACEHOLDER.getName());
    }

    public static RequestSpecification qaSandbox() {
        return spec(Service.QASANDBOX.getName());
    }
}
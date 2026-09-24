package com.playwright.tests.annotations;

import io.qameta.allure.LabelAnnotation;

import java.lang.annotation.*;

/**
 * Allure annotation for categorizing tests by layer (e.g., API, UI, E2E, UNIT).
 * Usage: @Layer("API") or @Layer("UI") or @Layer("E2E")
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@LabelAnnotation(name = "layer")
public @interface Layer {
    String value();
}
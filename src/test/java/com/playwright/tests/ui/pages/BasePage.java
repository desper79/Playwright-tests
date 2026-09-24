package com.playwright.tests.ui.pages;

import com.microsoft.playwright.Page;
import com.playwright.tests.config.TestConfig;
import java.nio.file.Path;

public abstract class BasePage {
    protected final Page page;

    protected BasePage(Page page) {
        this.page = page;
    }

    protected void waitForLoadState() {
        page.waitForLoadState();
    }

    public void takeScreenshot(String name) {
        page.screenshot(new Page.ScreenshotOptions()
                .setPath(Path.of("target/screenshots/" + name + ".png")));
    }

    protected String getBaseUrl() {
        return TestConfig.getUiBaseUrl();
    }
}
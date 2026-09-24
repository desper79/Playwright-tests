package com.playwright.tests.fixtures;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.playwright.tests.config.BrowserType;  // Ваш enum
import com.playwright.tests.config.TestConfig;
import com.playwright.tests.utils.AllureUtils;
import io.qameta.allure.Allure;
import org.junit.jupiter.api.extension.*;

import java.nio.file.Paths;

public class PlaywrightFixture implements BeforeEachCallback, AfterEachCallback {

    private static Playwright playwright;
    private static com.microsoft.playwright.Browser browser;  // ← Полное имя Playwright Browser
    private static final ThreadLocal<BrowserContext> context = new ThreadLocal<>();
    private static final ThreadLocal<Page> page = new ThreadLocal<>();
    private static String currentBrowserName;
    private static boolean isInitialized = false;

    // Инициализация Playwright и Browser (один раз для всех тестов)
    private static synchronized void initialize() {
        if (isInitialized) {
            return;
        }

        playwright = Playwright.create();

        BrowserType browserType = TestConfig.getPlaywrightBrowser();
        currentBrowserName = browserType.name();

        System.out.println("Starting browser: " + currentBrowserName);
        Allure.label("browser", currentBrowserName);

        browser = launchBrowser(playwright, browserType);
        isInitialized = true;
    }

    private static com.microsoft.playwright.Browser launchBrowser(Playwright playwright, BrowserType browserType) {
        boolean headless = TestConfig.isPlaywrightHeadless();
        int slowMo = TestConfig.getPlaywrightSlowMo();

        // Используем полное имя для LaunchOptions
        com.microsoft.playwright.BrowserType.LaunchOptions options = new com.microsoft.playwright.BrowserType.LaunchOptions()
                .setHeadless(headless)
                .setSlowMo(slowMo);

        switch (browserType) {
            case CHROMIUM:
                return playwright.chromium().launch(options);
            case FIREFOX:
                return playwright.firefox().launch(options);
            case WEBKIT:
                return playwright.webkit().launch(options);
            case CHROME:
                return playwright.chromium().launch(options.setChannel("chrome"));
            case MSEDGE:
                return playwright.chromium().launch(options.setChannel("msedge"));
            default:
                return playwright.chromium().launch(options);
        }
    }

    @Override
    public void beforeEach(ExtensionContext extensionContext) {
        // Инициализируем Playwright при первом тесте
        initialize();

        // Создаём новый контекст и страницу для каждого теста
        BrowserContext newContext = createBrowserContext();
        context.set(newContext);

        Page newPage = newContext.newPage();
        page.set(newPage);
    }

    private BrowserContext createBrowserContext() {
        com.microsoft.playwright.Browser.NewContextOptions options = new com.microsoft.playwright.Browser.NewContextOptions()
                .setViewportSize(1920, 1080);

        if (TestConfig.isPlaywrightVideoEnabled()) {
            options.setRecordVideoDir(Paths.get("target/videos"));
        }

        if (TestConfig.isPlaywrightTraceEnabled()) {
            options.setRecordHarPath(Paths.get("target/traces"));
        }

        return browser.newContext(options);
    }

    @Override
    public void afterEach(ExtensionContext extensionContext) {
        // Закрываем страницу и контекст после каждого теста
        Page currentPage = page.get();
        BrowserContext currentContext = context.get();

        // Прикрепляем скриншот при падении
        if (extensionContext.getExecutionException().isPresent() && currentPage != null) {
            AllureUtils.attachScreenshot(currentPage, "screenshot_on_failure");
        }

        if (currentPage != null) {
            currentPage.close();
        }
        if (currentContext != null) {
            currentContext.close();
        }

        page.remove();
        context.remove();
    }

    // Закрытие ресурсов через shutdown hook
    private static synchronized void close() {
        if (!isInitialized) {
            return;
        }

        if (browser != null) {
            browser.close();
        }
        if (playwright != null) {
            playwright.close();
        }
        isInitialized = false;
    }

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(PlaywrightFixture::close));
    }

    public static Page getPage() {
        Page currentPage = page.get();
        if (currentPage == null) {
            throw new IllegalStateException(
                    "Page is not available. Make sure the test class is annotated with @ExtendWith(PlaywrightFixture.class)"
            );
        }
        return currentPage;
    }

    public static BrowserContext getContext() {
        BrowserContext currentContext = context.get();
        if (currentContext == null) {
            throw new IllegalStateException("BrowserContext is not available.");
        }
        return currentContext;
    }

    public static String getCurrentBrowserName() {
        return currentBrowserName;
    }
}
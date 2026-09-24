package com.playwright.tests.ui.tests;

import com.playwright.tests.annotations.Layer;
import com.playwright.tests.fixtures.PlaywrightFixture;
import com.playwright.tests.ui.pages.LoginPage;
import com.playwright.tests.ui.pages.ProductsPage;
import com.playwright.tests.ui.pages.CartPage;
import com.playwright.tests.config.TestConfig;
import com.playwright.tests.utils.AllureUtils;
import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(PlaywrightFixture.class)
@Tag("UI")
@Tag("Cart")
@Layer("UI")
@Epic("UI Testing")
@Feature("Shopping Cart")
public class CartTests {

    private LoginPage loginPage;
    private ProductsPage productsPage;
    private CartPage cartPage;

    @BeforeEach
    void setUp() {
        // Авторизуемся перед каждым тестом корзины
        loginPage = new LoginPage(PlaywrightFixture.getPage());
        productsPage = new ProductsPage(PlaywrightFixture.getPage());
        cartPage = new CartPage(PlaywrightFixture.getPage());

        loginPage.navigate();
        loginPage.login(TestConfig.getStandardUsername(), TestConfig.getStandardPassword());
        productsPage.assertProductsPageLoaded();
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Description("Add product to cart increases cart badge")
    @Story("Cart - Add Items")
    @DisplayName("TC-010: Add product to cart updates counter")
    void addProductToCart_shouldIncreaseBadge() {
        int initialCount = productsPage.getCartItemsCount();

        productsPage.addProductToCart("Sauce Labs Backpack");
        int newCount = productsPage.getCartItemsCount();

        assertThat(newCount).isEqualTo(initialCount + 1);

        AllureUtils.attachText("Cart Count", String.format("Initial: %d → New: %d", initialCount, newCount));
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Description("Remove product from cart decreases badge")
    @Story("Cart - Remove Items")
    @DisplayName("TC-011: Remove product from cart updates counter")
    void removeProductFromCart_shouldDecreaseBadge() {
        productsPage.addProductToCart("Sauce Labs Backpack");
        productsPage.addProductToCart("Sauce Labs Bolt T-Shirt");

        cartPage.navigate();
        int countBeforeRemove = cartPage.getCartItemsCount();

        cartPage.removeFirstItem();
        int countAfterRemove = cartPage.getCartItemsCount();

        assertThat(countAfterRemove).isEqualTo(countBeforeRemove + 1);

        AllureUtils.attachScreenshot(PlaywrightFixture.getPage(), "cart_after_remove");
    }
}
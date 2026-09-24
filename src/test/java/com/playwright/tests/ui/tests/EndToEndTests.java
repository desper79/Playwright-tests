package com.playwright.tests.ui.tests;

import com.playwright.tests.annotations.Layer;
import com.playwright.tests.fixtures.PlaywrightFixture;
import com.playwright.tests.ui.pages.*;
import com.playwright.tests.config.TestConfig;
import com.playwright.tests.utils.AllureUtils;
import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(PlaywrightFixture.class)
@Tag("UI")
@Tag("E2E")
@Layer("UI")
@Epic("End-to-End Testing")
@Feature("Complete User Journey")
public class EndToEndTests {

    private LoginPage loginPage;
    private ProductsPage productsPage;
    private CartPage cartPage;
    private CheckoutPage checkoutPage;

    @BeforeEach
    void setUp() {
        loginPage = new LoginPage(PlaywrightFixture.getPage());
        productsPage = new ProductsPage(PlaywrightFixture.getPage());
        cartPage = new CartPage(PlaywrightFixture.getPage());
        checkoutPage = new CheckoutPage(PlaywrightFixture.getPage());
    }

    @Test
    @Severity(SeverityLevel.BLOCKER)
    @Description("Complete user journey: Login → Add to cart → Checkout")
    @Story("Full Purchase Flow")
    @DisplayName("TC-100: Complete purchase workflow")
    void completePurchaseWorkflow_shouldSucceed() {
        // Step 1: Login
        loginPage.navigate();
        loginPage.login(TestConfig.getStandardUsername(), TestConfig.getStandardPassword());
        productsPage.assertProductsPageLoaded();
        AllureUtils.attachScreenshot(PlaywrightFixture.getPage(), "01_products_page");

        // Step 2: Add products to cart
        productsPage.addProductToCart("Sauce Labs Backpack");
        productsPage.addProductToCart("Sauce Labs Bolt T-Shirt");
        AllureUtils.attachText("Cart Count", String.format("Items in cart: %d", productsPage.getCartItemsCount()));

        // Step 3: Go to cart
        productsPage.goToCart();
        cartPage.assertCartPageLoaded();
        AllureUtils.attachScreenshot(PlaywrightFixture.getPage(), "02_cart_page");

        // Step 4: Proceed to checkout
        cartPage.proceedToCheckout();
        checkoutPage.assertCheckoutPageLoaded();
        AllureUtils.attachScreenshot(PlaywrightFixture.getPage(), "03_checkout_page");

        // Step 5: Complete checkout
        checkoutPage.fillCustomerInfo("John", "Doe", "12345");
        checkoutPage.continueCheckout();
        checkoutPage.finishCheckout();

        // Step 6: Verify completion
        checkoutPage.assertOrderComplete();
        AllureUtils.attachScreenshot(PlaywrightFixture.getPage(), "04_order_complete");
    }
}
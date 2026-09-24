package com.playwright.tests.ui.tests;

import com.playwright.tests.annotations.Layer;
import com.playwright.tests.fixtures.PlaywrightFixture;
import com.playwright.tests.ui.pages.LoginPage;
import com.playwright.tests.ui.pages.ProductsPage;
import com.playwright.tests.ui.pages.CartPage;
import com.playwright.tests.ui.pages.CheckoutPage;
import com.playwright.tests.config.TestConfig;
import com.playwright.tests.utils.AllureUtils;
import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;  // ← Добавить

@ExtendWith(PlaywrightFixture.class)
@Tag("UI")
@Tag("Checkout")
@Layer("UI")
@Epic("UI Testing")
@Feature("Checkout Functionality")
public class CheckoutTests {

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

        // Login and add product to cart before each checkout test
        loginPage.navigate();
        loginPage.login(TestConfig.getStandardUsername(), TestConfig.getStandardPassword());
        productsPage.assertProductsPageLoaded();

        // Add at least one product to cart
        productsPage.addProductToCart("Sauce Labs Backpack");
        productsPage.goToCart();
        cartPage.assertCartPageLoaded();
    }

    // ========== Positive Scenarios ==========

    @Test
    @Severity(SeverityLevel.BLOCKER)
    @Description("Complete checkout with valid customer information")
    @Story("Checkout - Complete Flow")
    @DisplayName("TC-020: Complete checkout with valid info")
    void completeCheckout_withValidInfo_shouldSucceed() {
        // Proceed to checkout
        cartPage.proceedToCheckout();
        checkoutPage.assertCheckoutPageLoaded();

        // Fill customer information
        checkoutPage.fillCustomerInfo("John", "Doe", "12345");
        checkoutPage.continueCheckout();

        // Verify overview and finish
        checkoutPage.verifyTotalCalculation();
        checkoutPage.finishCheckout();

        // Verify completion
        checkoutPage.assertOrderComplete();
        AllureUtils.attachScreenshot(PlaywrightFixture.getPage(), "order_complete");
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Description("Checkout with multiple items in cart")
    @Story("Checkout - Multiple Items")
    @DisplayName("TC-021: Checkout with multiple items")
    void checkout_withMultipleItems_shouldSucceed() {
        // Добавляем дополнительные товары (уже на странице товаров)
        productsPage.addProductToCart("Sauce Labs Bolt T-Shirt");
        productsPage.addProductToCart("Sauce Labs Onesie");

        // Go to cart and checkout
        productsPage.goToCart();
        cartPage.proceedToCheckout();

        checkoutPage.fillCustomerInfo("Jane", "Smith", "67890");
        checkoutPage.continueCheckout();

        // Verify multiple items are present
        assertThat(checkoutPage.getCheckoutItemsCount())
                .as("Should have 3 items in checkout")
                .isEqualTo(3);

        checkoutPage.finishCheckout();
        checkoutPage.assertOrderComplete();

        AllureUtils.attachText("Checkout Summary", "Successfully checked out with 3 items");
    }

    // ========== Validation Scenarios ==========

    @Test
    @Severity(SeverityLevel.NORMAL)
    @Description("Checkout with missing first name shows error")
    @Story("Checkout - Validation")
    @DisplayName("TC-022: Missing first name shows error")
    void checkout_withMissingFirstName_shouldShowError() {
        cartPage.proceedToCheckout();
        checkoutPage.assertCheckoutPageLoaded();

        checkoutPage.fillCustomerInfo("", "Doe", "12345");
        checkoutPage.continueCheckout();

        checkoutPage.assertErrorMessage("First Name is required");
        AllureUtils.attachScreenshot(PlaywrightFixture.getPage(), "missing_first_name_error");
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @Description("Checkout with missing last name shows error")
    @Story("Checkout - Validation")
    @DisplayName("TC-023: Missing last name shows error")
    void checkout_withMissingLastName_shouldShowError() {
        cartPage.proceedToCheckout();
        checkoutPage.assertCheckoutPageLoaded();

        checkoutPage.fillCustomerInfo("John", "", "12345");
        checkoutPage.continueCheckout();

        checkoutPage.assertErrorMessage("Last Name is required");
        AllureUtils.attachScreenshot(PlaywrightFixture.getPage(), "missing_last_name_error");
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @Description("Checkout with missing postal code shows error")
    @Story("Checkout - Validation")
    @DisplayName("TC-024: Missing postal code shows error")
    void checkout_withMissingPostalCode_shouldShowError() {
        cartPage.proceedToCheckout();
        checkoutPage.assertCheckoutPageLoaded();

        checkoutPage.fillCustomerInfo("John", "Doe", "");
        checkoutPage.continueCheckout();

        checkoutPage.assertErrorMessage("Postal Code is required");
        AllureUtils.attachScreenshot(PlaywrightFixture.getPage(), "missing_postal_code_error");
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @Description("Checkout with empty cart shows error")
    @Story("Checkout - Empty Cart")
    @DisplayName("TC-025: Checkout with empty cart")
    void checkout_withEmptyCart_shouldShowError() {
        // Remove all items from cart
        cartPage.removeAllItems();
        cartPage.assertCartIsEmpty();

        cartPage.proceedToCheckout();
        // SauceDemo redirects to products page when cart is empty
        productsPage.assertProductsPageLoaded();

        AllureUtils.attachText("Empty Cart", "Checkout blocked - cart has no items");
    }

    // ========== Cancel Scenarios ==========

    @Test
    @Severity(SeverityLevel.NORMAL)
    @Description("Cancel checkout returns to cart")
    @Story("Checkout - Cancel")
    @DisplayName("TC-026: Cancel checkout returns to cart")
    void cancelCheckout_shouldReturnToCart() {
        cartPage.proceedToCheckout();
        checkoutPage.assertCheckoutPageLoaded();

        checkoutPage.cancelCheckout();

        // Should return to cart page
        cartPage.assertCartPageLoaded();
        AllureUtils.attachScreenshot(PlaywrightFixture.getPage(), "cancelled_checkout");
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @Description("Cancel from overview returns to products")
    @Story("Checkout - Cancel")
    @DisplayName("TC-027: Cancel from overview returns to products")
    void cancelFromOverview_shouldReturnToProducts() {
        cartPage.proceedToCheckout();
        checkoutPage.fillCustomerInfo("John", "Doe", "12345");
        checkoutPage.continueCheckout();

        // Cancel button on overview page returns to products
        // Note: SauceDemo doesn't have cancel on overview, it has 'Cancel' that returns to products
        checkoutPage.cancelCheckout();
        productsPage.assertProductsPageLoaded();

        AllureUtils.attachText("Cancel Action", "Checkout cancelled from overview page");
    }

    // ========== Price Verification ==========

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify price calculation during checkout")
    @Story("Checkout - Price Calculation")
    @DisplayName("TC-028: Verify price calculation is correct")
    void priceCalculation_shouldBeCorrect() {
        // Add items with known prices
        productsPage.addProductToCart("Sauce Labs Backpack");      // $29.99
        productsPage.addProductToCart("Sauce Labs Bolt T-Shirt");  // $15.99
        productsPage.addProductToCart("Sauce Labs Onesie");        // $7.99

        productsPage.goToCart();
        cartPage.proceedToCheckout();
        checkoutPage.fillCustomerInfo("John", "Doe", "12345");
        checkoutPage.continueCheckout();

        // Expected: 29.99 + 15.99 + 7.99 = 53.97 + tax
        double expectedItemTotal = 29.99 + 15.99 + 7.99;

        assertThat(checkoutPage.getItemTotalValue())
                .as("Item total should match sum of products")
                .isEqualTo(expectedItemTotal);

        checkoutPage.verifyTotalCalculation();

        AllureUtils.attachText("Price Verification",
                String.format("Items total: $%.2f\nTax: $%.2f\nFinal total: $%.2f",
                        checkoutPage.getItemTotalValue(),
                        checkoutPage.getTaxValue(),
                        checkoutPage.getTotalValue()));
    }

    // ========== Edge Cases ==========

    @Test
    @Severity(SeverityLevel.TRIVIAL)
    @Description("Checkout with special characters in address fields")
    @Story("Checkout - Edge Cases")
    @DisplayName("TC-029: Checkout with special characters in address")
    void checkout_withSpecialCharacters_shouldSucceed() {
        cartPage.proceedToCheckout();

        checkoutPage.fillCustomerInfo("John-Michael", "O'Doe", "A1B 2C3");
        checkoutPage.continueCheckout();
        checkoutPage.finishCheckout();

        checkoutPage.assertOrderComplete();
        AllureUtils.attachText("Special Characters", "Successfully processed with special characters in name and postal code");
    }

    @Test
    @Severity(SeverityLevel.TRIVIAL)
    @Description("Checkout with very long field values")
    @Story("Checkout - Edge Cases")
    @DisplayName("TC-030: Checkout with long field values")
    void checkout_withLongFieldValues_shouldHandleCorrectly() {
        String longName = "John".repeat(20);  // Very long name
        String longPostal = "1234567890".repeat(5);

        cartPage.proceedToCheckout();
        checkoutPage.fillCustomerInfo(longName, "Doe", longPostal);
        checkoutPage.continueCheckout();
        checkoutPage.finishCheckout();

        checkoutPage.assertOrderComplete();
        AllureUtils.attachText("Long Values", "Successfully processed with very long input values");
    }
}
package com.playwright.tests.ui.pages;

import com.microsoft.playwright.Page;
import com.playwright.tests.utils.AllureUtils;
import io.qameta.allure.Step;

import static org.assertj.core.api.Assertions.assertThat;

public class CartPage extends BasePage {

    // Locators
    private final String cartItems = "[data-test='inventory-item']";
    private final String cartItemNames = "[data-test='inventory-item-name']";
    private final String removeButtons = "[data-test^='remove']";
    private final String checkoutButton = "[data-test='checkout']";
    private final String continueShoppingButton = "[data-test='continue-shopping']";
    private final String cartTitle = "[data-test='title']";

    public CartPage(Page page) {
        super(page);
    }

    @Step("Navigate to cart page")
    public void navigate() {
        page.click("[data-test='shopping-cart-link']");
        waitForLoadState();
        assertCartPageLoaded();
    }

    @Step("Verify cart page is loaded")
    public void assertCartPageLoaded() {
        assertThat(page.locator(cartTitle).textContent())
                .as("Cart page title should be 'Your Cart'")
                .isEqualTo("Your Cart");
        AllureUtils.attachScreenshot(page, "cart_page_loaded");
    }

    @Step("Get number of items in cart")
    public int getCartItemsCount() {
        return page.locator(cartItems).count();
    }

    @Step("Get cart item names")
    public java.util.List<String> getCartItemNames() {
        return page.locator(cartItemNames).allTextContents();
    }

    @Step("Verify cart contains product: {productName}")
    public void assertCartContainsProduct(String productName) {
        java.util.List<String> itemNames = getCartItemNames();
        assertThat(itemNames)
                .as("Cart should contain product: " + productName)
                .contains(productName);
    }

    @Step("Verify cart does not contain product: {productName}")
    public void assertCartDoesNotContainProduct(String productName) {
        java.util.List<String> itemNames = getCartItemNames();
        assertThat(itemNames)
                .as("Cart should not contain product: " + productName)
                .doesNotContain(productName);
    }

    @Step("Remove first item from cart")
    public void removeFirstItem() {
        if (getCartItemsCount() > 0) {
            page.locator(removeButtons).first().click();
            waitForLoadState();
            AllureUtils.attachText("Cart Action", "Removed first item from cart");
        }
    }

    @Step("Remove item by product name: {productName}")
    public void removeItemByName(String productName) {
        String removeButton = String.format("[data-test='remove-%s']",
                productName.toLowerCase().replace(" ", "-"));
        page.click(removeButton);
        waitForLoadState();
        AllureUtils.attachText("Cart Action", "Removed item: " + productName);
    }

    @Step("Remove all items from cart")
    public void removeAllItems() {
        while (getCartItemsCount() > 0) {
            removeFirstItem();
        }
        AllureUtils.attachText("Cart Action", "Removed all items from cart");
    }

    @Step("Proceed to checkout")
    public void proceedToCheckout() {
        page.click(checkoutButton);
        waitForLoadState();
        AllureUtils.attachScreenshot(page, "checkout_clicked");
    }

    @Step("Continue shopping")
    public void continueShopping() {
        page.click(continueShoppingButton);
        waitForLoadState();
    }

    @Step("Verify cart is empty")
    public void assertCartIsEmpty() {
        assertThat(getCartItemsCount())
                .as("Cart should be empty")
                .isEqualTo(0);
        AllureUtils.attachScreenshot(page, "empty_cart");
    }

    @Step("Verify cart is not empty")
    public void assertCartIsNotEmpty() {
        assertThat(getCartItemsCount())
                .as("Cart should not be empty")
                .isGreaterThan(0);
    }

    @Step("Get total items count (using badge)")
    public int getCartBadgeCount() {
        if (page.locator("[data-test='shopping-cart-badge']").count() > 0) {
            String badgeText = page.locator("[data-test='shopping-cart-badge']").textContent();
            return Integer.parseInt(badgeText);
        }
        return 0;
    }

    @Step("Verify cart badge count equals {expectedCount}")
    public void assertCartBadgeCount(int expectedCount) {
        assertThat(getCartBadgeCount())
                .as("Cart badge count should be " + expectedCount)
                .isEqualTo(expectedCount);
    }
}
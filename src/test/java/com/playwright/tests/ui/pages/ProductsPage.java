package com.playwright.tests.ui.pages;

import com.microsoft.playwright.Page;
import org.assertj.core.api.Assertions;

public class ProductsPage extends BasePage {
    private final String productsTitle = "[data-test='title']";
    private final String shoppingCart = "[data-test='shopping-cart-link']";
    private final String inventoryItems = "[data-test='inventory-item']";

    public ProductsPage(Page page) {
        super(page);
    }

    public void assertProductsPageLoaded() {
        Assertions.assertThat(page.locator(productsTitle).textContent())
                .isEqualTo("Products");
        Assertions.assertThat(page.locator(inventoryItems).count()).isGreaterThan(0);
    }

    public void addProductToCart(String productName) {
        String buttonSelector = "[data-test='add-to-cart-" +
                productName.toLowerCase().replace(" ", "-") + "']";
        page.click(buttonSelector);
    }

    public int getCartItemsCount() {
        if (page.locator("[data-test='shopping-cart-badge']").count() > 0) {
            return Integer.parseInt(page.locator("[data-test='shopping-cart-badge']").textContent());
        }
        return 0;
    }

    public void goToCart() {
        page.click(shoppingCart);
        waitForLoadState();
    }
}
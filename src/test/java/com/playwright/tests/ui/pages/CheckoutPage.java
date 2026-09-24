package com.playwright.tests.ui.pages;

import com.microsoft.playwright.Page;
import com.playwright.tests.utils.AllureUtils;
import io.qameta.allure.Step;

import static org.assertj.core.api.Assertions.assertThat;

public class CheckoutPage extends BasePage {

    // Locators - Information Step
    private final String firstNameInput = "[data-test='firstName']";
    private final String lastNameInput = "[data-test='lastName']";
    private final String postalCodeInput = "[data-test='postalCode']";
    private final String continueButton = "[data-test='continue']";
    private final String cancelButton = "[data-test='cancel']";
    private final String checkoutTitle = "[data-test='title']";
    private final String errorMessage = "[data-test='error']";

    // Locators - Overview Step
    private final String cartItems = "[data-test='inventory-item']";
    private final String itemTotal = "[data-test='subtotal-label']";
    private final String taxAmount = "[data-test='tax-label']";
    private final String totalAmount = "[data-test='total-label']";
    private final String finishButton = "[data-test='finish']";

    // Locators - Complete Step
    private final String completeHeader = "[data-test='complete-header']";
    private final String completeText = "[data-test='complete-text']";
    private final String backHomeButton = "[data-test='back-to-products']";
    private final String ponyExpressImage = "[data-test='pony-express']";

    public CheckoutPage(Page page) {
        super(page);
    }

    // ========== Navigation & Verification ==========

    @Step("Verify checkout page is loaded")
    public void assertCheckoutPageLoaded() {
        assertThat(page.locator(checkoutTitle).textContent())
                .as("Checkout page title should be 'Checkout: Your Information'")
                .isEqualTo("Checkout: Your Information");
        AllureUtils.attachScreenshot(page, "checkout_info_page");
    }

    @Step("Verify checkout overview page is loaded")
    public void assertCheckoutOverviewLoaded() {
        assertThat(page.locator(checkoutTitle).textContent())
                .as("Overview page title should be 'Checkout: Overview'")
                .isEqualTo("Checkout: Overview");
        AllureUtils.attachScreenshot(page, "checkout_overview_page");
    }

    @Step("Verify order complete page is loaded")
    public void assertOrderCompletePageLoaded() {
        assertThat(page.locator(checkoutTitle).textContent())
                .as("Complete page title should be 'Checkout: Complete!'")
                .isEqualTo("Checkout: Complete!");
    }

    // ========== Information Step ==========

    @Step("Fill customer information: {firstName} {lastName}, ZIP: {postalCode}")
    public void fillCustomerInfo(String firstName, String lastName, String postalCode) {
        page.fill(firstNameInput, firstName);
        page.fill(lastNameInput, lastName);
        page.fill(postalCodeInput, postalCode);

        AllureUtils.attachText("Customer Info",
                String.format("First Name: %s\nLast Name: %s\nPostal Code: %s",
                        firstName, lastName, postalCode));
    }

    @Step("Continue to checkout overview")
    public void continueCheckout() {
        page.click(continueButton);
        waitForLoadState();
        assertCheckoutOverviewLoaded();
        AllureUtils.attachScreenshot(page, "checkout_overview");
    }

    @Step("Cancel checkout and return to cart")
    public void cancelCheckout() {
        page.click(cancelButton);
        waitForLoadState();
        AllureUtils.attachText("Checkout Action", "Checkout cancelled, returning to cart");
    }

    @Step("Verify error message: {expectedMessage}")
    public void assertErrorMessage(String expectedMessage) {
        assertThat(page.locator(errorMessage).textContent())
                .as("Error message should contain: " + expectedMessage)
                .contains(expectedMessage);
        AllureUtils.attachText("Error Message", page.locator(errorMessage).textContent());
    }

    @Step("Verify missing field error for {missingField}")
    public void assertMissingFieldError(String missingField) {
        assertThat(page.locator(errorMessage).textContent())
                .as("Should show error for missing field: " + missingField)
                .contains("required");
        AllureUtils.attachText("Validation Error",
                String.format("Missing field: %s\nError: %s",
                        missingField, page.locator(errorMessage).textContent()));
    }

    // ========== Overview Step ==========

    @Step("Get number of items in checkout overview")
    public int getCheckoutItemsCount() {
        return page.locator(cartItems).count();
    }

    @Step("Get item total text")
    public String getItemTotalText() {
        return page.locator(itemTotal).textContent();
    }

    @Step("Get item total value as double")
    public double getItemTotalValue() {
        String text = getItemTotalText();
        String number = text.replaceAll("[^0-9.]", "");
        return Double.parseDouble(number);
    }

    @Step("Get tax amount text")
    public String getTaxText() {
        return page.locator(taxAmount).textContent();
    }

    @Step("Get tax amount as double")
    public double getTaxValue() {
        String text = getTaxText();
        String number = text.replaceAll("[^0-9.]", "");
        return Double.parseDouble(number);
    }

    @Step("Get total amount text")
    public String getTotalText() {
        return page.locator(totalAmount).textContent();
    }

    @Step("Get total amount as double")
    public double getTotalValue() {
        String text = getTotalText();
        String number = text.replaceAll("[^0-9.]", "");
        return Double.parseDouble(number);
    }

    @Step("Verify total calculation is correct")
    public void verifyTotalCalculation() {
        double itemTotal = getItemTotalValue();
        double tax = getTaxValue();
        double total = getTotalValue();

        assertThat(itemTotal + tax)
                .as("Total should equal item total + tax")
                .isEqualTo(total);

        AllureUtils.attachText("Price Verification",
                String.format("Item Total: %.2f\nTax: %.2f\nTotal: %.2f\nCalculation: %.2f + %.2f = %.2f",
                        itemTotal, tax, total, itemTotal, tax, itemTotal + tax));
    }

    @Step("Finish checkout and complete order")
    public void finishCheckout() {
        page.click(finishButton);
        waitForLoadState();
        AllureUtils.attachScreenshot(page, "order_completed");
    }

    // ========== Complete Step ==========

    @Step("Verify order completion message")
    public void assertOrderComplete() {
        assertThat(page.locator(completeHeader).textContent())
                .as("Order complete header should be 'Thank you for your order!'")
                .isEqualTo("Thank you for your order!");

        assertThat(page.locator(completeText).textContent())
                .as("Order complete text should be correct")
                .contains("Your order has been dispatched");

        assertThat(page.locator(ponyExpressImage).isVisible())
                .as("Pony Express image should be displayed")
                .isTrue();

        AllureUtils.attachScreenshot(page, "order_complete_page");
    }

    @Step("Back to products page")
    public void backToProducts() {
        page.click(backHomeButton);
        waitForLoadState();
    }

    // ========== Combined Flows ==========

    @Step("Complete checkout with customer info: {firstName} {lastName}, ZIP: {postalCode}")
    public void completeCheckout(String firstName, String lastName, String postalCode) {
        fillCustomerInfo(firstName, lastName, postalCode);
        continueCheckout();
        finishCheckout();
        assertOrderComplete();
        AllureUtils.attachText("Checkout Flow",
                String.format("Checkout completed for: %s %s", firstName, lastName));
    }

    @Step("Verify checkout summary contains product: {productName}")
    public void assertCheckoutContainsProduct(String productName) {
        java.util.List<String> itemNames = page.locator("[data-test='inventory-item-name']").allTextContents();
        assertThat(itemNames)
                .as("Checkout overview should contain product: " + productName)
                .contains(productName);
    }
}
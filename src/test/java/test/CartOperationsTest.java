package test;

import base.BaseTest;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.CartPage;
import pages.LoginPage;
import pages.ProductDetailPage;
import pages.ProductPage;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class CartOperationsTest extends BaseTest {
    private static final String CART_USER_NAME = "Selenium Cart Login";
    private static final String CART_USER_EMAIL = "selenium.cart.login@example.com";
    private static final String CART_USER_PASSWORD = "Password123";

    @BeforeMethod
    public void loginAndSeedCart() {
        requireBackend();
        ensureCartLoginUserExists();

        openApp("/login");
        clearBrowserStorage();
        LoginPage loginPage = new LoginPage(driver);
        Assert.assertTrue(loginPage.isLoaded(), "Login page should load before cart tests");

        System.out.println("Cart test login credentials -> email: " + CART_USER_EMAIL
                + ", password: " + CART_USER_PASSWORD);
        loginPage.enterEmail(CART_USER_EMAIL);
        loginPage.enterPassword(CART_USER_PASSWORD);
        Assert.assertEquals(loginPage.enteredEmail(), CART_USER_EMAIL, "Typed login email should match test credential");
        Assert.assertEquals(loginPage.enteredPassword(), CART_USER_PASSWORD, "Typed login password should match test credential");
        loginPage.clickContinue();
        waitForUrlContains("/dashboard");

        clearCartForLoggedInUser();
        addFirstAvailableProductToCart();
        openApp("/cart");
        waitForUrlContains("/cart");
        Assert.assertTrue(new CartPage(driver).isLoaded(), "Cart page should load with seeded item");
    }

    @Test(description = "T092: Automate update cart")
    public void updateCartQuantityRefreshesItemAndSummary() {
        CartPage cartPage = new CartPage(driver);
        String productName = cartPage.firstItemName();
        BigDecimal unitPrice = cartPage.firstItemPrice();
        int initialQuantity = cartPage.firstItemQuantity();

        cartPage.increaseFirstQuantity();

        int updatedQuantity = initialQuantity + 1;
        Assert.assertEquals(cartPage.firstItemName(), productName, "Updating quantity should keep the same cart item");
        Assert.assertEquals(cartPage.firstItemQuantity(), updatedQuantity, "Quantity should increase by one");
        Assert.assertEquals(cartPage.summaryItemCount(), updatedQuantity, "Summary item count should match quantity");
        assertMoneyEquals(cartPage.firstItemTotal(), unitPrice.multiply(BigDecimal.valueOf(updatedQuantity)),
                "Item total should be recalculated after quantity update");
        assertMoneyEquals(cartPage.summarySubtotal(), cartPage.firstItemTotal(),
                "Summary subtotal should reflect the updated item total");
    }

    @Test(description = "T093: Automate remove item")
    public void removeCartItemShowsEmptyCartState() {
        CartPage cartPage = new CartPage(driver);

        cartPage.removeFirstItem();

        Assert.assertTrue(cartPage.hasEmptyCartMessage(), "Removing the only item should show the empty cart state");
        Assert.assertEquals(cartPage.itemCount(), 0, "Removed item should no longer be listed");
        Assert.assertFalse(cartPage.hasCheckoutButton(), "Checkout should not be available for an empty cart");
    }

    @Test(description = "T094: Validate cart summary")
    public void cartSummaryMatchesLineItemsTaxAndTotal() {
        CartPage cartPage = new CartPage(driver);
        BigDecimal unitPrice = cartPage.firstItemPrice();
        int quantity = cartPage.firstItemQuantity();
        BigDecimal expectedSubtotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
        BigDecimal expectedTax = expectedSubtotal.multiply(new BigDecimal("0.18"));
        BigDecimal expectedTotal = expectedSubtotal.add(expectedTax);

        Assert.assertEquals(cartPage.summaryItemCount(), quantity, "Summary item count should match cart quantity");
        assertMoneyEquals(cartPage.firstItemTotal(), expectedSubtotal, "Line item total should equal price times quantity");
        assertMoneyEquals(cartPage.summarySubtotal(), expectedSubtotal, "Summary subtotal should equal line item total");
        assertMoneyEquals(cartPage.summaryTax(), expectedTax, "Summary tax should be 18 percent of subtotal");
        assertMoneyEquals(cartPage.summaryTotal(), expectedTotal, "Summary total should include subtotal plus tax");
    }

    @Test(description = "T095: Handle cart operation edge cases")
    public void decreasingSingleQuantityRemovesItemWithoutNegativeQuantity() {
        CartPage cartPage = new CartPage(driver);
        Assert.assertEquals(cartPage.firstItemQuantity(), 1, "Seeded cart should start at quantity one");

        cartPage.decreaseFirstQuantity();

        Assert.assertTrue(cartPage.hasEmptyCartMessage(), "Quantity below one should remove the item");
        Assert.assertEquals(cartPage.itemCount(), 0, "Cart should not show zero or negative quantity rows");
        Assert.assertTrue(cartPage.hasBrowseProductsButton(), "Empty cart should offer a path back to products");
        Assert.assertFalse(cartPage.hasCheckoutButton(), "Empty cart should hide checkout actions");
    }

    private void addFirstAvailableProductToCart() {
        openApp("/products");
        waitForUrlContains("/products");

        ProductPage productPage = new ProductPage(driver);
        Assert.assertTrue(productPage.isLoaded(), "Products page should load before seeding cart");
        productPage.openFirstProduct();
        waitForUrlContains("/products/");

        ProductDetailPage detailPage = new ProductDetailPage(driver);
        Assert.assertTrue(detailPage.isLoaded(), "Product detail page should load before adding to cart");
        if (!detailPage.canAddToCart()) {
            throw new SkipException("First product is not currently available for cart testing");
        }

        detailPage.addToCart();
        Assert.assertTrue(detailPage.showsAddedToCartState(), "Product should show added-to-cart feedback");
    }

    private void ensureCartLoginUserExists() {
        String body = "{"
                + "\"name\":\"" + CART_USER_NAME + "\","
                + "\"email\":\"" + CART_USER_EMAIL + "\","
                + "\"password\":\"" + CART_USER_PASSWORD + "\""
                + "}";

        HttpRequest request = HttpRequest.newBuilder(URI.create(BACKEND_URL + "/api/auth/register"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        try {
            HttpResponse<String> response = HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofString());
            boolean created = response.statusCode() >= 200 && response.statusCode() < 300;
            boolean alreadyExists = response.statusCode() == 400
                    && response.body() != null
                    && response.body().contains("Email already in use");
            if (!created && !alreadyExists) {
                throw new IllegalStateException("Could not prepare cart login user. Status: "
                        + response.statusCode() + ", body: " + response.body());
            }
        } catch (Exception ex) {
            throw new IllegalStateException("Could not prepare cart login user", ex);
        }
    }

    private void clearCartForLoggedInUser() {
        HttpRequest request = HttpRequest.newBuilder(URI.create(BACKEND_URL + "/api/cart"))
                .header("Authorization", "Bearer " + loginUtils.storedToken())
                .DELETE()
                .build();

        try {
            HttpResponse<String> response = HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 400) {
                throw new IllegalStateException("Could not clear cart before test. Status: "
                        + response.statusCode() + ", body: " + response.body());
            }
        } catch (Exception ex) {
            throw new IllegalStateException("Could not clear cart before test", ex);
        }
    }

    private void assertMoneyEquals(BigDecimal actual, BigDecimal expected, String message) {
        BigDecimal normalizedActual = actual.setScale(2, RoundingMode.HALF_UP);
        BigDecimal normalizedExpected = expected.setScale(2, RoundingMode.HALF_UP);
        Assert.assertEquals(normalizedActual.compareTo(normalizedExpected), 0,
                message + " (expected " + normalizedExpected + ", got " + normalizedActual + ")");
    }
}

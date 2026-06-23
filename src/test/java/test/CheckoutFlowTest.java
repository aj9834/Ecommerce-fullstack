package test;

import base.BaseTest;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pages.CartPage;
import pages.CheckoutPage;
import pages.LoginPage;
import pages.OrdersPage;
import pages.ProductDetailPage;
import pages.ProductPage;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class CheckoutFlowTest extends BaseTest {
    private static final String CHECKOUT_USER_NAME = "Selenium Checkout Login";
    private static final String CHECKOUT_USER_EMAIL = "selenium.checkout.login@example.com";
    private static final String CHECKOUT_USER_PASSWORD = "Password123";
    private static final String VALID_PHONE = "9876543210";
    private static final String VALID_PINCODE = "560001";

    @BeforeMethod
    public void loginAndSeedCart() {
        requireBackend();
        ensureCheckoutLoginUserExists();

        openApp("/login");
        clearBrowserStorage();
        LoginPage loginPage = new LoginPage(driver);
        Assert.assertTrue(loginPage.isLoaded(), "Login page should load before checkout tests");

        System.out.println("Checkout test login credentials -> email: " + CHECKOUT_USER_EMAIL
                + ", password: " + CHECKOUT_USER_PASSWORD);
        loginPage.loginAs(CHECKOUT_USER_EMAIL, CHECKOUT_USER_PASSWORD);
        waitForUrlContains("/dashboard");

        clearCartForLoggedInUser();
        addFirstAvailableProductToCart();
        openApp("/cart");
        waitForUrlContains("/cart");
        Assert.assertTrue(new CartPage(driver).isLoaded(), "Cart page should load with a seeded checkout item");
    }

    @Test(description = "T096: Automate checkout initiation and verify order appears in orders")
    public void checkoutInitiationPlacesOrderAndShowsInOrders() {
        CartPage cartPage = new CartPage(driver);
        String cartProductName = cartPage.firstItemName();
        String shippingName = "Checkout User " + System.currentTimeMillis();

        cartPage.proceedToCheckout();
        waitForUrlContains("/checkout");

        CheckoutPage checkoutPage = new CheckoutPage(driver);
        Assert.assertTrue(checkoutPage.isLoaded(), "Checkout page should load after checkout initiation");
        checkoutPage.fillShippingDetails(
                shippingName,
                VALID_PHONE,
                "101 Test Avenue",
                "Bengaluru",
                "Karnataka",
                VALID_PINCODE
        );
        Assert.assertFalse(checkoutPage.hasFieldErrors(), "Valid checkout form should not show field errors before submit");
        checkoutPage.submitOrder();

        waitForUrlContains("/orders");
        OrdersPage ordersPage = new OrdersPage(driver);
        Assert.assertTrue(ordersPage.isLoaded(), "Orders page should load after placing an order");
        Assert.assertTrue(ordersPage.successMessage().matches("Order #\\d+ placed successfully\\."),
                "Orders page should show the placed-order success message");
        Assert.assertTrue(ordersPage.orderCount() > 0, "Placed order should be listed in orders");
        Assert.assertTrue(ordersPage.firstOrderId().matches("Order #\\d+"),
                "Listed order should display an order number");
        Assert.assertEquals(ordersPage.firstOrderShippingName(), shippingName,
                "Newest order should show the checkout shipping name");
        Assert.assertEquals(ordersPage.firstOrderShippingPhone(), VALID_PHONE,
                "Newest order should show the checkout phone number");
        Assert.assertTrue(ordersPage.firstOrderShippingAddress().contains(VALID_PINCODE),
                "Newest order should show the checkout pincode");
        Assert.assertEquals(ordersPage.firstOrderStatus(), "PLACED", "Order should be placed");
        Assert.assertEquals(ordersPage.firstOrderPayment(), "COD PENDING",
                "Cash-on-delivery order should be pending payment");
        Assert.assertTrue(ordersPage.firstOrderItemCount() > 0,
                "Placed order should include the cart item: " + cartProductName);
        Assert.assertTrue(ordersPage.firstOrderItemNames().contains(cartProductName),
                "Placed order should list the same product that was in the cart");
    }

    @Test(description = "T097: Validate checkout form inputs for valid and invalid phone/pincode")
    public void checkoutRejectsTooLongPincodeAndPhone() {
        CartPage cartPage = new CartPage(driver);
        cartPage.proceedToCheckout();
        waitForUrlContains("/checkout");

        CheckoutPage checkoutPage = new CheckoutPage(driver);
        Assert.assertTrue(checkoutPage.isLoaded(), "Checkout page should load before validation");
        checkoutPage.fillShippingDetails(
                "Checkout Validation User",
                "987654321012",
                "202 Validation Street",
                "Bengaluru",
                "Karnataka",
                "5600017"
        );
        checkoutPage.submitOrder();

        Assert.assertEquals(checkoutPage.phoneError(), "Enter a 10 digit phone number",
                "Phone numbers longer than the valid length should be rejected");
        Assert.assertEquals(checkoutPage.pincodeError(), "Enter a 6 digit pincode",
                "Pincodes longer than 6 digits should be rejected");
        Assert.assertTrue(driver.getCurrentUrl().contains("/checkout"),
                "Invalid checkout form should keep the user on checkout");

        checkoutPage.fillShippingDetails(
                "Checkout Validation User",
                VALID_PHONE,
                "202 Validation Street",
                "Bengaluru",
                "Karnataka",
                VALID_PINCODE
        );
        Assert.assertTrue(checkoutPage.fieldErrorsCleared(), "Valid phone and pincode should clear validation errors");
    }

    @Test(description = "T098: Validate checkout required field errors")
    public void checkoutRequiresAllShippingFields() {
        CartPage cartPage = new CartPage(driver);
        cartPage.proceedToCheckout();
        waitForUrlContains("/checkout");

        CheckoutPage checkoutPage = new CheckoutPage(driver);
        Assert.assertTrue(checkoutPage.isLoaded(), "Checkout page should load before required-field validation");
        checkoutPage.submitOrder();

        Assert.assertEquals(checkoutPage.shippingNameError(), "Name is required");
        Assert.assertEquals(checkoutPage.phoneError(), "Enter a 10 digit phone number");
        Assert.assertEquals(checkoutPage.shippingAddressError(), "Address is required");
        Assert.assertEquals(checkoutPage.cityError(), "City is required");
        Assert.assertEquals(checkoutPage.stateError(), "State is required");
        Assert.assertEquals(checkoutPage.pincodeError(), "Enter a 6 digit pincode");
        Assert.assertTrue(driver.getCurrentUrl().contains("/checkout"),
                "Missing required checkout fields should keep the user on checkout");
    }

    @Test(
            dataProvider = "invalidPhoneAndPincodeInputs",
            description = "T099: Validate invalid checkout phone and pincode formats"
    )
    public void checkoutRejectsInvalidPhoneAndPincodeFormats(
            String phone,
            String pincode,
            String caseName
    ) {
        CartPage cartPage = new CartPage(driver);
        cartPage.proceedToCheckout();
        waitForUrlContains("/checkout");

        CheckoutPage checkoutPage = new CheckoutPage(driver);
        Assert.assertTrue(checkoutPage.isLoaded(), "Checkout page should load before invalid-format validation");
        checkoutPage.fillShippingDetails(
                "Checkout Format User",
                phone,
                "303 Format Road",
                "Bengaluru",
                "Karnataka",
                pincode
        );
        checkoutPage.submitOrder();

        Assert.assertEquals(checkoutPage.phoneError(), "Enter a 10 digit phone number",
                caseName + " should reject the phone value");
        Assert.assertEquals(checkoutPage.pincodeError(), "Enter a 6 digit pincode",
                caseName + " should reject the pincode value");
        Assert.assertTrue(driver.getCurrentUrl().contains("/checkout"),
                caseName + " should keep the user on checkout");
    }

    @Test(description = "T100: Validate checkout page when cart is empty")
    public void checkoutWithEmptyCartShowsEmptyStateAndBrowseProductsAction() {
        clearCartForLoggedInUser();
        openApp("/checkout");
        waitForUrlContains("/checkout");

        CheckoutPage checkoutPage = new CheckoutPage(driver);
        Assert.assertTrue(checkoutPage.hasEmptyCheckoutMessage(),
                "Checkout should show an empty-cart state when no cart item exists");
        checkoutPage.browseProductsFromEmptyCheckout();

        waitForUrlContains("/products");
        ProductPage productPage = new ProductPage(driver);
        Assert.assertTrue(productPage.isLoaded(), "Browse Products should navigate from empty checkout to products");
    }

    @DataProvider
    public Object[][] invalidPhoneAndPincodeInputs() {
        return new Object[][]{
                {"987654321", "56001", "short phone and short pincode"},
                {"98765432101", "5600017", "too-long phone and too-long pincode"},
                {"98765abc10", "5600A1", "non-numeric phone and non-numeric pincode"}
        };
    }

    private void addFirstAvailableProductToCart() {
        openApp("/products");
        waitForUrlContains("/products");

        ProductPage productPage = new ProductPage(driver);
        Assert.assertTrue(productPage.isLoaded(), "Products page should load before seeding checkout cart");
        productPage.openFirstProduct();
        waitForUrlContains("/products/");

        ProductDetailPage detailPage = new ProductDetailPage(driver);
        Assert.assertTrue(detailPage.isLoaded(), "Product detail page should load before adding to checkout cart");
        if (!detailPage.canAddToCart()) {
            throw new SkipException("First product is not currently available for checkout testing");
        }

        detailPage.addToCart();
        Assert.assertTrue(detailPage.showsAddedToCartState(), "Product should show added-to-cart feedback");
    }

    private void ensureCheckoutLoginUserExists() {
        String body = "{"
                + "\"name\":\"" + CHECKOUT_USER_NAME + "\","
                + "\"email\":\"" + CHECKOUT_USER_EMAIL + "\","
                + "\"password\":\"" + CHECKOUT_USER_PASSWORD + "\""
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
                throw new IllegalStateException("Could not prepare checkout login user. Status: "
                        + response.statusCode() + ", body: " + response.body());
            }
        } catch (Exception ex) {
            throw new IllegalStateException("Could not prepare checkout login user", ex);
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
                throw new IllegalStateException("Could not clear cart before checkout test. Status: "
                        + response.statusCode() + ", body: " + response.body());
            }
        } catch (Exception ex) {
            throw new IllegalStateException("Could not clear cart before checkout test", ex);
        }
    }
}

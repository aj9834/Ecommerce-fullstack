package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.DemoPause;

import java.time.Duration;
import java.util.Optional;

public class CheckoutPage {
    public static final By LOADING_MESSAGE = By.id("checkout-loading");
    public static final By ERROR_MESSAGE = By.id("checkout-error");
    public static final By EMPTY_MESSAGE = By.id("checkout-empty");
    public static final By BROWSE_PRODUCTS = By.id("checkout-browse-products");
    public static final By FORM = By.id("checkout-form");
    public static final By SHIPPING_NAME = By.id("checkout-shipping-name");
    public static final By SHIPPING_PHONE = By.id("checkout-shipping-phone");
    public static final By SHIPPING_ADDRESS = By.id("checkout-shipping-address");
    public static final By CITY = By.id("checkout-city");
    public static final By STATE = By.id("checkout-state");
    public static final By PINCODE = By.id("checkout-pincode");
    public static final By SHIPPING_NAME_ERROR = By.cssSelector("[data-testid='checkout-shipping-name-error']");
    public static final By SHIPPING_ADDRESS_ERROR = By.cssSelector("[data-testid='checkout-shipping-address-error']");
    public static final By CITY_ERROR = By.cssSelector("[data-testid='checkout-city-error']");
    public static final By STATE_ERROR = By.cssSelector("[data-testid='checkout-state-error']");
    public static final By PINCODE_ERROR = By.cssSelector("[data-testid='checkout-pincode-error']");
    public static final By PHONE_ERROR = By.cssSelector("[data-testid='checkout-shipping-phone-error']");
    public static final By PLACE_ORDER = By.id("checkout-place-order");

    private final WebDriver driver;
    private final WebDriverWait wait;

    public CheckoutPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(7));
    }

    public boolean isLoaded() {
        waitForCheckoutToSettle();
        boolean loaded = wait.until(ExpectedConditions.visibilityOfElementLocated(FORM)).isDisplayed()
                && driver.findElement(PLACE_ORDER).isDisplayed();
        DemoPause.afterStep();
        return loaded;
    }

    public void fillShippingDetails(String name, String phone, String address, String city, String state, String pincode) {
        type(SHIPPING_NAME, name);
        type(SHIPPING_PHONE, phone);
        type(SHIPPING_ADDRESS, address);
        type(CITY, city);
        type(STATE, state);
        type(PINCODE, pincode);
        DemoPause.afterStep();
    }

    public void submitOrder() {
        wait.until(ExpectedConditions.elementToBeClickable(PLACE_ORDER)).click();
        DemoPause.afterStep();
    }

    public String shippingNameError() {
        String error = wait.until(ExpectedConditions.visibilityOfElementLocated(SHIPPING_NAME_ERROR)).getText();
        DemoPause.afterStep();
        return error;
    }

    public String phoneError() {
        String error = wait.until(ExpectedConditions.visibilityOfElementLocated(PHONE_ERROR)).getText();
        DemoPause.afterStep();
        return error;
    }

    public String shippingAddressError() {
        String error = wait.until(ExpectedConditions.visibilityOfElementLocated(SHIPPING_ADDRESS_ERROR)).getText();
        DemoPause.afterStep();
        return error;
    }

    public String cityError() {
        String error = wait.until(ExpectedConditions.visibilityOfElementLocated(CITY_ERROR)).getText();
        DemoPause.afterStep();
        return error;
    }

    public String stateError() {
        String error = wait.until(ExpectedConditions.visibilityOfElementLocated(STATE_ERROR)).getText();
        DemoPause.afterStep();
        return error;
    }

    public String pincodeError() {
        String error = wait.until(ExpectedConditions.visibilityOfElementLocated(PINCODE_ERROR)).getText();
        DemoPause.afterStep();
        return error;
    }

    public boolean hasFieldErrors() {
        boolean visible = isVisible(PHONE_ERROR) || isVisible(PINCODE_ERROR);
        DemoPause.afterStep();
        return visible;
    }

    public boolean fieldErrorsCleared() {
        boolean cleared = wait.until(ExpectedConditions.invisibilityOfElementLocated(PHONE_ERROR))
                && wait.until(ExpectedConditions.invisibilityOfElementLocated(PINCODE_ERROR));
        DemoPause.afterStep();
        return cleared;
    }

    public boolean hasEmptyCheckoutMessage() {
        waitForCheckoutToSettle();
        boolean visible = isVisible(EMPTY_MESSAGE);
        DemoPause.afterStep();
        return visible;
    }

    public void browseProductsFromEmptyCheckout() {
        wait.until(ExpectedConditions.elementToBeClickable(BROWSE_PRODUCTS)).click();
        DemoPause.afterStep();
    }

    private void type(By locator, String value) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        element.clear();
        element.sendKeys(value);
    }

    private void waitForCheckoutToSettle() {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(LOADING_MESSAGE));
        wait.until(driver -> isVisible(FORM) || isVisible(EMPTY_MESSAGE) || isVisible(ERROR_MESSAGE));
    }

    private boolean isVisible(By locator) {
        Optional<WebElement> element = driver.findElements(locator).stream().findFirst();
        return element.isPresent() && element.get().isDisplayed();
    }
}

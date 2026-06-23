package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.DemoPause;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class OrdersPage {
    public static final By LOADING_MESSAGE = By.id("orders-loading");
    public static final By ERROR_MESSAGE = By.id("orders-error");
    public static final By TITLE = By.id("orders-title");
    public static final By SUCCESS_MESSAGE = By.id("orders-success");
    public static final By EMPTY_MESSAGE = By.id("orders-empty");
    public static final By ORDER_CARDS = By.cssSelector("[data-testid='order-card']");
    public static final By FIRST_ORDER_ID = By.cssSelector("[data-testid='order-id']");
    public static final By FIRST_ORDER_STATUS = By.cssSelector("[data-testid='order-status']");
    public static final By FIRST_ORDER_PAYMENT = By.cssSelector("[data-testid='order-payment']");
    public static final By FIRST_ORDER_SHIPPING_NAME = By.cssSelector("[data-testid='order-shipping-name']");
    public static final By FIRST_ORDER_SHIPPING_ADDRESS = By.cssSelector("[data-testid='order-shipping-address']");
    public static final By FIRST_ORDER_SHIPPING_PHONE = By.cssSelector("[data-testid='order-shipping-phone']");
    public static final By FIRST_ORDER_ITEMS = By.cssSelector("[data-testid='order-item']");
    public static final By ORDER_ITEM_NAMES = By.cssSelector("[data-testid='order-item-name']");

    private final WebDriver driver;
    private final WebDriverWait wait;

    public OrdersPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(7));
    }

    public boolean isLoaded() {
        waitForOrdersToSettle();
        boolean loaded = wait.until(ExpectedConditions.visibilityOfElementLocated(TITLE)).isDisplayed()
                && (orderCount() > 0 || isVisible(EMPTY_MESSAGE) || isVisible(ERROR_MESSAGE));
        DemoPause.afterStep();
        return loaded;
    }

    public String successMessage() {
        String message = wait.until(ExpectedConditions.visibilityOfElementLocated(SUCCESS_MESSAGE)).getText();
        DemoPause.afterStep();
        return message;
    }

    public int orderCount() {
        waitForOrdersToSettle();
        int count = driver.findElements(ORDER_CARDS).size();
        DemoPause.afterStep();
        return count;
    }

    public String firstOrderId() {
        String id = firstOrder().findElement(FIRST_ORDER_ID).getText();
        DemoPause.afterStep();
        return id;
    }

    public String firstOrderStatus() {
        String status = firstOrder().findElement(FIRST_ORDER_STATUS).getText();
        DemoPause.afterStep();
        return status;
    }

    public String firstOrderPayment() {
        String payment = firstOrder().findElement(FIRST_ORDER_PAYMENT).getText();
        DemoPause.afterStep();
        return payment;
    }

    public String firstOrderShippingName() {
        String name = firstOrder().findElement(FIRST_ORDER_SHIPPING_NAME).getText();
        DemoPause.afterStep();
        return name;
    }

    public String firstOrderShippingAddress() {
        String address = firstOrder().findElement(FIRST_ORDER_SHIPPING_ADDRESS).getText();
        DemoPause.afterStep();
        return address;
    }

    public String firstOrderShippingPhone() {
        String phone = firstOrder().findElement(FIRST_ORDER_SHIPPING_PHONE).getText();
        DemoPause.afterStep();
        return phone;
    }

    public int firstOrderItemCount() {
        int count = firstOrder().findElements(FIRST_ORDER_ITEMS).size();
        DemoPause.afterStep();
        return count;
    }

    public List<String> firstOrderItemNames() {
        List<String> names = firstOrder().findElements(ORDER_ITEM_NAMES)
                .stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());
        DemoPause.afterStep();
        return names;
    }

    private WebElement firstOrder() {
        waitForOrdersToSettle();
        return wait.until(ExpectedConditions.visibilityOfElementLocated(ORDER_CARDS));
    }

    private void waitForOrdersToSettle() {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(LOADING_MESSAGE));
        wait.until(driver -> isVisible(TITLE) || isVisible(EMPTY_MESSAGE) || isVisible(ERROR_MESSAGE));
    }

    private boolean isVisible(By locator) {
        Optional<WebElement> element = driver.findElements(locator).stream().findFirst();
        return element.isPresent() && element.get().isDisplayed();
    }
}

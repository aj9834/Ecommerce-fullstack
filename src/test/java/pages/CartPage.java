package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.DemoPause;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CartPage {
    public static final By LOADING_MESSAGE = By.id("cart-loading");
    public static final By ERROR_MESSAGE = By.id("cart-error");
    public static final By TITLE = By.id("cart-title");
    public static final By EMPTY_CART = By.id("cart-empty");
    public static final By BROWSE_PRODUCTS = By.id("cart-browse-products");
    public static final By CART_ITEMS = By.cssSelector("[data-testid='cart-item']");
    public static final By ITEM_NAME = By.cssSelector("[data-testid='cart-item-name']");
    public static final By ITEM_PRICE = By.cssSelector("[class*='itemPrice']");
    public static final By ITEM_QUANTITY = By.cssSelector("[data-testid='cart-item-quantity']");
    public static final By ITEM_TOTAL = By.cssSelector("[data-testid='cart-item-total']");
    public static final By QUANTITY_DECREASE = By.cssSelector("[data-testid='cart-qty-decrease']");
    public static final By QUANTITY_INCREASE = By.cssSelector("[data-testid='cart-qty-increase']");
    public static final By REMOVE_ITEM = By.cssSelector("[data-testid='cart-item-remove']");
    public static final By SUMMARY = By.id("cart-summary");
    public static final By SUMMARY_ITEMS = By.id("cart-summary-items");
    public static final By SUMMARY_SUBTOTAL = By.id("cart-summary-subtotal");
    public static final By SUMMARY_TAX = By.id("cart-summary-tax");
    public static final By SUMMARY_TOTAL = By.id("cart-summary-total");
    public static final By CHECKOUT = By.id("cart-checkout");

    private static final Pattern INTEGER_PATTERN = Pattern.compile("\\d+");

    private final WebDriver driver;
    private final WebDriverWait wait;

    public CartPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(7));
    }

    public boolean isLoaded() {
        waitForCartToSettle();
        boolean loaded = wait.until(ExpectedConditions.visibilityOfElementLocated(TITLE)).isDisplayed()
                && (itemCount() > 0 || hasEmptyCartMessage());
        DemoPause.afterStep();
        return loaded;
    }

    public int itemCount() {
        waitForCartToSettle();
        int count = driver.findElements(CART_ITEMS).size();
        DemoPause.afterStep();
        return count;
    }

    public String firstItemName() {
        String name = firstItem().findElement(ITEM_NAME).getText();
        DemoPause.afterStep();
        return name;
    }

    public BigDecimal firstItemPrice() {
        BigDecimal price = parseMoney(firstItem().findElement(ITEM_PRICE).getText());
        DemoPause.afterStep();
        return price;
    }

    public int firstItemQuantity() {
        int quantity = quantityFrom(firstItem());
        DemoPause.afterStep();
        return quantity;
    }

    public BigDecimal firstItemTotal() {
        BigDecimal total = parseMoney(firstItem().findElement(ITEM_TOTAL).getText());
        DemoPause.afterStep();
        return total;
    }

    public void increaseFirstQuantity() {
        WebElement item = firstItem();
        int initialQuantity = quantityFrom(item);
        item.findElement(QUANTITY_INCREASE).click();
        wait.until(driver -> quantityFrom(firstItem()) == initialQuantity + 1);
        DemoPause.afterStep();
    }

    public void decreaseFirstQuantity() {
        WebElement item = firstItem();
        int initialQuantity = quantityFrom(item);
        item.findElement(QUANTITY_DECREASE).click();
        if (initialQuantity == 1) {
            wait.until(ExpectedConditions.visibilityOfElementLocated(EMPTY_CART));
        } else {
            wait.until(driver -> quantityFrom(firstItem()) == initialQuantity - 1);
        }
        DemoPause.afterStep();
    }

    public void removeFirstItem() {
        int initialCount = itemCount();
        firstItem().findElement(REMOVE_ITEM).click();
        if (initialCount == 1) {
            wait.until(ExpectedConditions.visibilityOfElementLocated(EMPTY_CART));
        } else {
            wait.until(ExpectedConditions.numberOfElementsToBeLessThan(CART_ITEMS, initialCount));
        }
        DemoPause.afterStep();
    }

    public int summaryItemCount() {
        String text = wait.until(ExpectedConditions.visibilityOfElementLocated(SUMMARY_ITEMS)).getText();
        Matcher matcher = INTEGER_PATTERN.matcher(text);
        if (!matcher.find()) {
            throw new IllegalStateException("Could not read summary item count from: " + text);
        }
        DemoPause.afterStep();
        return Integer.parseInt(matcher.group());
    }

    public BigDecimal summarySubtotal() {
        BigDecimal subtotal = parseMoney(wait.until(ExpectedConditions.visibilityOfElementLocated(SUMMARY_SUBTOTAL)).getText());
        DemoPause.afterStep();
        return subtotal;
    }

    public BigDecimal summaryTax() {
        BigDecimal tax = parseMoney(wait.until(ExpectedConditions.visibilityOfElementLocated(SUMMARY_TAX)).getText());
        DemoPause.afterStep();
        return tax;
    }

    public BigDecimal summaryTotal() {
        BigDecimal total = parseMoney(wait.until(ExpectedConditions.visibilityOfElementLocated(SUMMARY_TOTAL)).getText());
        DemoPause.afterStep();
        return total;
    }

    public boolean hasEmptyCartMessage() {
        waitForCartToSettle();
        boolean visible = isVisible(EMPTY_CART);
        DemoPause.afterStep();
        return visible;
    }

    public boolean hasBrowseProductsButton() {
        boolean visible = isVisible(BROWSE_PRODUCTS);
        DemoPause.afterStep();
        return visible;
    }

    public boolean hasCheckoutButton() {
        boolean visible = isVisible(CHECKOUT);
        DemoPause.afterStep();
        return visible;
    }

    public void proceedToCheckout() {
        wait.until(ExpectedConditions.elementToBeClickable(CHECKOUT)).click();
        DemoPause.afterStep();
    }

    private WebElement firstItem() {
        waitForCartToSettle();
        return wait.until(ExpectedConditions.visibilityOfElementLocated(CART_ITEMS));
    }

    private int quantityFrom(WebElement item) {
        return Integer.parseInt(item.findElement(ITEM_QUANTITY).getText().trim());
    }

    private BigDecimal parseMoney(String text) {
        String amount = text.replaceAll("[^0-9.]", "");
        if (amount.isBlank()) {
            throw new IllegalStateException("Could not read money amount from: " + text);
        }
        return new BigDecimal(amount);
    }

    private void waitForCartToSettle() {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(LOADING_MESSAGE));
        wait.until(driver -> isVisible(TITLE) || isVisible(EMPTY_CART) || isVisible(ERROR_MESSAGE));
    }

    private boolean isVisible(By locator) {
        Optional<WebElement> element = driver.findElements(locator).stream().findFirst();
        return element.isPresent() && element.get().isDisplayed();
    }
}

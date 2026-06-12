package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.DemoPause;

import java.time.Duration;

public class ProductDetailPage {
    public static final By LOADING_MESSAGE = By.id("product-detail-loading");
    public static final By ERROR_MESSAGE = By.id("product-detail-error");
    public static final By BACK_BUTTON = By.id("product-detail-back");
    public static final By NAME = By.id("product-detail-name");
    public static final By PRICE = By.id("product-detail-price");
    public static final By CATEGORY = By.cssSelector("[data-testid='product-detail-category']");
    public static final By DESCRIPTION = By.cssSelector("[data-testid='product-detail-description']");
    public static final By STOCK = By.cssSelector("[data-testid='product-detail-stock']");
    public static final By QUANTITY_DECREASE = By.id("product-detail-quantity-decrease");
    public static final By QUANTITY_INCREASE = By.id("product-detail-quantity-increase");
    public static final By QUANTITY_VALUE = By.id("product-detail-quantity-value");
    public static final By ADD_TO_CART = By.id("product-detail-add-to-cart");

    private final WebDriver driver;
    private final WebDriverWait wait;

    public ProductDetailPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(5));
    }

    public boolean isLoaded() {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(LOADING_MESSAGE));
        boolean loaded = wait.until(ExpectedConditions.visibilityOfElementLocated(NAME)).isDisplayed()
                && driver.findElement(PRICE).isDisplayed()
                && driver.findElement(DESCRIPTION).isDisplayed()
                && driver.findElement(STOCK).isDisplayed()
                && driver.findElement(BACK_BUTTON).isDisplayed();
        DemoPause.afterStep();
        return loaded;
    }

    public String productName() {
        String name = wait.until(ExpectedConditions.visibilityOfElementLocated(NAME)).getText();
        DemoPause.afterStep();
        return name;
    }

    public String productPrice() {
        String price = wait.until(ExpectedConditions.visibilityOfElementLocated(PRICE)).getText();
        DemoPause.afterStep();
        return price;
    }

    public String productCategory() {
        String category = wait.until(ExpectedConditions.visibilityOfElementLocated(CATEGORY)).getText();
        DemoPause.afterStep();
        return category;
    }

    public String productDescription() {
        String description = wait.until(ExpectedConditions.visibilityOfElementLocated(DESCRIPTION)).getText();
        DemoPause.afterStep();
        return description;
    }

    public String stockText() {
        String stock = wait.until(ExpectedConditions.visibilityOfElementLocated(STOCK)).getText();
        DemoPause.afterStep();
        return stock;
    }

    public boolean canAddToCart() {
        boolean enabled = wait.until(ExpectedConditions.visibilityOfElementLocated(ADD_TO_CART)).isEnabled();
        DemoPause.afterStep();
        return enabled;
    }

    public boolean hasQuantitySelector() {
        boolean visible = !driver.findElements(QUANTITY_VALUE).isEmpty()
                && driver.findElement(QUANTITY_VALUE).isDisplayed();
        DemoPause.afterStep();
        return visible;
    }

    public int quantity() {
        String quantityText = wait.until(ExpectedConditions.visibilityOfElementLocated(QUANTITY_VALUE)).getText();
        DemoPause.afterStep();
        return Integer.parseInt(quantityText);
    }

    public void increaseQuantity() {
        wait.until(ExpectedConditions.elementToBeClickable(QUANTITY_INCREASE)).click();
        DemoPause.afterStep();
    }

    public void backToProducts() {
        wait.until(ExpectedConditions.elementToBeClickable(BACK_BUTTON)).click();
        DemoPause.afterStep();
    }
}

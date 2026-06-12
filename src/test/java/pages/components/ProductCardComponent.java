package pages.components;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class ProductCardComponent {
    private static final By NAME = By.cssSelector("[data-testid='product-name']");
    private static final By PRICE = By.cssSelector("[data-testid='product-price']");
    private static final By CATEGORY = By.cssSelector("[data-testid='product-category']");
    private static final By STOCK = By.cssSelector("[data-testid='product-stock']");
    private static final By ADD_TO_CART = By.cssSelector("[data-testid='add-to-cart']");

    private final WebElement root;

    public ProductCardComponent(WebElement root) {
        this.root = root;
    }

    public void open() {
        root.click();
    }

    public String name() {
        return text(NAME);
    }

    public String price() {
        return text(PRICE);
    }

    public String category() {
        return text(CATEGORY);
    }

    public String stockText() {
        return text(STOCK);
    }

    public String addToCartButtonText() {
        return text(ADD_TO_CART);
    }

    public boolean isAddToCartEnabled() {
        return root.findElement(ADD_TO_CART).isEnabled();
    }

    private String text(By locator) {
        return root.findElement(locator).getText().trim();
    }
}

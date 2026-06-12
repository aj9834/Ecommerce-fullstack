package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import pages.components.ProductCardComponent;
import utils.DemoPause;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ProductPage {
    public static final By TITLE = By.id("products-title");
    public static final By GRID = By.id("product-grid");
    public static final By PRODUCT_CARDS = By.cssSelector("[data-testid='product-card']");
    public static final By PRODUCT_NAMES = By.cssSelector("[data-testid='product-name']");
    public static final By PRODUCT_PRICES = By.cssSelector("[data-testid='product-price']");
    public static final By PRODUCT_STOCK = By.cssSelector("[data-testid='product-stock']");
    public static final By SEARCH_INPUT = By.id("product-search");
    public static final By CATEGORY_SELECT = By.id("product-category");
    public static final By SEARCH_BUTTON = By.id("product-search-submit");
    public static final By RESET_BUTTON = By.id("product-search-reset");
    public static final By LOADING_MESSAGE = By.id("products-loading");
    public static final By ERROR_MESSAGE = By.id("products-error");
    public static final By EMPTY_MESSAGE = By.id("products-empty");

    private final WebDriver driver;
    private final WebDriverWait wait;

    public ProductPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(7));
    }

    public boolean isLoaded() {
        boolean loaded = wait.until(ExpectedConditions.visibilityOfElementLocated(TITLE)).isDisplayed();
        waitForResultsToSettle();
        DemoPause.afterStep();
        return loaded;
    }

    public void search(String keyword) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(SEARCH_INPUT)).clear();
        driver.findElement(SEARCH_INPUT).sendKeys(keyword);
        wait.until(ExpectedConditions.elementToBeClickable(SEARCH_BUTTON)).click();
        waitForResultsToSettle();
        DemoPause.afterStep();
    }

    public void filterByCategory(String category) {
        new Select(wait.until(ExpectedConditions.visibilityOfElementLocated(CATEGORY_SELECT)))
                .selectByVisibleText(category);
        wait.until(ExpectedConditions.elementToBeClickable(SEARCH_BUTTON)).click();
        waitForResultsToSettle();
        DemoPause.afterStep();
    }

    public void resetFilters() {
        wait.until(ExpectedConditions.elementToBeClickable(RESET_BUTTON)).click();
        waitForResultsToSettle();
        DemoPause.afterStep();
    }

    public int productCount() {
        waitForResultsToSettle();
        wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(PRODUCT_CARDS, 0));
        List<WebElement> cards = driver.findElements(PRODUCT_CARDS);
        DemoPause.afterStep();
        return cards.size();
    }

    public String firstProductName() {
        String name = firstProductCard().name();
        DemoPause.afterStep();
        return name;
    }

    public String firstProductPrice() {
        String price = firstProductCard().price();
        DemoPause.afterStep();
        return price;
    }

    public String firstProductStockText() {
        String stockText = firstProductCard().stockText();
        DemoPause.afterStep();
        return stockText;
    }

    public String firstProductCategory() {
        String category = firstProductCard().category();
        DemoPause.afterStep();
        return category;
    }

    public List<String> visibleProductNames() {
        List<String> names = productCards().stream()
                .map(ProductCardComponent::name)
                .collect(Collectors.toList());
        DemoPause.afterStep();
        return names;
    }

    public List<String> visibleProductCategories() {
        List<String> categories = productCards().stream()
                .map(ProductCardComponent::category)
                .collect(Collectors.toList());
        DemoPause.afterStep();
        return categories;
    }

    public List<String> availableCategories() {
        List<String> categories = new Select(wait.until(ExpectedConditions.visibilityOfElementLocated(CATEGORY_SELECT)))
                .getOptions()
                .stream()
                .map(WebElement::getText)
                .map(String::trim)
                .filter(option -> !option.equalsIgnoreCase("All Categories"))
                .filter(option -> !option.isBlank())
                .collect(Collectors.toList());
        DemoPause.afterStep();
        return categories;
    }

    public boolean hasNoResultsMessage() {
        waitForResultsToSettle();
        boolean visible = isVisible(EMPTY_MESSAGE);
        DemoPause.afterStep();
        return visible;
    }

    public boolean isLoadingMessageGone() {
        boolean gone = wait.until(ExpectedConditions.invisibilityOfElementLocated(LOADING_MESSAGE));
        DemoPause.afterStep();
        return gone;
    }

    public void openFirstProduct() {
        firstProductCard().open();
        DemoPause.afterStep();
    }

    private ProductCardComponent firstProductCard() {
        waitForResultsToSettle();
        WebElement card = wait.until(ExpectedConditions.visibilityOfElementLocated(PRODUCT_CARDS));
        return new ProductCardComponent(card);
    }

    private List<ProductCardComponent> productCards() {
        waitForResultsToSettle();
        wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(PRODUCT_CARDS, 0));
        return driver.findElements(PRODUCT_CARDS)
                .stream()
                .map(ProductCardComponent::new)
                .collect(Collectors.toList());
    }

    private void waitForResultsToSettle() {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(LOADING_MESSAGE));
        wait.until(driver -> isVisible(GRID) || isVisible(EMPTY_MESSAGE) || isVisible(ERROR_MESSAGE));
    }

    private boolean isVisible(By locator) {
        Optional<WebElement> element = driver.findElements(locator).stream().findFirst();
        return element.isPresent() && element.get().isDisplayed();
    }
}

package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.DemoPause;

import java.time.Duration;

public class NavigationPage {
    public static final By LOGO = By.cssSelector("#nav-logo, [class*='logo']");
    public static final By HOME = By.xpath("//button[@id='nav-home' or normalize-space()='Home']");
    public static final By PRODUCTS = By.xpath("//button[@id='nav-products' or normalize-space()='Products']");
    public static final By CART = By.xpath("//button[@id='nav-cart' or contains(normalize-space(), 'Cart')]");
    public static final By ORDERS = By.xpath("//button[@id='nav-orders' or normalize-space()='Orders']");
    public static final By PROFILE = By.xpath("//button[@id='nav-profile' or normalize-space()='Profile']");
    public static final By USER_NAME = By.cssSelector("#nav-user, [class*='userName']");
    public static final By LOGOUT = By.xpath("//button[@id='logout-button' or normalize-space()='Logout']");

    private final WebDriver driver;
    private final WebDriverWait wait;

    public NavigationPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(5));
    }

    public void goHome() {
        click(HOME);
    }

    public void goToProducts() {
        click(PRODUCTS);
    }

    public void goToCart() {
        click(CART);
    }

    public void goToOrders() {
        click(ORDERS);
    }

    public void goToProfile() {
        click(PROFILE);
    }

    public void logout() {
        click(LOGOUT);
    }

    public boolean isLoaded() {
        boolean loaded = wait.until(ExpectedConditions.visibilityOfElementLocated(LOGO)).isDisplayed()
                && driver.findElement(LOGOUT).isDisplayed();
        DemoPause.afterStep();
        return loaded;
    }

    private void click(By locator) {
        wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
        DemoPause.afterStep();
    }
}

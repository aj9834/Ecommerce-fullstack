package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.DemoPause;

import java.time.Duration;

public class RegisterPage {
    public static final By NAME_FIELD_BY_ID = By.cssSelector("#register-name, input[name='name']");
    public static final By EMAIL_FIELD_BY_CSS = By.cssSelector("#register-email, input[name='email']");
    public static final By PASSWORD_FIELD_BY_ID = By.cssSelector("#register-password, input[name='password']");
    public static final By CREATE_ACCOUNT_BUTTON_BY_XPATH =
            By.xpath("//button[@id='register-submit' or @type='submit']");
    public static final By SIGN_IN_LINK = By.linkText("Sign in");
    public static final By ERROR_BANNER = By.id("register-error");

    private final WebDriver driver;
    private final WebDriverWait wait;

    public RegisterPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void enterName(String name) {
        type(NAME_FIELD_BY_ID, name);
    }

    public void enterEmail(String email) {
        type(EMAIL_FIELD_BY_CSS, email);
    }

    public void enterPassword(String password) {
        type(PASSWORD_FIELD_BY_ID, password);
    }

    public void clickCreateAccount() {
        wait.until(ExpectedConditions.elementToBeClickable(CREATE_ACCOUNT_BUTTON_BY_XPATH)).click();
        DemoPause.afterStep();
    }

    public void registerAs(String name, String email, String password) {
        enterName(name);
        enterEmail(email);
        enterPassword(password);
        clickCreateAccount();
    }

    public String getEmailValidationMessage() {
        return driver.findElement(EMAIL_FIELD_BY_CSS).getAttribute("validationMessage");
    }

    public String getPasswordValidationMessage() {
        return driver.findElement(PASSWORD_FIELD_BY_ID).getAttribute("validationMessage");
    }

    public boolean isLoaded() {
        boolean loaded = wait.until(ExpectedConditions.visibilityOfElementLocated(NAME_FIELD_BY_ID)).isDisplayed()
                && driver.findElement(EMAIL_FIELD_BY_CSS).isDisplayed()
                && driver.findElement(PASSWORD_FIELD_BY_ID).isDisplayed();
        DemoPause.afterStep();
        return loaded;
    }

    private void type(By locator, String value) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        element.clear();
        element.sendKeys(value);
        DemoPause.afterStep();
    }
}

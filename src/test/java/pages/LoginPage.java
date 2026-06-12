package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.DemoPause;

import java.time.Duration;

public class LoginPage {
    public static final By EMAIL_FIELD_BY_ID = By.cssSelector("#login-email, input[name='email']");
    public static final By PASSWORD_FIELD_BY_CSS = By.cssSelector("#login-password, input[name='password']");
    public static final By CONTINUE_BUTTON_BY_XPATH = By.xpath("//button[@id='login-submit' or @type='submit']");
    public static final By ERROR_BANNER = By.cssSelector("#login-error, [class*='errorBanner']");
    public static final By CREATE_ACCOUNT_LINK = By.linkText("Create an account");
    public static final By FORGOT_PASSWORD_LINK = By.linkText("Forgot password?");

    private final WebDriver driver;
    private final WebDriverWait wait;

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void enterEmail(String email) {
        WebElement emailField = wait.until(ExpectedConditions.visibilityOfElementLocated(EMAIL_FIELD_BY_ID));
        emailField.clear();
        emailField.sendKeys(email);
        DemoPause.afterStep();
    }

    public void enterPassword(String password) {
        WebElement passwordField = wait.until(ExpectedConditions.visibilityOfElementLocated(PASSWORD_FIELD_BY_CSS));
        passwordField.clear();
        passwordField.sendKeys(password);
        DemoPause.afterStep();
    }

    public void clickContinue() {
        wait.until(ExpectedConditions.elementToBeClickable(CONTINUE_BUTTON_BY_XPATH)).click();
        DemoPause.afterStep();
    }

    public void loginAs(String email, String password) {
        enterEmail(email);
        enterPassword(password);
        clickContinue();
    }

    public String getErrorMessage() {
        String message = wait.until(ExpectedConditions.visibilityOfElementLocated(ERROR_BANNER)).getText();
        DemoPause.afterStep();
        return message;
    }

    public String getEmailValidationMessage() {
        return driver.findElement(EMAIL_FIELD_BY_ID).getAttribute("validationMessage");
    }

    public boolean isLoaded() {
        boolean loaded = wait.until(ExpectedConditions.visibilityOfElementLocated(EMAIL_FIELD_BY_ID)).isDisplayed()
                && driver.findElement(PASSWORD_FIELD_BY_CSS).isDisplayed()
                && driver.findElement(CONTINUE_BUTTON_BY_XPATH).isDisplayed();
        DemoPause.afterStep();
        return loaded;
    }
}

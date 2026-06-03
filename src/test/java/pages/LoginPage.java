package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LoginPage {
    private WebDriver driver;

    // 1. Locators: Define how to find the elements on the page
    private By emailField = By.cssSelector("input[type='email']");
    private By passwordField = By.cssSelector("input[type='password']");
    private By continueButton = By.xpath("//button[contains(text(), 'Continue')]");

    // 2. Constructor: Pass the driver from the test to this page
    public LoginPage(WebDriver driver) {
        this.driver = driver;
    }

    // 3. Actions: Methods to interact with the page
    public void enterEmail(String email) {
        driver.findElement(emailField).sendKeys(email);
    }

    public void enterPassword(String password) {
        driver.findElement(passwordField).sendKeys(password);
    }

    public void clickContinue() {
        driver.findElement(continueButton).click();
    }
}
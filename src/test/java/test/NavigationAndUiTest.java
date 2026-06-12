package test;

import base.BaseTest;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.LoginPage;
import pages.NavigationPage;
import pages.RegisterPage;

public class NavigationAndUiTest extends BaseTest {

    @Test(description = "T066: Identify locators by id, css, and xpath")
    public void identifyLocatorsUsingIdCssAndXpath() {
        openApp("/login");
        clearBrowserStorage();

        Assert.assertTrue(driver.findElement(LoginPage.EMAIL_FIELD_BY_ID).isDisplayed(), "Email field should be found by id");
        Assert.assertTrue(driver.findElement(LoginPage.PASSWORD_FIELD_BY_CSS).isDisplayed(), "Password field should be found by css");
        Assert.assertTrue(driver.findElement(LoginPage.CONTINUE_BUTTON_BY_XPATH).isDisplayed(), "Continue button should be found by xpath");
    }

    @Test(description = "T067: Implement basic navigation tests for auth pages")
    public void navigateBetweenPublicAuthPages() {
        openApp("/login");
        clearBrowserStorage();

        click(LoginPage.CREATE_ACCOUNT_LINK);
        waitForUrlContains("/register");
        Assert.assertTrue(new RegisterPage(driver).isLoaded(), "Register page should load after clicking Create an account");

        click(RegisterPage.SIGN_IN_LINK);
        waitForUrlContains("/login");
        Assert.assertTrue(new LoginPage(driver).isLoaded(), "Login page should load after clicking Sign in");

        click(LoginPage.FORGOT_PASSWORD_LINK);
        waitForUrlContains("/forgot-password");
        Assert.assertTrue(driver.findElement(By.cssSelector("input[name='email']")).isDisplayed(), "Forgot password email field should be visible");
    }

    @Test(description = "T067: Validate protected navigation UI elements after login")
    public void navigateProtectedMenuItemsAfterLogin() {
        requireBackend();
        openApp("/register");
        clearBrowserStorage();

        RegisterPage registerPage = new RegisterPage(driver);
        registerPage.registerAs("Selenium Navigation", uniqueEmail("selenium.nav"), "Password123");
        waitForUrlContains("/dashboard");

        NavigationPage navigation = new NavigationPage(driver);
        Assert.assertTrue(navigation.isLoaded(), "Navigation bar should be visible after login");

        navigation.goToProducts();
        waitForUrlContains("/products");

        navigation.goToCart();
        waitForUrlContains("/cart");

        navigation.goToOrders();
        waitForUrlContains("/orders");

        navigation.goToProfile();
        waitForUrlContains("/profile");
    }

    @Test(description = "T068: Implement implicit and explicit waits")
    public void waitForLoginUiWithExplicitWait() {
        openApp("/login");
        clearBrowserStorage();

        Assert.assertTrue(new LoginPage(driver).isLoaded(), "Login UI should be visible using explicit waits");
        Assert.assertTrue(driver.manage().timeouts().getImplicitWaitTimeout().getSeconds() >= 10,
                "Implicit wait should be configured in BaseTest");
    }

    @Test(description = "T069: Handle alerts and popups")
    public void handleBrowserAlertPopup() {
        openApp("/login");
        clearBrowserStorage();

        ((JavascriptExecutor) driver).executeScript("window.alert('Selenium popup test');");
        Assert.assertTrue(wait.until(ExpectedConditions.alertIsPresent()).getText().contains("Selenium popup test"));
        Assert.assertTrue(acceptAlertIfPresent(), "Alert popup should be accepted");
    }
}

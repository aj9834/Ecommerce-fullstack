package test;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.LoginPage;
import pages.NavigationPage;
import utils.LoginUtils;

public class LoginTest extends BaseTest {

    @Test(description = "T072, T075, T078, T079: Automate login with Page Object Model and reusable login utilities")
    public void loginWithValidCredentialsAndLogout() {
        String email = "selenium.login@example.com";
        loginUtils.loginWithDemoSession("Selenium Login", email);

        NavigationPage navigation = new NavigationPage(driver);
        Assert.assertTrue(navigation.isLoaded(), "Valid user should see navigation after login");
        Assert.assertTrue(driver.getCurrentUrl().contains("/dashboard"), "Valid user should be redirected to dashboard");

        navigation.logout();
        waitForUrlContains("/login");
    }

    @Test(description = "T073, T074, T075: Automate invalid login and validate error message assertions")
    public void invalidLoginShowsErrorMessage() {
        openApp("/login");
        clearBrowserStorage();

        LoginPage loginPage = new LoginPage(driver);
        loginPage.loginAs("missing.user@example.com", "wrong-password");

        String errorMessage = loginPage.getErrorMessage();
        Assert.assertFalse(errorMessage.isBlank(), "Invalid login should show an error message");
        Assert.assertTrue(
                errorMessage.toLowerCase().contains("bad")
                        || errorMessage.toLowerCase().contains("invalid")
                        || errorMessage.toLowerCase().contains("not found"),
                "Error message should explain that credentials are invalid"
        );
        Assert.assertTrue(driver.getCurrentUrl().contains("/login"), "Invalid user should remain on login page");
    }

    @Test(description = "T071: Validate login input fields")
    public void validateLoginInputFields() {
        openApp("/login");
        clearBrowserStorage();

        LoginPage loginPage = new LoginPage(driver);
        loginPage.enterEmail("invalid-email");
        loginPage.enterPassword("Password123");
        loginPage.clickContinue();

        Assert.assertFalse(loginPage.getEmailValidationMessage().isBlank(), "Invalid email should show browser validation");
    }

    @Test(description = "Task 36, US014: Automate logout functionality and validate session handling")
    public void logoutClearsSessionAndProtectsDashboard() {
        loginUtils.loginWithDemoSession("Selenium Logout", "selenium.logout@example.com");

        Assert.assertEquals(loginUtils.storedToken(), LoginUtils.DEMO_TOKEN, "Demo login should create a token");
        Assert.assertTrue(loginUtils.hasStoredUser(), "Demo login should store the active user");

        new NavigationPage(driver).logout();
        waitForUrlContains("/login");

        Assert.assertFalse(loginUtils.hasStoredToken(), "Logout should remove token from local storage");
        Assert.assertFalse(loginUtils.hasStoredUser(), "Logout should remove user from local storage");

        openApp("/dashboard");
        waitForUrlContains("/login");
        Assert.assertTrue(new LoginPage(driver).isLoaded(), "Logged out users should be redirected to login");
    }
}

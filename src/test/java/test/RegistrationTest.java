package test;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.NavigationPage;
import pages.RegisterPage;

public class RegistrationTest extends BaseTest {

    @Test(description = "T070: Automate user registration flow")
    public void registerNewUserSuccessfully() {
        requireBackend();
        openApp("/register");
        clearBrowserStorage();

        RegisterPage registerPage = new RegisterPage(driver);
        registerPage.registerAs("Selenium User", uniqueEmail("selenium.user"), "Password123");

        waitForUrlContains("/dashboard");
        Assert.assertTrue(new NavigationPage(driver).isLoaded(), "Newly registered user should land on dashboard");
    }

    @Test(description = "T071: Validate input fields")
    public void validateRegistrationInputFields() {
        openApp("/register");
        clearBrowserStorage();

        RegisterPage registerPage = new RegisterPage(driver);
        Assert.assertTrue(registerPage.isLoaded(), "Registration fields should be visible");

        registerPage.enterName("Selenium Validation");
        registerPage.enterEmail("not-an-email");
        registerPage.enterPassword("Password123");
        registerPage.clickCreateAccount();
        Assert.assertFalse(registerPage.getEmailValidationMessage().isBlank(), "Invalid email should show browser validation");

        registerPage.enterEmail(uniqueEmail("selenium.validation"));
        registerPage.enterPassword("123");
        registerPage.clickCreateAccount();
        Assert.assertFalse(registerPage.getPasswordValidationMessage().isBlank(), "Short password should show browser validation");
    }

    @Test(description = "T074: Validate registration error messages")
    public void duplicateRegistrationShowsErrorMessage() {
        requireBackend();
        String email = uniqueEmail("selenium.duplicate");

        openApp("/register");
        clearBrowserStorage();
        RegisterPage registerPage = new RegisterPage(driver);
        registerPage.registerAs("Duplicate User", email, "Password123");
        waitForUrlContains("/dashboard");

        openApp("/register");
        clearBrowserStorage();
        registerPage = new RegisterPage(driver);
        registerPage.registerAs("Duplicate User", email, "Password123");

        Assert.assertTrue(visibleText(RegisterPage.ERROR_BANNER).contains("Email already in use"),
                "Duplicate registration should show a visible error message");
    }
}

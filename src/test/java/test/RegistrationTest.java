package test;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pages.NavigationPage;
import pages.RegisterPage;
import utils.ExcelTestData;

import java.util.Map;

public class RegistrationTest extends BaseTest {

    @DataProvider(name = "validRegistrationData")
    public Object[][] validRegistrationData() {
        return ExcelTestData.rows("RegistrationData", "REGISTER_VALID_NEW_USER");
    }

    @DataProvider(name = "registrationValidationData")
    public Object[][] registrationValidationData() {
        return ExcelTestData.rows(
                "RegistrationData",
                "REGISTER_INVALID_EMAIL_FORMAT",
                "REGISTER_SHORT_PASSWORD"
        );
    }

    @DataProvider(name = "duplicateRegistrationData")
    public Object[][] duplicateRegistrationData() {
        return ExcelTestData.rows("RegistrationData", "REGISTER_DUPLICATE_EMAIL");
    }

    @Test(dataProvider = "validRegistrationData", description = "T070: Automate user registration flow")
    public void registerNewUserSuccessfully(Map<String, String> data) {
        requireBackendIfNeeded(data);
        openApp("/register");
        clearBrowserStorage();

        RegisterPage registerPage = new RegisterPage(driver);
        registerPage.registerAs(
                ExcelTestData.value(data, "name"),
                ExcelTestData.value(data, "email"),
                ExcelTestData.value(data, "password")
        );

        waitForUrlContains(ExcelTestData.value(data, "expectedUrlContains"));
        Assert.assertTrue(new NavigationPage(driver).isLoaded(), "Newly registered user should land on dashboard");
    }

    @Test(dataProvider = "registrationValidationData", description = "T071: Validate input fields")
    public void validateRegistrationInputFields(Map<String, String> data) {
        openApp("/register");
        clearBrowserStorage();

        RegisterPage registerPage = new RegisterPage(driver);
        Assert.assertTrue(registerPage.isLoaded(), "Registration fields should be visible");

        registerPage.enterName(ExcelTestData.value(data, "name"));
        registerPage.enterEmail(ExcelTestData.value(data, "email"));
        registerPage.enterPassword(ExcelTestData.value(data, "password"));
        registerPage.clickCreateAccount();

        String validationField = ExcelTestData.value(data, "validationField");
        if ("email".equals(validationField)) {
            Assert.assertFalse(registerPage.getEmailValidationMessage().isBlank(), "Invalid email should show browser validation");
        } else if ("password".equals(validationField)) {
            Assert.assertFalse(registerPage.getPasswordValidationMessage().isBlank(), "Short password should show browser validation");
        } else {
            Assert.fail("Unsupported registration validation field in Excel data: " + validationField);
        }
    }

    @Test(dataProvider = "duplicateRegistrationData", description = "T074: Validate registration error messages")
    public void duplicateRegistrationShowsErrorMessage(Map<String, String> data) {
        requireBackendIfNeeded(data);
        String email = ExcelTestData.value(data, "email");

        openApp("/register");
        clearBrowserStorage();
        RegisterPage registerPage = new RegisterPage(driver);
        registerPage.registerAs(
                ExcelTestData.value(data, "name"),
                email,
                ExcelTestData.value(data, "password")
        );
        waitForUrlContains(ExcelTestData.value(data, "setupSuccessUrlContains"));

        openApp("/register");
        clearBrowserStorage();
        registerPage = new RegisterPage(driver);
        registerPage.registerAs(
                ExcelTestData.value(data, "name"),
                email,
                ExcelTestData.value(data, "password")
        );

        Assert.assertTrue(visibleText(RegisterPage.ERROR_BANNER).contains(ExcelTestData.value(data, "expectedMessageContains")),
                "Duplicate registration should show a visible error message");
    }

    private void requireBackendIfNeeded(Map<String, String> data) {
        if (ExcelTestData.isTrue(data, "requiresBackend")) {
            requireBackend();
        }
    }
}

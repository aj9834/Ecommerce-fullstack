package test;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pages.LoginPage;
import pages.NavigationPage;
import utils.ExcelTestData;
import utils.LoginUtils;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;

public class LoginTest extends BaseTest {

    @DataProvider(name = "validLoginData")
    public Object[][] validLoginData() {
        return ExcelTestData.rows("LoginData", "LOGIN_VALID_DEMO");
    }

    @DataProvider(name = "invalidLoginData")
    public Object[][] invalidLoginData() {
        return ExcelTestData.rows("LoginData", "LOGIN_INVALID_CREDENTIALS");
    }

    @DataProvider(name = "loginValidationData")
    public Object[][] loginValidationData() {
        return ExcelTestData.rows("LoginData", "LOGIN_INVALID_EMAIL_FORMAT");
    }

    @DataProvider(name = "logoutData")
    public Object[][] logoutData() {
        return ExcelTestData.rows("LoginData", "LOGIN_LOGOUT_SESSION");
    }

    @Test(
            dataProvider = "validLoginData",
            description = "T072, T075, T078, T079: Automate login with Page Object Model and reusable login utilities"
    )
    public void loginWithValidCredentialsAndLogout(Map<String, String> data) {
        loginUtils.loginWithDemoSession(
                ExcelTestData.value(data, "name"),
                ExcelTestData.value(data, "email")
        );

        NavigationPage navigation = new NavigationPage(driver);
        Assert.assertTrue(navigation.isLoaded(), "Valid user should see navigation after login");
        Assert.assertTrue(
                driver.getCurrentUrl().contains(ExcelTestData.value(data, "expectedUrlContains")),
                ExcelTestData.description(data)
        );

        navigation.logout();
        waitForUrlContains(ExcelTestData.value(data, "postActionUrlContains"));
    }

    @Test(dataProvider = "invalidLoginData", description = "T073, T074, T075: Automate invalid login and validate error message assertions")
    public void invalidLoginShowsErrorMessage(Map<String, String> data) {
        openApp("/login");
        clearBrowserStorage();

        LoginPage loginPage = new LoginPage(driver);
        loginPage.loginAs(
                ExcelTestData.value(data, "email"),
                ExcelTestData.value(data, "password")
        );

        String errorMessage = loginPage.getErrorMessage();
        Assert.assertFalse(errorMessage.isBlank(), "Invalid login should show an error message");
        assertMessageContainsAny(errorMessage, ExcelTestData.value(data, "expectedMessageContains"));
        Assert.assertTrue(
                driver.getCurrentUrl().contains(ExcelTestData.value(data, "expectedUrlContains")),
                "Invalid user should remain on login page"
        );
    }

    @Test(dataProvider = "loginValidationData", description = "T071: Validate login input fields")
    public void validateLoginInputFields(Map<String, String> data) {
        openApp("/login");
        clearBrowserStorage();

        LoginPage loginPage = new LoginPage(driver);
        loginPage.enterEmail(ExcelTestData.value(data, "email"));
        loginPage.enterPassword(ExcelTestData.value(data, "password"));
        loginPage.clickContinue();

        Assert.assertEquals(ExcelTestData.value(data, "validationField"), "email", "Login validation row should target email");
        Assert.assertFalse(loginPage.getEmailValidationMessage().isBlank(), "Invalid email should show browser validation");
    }

    @Test(dataProvider = "logoutData", description = "Task 36, US014: Automate logout functionality and validate session handling")
    public void logoutClearsSessionAndProtectsDashboard(Map<String, String> data) {
        loginUtils.loginWithDemoSession(
                ExcelTestData.value(data, "name"),
                ExcelTestData.value(data, "email")
        );

        Assert.assertEquals(loginUtils.storedToken(), LoginUtils.DEMO_TOKEN, "Demo login should create a token");
        Assert.assertTrue(loginUtils.hasStoredUser(), "Demo login should store the active user");

        new NavigationPage(driver).logout();
        waitForUrlContains(ExcelTestData.value(data, "expectedUrlContains"));

        Assert.assertFalse(loginUtils.hasStoredToken(), "Logout should remove token from local storage");
        Assert.assertFalse(loginUtils.hasStoredUser(), "Logout should remove user from local storage");

        openApp("/dashboard");
        waitForUrlContains(ExcelTestData.value(data, "expectedUrlContains"));
        Assert.assertTrue(new LoginPage(driver).isLoaded(), "Logged out users should be redirected to login");
    }

    private void assertMessageContainsAny(String actualMessage, String expectedMessageContains) {
        String actual = actualMessage.toLowerCase(Locale.ROOT);
        boolean matched = Arrays.stream(expectedMessageContains.split("\\|"))
                .map(String::trim)
                .filter(expected -> !expected.isBlank())
                .anyMatch(expected -> actual.contains(expected.toLowerCase(Locale.ROOT)));

        Assert.assertTrue(matched, "Error message should match expected Excel data. Actual: " + actualMessage);
    }
}

package test;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.LoginPage;

public class LoginTest extends BaseTest {

    @Test
    public void testSuccessfulLogin() {
        // 1. Navigate to your local Vite application
        driver.get("http://localhost:5173/login");

        // 2. Initialize the Page Object
        LoginPage loginPage = new LoginPage(driver);

        // 3. Perform actions
        loginPage.enterEmail("ashish.joshi.ac11@gmail.com");
        loginPage.enterPassword("ash1432");
        loginPage.clickContinue();

        // 4. Add an assertion to verify the login worked
        // For example, wait for the URL to change to the dashboard,
        // or check if a specific element like a "Logout" button appears.
        // Assert.assertTrue(driver.getCurrentUrl().contains("/dashboard"));
    }
}
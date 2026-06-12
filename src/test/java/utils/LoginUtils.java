package utils;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pages.LoginPage;

import java.time.Duration;

public class LoginUtils {
    public static final String DEMO_TOKEN = "demo-video-token";

    private final WebDriver driver;
    private final WebDriverWait wait;
    private final String frontendUrl;

    public LoginUtils(WebDriver driver, String frontendUrl) {
        this.driver = driver;
        this.frontendUrl = frontendUrl;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void openLoginPage() {
        open("/login");
    }

    public void loginWithCredentials(String email, String password) {
        openLoginPage();
        new LoginPage(driver).loginAs(email, password);
        wait.until(ExpectedConditions.urlContains("/dashboard"));
        DemoPause.afterStep();
    }

    public void loginWithDemoSession(String name, String email) {
        openLoginPage();
        clearSession();
        createDemoSession(name, email);
        open("/dashboard");
        wait.until(ExpectedConditions.urlContains("/dashboard"));
        DemoPause.afterStep();
    }

    public void createDemoSession(String name, String email) {
        ((JavascriptExecutor) driver).executeScript(
                "localStorage.setItem('token', arguments[0]);"
                        + "localStorage.setItem('user', JSON.stringify({"
                        + "name: arguments[1], email: arguments[2], role: 'USER'"
                        + "}));",
                DEMO_TOKEN,
                name,
                email
        );
        DemoPause.afterStep();
    }

    public void clearSession() {
        ((JavascriptExecutor) driver).executeScript("localStorage.clear(); sessionStorage.clear();");
        DemoPause.afterStep();
    }

    public boolean hasStoredToken() {
        return Boolean.TRUE.equals(((JavascriptExecutor) driver)
                .executeScript("return Boolean(localStorage.getItem('token'));"));
    }

    public boolean hasStoredUser() {
        return Boolean.TRUE.equals(((JavascriptExecutor) driver)
                .executeScript("return Boolean(localStorage.getItem('user'));"));
    }

    public String storedToken() {
        Object token = ((JavascriptExecutor) driver).executeScript("return localStorage.getItem('token');");
        return token == null ? "" : token.toString();
    }

    private void open(String path) {
        driver.get(frontendUrl + (path.startsWith("/") ? path : "/" + path));
        DemoPause.afterStep();
    }
}

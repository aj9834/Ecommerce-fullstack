package base;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.SkipException;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import utils.DemoPause;
import utils.LoginUtils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class BaseTest {

    protected static final String FRONTEND_URL = System.getProperty("frontend.url", "http://localhost:5173");
    protected static final String BACKEND_URL = System.getProperty("backend.url", "http://localhost:8081");

    protected WebDriver driver;
    protected WebDriverWait wait;
    protected LoginUtils loginUtils;

    @BeforeMethod
    public void setUp() {
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        if (Boolean.getBoolean("headless")) {
            options.addArguments("--headless=new");
        }
        options.addArguments("--remote-allow-origins=*");

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(4));

        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(4));
        driver.get(FRONTEND_URL + "/login");
        loginUtils = new LoginUtils(driver, FRONTEND_URL);
        DemoPause.afterStep();
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            DemoPause.beforeBrowserClose();
            driver.quit();
        }
    }

    protected void openApp(String path) {
        String targetUrl = FRONTEND_URL + (path.startsWith("/") ? path : "/" + path);
        driver.get(targetUrl);
        DemoPause.afterStep();

    }

    protected void requireBackend() {
        if (!isReachable(BACKEND_URL + "/api/auth/login")) {
            DemoPause.afterStep();
            throw new SkipException("Backend is not running at " + BACKEND_URL);
        }
    }

    protected void createDemoSession(String name, String email) {
        loginUtils.createDemoSession(name, email);
    }

    protected void clearBrowserStorage() {
        loginUtils.clearSession();
    }

    protected void waitForUrlContains(String path) {
        wait.until(ExpectedConditions.urlContains(path));
        DemoPause.afterStep();
    }

    protected void click(By locator) {
        wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
        DemoPause.afterStep();
    }

    protected String visibleText(By locator) {
        String text = wait.until(ExpectedConditions.visibilityOfElementLocated(locator)).getText();
        DemoPause.afterStep();
        return text;
    }

    protected boolean acceptAlertIfPresent() {
        try {
            Alert alert = new WebDriverWait(driver, Duration.ofSeconds(2))
                    .until(ExpectedConditions.alertIsPresent());
            alert.accept();
            DemoPause.afterStep();
            return true;
        } catch (TimeoutException | NoAlertPresentException ex) {
            return false;
        }
    }

    protected boolean dismissAlertIfPresent() {
        try {
            Alert alert = new WebDriverWait(driver, Duration.ofSeconds(2))
                    .until(ExpectedConditions.alertIsPresent());
            alert.dismiss();
            DemoPause.afterStep();
            return true;
        } catch (TimeoutException | NoAlertPresentException ex) {
            return false;
        }
    }

    protected String uniqueEmail(String prefix) {
        return prefix + "+" + System.currentTimeMillis() + "@example.com";
    }

    private boolean isReachable(String url) {
        try {
            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(2))
                    .build();
            HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                    .timeout(Duration.ofSeconds(2))
                    .GET()
                    .build();
            HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
            return response.statusCode() < 500;
        } catch (Exception ex) {
            return false;
        }
    }
}

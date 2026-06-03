package base;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import java.time.Duration;

public class BaseTest {

    // Protected driver so subclasses can access it
    protected WebDriver driver;

    @BeforeMethod
    public void setUp() {
        // Automatically setup the ChromeDriver binary
        WebDriverManager.chromedriver().setup();

        // Initialize the driver
        driver = new ChromeDriver();

        // Basic configuration
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }

    @AfterMethod
    public void tearDown() {
        // Close the browser after the test completes
        if (driver != null) {
            driver.quit();
        }
    }
}
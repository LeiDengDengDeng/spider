import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

/**
 * Created by deng on 2017/5/16.
 */
public class MyWebDriver {
    private final static String CHROME_DRIVER_PATH = "/Users/deng/IdeaProjects/Spider/src/chromedriver";

    public static WebDriver createWebDriver() {
        System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_PATH);
        return new ChromeDriver();
    }
}

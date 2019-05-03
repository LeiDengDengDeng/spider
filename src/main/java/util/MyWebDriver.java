package util;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.List;

/**
 * Created by deng on 2017/5/16.
 */
public class MyWebDriver {
    private static WebDriver driver;
    private final static String CHROME_DRIVER_PATH = "/Users/deng/IdeaProjects/Spider/src/chromedriver"; // chromedriver路径

    public static WebDriver createWebDriver() {
        //TODO:重构MyWebDriver类
        System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_PATH);
        return driver = driver == null ? new ChromeDriver() : driver;
    }

    /**
     * 等待元素加载完毕
     *
     * @param type       定位类型
     * @param expression 定位表达式
     * @param minTimeout 最小等待时间
     * @param maxTimeout 最长等待时间
     */
    public static void waitForElementLoad(ByType type, String expression, long minTimeout, long maxTimeout) {
        for (int second = 0; ; second += 1000) {
            try {
                if (second >= minTimeout && (second >= maxTimeout || isElementPresent(type, expression))) {
                    // 已等待最小等待时间且超过最大等待时间或者指定元素出现时，跳出循环
                    break;
                }
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
        }
    }

    /**
     * 元素是否出现
     *
     * @param type       定位类型
     * @param expression 定位表达式
     * @return
     */
    public static boolean isElementPresent(ByType type, String expression) {
        try {
            if (getElementBy(type, expression).isDisplayed()) {
                return true;
            } else {
                return false;
            }
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    public static boolean areElementsPresent(ByType type, String expression, int num) {
        if (getElementsBy(type, expression).get(num).isDisplayed()) {
            return true;
        } else {
            return false;
        }
    }

    public static WebElement getElementBy(ByType type, String expression) {
        switch (type) {
            case X_PATH:
                return driver.findElement(By.xpath(expression));
            case ID:
                return driver.findElement(By.id(expression));
            case CLASS_NAME:
                return driver.findElement(By.className(expression));
            case LINK_TEXT:
                return driver.findElement(By.linkText(expression));
            case NAME:
                return driver.findElement(By.name(expression));
            case PARTIAL_LINK_TEXT:
                return driver.findElement(By.partialLinkText(expression));
            case CSS_SELECTOR:
                return driver.findElement(By.cssSelector(expression));
            default:
                return driver.findElement(By.xpath(expression));

        }
    }

    public static List<WebElement> getElementsBy(ByType elementType, String expression) {
        switch (elementType) {
            case X_PATH:
                return driver.findElements(By.xpath(expression));
            case CLASS_NAME:
                return driver.findElements(By.className(expression));
            case LINK_TEXT:
                return driver.findElements(By.linkText(expression));
            case NAME:
                return driver.findElements(By.name(expression));
            case PARTIAL_LINK_TEXT:
                return driver.findElements(By.partialLinkText(expression));
            case CSS_SELECTOR:
                return driver.findElements(By.cssSelector(expression));
            default:
                return driver.findElements(By.xpath(expression));

        }
    }
}

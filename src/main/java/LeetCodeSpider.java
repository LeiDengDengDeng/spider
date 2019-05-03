import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import util.ByType;
import util.Html2Md;
import util.MyFileWriter;
import util.MyWebDriver;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by deng on 2018/7/23.
 */
public class LeetCodeSpider implements Spider {

    private final static String TARGET_URL = "https://leetcode-cn.com/problemset/all/";
    private final static String FILE_LOC = "/Users/deng/Documents/研究生work/题库/leetcode_crawler/";

    @Override
    public void run() throws InterruptedException {
        WebDriver driver = MyWebDriver.createWebDriver();
        driver.get(TARGET_URL);
        Thread.sleep(2000);

        List<WebElement> elements = driver.findElement(By.xpath("//*[@id=\"question-app\"]/div/div[2]/div[2]/div[2]/table/tbody[1]")).findElements(By.tagName("a"));
        List<String> titles = new ArrayList<>();
        List<String> hrefs = new ArrayList<>();
        for (int i = 0; i < elements.size(); i++) {
            if (elements.get(i).getAttribute("href").contains("article")) {
                continue;
            }
            titles.add(elements.get(i).getText());
            hrefs.add(elements.get(i).getAttribute("href"));
        }

        for (int i = 0; i < titles.size(); i++) {
            String title = titles.get(i);
            String href = hrefs.get(i);
            driver.get(href);

            MyWebDriver.waitForElementLoad(ByType.X_PATH,"//*[@id=\"desktop-side-bar\"]/div/ul/li[1]/span[2]",0,10000);
//            Thread.sleep(2000);
            try {
                String difficulty = driver.findElement(By.xpath("//*[@id=\"desktop-side-bar\"]/div/ul/li[1]/span[2]")).getText();
                String description = Html2Md.getMarkDownText(driver.findElement(By.xpath("//*[@id=\"descriptionContent\"]/div[1]/div/div[2]/div[2]")).getAttribute("innerHTML"));
                StringBuilder code = new StringBuilder();
                for (WebElement element : driver.findElements(By.xpath("//*[@id=\"question-detail-app\"]/div/div[3]/div/div/div[2]/div/div[6]/div[1]/div/div/div/div[5]/div"))) {
                    code.append(element.findElement(By.className(" CodeMirror-line ")).getText() + "\n");
                }

                // 获取知识点
                driver.findElement(By.id("show-tags-btn-topics")).click();
                StringBuilder tags = new StringBuilder();
                for (WebElement tag : driver.findElements(By.xpath("//*[@id=\"tags-topics\"]/a"))) {
                    tags.append(tag.getText() + " ");
                }

                String content = "题目:" + title + "  " + "难度:" + difficulty + "  " + "知识点:" + tags + "\n"
                        + "----------\n" + description + "\n"
                        + "----------\n" + code;

                String[] splitStrs = href.split("/");
                String engTitle = splitStrs[splitStrs.length - 1];

                MyFileWriter.writeString(FILE_LOC + difficulty + "/" + i + "." + engTitle + ".txt", content);
            } catch (Exception e) {
                System.out.println("Error:" + i + " " + href);
                continue;
            }
        }

        driver.quit();
    }


    public static void main(String[] args) {
        try {
            new LeetCodeSpider().run();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

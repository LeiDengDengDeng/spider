import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * Created by deng on 2017/5/17.
 */
public class ShanghaiStockSpider implements Spider {

    private final static int PAGE_COUNT = 60; // 写死了的总页码
    private final static String TARGET_URL = "http://english.sse.com.cn";
    private final static String FILE_NAME = "/Users/deng/IdeaProjects/Spider/src/ShanghaiStockNews.txt";

    public void run() throws InterruptedException {
        StringBuilder allNewses = new StringBuilder();

        WebDriver driver = MyWebDriver.createWebDriver();
        WebDriver newsDriver = MyWebDriver.createWebDriver();
        driver.get(TARGET_URL + "/aboutsse/news/newsrelease/");

        for (int i = 1; i <= PAGE_COUNT; i++) {
            Document document = Jsoup.parse(driver.getPageSource());
            Element div = document.getElementById("sse_list_1");
            Element ul = div.getElementsByTag("ul").get(0);

            List<Element> aTags = ul.getElementsByTag("a");
            // 遍历单条新闻
            for (Element a : aTags) {
                String href = a.attr("href");
                // 跳过pdf文件
                if (href.endsWith("pdf")) continue;
                String date = a.getElementsByTag("span").get(0).text();
                newsDriver.get(TARGET_URL + href);
                System.out.println(href);

                Document newsDocument = Jsoup.parse(newsDriver.getPageSource());
                Element article =newsDocument.getElementsByClass("article-infor").get(0);
                allNewses.append(article.getElementsByTag("h2").get(0).text() + "\n");
                allNewses.append(date + "\n");
                allNewses.append(article.getElementsByClass("allZoom").get(0).text() + "\n");
                allNewses.append("\n");
            }
            System.out.println("Page " + i + " finished!");

            // 利用url直接翻页
            List<WebElement> pageElements = driver.findElements(By.className("classPage"));
            WebElement next = pageElements.get(pageElements.size() - 1);
            next.click();
            Thread.sleep(200);
        }
        Thread.sleep(3000);
        driver.quit();
        newsDriver.quit();

        MyFileWriter.writeString(FILE_NAME, allNewses.toString());
    }

    public static void main(String[] args) {
        try {
            new ShanghaiStockSpider().run();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.WebDriver;

import java.util.List;

/**
 * Created by deng on 2017/5/16.
 */
public class ShenzhenStockSpider implements Spider {

    private final static int PAGE_COUNT = 15; // 写死了的总页码
    private final static String TARGET_URL = "http://www.szse.cn/main/en/AboutSZSE/SZSENews/SZSENews/";
    private final static String FILE_NAME = "/Users/deng/IdeaProjects/Spider/src/ShenzhenStockNews.txt";

    public void run() throws InterruptedException {
        StringBuilder allNewses = new StringBuilder();

        WebDriver driver = MyWebDriver.createWebDriver();
        WebDriver newsDriver = MyWebDriver.createWebDriver();
        driver.get(TARGET_URL);

        for (int i = 1; i < PAGE_COUNT; i++) {
            Document document = Jsoup.parse(driver.getPageSource());
            List<Element> tds = document.getElementsByClass("tdline2");

            // 遍历单条新闻
            for (Element td : tds) {
                Element a = td.getElementsByTag("a").get(0);
                try {
                    String originHref = a.attr("href");
                    String href = originHref.substring(originHref.lastIndexOf("/") + 1, originHref.lastIndexOf("'"));
                    System.out.println(href);
                    newsDriver.get(TARGET_URL + href);

                    Document newsDocument = Jsoup.parse(newsDriver.getPageSource());
                    allNewses.append(newsDocument.getElementsByClass("yellow_bt15").get(0).text() + "\n");
                    allNewses.append(newsDocument.getElementsByClass("botborder1").get(0).text() + "\n");
                    allNewses.append(newsDocument.getElementsByClass("news_zw").get(0).text() + "\n");
                    allNewses.append("\n");
                } catch (java.lang.StringIndexOutOfBoundsException e) {
                    continue;
                }
            }
            System.out.println("Page " + i + " finished!");

            // 利用url直接翻页
            driver.get(TARGET_URL + "index_" + i + ".shtml");
            Thread.sleep(200);
        }
        Thread.sleep(3000);
        driver.quit();
        newsDriver.quit();

        MyFileWriter.writeString(FILE_NAME, allNewses.toString());
    }

    public static void main(String[] args) {
        try {
            new ShenzhenStockSpider().run();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

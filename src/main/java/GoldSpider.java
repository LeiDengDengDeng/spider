import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.WebDriver;

import java.util.List;

/**
 * Created by deng on 2017/8/9.
 */
public class GoldSpider implements Spider {

    private final static int PAGE_COUNT = 368; // 写死了的总页码
    private final static String TARGET_URL_PREFIX = "https://www.bullionvault.com/gold-news/latest-articles?page=";
    private final static String FILE_NAME = "/Users/deng/IdeaProjects/Spider/src/GoldNews.csv";

    public void run() throws InterruptedException {
        StringBuilder allNewses = new StringBuilder();

        WebDriver driver = MyWebDriver.createWebDriver();
        WebDriver newsDriver = MyWebDriver.createWebDriver();
        for (int i = 0; i <= PAGE_COUNT; i++) {
            driver.get(TARGET_URL_PREFIX + i);
            Document document = Jsoup.parse(driver.getPageSource());

            Element list = document.getElementById("block-system-main");
            List<Element> aTags = list.getElementsByTag("a");
            if(aTags.size()==0){
                break;
            }

            for (int m = 0; m < aTags.size(); m += 2) {
                String href = aTags.get(m).attr("href");
                newsDriver.get(href);

//                Document newsDocument = Jsoup.parse(newsDriver.getPageSource());
//                Element timeSourceElement = newsDocument.getElementsByClass("time-source").get(0);
//                String[] timeAndSource = timeSourceElement.text().split(" ");
//                String time = timeAndSource[0].substring(0, timeAndSource[0].indexOf("日")).replace('年', '-').replace('月', '-');
//                String source = timeAndSource[1];
//                String title = newsDocument.getElementById("artibodyTitle").text();
//                String content = newsDocument.getElementById("artibody").text();
//
//                allNewses.append(time + "," + href + "," + title + "," + content + "," + source + ",0\n");
//                System.out.println(allNewses);
            }
        }
        Thread.sleep(3000);
        driver.quit();
        newsDriver.quit();

        MyFileWriter.writeString(FILE_NAME, allNewses.toString());
    }

    public static void main(String[] args) {
        try {
            new GoldSpider().run();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

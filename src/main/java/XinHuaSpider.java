import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.List;

/**
 * Created by deng on 2017/5/15.
 */
public class XinHuaSpider implements Spider {

    private final static String TARGET_URL = "http://www.xinhuanet.com/english/list/china-business.htm";
    private final static String FILE_NAME = "/Users/deng/IdeaProjects/Spider/src/XinhuaNews.txt";

    public void run() throws InterruptedException {
        StringBuilder allNewses = new StringBuilder();

        WebDriver driver = MyWebDriver.createWebDriver();
        WebDriver newsDriver = MyWebDriver.createWebDriver();
        driver.get(TARGET_URL);

        String lastFirstNewsURL = "";
        String presentFirstNewsURL = "";
        for (int i = 1; ; i++) {
            lastFirstNewsURL = presentFirstNewsURL;

            Document document = Jsoup.parse(driver.getPageSource());
            Element ul = document.getElementById("showData0");
            List<Element> aTags = ul.getElementsByTag("a");

            // 如果翻页后的内容还与前一页的内容相同，代表已到终点
            presentFirstNewsURL = aTags.get(0).attr("href");
            if (presentFirstNewsURL.equals(lastFirstNewsURL)) break;

            // 遍历单条新闻
            for (Element a : aTags) {
                System.out.println(a.attr("href"));
                newsDriver.get(a.attr("href"));
                Document newsDocument = Jsoup.parse(newsDriver.getPageSource());

                List<Element> bTitle = newsDocument.getElementsByClass("Btitle");
                List<Element> source = newsDocument.getElementsByClass("source");
                List<Element> time = newsDocument.getElementsByClass("time");
                List<Element> editor = newsDocument.getElementsByClass("editor");
                List<Element> content = newsDocument.getElementsByClass("content");
                if (bTitle.size() == 1 && source.size() == 1 && time.size() == 1 && editor.size() == 1 && content.size() == 1) {
                    allNewses.append(bTitle.get(0).text() + "\n");
                    allNewses.append(source.get(0).text() + "\n");
                    allNewses.append(time.get(0).text() + "\n");
                    allNewses.append(editor.get(0).text() + "\n");
                    for (Element p : content.get(0).getElementsByTag("p")) {
                        allNewses.append(p.text() + "\n");
                    }
                    allNewses.append("\n");
                }
            }
            System.out.println("Page " + i + " finished!");

            // 翻页
            driver.findElement(By.className("_wPaginate_link_next")).click();

            Thread.sleep(200);
        }
        Thread.sleep(3000);
        driver.quit();
        newsDriver.quit();

        MyFileWriter.writeString(FILE_NAME, allNewses.toString());
    }
}

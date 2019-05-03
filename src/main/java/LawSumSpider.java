import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.WebDriver;
import util.ByType;
import util.MyFileWriter;
import util.MyWebDriver;

import java.io.File;

/**
 * Created by deng on 2018/4/14.
 */
public class LawSumSpider implements Spider {
    public void run() throws InterruptedException {
        WebDriver driver = MyWebDriver.createWebDriver();

        String folderPath = "/Users/deng/Documents/毕设/案由&法条数据/民事案由法条统计_法条文本汇编 毕朝国";
        File rootFolder = new File(folderPath);
        if (rootFolder.exists()) {
            for (File singleFile : rootFolder.listFiles()) {
                String fileName = singleFile.getName();
                int lastNumLoc = getLastIndexOfNum(fileName);
                if (lastNumLoc != -1) {
                    String lawCode = fileName.substring(0, getLastIndexOfNum(fileName) + 1);
                    // 循环遍历所有案由
                    spider(driver, lawCode);
                }
            }
        }

        driver.quit();
    }


    public void spider(WebDriver driver, String lawCode) throws InterruptedException {
        driver.get("http://www.lawsum.com/qrcode/index.html?content=" + lawCode);

        // 至少等待6秒，防止爬虫被封
        MyWebDriver.waitForElementLoad(ByType.ID,"contentTT",6000,10000);

        Document document = Jsoup.parse(driver.getPageSource());
        Element content = document.getElementById("detailsContent");
        MyFileWriter.writeString("/Users/deng/Documents/毕设/案由&法条数据/法合案由html数据/" + lawCode + ".txt", content.html());
    }

    private int getLastIndexOfNum(String str) {
        int loc = 0;
        for (int i = 0; i <= 9; i++) {
            loc = Math.max(loc, str.lastIndexOf('0' + i));
        }
        return loc;
    }

    public static void main(String[] args) {
        try {
            new LawSumSpider().run();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

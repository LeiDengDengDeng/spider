import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import util.ExcelUtil;
import util.MyFileWriter;
import util.MyWebDriver;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by deng on 2018/7/16.
 */
public class ShenSpider implements Spider {

    private final static String TARGET_URL = "http://irm.cninfo.com.cn/ircs/interaction/topSearchForSzse.do";
    private final static String FILE_NAME = "/Users/deng/IdeaProjects/Spider/src/ShenQuestion.txt";

    private final static String[] START_DATES = new String[]{"01-01", "04-01", "07-01", "10-01"};
    private final static String[] END_DATES = new String[]{"03-31", "06-30", "09-30", "12-31"};

    public void run() throws InterruptedException {
        StringBuilder result = new StringBuilder();

        WebDriver driver = MyWebDriver.createWebDriver();
        try {
            driver.get(TARGET_URL);

            boolean popFlag = true; // 只有第一次搜索会弹窗
            String[] codes = getCodes();
            for (int i = 0; i < codes.length; i++) {
                if (Integer.parseInt(codes[i]) < 2701) {
                    continue;
                } else if (Integer.parseInt(codes[i]) >= 300000) {
                    break;
                }

                result.append(codes[i] + " ");
                System.out.print(codes[i] + " ");

                driver.findElement(By.id("condition.stockcode")).clear();
                driver.findElement(By.id("condition.stockcode")).sendKeys(codes[i]);
                driver.findElement(By.id("more")).click();

                // 等待"更多条件"加载完成"
                boolean clickable = false;
                while (!clickable) {
                    try {
                        driver.findElement(By.xpath("//*[@id=\"tac_box2\"]/div/div/dl[3]/dt[2]/input")).click();
                        clickable = true;
                    } catch (org.openqa.selenium.ElementNotVisibleException e1) {
                        continue;
                    }
                }

                for (int m = 2013; m < 2018; m++) {
                    for (int n = 0; n < START_DATES.length; n++) {
                        driver.findElement(By.id("condition.dateFrom")).clear();
                        driver.findElement(By.id("condition.dateFrom")).sendKeys(m + "-" + START_DATES[n]);
                        driver.findElement(By.id("condition.dateTo")).clear();
                        driver.findElement(By.id("condition.dateTo")).sendKeys(m + "-" + END_DATES[n]);
                        driver.findElement(By.xpath("//*[@id=\"topSearchForm\"]/div/p[15]/input")).click();

                        // 把句柄切换到弹窗页面
                        if (popFlag) {
                            Set<String> handles = driver.getWindowHandles();
                            handles.remove(driver.getWindowHandle());//去掉当前句柄
                            driver.switchTo().window(handles.iterator().next());

                            popFlag = false;
                        }

                        int quesCount = 0;
                        try {
                            WebElement element = driver.findElement(By.xpath("//*[@id=\"box_center\"]/div/div[2]/table/tbody/tr[2]/td/a[last()-1]"));
                            int totalPageCount = Integer.parseInt(element.getText());
                            quesCount += 10 * (totalPageCount - 1); // 每个完整页有10个问题
                            element.click();
                        } catch (org.openqa.selenium.NoSuchElementException e1) {
                            // 如果出现总页数只有一页的情况时，会抛出该异常
                        } finally {
                            quesCount += driver.findElements(By.xpath("//*[@id=\"con_one_1\"]/div/ul/li")).size();
                        }

                        result.append(quesCount + " ");
                        System.out.print(quesCount + " ");
                    }
                }
                result.append("\n");
                System.out.println();
            }
        } catch (Exception e) {
            e.printStackTrace();

            driver.quit();
            MyFileWriter.writeString(FILE_NAME, result.toString());
        }
    }

    private String[] getCodes() {
        String fileName = "src/深证A股.xlsx";
        Workbook workbook = ExcelUtil.getWorkbook(fileName);
        Sheet sheet = workbook.getSheetAt(0);

        List<String> codeList = new ArrayList<>();
        for (int i = 1; i < sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) break;

            String cellVal = ExcelUtil.getCellVal(row.getCell(0));
            codeList.add(cellVal.substring(0, cellVal.indexOf('.')));
        }
        return codeList.toArray(new String[0]);
    }

    public static void main(String[] args) {
        try {
            new ShenSpider().run();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

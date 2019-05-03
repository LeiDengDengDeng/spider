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

/**
 * Created by deng on 2018/7/16.
 */
public class ShenRelationSpider implements Spider {

    private final static String TARGET_URL = "http://irm.cninfo.com.cn/ircs/interaction/irmInformationTab.do";
    private final static String FILE_NAME = "/Users/deng/IdeaProjects/Spider/src/ShenRelation.txt";


    private final static String[] START_DATES = new String[]{"01-01", "04-01", "07-01", "10-01"};
    private final static String[] END_DATES = new String[]{"03-31", "06-30", "09-30", "12-31"};


    public void run() throws InterruptedException {
        StringBuilder result = new StringBuilder();
        WebDriver driver = MyWebDriver.createWebDriver();

        String[] codes = getCodes();
        driver.get(TARGET_URL);

        for (int i = 0; i < codes.length; i++) {
            if (Integer.parseInt(codes[i]) < 300325) {
                continue;
            }

            result.append(codes[i] + "\t");
            System.out.print(codes[i] + "\t");

            driver.findElement(By.id("stkcode")).clear();
            driver.findElement(By.id("stkcode")).sendKeys(codes[i]);
            driver.findElement(By.id("irmType")).sendKeys("调研活动");

            for (int m = 2013; m < 2018; m++) {
                for (int n = 0; n < START_DATES.length; n++) {
                    driver.findElement(By.id("beginDate")).clear();
                    driver.findElement(By.id("beginDate")).sendKeys(m + "-" + START_DATES[n]);
                    driver.findElement(By.id("endDate")).clear();
                    driver.findElement(By.id("endDate")).sendKeys(m + "-" + END_DATES[n]);
                    driver.findElement(By.xpath("//*[@id=\"irmForm\"]/p[11]/input")).click();

                    int count = 0;
                    // 进入iFrame
                    driver.switchTo().frame("irmInfoFrame");
                    Thread.sleep(200);
                    if (!driver.getPageSource().contains("暂无数据")) {
                        try {
                            WebElement element = driver.findElement(By.xpath("//*[@id=\"con_one_11\"]/div[2]/table/tbody/tr[2]/td/a[last()-1]"));
                            int totalPageCount = Integer.parseInt(element.getText());
                            count += 85 * (totalPageCount - 1);
                            element.click();
                        } catch (Exception e1) {
                        } finally {
                            count += driver.findElements(By.xpath("//*[@id=\"con_one_11\"]/div[1]/table/tbody/tr")).size();
                        }
                    }
                    // 回到主窗口
                    driver.switchTo().defaultContent();

                    result.append(count + " ");
                    System.out.print(count + " ");
                }
            }

            result.append("\n");
            System.out.println();
        }

        driver.quit();
        MyFileWriter.writeString(FILE_NAME, result.toString());
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
            new ShenRelationSpider().run();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by deng on 2017/5/16.
 */
public class MyFileWriter {

    public static void writeString(String fileName, String content) {
        FileWriter fileWriter = null;
        try {
            File file = new File(fileName);
            if (!file.exists()) file.createNewFile();

            fileWriter = new FileWriter(file);
            fileWriter.write(content);
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

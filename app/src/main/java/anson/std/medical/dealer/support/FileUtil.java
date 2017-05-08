package anson.std.medical.dealer.support;

import android.content.Context;

import com.alibaba.fastjson.JSON;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by anson on 17-5-8.
 */

public class FileUtil {

    public static boolean isFileExists(Context context, String fileName) {
        String[] files = context.fileList();
        for (int i = 0; i < files.length; i++) {
            if (fileName.equals(files[i])) {
                return true;
            }
        }
        return false;
    }

    public static File createFile(Context context, String fileName) {
        File directory = context.getFilesDir();

        File file = new File(directory, fileName);
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    public static void flushFileByObject(FileOutputStream fileOutputStream, Object obj) {
        String json = JSON.toJSONString(obj);
        try {
            fileOutputStream.write(json.getBytes());
            fileOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static <T> T readFile(FileInputStream fileInputStream, Class<T> tClass) {
        try {
            return JSON.parseObject(fileInputStream, tClass);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}

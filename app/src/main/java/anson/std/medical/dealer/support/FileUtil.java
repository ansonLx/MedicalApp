package anson.std.medical.dealer.support;

import android.content.Context;

import com.alibaba.fastjson.JSON;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by anson on 17-5-8.
 */

public class FileUtil {

    public static File getInternalExternalDirectory(Context context) {
        return context.getExternalFilesDir(null);
    }

    public static List<File> getSDCardExternalDirectory(Context context) {
        File[] dirs = context.getExternalFilesDirs(null);
        if (dirs.length != 1) {
            List<File> sdCards = new ArrayList<>();
            File internalFileDir = getInternalExternalDirectory(context);
            for (int i = 0; i != dirs.length; i++) {
                if (!dirs[i].getAbsolutePath().equals(internalFileDir.getAbsolutePath())) {
                    sdCards.add(dirs[i]);
                }
            }
            return sdCards;
        }
        return null;
    }

    /**
     * sdcard 0 > internal external store
     * @param context
     * @return
     */
    public static File getAppPrivateDirectory(Context context){
        File appPrivateDir;
        List<File> sdCards = FileUtil.getSDCardExternalDirectory(context);
        if (sdCards != null) {
            appPrivateDir = sdCards.get(0);
        } else {
            appPrivateDir = FileUtil.getInternalExternalDirectory(context);
        }
        return appPrivateDir;
    }

    public static File createFile(File directory, String fileName) {
        File file = new File(directory, fileName);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    public static void flushFileByObject(File file, Object obj) {
        FileOutputStream fileOutputStream = null;
        String json = JSON.toJSONString(obj);
        try {
            fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(json.getBytes());
            fileOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(fileOutputStream != null){
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static <T> T readFile(File file, Class<T> tClass) {
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
            return JSON.parseObject(fileInputStream, tClass);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(fileInputStream != null){
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}

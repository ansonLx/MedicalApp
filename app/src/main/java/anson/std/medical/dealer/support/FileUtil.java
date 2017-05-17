package anson.std.medical.dealer.support;

import android.content.Context;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ServiceCompat;

import com.alibaba.fastjson.JSON;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import anson.std.medical.dealer.MedicalApplication;

/**
 * Created by anson on 17-5-8.
 */

public class FileUtil {

    private static Context context = MedicalApplication.getMedicalApplicationContext();

    public static File getInternalExternalDirectory() {
        return context.getExternalFilesDir(null);
    }

    public static List<File> getSDCardExternalDirectory() {
        File[] dirs = context.getExternalFilesDirs(null);
        if (dirs.length != 1) {
            List<File> sdCards = new ArrayList<>();
            File internalFileDir = getInternalExternalDirectory();
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
     * @return
     */
    public static File getAppPrivateDirectoryForLog(){
        File appPrivateDir;
        List<File> sdCards = FileUtil.getSDCardExternalDirectory();
        if (sdCards != null) {
            appPrivateDir = sdCards.get(0);
        } else {
            appPrivateDir = FileUtil.getInternalExternalDirectory();
        }
        return appPrivateDir;
    }

    /**
     * sdcard 0 > internal external store
     * @return
     */
    public static File getAppPrivateDirectory(){
        return context.getFilesDir();
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

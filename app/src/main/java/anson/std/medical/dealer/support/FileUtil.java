package anson.std.medical.dealer.support;

import android.content.Context;

/**
 * Created by anson on 17-5-8.
 */

public class FileUtil {

    public static boolean isFileExists(Context context, String fileName){
        String[] files = context.fileList();
        for (int i = 0; i < files.length; i++) {
            if(fileName.equals(files[i])){
                return true;
            }
        }
        return false;
    }
}

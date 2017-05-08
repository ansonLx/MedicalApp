package anson.std.medical.dealer.util;

import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by anson on 17-5-8.
 */

public class LogUtil {
    private static final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");

    public static void log(TextView logView, String msg) {
        if (logView != null) {
            String message = sdf.format(new Date()) + " " + msg + "\n";
            logView.append(message);
            int offset = logView.getLineCount() * logView.getLineHeight();
            int height = logView.getHeight();
            if (offset > height) {
                logView.scrollTo(0, offset - height);
            }
        }
        Toast.makeText(logView.getContext(), msg, Toast.LENGTH_LONG).show();

        System.out.println(msg);
    }

    public static void log(String msg){
        log(null, msg);
    }
}

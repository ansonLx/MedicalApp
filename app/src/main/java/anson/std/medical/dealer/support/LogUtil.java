package anson.std.medical.dealer.support;

import android.content.Context;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by anson on 17-5-8.
 */
public class LogUtil {
    private static final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");

    public static void log(Context context, String msg) {
        System.out.println(msg);
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static void log(TextView logView, String msg) {
        if (logView != null) {
            String message = sdf.format(new Date()) + "-[" + Thread.currentThread().getName() + "] " + msg + "\n";
            logView.append(message);
            int offset = logView.getLineCount() * logView.getLineHeight();
            int height = logView.getHeight();
            if (offset > height) {
                logView.scrollTo(0, offset - height);
            }
            log(logView.getContext(), msg);
        }
    }
}

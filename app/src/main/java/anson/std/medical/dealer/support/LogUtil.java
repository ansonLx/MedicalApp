package anson.std.medical.dealer.support;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Date;

import anson.std.medical.dealer.MedicalApplication;

/**
 * Created by anson on 17-5-8.
 */
public class LogUtil {

    private static final boolean debug = true;
    private static final boolean default_wirte = true;
    private static final int toast_show_time = Toast.LENGTH_SHORT;
    private static final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");

    private static HandlerThread handlerThread;
    private static Handler logHandler;
    private static Context context = MedicalApplication.getMedicalApplicationContext();

    private static File logFile;

    static {

        // create log file
        File appDir = FileUtil.getAppPrivateDirectoryForLog();
        File logDir = new File(appDir, "log");
        if (!logDir.exists()) {
            logDir.mkdirs();
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-HHmmssSSS");
        Date date = new Date();
        String fileName = dateFormat.format(date) + ".out";
        logFile = FileUtil.createFile(logDir, fileName);

        // handler thread and handler
        handlerThread = new HandlerThread("Medical_LogUtil_Log_Thread");
        handlerThread.start();
        logHandler = new LogHandler(handlerThread.getLooper());
    }

    public static void log(String msgFormat, Object[] msgParam, boolean isView, boolean isWriteFile) {
        Message message = logHandler.obtainMessage();
        message.obj = new Object[]{msgFormat, msgParam, isView, isWriteFile, System.currentTimeMillis()};
        logHandler.sendMessage(message);
    }

    public static void log(String msg){
        log(msg, null, false, default_wirte);
    }

    public static void log(String msg, Object... param){
        log(msg, param, false, default_wirte);
    }

    public static void logView(String msg){
        log(msg, null, true, default_wirte);
    }

    public static void logView(String msg, Object[] param){
        log(msg, param, true, default_wirte);
    }

    private static String getObjString(Object o) {
        if (o instanceof Throwable) {
            return ((Throwable) o).getLocalizedMessage();
        } else {
            return o == null ? null : o.toString();
        }
    }

    private static String formatMessage(String msgFormatter, Object[] obj) {
        StringBuffer stringBuffer = new StringBuffer();

        if (msgFormatter.contains("{}")) {
            int index = 0;
            int standerIndex;
            int paramIndex = 0;
            while ((standerIndex = msgFormatter.indexOf("{}", index)) != -1) {
                stringBuffer.append(msgFormatter.substring(index, standerIndex));
                if (paramIndex > obj.length - 1 && paramIndex != 0) {
                    stringBuffer.append(msgFormatter.substring(standerIndex));
                    break;
                } else {
                    stringBuffer.append(getObjString(obj[paramIndex++]));
                }
                index = standerIndex + 2;
            }
            if (standerIndex == -1) {
                stringBuffer.append(msgFormatter.substring(index));
            }
        } else {
            stringBuffer.append(msgFormatter);
        }
        return stringBuffer.toString();
    }

    private static class LogHandler extends Handler {
        private RandomAccessFile randomAccessFile;

        public LogHandler(Looper looper) {
            super(looper);
            try {
                randomAccessFile = new RandomAccessFile(logFile, "rw");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void handleMessage(Message message) {
            Object[] objects = (Object[]) message.obj;
            String msg = (String) objects[0];
            Object[] param = (String[]) objects[1];
            boolean isView = (boolean) objects[2];
            boolean isWriteFile = (boolean) objects[3];
            long timeMillis = (long) objects[4];

            String logString = formatMessage(msg, param);

            // show log
            if (isView) {
                Toast.makeText(context, msg, toast_show_time).show();
            }

            // log to consul
            if(debug){
                System.out.println(logString);
            }

            // save log
            if (isWriteFile) {
                try {
                    logString = "[" + sdf.format(new Date(timeMillis)) + "] " + logString + "\n";
                    randomAccessFile.write(logString.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}

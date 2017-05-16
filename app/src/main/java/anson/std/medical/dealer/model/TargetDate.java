package anson.std.medical.dealer.model;

import android.content.Context;

import java.util.List;

import anson.std.medical.dealer.MedicalApplication;
import anson.std.medical.dealer.R;

/**
 * Created by anson on 17-5-15.
 */

public class TargetDate {

    private String dateStr;
    private int week = -1;
    private boolean ifFullDay;
    private boolean amPm;

    public String getDateStr() {
        return dateStr;
    }

    public void setDateStr(String dateStr) {
        this.dateStr = dateStr;
    }

    public int getWeek() {
        return week;
    }

    public void setWeek(int week) {
        this.week = week;
    }

    public boolean isIfFullDay() {
        return ifFullDay;
    }

    public void setIfFullDay(boolean ifFullDay) {
        this.ifFullDay = ifFullDay;
    }

    public boolean isAmPm() {
        return amPm;
    }

    public void setAmPm(boolean amPm) {
        this.amPm = amPm;
    }

    public static String getWeekByNum(int week){
        Context context = MedicalApplication.getMedicalApplicationContext();
        String weekStr;
        switch (week) {
            case 0:
                weekStr = context.getString(R.string.date_picker_week_7);
                break;
            case 1:
                weekStr = context.getString(R.string.date_picker_week_1);
                break;
            case 2:
                weekStr = context.getString(R.string.date_picker_week_2);
                break;
            case 3:
                weekStr = context.getString(R.string.date_picker_week_3);
                break;
            case 4:
                weekStr = context.getString(R.string.date_picker_week_4);
                break;
            case 5:
                weekStr = context.getString(R.string.date_picker_week_5);
                break;
            case 6:
                weekStr = context.getString(R.string.date_picker_week_6);
                break;
            default:
                weekStr = "";
        }
        return weekStr;
    }

    public static String list2String(List<TargetDate> targetDateList) {
        Context context = MedicalApplication.getMedicalApplicationContext();
        StringBuilder stringBuilder = new StringBuilder();
        for (TargetDate targetDate : targetDateList) {
            if (targetDate.dateStr != null) {
                stringBuilder.append(targetDate.dateStr).append("-");
            }
            if (targetDate.week != -1) {
                stringBuilder.append(getWeekByNum(targetDate.week)).append("-");
            }
            if (targetDate.isIfFullDay()) {
                stringBuilder.append(context.getString(R.string.date_picker_full));
            } else {
                if (targetDate.isAmPm()) {
                    stringBuilder.append(context.getString(R.string.date_picker_am));
                } else {
                    stringBuilder.append(context.getString(R.string.date_picker_pm));
                }
            }
            stringBuilder.append(";");
        }
        return stringBuilder.deleteCharAt(stringBuilder.length() - 1).toString();
    }
}

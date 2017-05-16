package anson.std.medical.dealer.activity.support;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import anson.std.medical.dealer.Consumer;
import anson.std.medical.dealer.R;
import anson.std.medical.dealer.model.TargetDate;
import anson.std.medical.dealer.support.Constants;

/**
 * Created by anson on 17-5-15.
 */

public class DatePicker extends ArrayAdapter {
    private static final int days = 7;
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    private Context context;
    private int resource;
    private AlertDialog dialog;

    private List<DateItem> dateItemList = new ArrayList<>();
    private List<TargetDate> initDate;
    private boolean multiChoice = false;
    private boolean isShowDate = true;
    private boolean isShowWeek = false;

    private Consumer<List<TargetDate>> pickerCallback;

    public static DatePicker getInstance(Context context) {
        return new DatePicker(context);
    }

    private DatePicker(Context context) {
        super(context, R.layout.medical_date_picker_list_view_layout);
        this.context = context;
        this.resource = R.layout.medical_date_picker_list_view_layout;
    }

    public DatePicker build() {
        if (!isShowDate && !isShowWeek) {
            isShowDate = true;
        }
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setAdapter(this, null).setTitle(R.string.date_picker_title);
        if (multiChoice) {
            dialog.setPositiveButton(context.getString(R.string.date_picker_confirm), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    doReturn();
                }
            });
        }
        this.dialog = dialog.create();
        initDateItem();
        return this;
    }

    public DatePicker setMultiChoice(boolean multiChoice) {
        this.multiChoice = multiChoice;
        return this;
    }

    public DatePicker setPickerCallback(Consumer<List<TargetDate>> callback) {
        this.pickerCallback = callback;
        return this;
    }

    public DatePicker setShowDate(boolean isShowDate) {
        this.isShowDate = isShowDate;
        return this;
    }

    public DatePicker setShowWeek(boolean isShowWeek) {
        this.isShowWeek = isShowWeek;
        return this;
    }

    public DatePicker setInitDateForWeek(List<TargetDate> targetDateList) {
        this.initDate = targetDateList;
        initDateItem();
        return this;
    }

    private void initDateItem() {
        dateItemList.clear();
        Calendar now = Calendar.getInstance();
        for (int i = 0; i != days; i++) {
            now.add(Calendar.DAY_OF_YEAR, 1);
            DateItem dateItem = new DateItem();
            if (isShowDate) {
                dateItem.date = (Calendar) now.clone();
                dateItem.week = dateItem.date.get(Calendar.DAY_OF_WEEK) - 1;
            } else if (isShowWeek) {
                dateItem.week = i;
                if (initDate != null) {
                    for (TargetDate targetDate : initDate) {
                        if (targetDate.getWeek() == dateItem.week) {
                            if (targetDate.isIfFullDay()) {
                                dateItem.time = Constants.time_full;
                            } else {
                                if (targetDate.isAmPm()) {
                                    dateItem.time = Constants.time_am;
                                } else {
                                    dateItem.time = Constants.time_pm;
                                }
                            }
                        }
                    }
                }
            }
            dateItemList.add(dateItem);
        }
    }

    private void doReturn() {
        List<TargetDate> targetDateList = new ArrayList<>();
        for (DateItem dateItem : dateItemList) {
            if (dateItem.time != 0) {
                TargetDate targetDate = new TargetDate();
                if (isShowDate) {
                    targetDate.setDateStr(sdf.format(dateItem.date.getTime()));
                }
                if (isShowWeek) {
                    targetDate.setWeek(dateItem.week);
                }
                if (dateItem.time == Constants.time_am) {
                    targetDate.setAmPm(true);
                } else if (dateItem.time == Constants.time_full) {
                    targetDate.setIfFullDay(true);
                }
                targetDateList.add(targetDate);
            }
        }
        destroyDialog();
        pickerCallback.apply(targetDateList);
    }

    public void showDialog() {
        dialog.show();
    }

    public void destroyDialog() {
        dialog.dismiss();
    }

    @Override
    public int getCount() {
        return dateItemList.size();
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        final DateItem dateItem = dateItemList.get(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(resource, null);
        }
        if (isShowDate) {
            TextView dateStr = (TextView) convertView.findViewById(R.id.date_picker_date_str_view);
            dateStr.setText(sdf.format(dateItem.date.getTime()));
        }
        if (isShowWeek) {
            TextView weekStr = (TextView) convertView.findViewById(R.id.date_picker_week_str_view);
            weekStr.setText(TargetDate.getWeekByNum(dateItem.week));
        }

        final CheckBox amCheckBox = (CheckBox) convertView.findViewById(R.id.date_picker_am_cb);
        final CheckBox pmCheckBox = (CheckBox) convertView.findViewById(R.id.date_picker_pm_cb);
        final CheckBox fullCheckBox = (CheckBox) convertView.findViewById(R.id.date_picker_full_cb);
        if (!multiChoice) {
            amCheckBox.setChecked(false);
            pmCheckBox.setChecked(false);
            fullCheckBox.setChecked(false);
            for (DateItem di : dateItemList) {
                di.time = 0;
            }
        } else {
            if (dateItem.time == Constants.time_full) {
                fullCheckBox.setChecked(true);
            } else if (dateItem.time == Constants.time_am) {
                amCheckBox.setChecked(true);
            } else if (dateItem.time == Constants.time_pm) {
                pmCheckBox.setChecked(true);
            }
        }
        amCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (amCheckBox.isChecked()) {
                    pmCheckBox.setChecked(false);
                    fullCheckBox.setChecked(false);
                    dateItem.time = Constants.time_am;
                    if (!multiChoice) {
                        doReturn();
                    }
                } else {
                    dateItem.time = 0;
                }
            }
        });
        pmCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pmCheckBox.isChecked()) {
                    amCheckBox.setChecked(false);
                    fullCheckBox.setChecked(false);
                    dateItem.time = Constants.time_pm;
                    if (!multiChoice) {
                        doReturn();
                    }
                } else {
                    dateItem.time = 0;
                }
            }
        });
        fullCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fullCheckBox.isChecked()) {
                    amCheckBox.setChecked(false);
                    pmCheckBox.setChecked(false);
                    dateItem.time = Constants.time_full;
                    if (!multiChoice) {
                        doReturn();
                    }
                } else {
                    dateItem.time = 0;
                }
            }
        });

        return convertView;
    }

    private class DateItem {
        private Calendar date;
        private int week;
        private int time;
    }
}

package anson.std.medical.dealer.activity.support;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import anson.std.medical.dealer.Consumer;
import anson.std.medical.dealer.R;
import anson.std.medical.dealer.model.Doctor;
import anson.std.medical.dealer.model.TargetDate;

/**
 * Created by anson on 17-5-15.
 */

public class MedicalDoctorListViewAdapter extends ArrayAdapter {

    private int resource;
    private Context context;
    private int checkedColor;
    private List<DataOnItem> dataList = new ArrayList<>();
    private Consumer<Doctor> editCallback;
    private Consumer<Doctor> delCallback;
    private Consumer<Doctor> addSkillCallback;
    private boolean hasBtn;

    public MedicalDoctorListViewAdapter(Context context, Consumer<Doctor> addSkillCallback){
        super(context, R.layout.medical_resource_list_view_without_btn_layout);
        this.resource = R.layout.medical_resource_list_view_without_btn_layout;
        this.context = context;
        this.checkedColor = context.getResources().getColor(R.color.list_selected, null);
        this.addSkillCallback = addSkillCallback;
        hasBtn = false;
    }

    public MedicalDoctorListViewAdapter(Context context, Consumer<Doctor> editCallback, Consumer<Doctor> delCallback) {
        super(context, R.layout.medical_resource_list_view_layout);
        this.resource = R.layout.medical_resource_list_view_layout;
        this.context = context;
        this.checkedColor = context.getResources().getColor(R.color.list_selected, null);
        this.editCallback = editCallback;
        this.delCallback = delCallback;
        hasBtn = true;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final DataOnItem dataOnItem = dataList.get(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(resource, null);
        }
        TextView doctorNameView = (TextView) convertView.findViewById(R.id.doctor_name_view);
        TextView doctorTitleView = (TextView) convertView.findViewById(R.id.doctor_title_view);
        TextView doctorSkillView = (TextView) convertView.findViewById(R.id.doctor_skill_view);
        if(!hasBtn && addSkillCallback != null){
            Button doctorAddSkillBtn = (Button) convertView.findViewById(R.id.doctor_add_skill_btn);
            doctorAddSkillBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addSkillCallback.apply(dataOnItem.doctor);
                }
            });
        }
        if(hasBtn){
            TextView doctorTimerView = (TextView) convertView.findViewById(R.id.doctor_timer_view);
            Button editBtn = (Button) convertView.findViewById(R.id.edit_btn);
            Button delBtn = (Button) convertView.findViewById(R.id.del_btn);
            if(dataOnItem.doctor.getTargetDateList() == null){
                doctorTimerView.setText(R.string.doctor_info_unknow);
            } else {
                doctorTimerView.setText(TargetDate.list2String(dataOnItem.doctor.getTargetDateList()));
            }
            if(editCallback != null){
                editBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editCallback.apply(dataOnItem.doctor);
                    }
                });
            }
            if(delCallback != null){
                delBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        delCallback.apply(dataOnItem.doctor);
                    }
                });
            }
        }

        dataOnItem.position = position;
        dataOnItem.nameView = convertView;
        if(!dataOnItem.isCheck){
            convertView.setBackgroundColor(Color.TRANSPARENT);
        } else {
            convertView.setBackgroundColor(checkedColor);
        }
        doctorNameView.setText(dataOnItem.doctor.getName());
        doctorTitleView.setText(dataOnItem.doctor.getTitle());
        doctorSkillView.setText(dataOnItem.doctor.getSkill());

        convertView.setTag(dataOnItem);
        return convertView;
    }

    public String onItemClick(View view, Consumer<Doctor> checkedCallback, Consumer<Doctor> unCheckedCallback){
        String doctorName = "";
        DataOnItem dataOnItem = (DataOnItem) view.getTag();
        for(DataOnItem data : dataList){
            if (data.position == dataOnItem.position) {
                dataOnItem.isCheck = !dataOnItem.isCheck;
            } else {
                data.isCheck = false;
            }
            if (data.nameView != null) {
                data.nameView.setBackgroundColor(Color.TRANSPARENT);
            }
        }
        if (dataOnItem.isCheck) {
            doctorName = dataOnItem.doctor.getName();
            view.setBackgroundColor(checkedColor);
            if(checkedCallback != null){
                checkedCallback.apply(dataOnItem.doctor);
            }
        } else {
            if(unCheckedCallback != null){
                unCheckedCallback.apply(dataOnItem.doctor);
            }
        }
        return doctorName;
    }

    public void flushData(){
        dataList.clear();
        notifyDataSetChanged();
    }

    public void addData(List<Doctor> doctorList){
        for(Doctor doctor : doctorList){
            DataOnItem dataOnItem = new DataOnItem();
            dataOnItem.doctor = doctor;
            dataOnItem.isCheck = false;
            dataList.add(dataOnItem);
        }
        notifyDataSetChanged();
    }

    public Doctor getSelectDoctor(){
        for(DataOnItem dataOnItem : dataList){
            if(dataOnItem.isCheck){
                return dataOnItem.doctor;
            }
        }
        return null;
    }

    private class DataOnItem {
        private int position;
        private Doctor doctor;
        private boolean isCheck;
        private View nameView;
    }
}

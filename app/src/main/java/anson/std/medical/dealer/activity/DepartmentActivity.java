package anson.std.medical.dealer.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import anson.std.medical.dealer.Consumer;
import anson.std.medical.dealer.MedicalForegroundService;
import anson.std.medical.dealer.R;
import anson.std.medical.dealer.activity.support.MedicalServiceConnection;
import anson.std.medical.dealer.aservice.MedicalForegroundServiceImpl;
import anson.std.medical.dealer.model.Department;
import anson.std.medical.dealer.model.Hospital;
import anson.std.medical.dealer.model.Medical;
import anson.std.medical.dealer.support.Constants;

public class DepartmentActivity extends AppCompatActivity {

    private EditText departmentIdEditText;
    private EditText departmentNameEditText;

    private MedicalForegroundService medicalForegroundService;
    private MedicalServiceConnection medicalServiceConnection;

    private String selectedHospitalId;
    private String departmentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_department);
        setTitle(R.string.activity_label_department_create);
        departmentIdEditText = (EditText) findViewById(R.id.department_id_edit_view);
        departmentNameEditText = (EditText) findViewById(R.id.department_name_edit_view);

        Intent startIntent = getIntent();
        if(startIntent.hasExtra(Constants.key_intent_department_id)){
            departmentId = startIntent.getStringExtra(Constants.key_intent_department_id);
            Button saveBtn = (Button) findViewById(R.id.department_save_btn);
            saveBtn.setText(R.string.department_save_edit);
            setTitle(R.string.department_save_edit);
        }

        medicalServiceConnection = new MedicalServiceConnection(new Consumer<MedicalForegroundService>() {
            @Override
            public void apply(MedicalForegroundService medicalService) {
                medicalForegroundService = medicalService;
                init();
            }
        });
        Intent bindIntent = new Intent(this, MedicalForegroundServiceImpl.class);
        bindService(bindIntent, medicalServiceConnection, Context.BIND_AUTO_CREATE);
    }

    public void save(View view){
        String inputDepartmentName = departmentNameEditText.getText().toString();
        String inputDepartmentId = departmentIdEditText.getText().toString();
        Medical medical = medicalForegroundService.getMedicalData();
        Hospital hospital = null;
        for(Hospital h : medical.getHospitalList()){
            if(h.getId().equals(selectedHospitalId)){
                hospital = h;
                break;
            }
        }

        if(departmentId == null){
            List<Department> departmentList = hospital.getDepartmentList();
            if(departmentList == null){
                departmentList = new ArrayList<>();
                hospital.setDepartmentList(departmentList);
            }
            Department department = new Department();
            department.setId(inputDepartmentId);
            department.setName(inputDepartmentName);
            departmentList.add(department);
        } else {
            List<Department> departmentList = hospital.getDepartmentList();
            for(Department department : departmentList){
                if(department.getId().equals(departmentId)){
                    department.setId(inputDepartmentId);
                    department.setName(inputDepartmentName);
                    break;
                }
            }
        }

        medicalForegroundService.saveMedicalData(medical, null);
        Intent intent = new Intent(this, DepartmentListActivity.class);
        startActivity(intent);
    }

    private void init(){
        selectedHospitalId = medicalForegroundService.getTemp(Constants.key_intent_selected_hospital_id);
        if(departmentId != null){
            Medical medical = medicalForegroundService.getMedicalData();
            Hospital hospital = null;
            for(Hospital h : medical.getHospitalList()){
                if(h.getId().equals(selectedHospitalId)){
                    hospital = h;
                    break;
                }
            }
            List<Department> departmentList = hospital.getDepartmentList();
            for(Department department : departmentList){
                if(department.getId().equals(departmentId)){
                    departmentIdEditText.setText(department.getId());
                    departmentNameEditText.setText(department.getName());
                    break;
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(medicalServiceConnection);
    }
}

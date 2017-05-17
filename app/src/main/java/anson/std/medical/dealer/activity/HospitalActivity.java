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
import anson.std.medical.dealer.HandleResult;
import anson.std.medical.dealer.MedicalForegroundService;
import anson.std.medical.dealer.R;
import anson.std.medical.dealer.activity.support.MedicalServiceConnection;
import anson.std.medical.dealer.aservice.MedicalForegroundServiceImpl;
import anson.std.medical.dealer.model.Hospital;
import anson.std.medical.dealer.model.Medical;
import anson.std.medical.dealer.support.Constants;

public class HospitalActivity extends AppCompatActivity {

    private EditText hospitalNameEditText;
    private EditText hospitalIdEditText;

    private MedicalServiceConnection medicalServiceConnection;
    private MedicalForegroundService medicalForegroundService;

    private String hospitalId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital);
        setTitle(R.string.activity_label_hospital_create);
        hospitalNameEditText = (EditText) findViewById(R.id.hospital_name_edit_view);
        hospitalIdEditText = (EditText) findViewById(R.id.hospital_id_edit_view);

        Intent startIntent = getIntent();
        if(startIntent != null && startIntent.hasExtra(Constants.key_intent_hospital_id)){
            hospitalId = startIntent.getStringExtra(Constants.key_intent_hospital_id);
            Button saveBtn = (Button) findViewById(R.id.save_btn);
            saveBtn.setText(R.string.hospital_save_edit);
            setTitle(R.string.activity_label_hospital_edit);
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

    public void saveHospital(View view){
        String hospitalIdEdit = hospitalIdEditText.getText().toString();
        String hospitalNameEdit = hospitalNameEditText.getText().toString();
        Medical medical = medicalForegroundService.getMedicalData();
        if(hospitalId != null){
            List<Hospital> hospitalList = medicalForegroundService.getMedicalData().getHospitalList();
            if(hospitalList != null){
                for(Hospital hospital : hospitalList){
                    if(hospital.getId().equals(hospitalId)){
                        hospital.setId(hospitalIdEdit);
                        hospital.setName(hospitalNameEdit);
                        break;
                    }
                }
            }
        } else {
            List<Hospital> hospitalList = medical.getHospitalList();
            if(hospitalList == null){
                hospitalList = new ArrayList<>();
                medical.setHospitalList(hospitalList);
            }
            Hospital hospital = new Hospital();
            hospital.setId(hospitalIdEdit);
            hospital.setName(hospitalNameEdit);
            hospitalList.add(hospital);
        }
        medicalForegroundService.saveMedicalData(medical, null);
        Intent intent = new Intent(this, HospitalListActivity.class);
        startActivity(intent);
    }

    private void init(){
        if(hospitalId != null){
            List<Hospital> hospitalList = medicalForegroundService.getMedicalData().getHospitalList();
            if(hospitalList != null){
                for(Hospital hospital : hospitalList){
                    if(hospital.getId().equals(hospitalId)){
                        hospitalNameEditText.setText(hospital.getName());
                        hospitalIdEditText.setText(hospital.getId());
                        break;
                    }
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

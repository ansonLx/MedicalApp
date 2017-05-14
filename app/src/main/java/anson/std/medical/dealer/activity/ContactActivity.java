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
import anson.std.medical.dealer.model.Medical;
import anson.std.medical.dealer.model.Patient;
import anson.std.medical.dealer.support.Constants;

public class ContactActivity extends AppCompatActivity {

    private EditText contactNameEditText;
    private EditText contactIdEditText;
    private EditText hospitalCardEditText;
    private EditText medicareCardEditText;

    private MedicalForegroundService medicalService;
    private MedicalServiceConnection medicalServiceConnection;
    private String contactId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        contactNameEditText = (EditText) findViewById(R.id.contact_name_edit_view);
        contactIdEditText = (EditText) findViewById(R.id.contact_id_edit_view);
        hospitalCardEditText = (EditText) findViewById(R.id.contact_hospital_card_edit_view);
        medicareCardEditText = (EditText) findViewById(R.id.contact_medicare_card_edit_view);

        Intent startActivityIntent = getIntent();
        if(startActivityIntent != null){
            if(startActivityIntent.hasExtra(Constants.key_intent_contact_id)){
                contactId = startActivityIntent.getStringExtra(Constants.key_intent_contact_id);
                Button btn = (Button) findViewById(R.id.save_btn);
                btn.setText(R.string.edit_contact);
            }
        }

        // bind service
        Intent bindIntent = new Intent(this, MedicalForegroundServiceImpl.class);
        medicalServiceConnection = new MedicalServiceConnection(new Consumer<MedicalForegroundService>() {
            @Override
            public void apply(MedicalForegroundService medicalForegroundService) {
                medicalService = medicalForegroundService;
                initEdit();
            }
        });
        bindService(bindIntent, medicalServiceConnection, Context.BIND_AUTO_CREATE);
    }

    public void addContact(View view){
        String inputContactId = contactIdEditText.getText().toString();
        String inputContactName = contactNameEditText.getText().toString();
        String inputHospitalCard = hospitalCardEditText.getText().toString();
        String inputMedicareCard = medicareCardEditText.getText().toString();

        Medical medical = medicalService.getMedicalData();
        if(contactId != null){
            List<Patient> patientList = medical.getPatientList();
            for(Patient patient : patientList){
                if(contactId.equals(patient.getId())){
                    patient.setId(inputContactId);
                    patient.setName(inputContactName);
                    patient.setHospitalCard(inputHospitalCard);
                    patient.setMedicareCard(inputMedicareCard);
                }
            }
        } else {
            List<Patient> patientList = medical.getPatientList();
            if(patientList == null){
                patientList = new ArrayList<>();
                medical.setPatientList(patientList);
            }
            Patient patient = new Patient();
            patient.setId(inputContactId);
            patient.setName(inputContactName);
            patient.setHospitalCard(inputHospitalCard);
            patient.setMedicareCard(inputMedicareCard);
            patientList.add(patient);
        }

        medicalService.saveMedicalData(medical, null);
        Intent back2Contacts = new Intent(this, ContactListActivity.class);
        startActivity(back2Contacts);
    }

    private void initEdit(){
        if(contactId != null){
            List<Patient> patientList = medicalService.getMedicalData().getPatientList();
            if(patientList != null){
                for(Patient patient : patientList){
                    if(patient.getId().equals(contactId)){
                        contactNameEditText.setText(patient.getName());
                        contactIdEditText.setText(patient.getId());
                        hospitalCardEditText.setText(patient.getHospitalCard());
                        medicareCardEditText.setText(patient.getMedicareCard());
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

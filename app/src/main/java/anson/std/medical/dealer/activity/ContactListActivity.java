package anson.std.medical.dealer.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.lang.reflect.Method;
import java.util.List;

import anson.std.medical.dealer.Consumer;
import anson.std.medical.dealer.MedicalForegroundService;
import anson.std.medical.dealer.R;
import anson.std.medical.dealer.activity.support.MedicalConfirmDialog;
import anson.std.medical.dealer.activity.support.MedicalListViewArrayAdapter;
import anson.std.medical.dealer.activity.support.MedicalServiceConnection;
import anson.std.medical.dealer.aservice.MedicalForegroundServiceImpl;
import anson.std.medical.dealer.model.Medical;
import anson.std.medical.dealer.model.Patient;
import anson.std.medical.dealer.support.Constants;

public class ContactListActivity extends AppCompatActivity {

    private Context context;
    private ListView listView;
    private TextView nameView;
    private MedicalForegroundService medicalService;
    private MedicalServiceConnection medicalServiceConnection;
    private Method getNameMethod;
    private MedicalListViewArrayAdapter<Patient> listViewAdapter;
    private MedicalConfirmDialog<Patient> confirmDialog;

    public ContactListActivity() {
        try {
            getNameMethod = Patient.class.getMethod("getName");
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);

        context = this;
        listView = (ListView) findViewById(R.id.contact_list_view);
        nameView = (TextView) findViewById(R.id.checked_contact_name_view);

        confirmDialog = new MedicalConfirmDialog<>(context);

        listViewAdapter = new MedicalListViewArrayAdapter<>(context, null, getNameMethod, new Consumer<Patient>() {
            @Override
            public void apply(Patient patient) {
                Intent intent = new Intent(context, ContactActivity.class);
                intent.putExtra(Constants.key_intent_contact_id, patient.getId());
                startActivity(intent);
            }
        }, new Consumer<Patient>() {
            @Override
            public void apply(Patient patient) {
                confirmDialog.openConfirmDialog(getString(R.string.del_message), patient, new Consumer<Patient>() {
                    @Override
                    public void apply(Patient patient) {
                        Medical medical = medicalService.getMedicalData();
                        List<Patient> patientList = medical.getPatientList();
                        int index = -1;
                        for (Patient p : patientList) {
                            if (p.getId().equals(patient.getId())) {
                                index = patientList.indexOf(p);
                            }
                        }
                        if (index != -1) {
                            patientList.remove(index);
                            medicalService.saveMedicalData(medical, null);
                            nameView.setText("");
                            listViewAdapter.flushData(patientList);
                            medicalService.clearTemp(true);
                        }
                    }
                });
            }
        });
        listView.setAdapter(listViewAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String checkedName = listViewAdapter.onItemClick(view);
                nameView.setText(checkedName);
            }
        });

        // bind service
        Intent bindIntent = new Intent(this, MedicalForegroundServiceImpl.class);
        medicalServiceConnection = new MedicalServiceConnection(new Consumer<MedicalForegroundService>() {
            @Override
            public void apply(MedicalForegroundService medicalForegroundService) {
                medicalService = medicalForegroundService;
                initContact();
            }
        });
        bindService(bindIntent, medicalServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(medicalServiceConnection);
    }

    private void initContact() {
        List<Patient> patients = medicalService.getMedicalData().getPatientList();
        if (patients != null && !patients.isEmpty()) {
            listViewAdapter.flushData(patients);
        }
    }

    public void createContact(View view) {
        Intent intent = new Intent(this, ContactActivity.class);
        startActivity(intent);
    }

    public void selectContact(View view) {
        Patient patient = listViewAdapter.getSelectedItem();
        if (patient != null) {
            medicalService.setTemp(Constants.key_intent_selected_contact_id, patient.getId());
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(Constants.key_intent_selected_contact_id, patient.getId());
            startActivity(intent);
        } else {
            medicalService.clearTemp(true);
        }
    }
}

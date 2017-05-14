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
import anson.std.medical.dealer.activity.support.MedicalListViewArrayAdapter;
import anson.std.medical.dealer.activity.support.MedicalServiceConnection;
import anson.std.medical.dealer.aservice.MedicalForegroundServiceImpl;
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
            listViewAdapter = new MedicalListViewArrayAdapter<>(context, patients, getNameMethod, new Consumer<Patient>() {
                @Override
                public void apply(Patient patient) {
                    Intent intent = new Intent(context, ContactActivity.class);
                    intent.putExtra(Constants.key_intent_contact_id, patient.getId());
                    startActivity(intent);
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
        }
    }


    public void createContact(View view) {
        Intent intent = new Intent(this, ContactActivity.class);
        startActivity(intent);
    }

    public void selectContact(View view) {
        Patient patient = listViewAdapter.getSelectedItem();
        if(patient != null){
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(Constants.key_intent_contact_id, patient.getId());
            startActivity(intent);
        }
    }
}

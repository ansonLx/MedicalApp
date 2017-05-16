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
import anson.std.medical.dealer.model.Hospital;
import anson.std.medical.dealer.model.Medical;
import anson.std.medical.dealer.support.Constants;

public class HospitalListActivity extends AppCompatActivity {

    private Context context;
    private ListView listView;
    private TextView nameView;

    private MedicalServiceConnection medicalServiceConnection;
    private MedicalForegroundService medicalForegroundService;
    private MedicalListViewArrayAdapter<Hospital> medicalListViewArrayAdapter;
    private MedicalConfirmDialog<Hospital> delConfirmDialog;
    private Method getNameMethod;

    public HospitalListActivity() {
        try {
            getNameMethod = Hospital.class.getMethod("getName");
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_list);
        context = this;
        listView = (ListView) findViewById(R.id.hospital_list_view);
        nameView = (TextView) findViewById(R.id.hospital_selected_text_view);

        delConfirmDialog = new MedicalConfirmDialog<>(this);

        medicalServiceConnection = new MedicalServiceConnection(new Consumer<MedicalForegroundService>() {
            @Override
            public void apply(MedicalForegroundService medicalService) {
                medicalForegroundService = medicalService;
                initData();
            }
        });
        Intent bindIntent = new Intent(this, MedicalForegroundServiceImpl.class);
        bindService(bindIntent, medicalServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(medicalServiceConnection);
    }

    public void toCreateHospital(View view) {
        Intent intent = new Intent(this, HospitalActivity.class);
        startActivity(intent);
    }

    public void selectHospital(View view) {
        Hospital hospital = medicalListViewArrayAdapter.getSelectedItem();
        if (hospital != null) {
            medicalForegroundService.setTemp(Constants.key_intent_selected_hospital_id, hospital.getId());
            Intent intent = new Intent(this, DepartmentListActivity.class);
            startActivity(intent);
        }
    }

    private void initData() {
        final Medical medical = medicalForegroundService.getMedicalData();
        if (medical != null) {
            List<Hospital> hospitalList = medical.getHospitalList();
            if (hospitalList != null) {
                medicalListViewArrayAdapter = new MedicalListViewArrayAdapter<>(context, hospitalList, getNameMethod, new Consumer<Hospital>() {
                    @Override
                    public void apply(Hospital hospital) {
                        Intent intent = new Intent(context, HospitalActivity.class);
                        intent.putExtra(Constants.key_intent_hospital_id, hospital.getId());
                        startActivity(intent);
                    }
                }, new Consumer<Hospital>() {
                    @Override
                    public void apply(Hospital hospital) {
                        delConfirmDialog.openConfirmDialog(getString(R.string.del_message), hospital, new Consumer<Hospital>() {
                            @Override
                            public void apply(Hospital delHospital) {
                                Medical m = medicalForegroundService.getMedicalData();
                                List<Hospital> hospitals = m.getHospitalList();
                                int index = -1;
                                for (Hospital h : hospitals) {
                                    if (h.getId().equals(delHospital.getId())) {
                                        index = hospitals.indexOf(h);
                                    }
                                }
                                if (index != -1) {
                                    hospitals.remove(index);
                                    medicalForegroundService.saveMedicalData(medical, null);
                                    nameView.setText("");
                                    medicalForegroundService.clearTemp(false);
                                    medicalListViewArrayAdapter.flushData(hospitals);
                                }
                            }
                        });
                    }
                });
                listView.setAdapter(medicalListViewArrayAdapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String checkedName = medicalListViewArrayAdapter.onItemClick(view);
                        nameView.setText(checkedName);
                    }
                });
            }
        }
    }
}

package anson.std.medical.dealer.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import anson.std.medical.dealer.Consumer;
import anson.std.medical.dealer.MedicalForegroundService;
import anson.std.medical.dealer.R;
import anson.std.medical.dealer.activity.support.MedicalConfirmDialog;
import anson.std.medical.dealer.activity.support.MedicalDoctorListViewAdapter;
import anson.std.medical.dealer.activity.support.MedicalServiceConnection;
import anson.std.medical.dealer.aservice.MedicalForegroundServiceImpl;
import anson.std.medical.dealer.model.Department;
import anson.std.medical.dealer.model.Doctor;
import anson.std.medical.dealer.model.Hospital;
import anson.std.medical.dealer.model.Medical;
import anson.std.medical.dealer.support.Constants;

public class DoctorListActivity extends AppCompatActivity {

    private Context context;
    private ListView listView;
    private TextView nameView;

    private MedicalServiceConnection medicalServiceConnection;
    private MedicalForegroundService medicalForegroundService;
    private MedicalDoctorListViewAdapter medicalDoctorListViewAdapter;
    private MedicalConfirmDialog<Doctor> delConfirmDialog;

    private String selectedHospitalId;
    private String selectedDepartmentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_list);
        setTitle(R.string.activity_label_doctor);
        context = this;
        listView = (ListView) findViewById(R.id.doctor_list_view);
        nameView = (TextView) findViewById(R.id.doctor_name_input);

        delConfirmDialog = new MedicalConfirmDialog(this);

        medicalDoctorListViewAdapter = new MedicalDoctorListViewAdapter(context, getModifyInListCallback(), getDelInListCallback());
        listView.setAdapter(medicalDoctorListViewAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String doctorName = medicalDoctorListViewAdapter.onItemClick(view, null, null);
                nameView.setText(doctorName);
            }
        });

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

    public void createDoctor(View view) {
        Intent intent = new Intent(this, DoctorActivity.class);
        startActivity(intent);
    }

    public void selectDoctor(View view) {
        Doctor doctor = medicalDoctorListViewAdapter.getSelectDoctor();
        if (doctor != null) {
            medicalForegroundService.setTemp(Constants.key_intent_selected_doctor_id, doctor.getId());
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(Constants.key_intent_selected_doctor_id, doctor.getId());
            startActivity(intent);
        }
    }

    private void init() {
        selectedHospitalId = medicalForegroundService.getTemp(Constants.key_intent_selected_hospital_id);
        selectedDepartmentId = medicalForegroundService.getTemp(Constants.key_intent_selected_department_id);
        Medical medical = medicalForegroundService.getMedicalData();
        if (medical != null) {
            List<Hospital> hospitalList = medical.getHospitalList();
            if (hospitalList != null) {
                Hospital hospital = null;
                for (Hospital h : hospitalList) {
                    if (h.getId().equals(selectedHospitalId)) {
                        hospital = h;
                        break;
                    }
                }
                if (hospital != null) {
                    List<Department> departmentList = hospital.getDepartmentList();
                    if (departmentList != null) {
                        Department department = null;
                        for (Department d : departmentList) {
                            if (d.getId().equals(selectedDepartmentId)) {
                                department = d;
                                break;
                            }
                        }
                        if (department != null) {
                            List<Doctor> doctorList = department.getDoctorList();
                            if (doctorList != null) {
                                medicalDoctorListViewAdapter.addData(doctorList);
                            }
                        }
                    }
                }
            }
        }
    }

    private Consumer<Doctor> getModifyInListCallback() {
        return new Consumer<Doctor>() {
            @Override
            public void apply(Doctor doctor) {
                Intent intent = new Intent(context, DoctorActivity.class);
                intent.putExtra(Constants.key_intent_doctor_id, doctor.getId());
                startActivity(intent);
            }
        };
    }

    private Consumer<Doctor> getDelInListCallback() {
        return new Consumer<Doctor>() {
            @Override
            public void apply(Doctor doctor) {
                delConfirmDialog.openConfirmDialog(getString(R.string.del_message), doctor, new Consumer<Doctor>() {
                    @Override
                    public void apply(Doctor doctor) {
                        Medical medical = medicalForegroundService.getMedicalData();
                        Hospital hospital = null;
                        String hospitalId = medicalForegroundService.getTemp(Constants.key_intent_selected_hospital_id);
                        for (Hospital h : medical.getHospitalList()) {
                            if (h.getId().equals(hospitalId)) {
                                hospital = h;
                                break;
                            }
                        }
                        if (hospital != null) {
                            String departmentId = medicalForegroundService.getTemp(Constants.key_intent_selected_department_id);
                            Department department = null;
                            for (Department d : hospital.getDepartmentList()) {
                                if (d.getId().equals(departmentId)) {
                                    department = d;
                                    break;
                                }
                            }
                            if (department != null) {
                                List<Doctor> doctorList = department.getDoctorList();
                                int index = -1;
                                for (Doctor d : doctorList) {
                                    if (d.getId().equals(doctor.getId())) {
                                        index = doctorList.indexOf(d);
                                    }
                                }
                                if (index != -1) {
                                    doctorList.remove(index);
                                    nameView.setText("");
                                    medicalForegroundService.saveMedicalData(medical, null);
                                    medicalDoctorListViewAdapter.flushData();
                                    medicalDoctorListViewAdapter.addData(doctorList);
                                }
                            }
                        }
                    }
                });
            }
        };
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(medicalServiceConnection);
    }
}

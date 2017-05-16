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
import anson.std.medical.dealer.model.Department;
import anson.std.medical.dealer.model.Hospital;
import anson.std.medical.dealer.model.Medical;
import anson.std.medical.dealer.support.Constants;

public class DepartmentListActivity extends AppCompatActivity {

    private Context context;

    private ListView departmentListView;
    private TextView nameView;

    private MedicalForegroundService medicalForegroundService;
    private MedicalServiceConnection medicalServiceConnection;
    private MedicalListViewArrayAdapter<Department> departmentMedicalListViewArrayAdapter;
    private MedicalConfirmDialog<Department> delConfirmDialog;

    private String selectedHospitalId;
    private Method getDepartmentNameMethod;

    public DepartmentListActivity() {
        try {
            getDepartmentNameMethod = Department.class.getMethod("getName");
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_department_list);

        context = this;
        departmentListView = (ListView) findViewById(R.id.department_list_view);
        nameView = (TextView) findViewById(R.id.department_name_view);
        delConfirmDialog = new MedicalConfirmDialog<>(context);

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

    public void toCreateDepartment(View view) {
        Intent intent = new Intent(this, DepartmentActivity.class);
        startActivity(intent);
    }

    public void selectDepartment(View view) {
        Department department = departmentMedicalListViewArrayAdapter.getSelectedItem();
        if (department != null) {
            medicalForegroundService.setTemp(Constants.key_intent_selected_department_id, department.getId());
            Intent intent = new Intent(this, DoctorListActivity.class);
            startActivity(intent);
        }
    }

    private void init() {
        selectedHospitalId = medicalForegroundService.getTemp(Constants.key_intent_selected_hospital_id);
        List<Hospital> hospitalList = medicalForegroundService.getMedicalData().getHospitalList();
        Hospital hospital = null;
        for (Hospital h : hospitalList) {
            if (h.getId().equals(selectedHospitalId)) {
                hospital = h;
                break;
            }
        }
        List<Department> departmentList = hospital.getDepartmentList();
        if (departmentList != null) {
            departmentMedicalListViewArrayAdapter = new MedicalListViewArrayAdapter<>(context, departmentList, getDepartmentNameMethod, new Consumer<Department>() {
                @Override
                public void apply(Department department) {
                    Intent intent = new Intent(context, DepartmentActivity.class);
                    intent.putExtra(Constants.key_intent_department_id, department.getId());
                    startActivity(intent);
                }
            }, new Consumer<Department>() {
                @Override
                public void apply(Department department) {
                    delConfirmDialog.openConfirmDialog(getString(R.string.del_message), department, new Consumer<Department>() {
                        @Override
                        public void apply(Department delDepartment) {
                            Medical medical = medicalForegroundService.getMedicalData();
                            List<Hospital> hospitals = medical.getHospitalList();
                            Hospital hospital1 = null;
                            for(Hospital h : hospitals){
                                if(h.getId().equals(selectedHospitalId)){
                                    hospital1 = h;
                                    break;
                                }
                            }
                            if(hospital1 != null){
                                List<Department> departments = hospital1.getDepartmentList();
                                int index = -1;
                                for(Department d : departments){
                                    if(d.getId().equals(delDepartment.getId())){
                                        index = departments.indexOf(d);
                                        break;
                                    }
                                }
                                if(index != -1){
                                    departments.remove(index);
                                    nameView.setText("");
                                    medicalForegroundService.saveMedicalData(medical, null);
                                    medicalForegroundService.clearTemp(false);
                                    medicalForegroundService.setTemp(Constants.key_intent_selected_hospital_id, selectedHospitalId);
                                    departmentMedicalListViewArrayAdapter.flushData(departments);
                                }
                            }
                        }
                    });
                }
            });
            departmentListView.setAdapter(departmentMedicalListViewArrayAdapter);
            departmentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String checkedName = departmentMedicalListViewArrayAdapter.onItemClick(view);
                    nameView.setText(checkedName);
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(medicalServiceConnection);
    }
}

package anson.std.medical.dealer.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import anson.std.medical.dealer.Consumer;
import anson.std.medical.dealer.HandleResult;
import anson.std.medical.dealer.MedicalForegroundService;
import anson.std.medical.dealer.R;
import anson.std.medical.dealer.activity.support.DatePicker;
import anson.std.medical.dealer.activity.support.MedicalActivityHandler;
import anson.std.medical.dealer.activity.support.MedicalDoctorListViewAdapter;
import anson.std.medical.dealer.activity.support.MedicalServiceConnection;
import anson.std.medical.dealer.aservice.MedicalForegroundServiceImpl;
import anson.std.medical.dealer.model.Department;
import anson.std.medical.dealer.model.Doctor;
import anson.std.medical.dealer.model.Hospital;
import anson.std.medical.dealer.model.Medical;
import anson.std.medical.dealer.model.MedicalResource;
import anson.std.medical.dealer.model.Patient;
import anson.std.medical.dealer.model.TargetDate;
import anson.std.medical.dealer.support.Constants;

public class DoctorActivity extends AppCompatActivity {

    private Context context;
    private EditText doctorNameInput;
    private TextView doctorIdView;
    private TextView doctorTitleView;
    private TextView doctorSkillView;
    private TextView doctorTimerView;
    private TextView doctorSearchView;
    private ListView searchListView;
    private ProgressDialog processingDialog;

    private MedicalDoctorListViewAdapter medicalDoctorListViewAdapter;
    private MedicalServiceConnection medicalServiceConnection;
    private static MedicalActivityHandler handler;
    private MedicalForegroundService medicalForegroundService;
    private String editDoctorId;
    private DatePicker doctorTimerDatePicker;
    private DatePicker doctorSearchDatePicker;

    private TargetDate searchDate;
    private List<TargetDate> doctorTimerDateList;

    private void initView() {
        this.context = this;
        this.doctorIdView = (TextView) findViewById(R.id.doctor_id_view);
        this.doctorNameInput = (EditText) findViewById(R.id.doctor_name_input);
        this.doctorTitleView = (TextView) findViewById(R.id.doctor_title_view);
        this.doctorSkillView = (TextView) findViewById(R.id.doctor_skill_view);
        this.doctorTimerView = (TextView) findViewById(R.id.doctor_timer_view);
        this.doctorSearchView = (TextView) findViewById(R.id.doctor_search_view);
        this.searchListView = (ListView) findViewById(R.id.doctor_list_view);
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor);
        initView();
        initDatePicker();
        initListView();
        initLoadingDialog();

        if (handler == null) {
            handler = new MedicalActivityHandler();
        }

        doctorSearchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doctorSearchDatePicker.showDialog();
            }
        });
        doctorTimerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doctorTimerDatePicker.showDialog();
            }
        });

        Intent startIntent = getIntent();
        if (startIntent.hasExtra(Constants.key_intent_doctor_id)) {
            editDoctorId = startIntent.getStringExtra(Constants.key_intent_doctor_id);
            Button saveBtn = (Button) findViewById(R.id.doctor_create_btn);
            saveBtn.setText(R.string.doctor_save);
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

    private void init() {
        if (editDoctorId != null) {
            Doctor editDoctor = medicalForegroundService.getDoctorById(editDoctorId);
            if (editDoctor != null) {
                doctorIdView.setText(editDoctor.getId());
                doctorNameInput.setText(editDoctor.getName());
                doctorTitleView.setText(editDoctor.getTitle());
                doctorSkillView.setText(editDoctor.getSkill());
                doctorTimerDateList = editDoctor.getTargetDateList();
                if (doctorTimerDateList != null) {
                    doctorTimerView.setText(TargetDate.list2String(doctorTimerDateList));
                    doctorTimerDatePicker.setInitDateForWeek(doctorTimerDateList);
                }
            }
        }

        // test
        List<Doctor> testDoctorList = new ArrayList<>();
        for (int i = 0; i != 20; i++) {
            Doctor doctor = new Doctor();
            doctor.setId("doctorId_" + i);
            doctor.setName("doctorName_" + i);
            doctor.setTitle("doctorTitle_" + i);
            doctor.setSkill("doctorSkill_" + i);
            testDoctorList.add(doctor);
        }
//        medicalDoctorListViewAdapter.addData(testDoctorList);
    }

    private void initDatePicker() {
        doctorTimerDatePicker = DatePicker.getInstance(this).setMultiChoice(true).setShowDate(false)
                .setShowWeek(true).setPickerCallback(new Consumer<List<TargetDate>>() {
                    @Override
                    public void apply(List<TargetDate> targetDates) {
                        if (targetDates.isEmpty()) {
                            if (editDoctorId == null) {
                                doctorTimerDateList = null;
                                doctorTimerView.setText(R.string.doctor_timer);
                            } else {
                                Doctor editDoctor = medicalForegroundService.getDoctorById(editDoctorId);
                                doctorTimerDateList = editDoctor.getTargetDateList();
                                if (doctorTimerDateList != null) {
                                    doctorTimerView.setText(TargetDate.list2String(doctorTimerDateList));
                                } else {
                                    doctorTimerView.setText(R.string.doctor_timer);
                                }
                            }
                        } else {
                            doctorTimerDateList = targetDates;
                            doctorTimerView.setText(TargetDate.list2String(targetDates));
                        }
                    }
                }).build();

        doctorSearchDatePicker = DatePicker.getInstance(this).setPickerCallback(new Consumer<List<TargetDate>>() {
            @Override
            public void apply(List<TargetDate> targetDates) {
                if (!targetDates.isEmpty()) {
                    searchDate = targetDates.get(0);
                    doctorSearchView.setText(TargetDate.list2String(Arrays.asList(searchDate)));

                    // load from 114
                    loadMedicalResource();
                } else {
                    searchDate = null;
                    doctorSearchView.setText(R.string.doctor_search);
                }
            }
        }).build();
    }

    private void initListView() {
        medicalDoctorListViewAdapter = new MedicalDoctorListViewAdapter(this);
        searchListView.setAdapter(medicalDoctorListViewAdapter);
        searchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                medicalDoctorListViewAdapter.onItemClick(view, new Consumer<Doctor>() {
                    @Override
                    public void apply(Doctor doctor) {
                        doctorIdView.setText(doctor.getId());
                        doctorNameInput.setText(doctor.getName());
                        doctorTitleView.setText(doctor.getTitle());
                        doctorSkillView.setText(doctor.getSkill());
                    }
                }, new Consumer<Doctor>() {
                    @Override
                    public void apply(Doctor doctor) {
                        if (editDoctorId != null) {
                            Doctor editDoctor = medicalForegroundService.getDoctorById(editDoctorId);
                            if (editDoctor != null) {
                                doctorIdView.setText(editDoctor.getId());
                                doctorNameInput.setText(editDoctor.getName());
                                doctorTitleView.setText(editDoctor.getTitle());
                                doctorSkillView.setText(editDoctor.getSkill());
                                doctorTimerView.setText(TargetDate.list2String(editDoctor.getTargetDateList()));
                            }
                        } else {
                            doctorIdView.setText(getText(R.string.doctor_info_unknow));
                            doctorNameInput.setText("");
                            doctorTitleView.setText(getText(R.string.doctor_info_unknow));
                            doctorSkillView.setText(getText(R.string.doctor_info_unknow));
                        }
                    }
                });
            }
        });
    }

    private void initLoadingDialog() {
        processingDialog = new ProgressDialog(context);
        processingDialog.setMessage(getString(R.string.loading_message));
        processingDialog.setIndeterminate(true);
        processingDialog.setCancelable(false);
    }

    private void loadMedicalResource() {
        if (searchDate != null) {
            medicalDoctorListViewAdapter.flushData();
            String hospitalId = medicalForegroundService.getTemp(Constants.key_intent_selected_hospital_id);
            String departmentId = medicalForegroundService.getTemp(Constants.key_intent_selected_department_id);
            if (searchDate.isIfFullDay()) {
                final Consumer<HandleResult> pmCallback = new Consumer<HandleResult>() {
                    @Override
                    public void apply(HandleResult handleResult) {
                        Message message = handler.obtainMessage();
                        Object[] os = new Object[]{getProcessMedicalResourceListCallback(), handleResult};
                        message.obj = os;
                        handler.sendMessage(message);
                    }
                };
                Consumer<HandleResult> amCallback = new Consumer<HandleResult>() {
                    @Override
                    public void apply(HandleResult handleResult) {
                        Message message = handler.obtainMessage();
                        Object[] os = new Object[]{getProcessMedicalResourceListCallbackForFullDayAm(pmCallback), handleResult};
                        message.obj = os;
                        handler.sendMessage(message);
                    }
                };
                openProcessingDialog();
                medicalForegroundService.listMedicalResource(hospitalId, departmentId, searchDate.getDateStr(), true, amCallback);
            } else {
                Consumer<HandleResult> callback = new Consumer<HandleResult>() {
                    @Override
                    public void apply(HandleResult handleResult) {
                        Message message = handler.obtainMessage();
                        Object[] os = new Object[]{getProcessMedicalResourceListCallback(), handleResult};
                        message.obj = os;
                        handler.sendMessage(message);
                    }
                };
                openProcessingDialog();
                medicalForegroundService.listMedicalResource(hospitalId, departmentId, searchDate.getDateStr(), searchDate.isAmPm(), callback);
            }
        }
    }

    private void addMedicalResourceToListView(List<MedicalResource> medicalResources) {
        if (medicalResources != null && !medicalResources.isEmpty()) {
            List<Doctor> doctorList = new ArrayList<>();
            for (MedicalResource mr : medicalResources) {
                Doctor doctor = new Doctor();
                doctor.setName(mr.getDoctorName());
                doctor.setTitle(mr.getDoctorTitleName());
                doctor.setId(mr.getDoctorId());
                doctor.setSkill(mr.getSkill());
                doctorList.add(doctor);
            }
            medicalDoctorListViewAdapter.addData(doctorList);
        }
    }

    private Consumer<HandleResult> getProcessMedicalResourceListCallback() {
        return new Consumer<HandleResult>() {
            @Override
            public void apply(HandleResult handleResult) {
                List<MedicalResource> medicalResourceList = handleResult.getResourceList();
                addMedicalResourceToListView(medicalResourceList);
                closeProcessingDialog();
            }
        };
    }

    private Consumer<HandleResult> getProcessMedicalResourceListCallbackForFullDayAm(final Consumer<HandleResult> pmCallback) {
        return new Consumer<HandleResult>() {
            @Override
            public void apply(HandleResult handleResult) {
                String hospitalId = medicalForegroundService.getTemp(Constants.key_intent_selected_hospital_id);
                String departmentId = medicalForegroundService.getTemp(Constants.key_intent_selected_department_id);
                List<MedicalResource> medicalResourceList = handleResult.getResourceList();
                addMedicalResourceToListView(medicalResourceList);
                medicalForegroundService.listMedicalResource(hospitalId, departmentId, searchDate.getDateStr(), false, pmCallback);
            }
        };
    }

    private void openProcessingDialog() {
        processingDialog.show();
    }

    private void closeProcessingDialog() {
        processingDialog.dismiss();
    }

    public void saveDoctor(View view) {
        String doctorName = doctorNameInput.getText().toString().trim();
        String doctorId = doctorIdView.getText().toString();
        String doctorTitle = doctorTitleView.getText().toString();
        String doctorSkill = doctorSkillView.getText().toString();
        Doctor doctor;
        Medical medical = medicalForegroundService.getMedicalData();
        if (editDoctorId != null) {
            doctor = medicalForegroundService.getDoctorById(editDoctorId);
        } else {
            doctor = new Doctor();
            Hospital hospital = null;
            String selectedHospitalId = medicalForegroundService.getTemp(Constants.key_intent_selected_hospital_id);
            for (Hospital h : medical.getHospitalList()) {
                if (h.getId().equals(selectedHospitalId)) {
                    hospital = h;
                    break;
                }
            }
            if (hospital != null) {
                Department department = null;
                String departmentId = medicalForegroundService.getTemp(Constants.key_intent_selected_department_id);
                for (Department d : hospital.getDepartmentList()) {
                    if (d.getId().equals(departmentId)) {
                        department = d;
                        break;
                    }
                }
                if (department != null) {
                    List<Doctor> doctorList = department.getDoctorList();
                    if (doctorList == null) {
                        doctorList = new ArrayList<>();
                        department.setDoctorList(doctorList);
                    }
                    doctorList.add(doctor);
                }
            }
        }
        doctor.setName(doctorName);
        doctor.setId(doctorId);
        doctor.setTitle(doctorTitle);
        doctor.setSkill(doctorSkill);
        doctor.setTargetDateList(doctorTimerDateList);
        medicalForegroundService.saveMedicalData(medical, null);

        Intent intent = new Intent(this, DoctorListActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(medicalServiceConnection);
    }
}

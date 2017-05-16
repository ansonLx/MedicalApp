package anson.std.medical.dealer.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import anson.std.medical.dealer.MedicalForegroundService;
import anson.std.medical.dealer.R;
import anson.std.medical.dealer.Consumer;
import anson.std.medical.dealer.HandleResult;
import anson.std.medical.dealer.activity.support.DatePicker;
import anson.std.medical.dealer.activity.support.MedicalActivityHandler;
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
import anson.std.medical.dealer.support.LogUtil;

public class MainActivity extends AppCompatActivity {

    private static final Uri content_sms = Uri.parse("content://sms/");

    private TextView logView;
    private TextView patientView;
    private TextView doctorView;
    private TextView dateView;
    private static MedicalActivityHandler handler;

    private MedicalForegroundService medicalService;
    private MedicalServiceConnection medicalServiceConnection;
    private SmsObserver smsObserver;
    private DatePicker datePicker;

    private String patientId;
    private String doctorId;
    private TargetDate targetDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initAndroidPermissions();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initComponents();
        initDatePicker();
        registerSmsContentChangeObserver();

        // handler
        if (handler == null) {
            handler = new MedicalActivityHandler();
        }

        // start bg service and bind to it
        Intent startServiceIntent = new Intent(this, MedicalForegroundServiceImpl.class);
        startService(startServiceIntent);
        Intent bindIntent = new Intent(this, MedicalForegroundServiceImpl.class);
        medicalServiceConnection = new MedicalServiceConnection(new Consumer<MedicalForegroundService>() {
            @Override
            public void apply(MedicalForegroundService medicalForegroundService) {
                medicalService = medicalForegroundService;
                initData();
            }
        });
        bindService(bindIntent, medicalServiceConnection, Context.BIND_AUTO_CREATE);

        LogUtil.log("main activity on create");

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(medicalServiceConnection);
        unregisterSmsContentObserver();
    }

    public void toSelectContact(View view) {
        Intent intent = new Intent(this, ContactListActivity.class);
        startActivity(intent);
    }

    public void toSelectDoctor(View view) {
        Intent intent = new Intent(this, HospitalListActivity.class);
        startActivity(intent);
    }

    public void selectDate(View view) {
        datePicker.showDialog();
    }

    public void start(View view){
        LogUtil.logView("start!");
    }

    private void initComponents() {
        logView = (TextView) findViewById(R.id.log_view);
        logView.setMovementMethod(new ScrollingMovementMethod());
        patientView = (TextView) findViewById(R.id.patient_view);
        doctorView = (TextView) findViewById(R.id.doctor_view);
        dateView = (TextView) findViewById(R.id.date_view);
    }

    private void initAndroidPermissions() {
        if (ContextCompat.checkSelfPermission(this, "android.permission.READ_SMS") != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{"android.permission.READ_SMS"}, 1);
            LogUtil.log("no sms read permission");
        }
        if (ActivityCompat.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE") != PackageManager.PERMISSION_GRANTED) {
            LogUtil.log("no external store write permission");
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE",
                    "android.permission.READ_EXTERNAL_STORAGE"}, 1);
        }
    }

    private void initDatePicker() {
        datePicker = DatePicker.getInstance(this).setShowDate(true).setShowWeek(true).setMultiChoice(false)
                .setPickerCallback(new Consumer<List<TargetDate>>() {
                    @Override
                    public void apply(List<TargetDate> targetDates) {
                        if (targetDates.isEmpty()) {
                            dateView.setText(R.string.info_view_text);
                            targetDate = null;
                        } else {
                            targetDate = targetDates.get(0);
                            dateView.setText(TargetDate.list2String(targetDates));
                        }
                    }
                }).build();
    }

    private void initData() {
        medicalService.loadMedicalData(new Consumer<HandleResult>() {

            @Override
            public void apply(HandleResult handleResult) {
                Message message = handler.obtainMessage();
                Object[] os = new Object[2];
                os[0] = new Consumer<HandleResult>() {

                    @Override
                    public void apply(HandleResult handleResult) {
                        initMedicalData(handleResult);
                    }
                };
                os[1] = handleResult;
                message.obj = os;
                handler.sendMessage(message);
            }
        });
    }

    private void initMedicalData(HandleResult handleResult) {
        if (!handleResult.isOccurError()) {
            medicalService.login114();

            Medical medical = medicalService.getMedicalData();
            patientId = medicalService.getTemp(Constants.key_intent_selected_contact_id);
            doctorId = medicalService.getTemp(Constants.key_intent_selected_doctor_id);

            // set contact name
            if (patientId != null) {
                List<Patient> patientList = medical.getPatientList();
                if (patientList != null) {
                    for (Patient patient : patientList) {
                        if (patient.getId().equals(patientId)) {
                            patientView.setText(patient.getName());
                            break;
                        }
                    }
                }
            } else {
                medicalService.clearTemp(true);
            }

            // set doctor info
            if (doctorId == null) {
                medicalService.clearTemp(false);
            } else {
                String hospitalId = medicalService.getTemp(Constants.key_intent_selected_hospital_id);
                String departmentId = medicalService.getTemp(Constants.key_intent_selected_department_id);
                String doctorId = medicalService.getTemp(Constants.key_intent_selected_doctor_id);
                Hospital hospital = null;
                Department department = null;
                Doctor doctor = null;
                for (Hospital h : medical.getHospitalList()) {
                    if (h.getId().equals(hospitalId)) {
                        hospital = h;
                        break;
                    }
                }
                if (hospital != null) {
                    for (Department d : hospital.getDepartmentList()) {
                        if (d.getId().equals(departmentId)) {
                            department = d;
                            break;
                        }
                    }
                }
                if (department != null) {
                    for (Doctor d : department.getDoctorList()) {
                        if (d.getId().equals(doctorId)) {
                            doctor = d;
                        }
                    }
                }
                if (doctor != null) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(hospital.getName()).append("-")
                            .append(department.getName()).append("-")
                            .append(doctor.getName());
                    doctorView.setText(sb.toString());
                }
            }
        }
    }

    private void registerSmsContentChangeObserver() {
        smsObserver = new SmsObserver(handler);
        getContentResolver().registerContentObserver(content_sms, true, smsObserver);
    }

    private void unregisterSmsContentObserver() {
        getContentResolver().unregisterContentObserver(smsObserver);
    }

    private void readUnread114Sms() {
        String[] projection = new String[]{"address", "body", "date", "status"};
        Cursor cursor = getContentResolver().query(content_sms, projection, "address='114'", null, "date desc");
        if (cursor != null) {
            while (cursor.moveToNext()) {
                LogUtil.log("a message ----->");
                String[] fields = cursor.getColumnNames();
                for (int i = 0; i < fields.length; i++) {
                    String fieldName = fields[i];
                    String value = cursor.getString(cursor.getColumnIndex(fieldName));
                    System.out.println("\t " + fieldName + " --> " + value);
                }
            }
        }
    }

    private class SmsObserver extends ContentObserver {

        public SmsObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);
            LogUtil.log(uri.toString());
            readUnread114Sms();
        }
    }

}

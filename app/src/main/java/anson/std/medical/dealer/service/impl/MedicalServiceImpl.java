package anson.std.medical.dealer.service.impl;

import android.util.Base64;

import java.io.File;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import anson.std.medical.dealer.Consumer;
import anson.std.medical.dealer.HandleResult;
import anson.std.medical.dealer.MedicalService;
import anson.std.medical.dealer.Medical114Api;
import anson.std.medical.dealer.model.Department;
import anson.std.medical.dealer.model.Doctor;
import anson.std.medical.dealer.model.Hospital;
import anson.std.medical.dealer.model.Medical;
import anson.std.medical.dealer.model.MedicalResource;
import anson.std.medical.dealer.model.Patient;
import anson.std.medical.dealer.model.TargetDate;
import anson.std.medical.dealer.support.Constants;
import anson.std.medical.dealer.support.FileUtil;
import anson.std.medical.dealer.support.LogUtil;
import anson.std.medical.dealer.web.api.impl.DoctorFilter;
import anson.std.medical.dealer.web.api.impl.MResponse;

/**
 * Created by anson on 17-5-9.
 */

public class MedicalServiceImpl implements MedicalService {
    private static final String encryKey = "adfjpzcxvkpadpfqreqwer_+)(*&()^*(";
    public static final String expertPrefix = "ex_";

    private Map<String, String> tempMap;

    private Medical114Api medical114Api;
    private Medical medical;
    private boolean isLogin;

    public MedicalServiceImpl(Medical114Api medical114Api) {
        this.medical114Api = medical114Api;
        tempMap = new HashMap<>();
    }

    public boolean isDataLoaded() {
        return medical != null;
    }

    /**
     * use first sd card if it can access else use internal external store
     *
     * @param callback
     */
    @Override
    public void loadMedicalData(Consumer<HandleResult> callback) {
        if (medical == null) {
            File appPrivateDir = FileUtil.getAppPrivateDirectory();
            File medicalFile = new File(appPrivateDir, Constants.medical_data_file_name);
            HandleResult handleResult = new HandleResult();
            if (medicalFile.exists() && medicalFile.isFile()) {
                medical = FileUtil.readFile(medicalFile, Medical.class);
                deEncryptModel(medical);
                LogUtil.log("load medical data from file success");

                handleResult.setOccurError(false);
                handleResult.setMessage("data load success");
                handleResult.setMedical(medical);
                callback.apply(handleResult);
            } else {
                handleResult.setOccurError(true);
                handleResult.setMessage("no data file exists");
                callback.apply(handleResult);
            }
        } else {
            LogUtil.log("medical data has been loaded, need not to reload it");
        }
    }

    @Override
    public void saveMedicalData(Medical medical, Consumer<HandleResult> callback) {
        if (medical != null) {
            File appPrivateDir = FileUtil.getAppPrivateDirectory();
            File medicalFile = new File(appPrivateDir, Constants.medical_data_file_name);
            encryptModel(medical);
            FileUtil.flushFileByObject(medicalFile, medical);
            deEncryptModel(medical);
            this.medical = medical;
            LogUtil.logView("write medical data to file success");
            if (callback != null) {
                HandleResult handleResult = new HandleResult();
                handleResult.setOccurError(false);
                handleResult.setMessage("write medical data to file success");
                callback.apply(handleResult);
            }
        } else {
            LogUtil.logView("medical is null, nothing will write to file");
        }
    }

    @Override
    public void setTemp(String key, String tempValue) {
        tempMap.put(key, tempValue);
    }

    @Override
    public String getTemp(String key) {
        return tempMap.get(key);
    }

    @Override
    public void clearTemp(boolean clearContact) {
        if (clearContact) {
            tempMap.remove(Constants.key_intent_selected_contact_id);
        } else {
            String contactId = tempMap.get(Constants.key_intent_selected_contact_id);
            tempMap.clear();
            if (contactId != null) {
                tempMap.put(Constants.key_intent_selected_contact_id, contactId);
            }
        }
    }

    @Override
    public Doctor getDoctorById(String doctorId) {
        Doctor doctor = null;
        if (medical != null) {
            Hospital hospital = null;
            String selectHospitalId = tempMap.get(Constants.key_intent_selected_hospital_id);
            for (Hospital h : medical.getHospitalList()) {
                if (h.getId().equals(selectHospitalId)) {
                    hospital = h;
                    break;
                }
            }
            if (hospital != null) {
                Department department = null;
                String departmentId = tempMap.get(Constants.key_intent_selected_department_id);
                for (Department d : hospital.getDepartmentList()) {
                    if (d.getId().equals(departmentId)) {
                        department = d;
                        break;
                    }
                }
                for (Doctor d : department.getDoctorList()) {
                    if (d.getId().equals(doctorId)) {
                        doctor = d;
                        break;
                    }
                }
            }
        }
        return doctor;
    }

    @Override
    public String getNextExpertDoctorId() {
        String exDoctorId = null;
        if (medical != null) {
            List<Hospital> hospitalList = medical.getHospitalList();
            if (hospitalList != null) {
                for (Hospital hospital : hospitalList) {
                    List<Department> departmentList = hospital.getDepartmentList();
                    if (departmentList != null) {
                        for (Department department : departmentList) {
                            List<Doctor> doctorList = department.getDoctorList();
                            if (doctorList != null) {
                                for (Doctor doctor : doctorList) {
                                    if (isExpertDoctor(doctor.getId())) {
                                        if (exDoctorId == null) {
                                            exDoctorId = doctor.getId();
                                        } else {
                                            int exNum = Integer.parseInt(exDoctorId.replace(expertPrefix, ""));
                                            int doctorIdNum = Integer.parseInt(doctor.getId().replace(expertPrefix, ""));
                                            if (doctorIdNum > exNum) {
                                                exDoctorId = doctor.getId();
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (exDoctorId == null) {
            exDoctorId = expertPrefix + 1;
        } else {
            int exNum = Integer.parseInt(exDoctorId.replace(expertPrefix, ""));
            exDoctorId = expertPrefix + (exNum + 1);
        }
        return exDoctorId;
    }

    @Override
    public boolean isExpertDoctor(String doctorId) {
        return doctorId.startsWith(expertPrefix);
    }

    @Override
    public Medical getMedicalData() {
        return medical;
    }

    @Override
    public boolean isLogin114() {
        return isLogin;
    }

    @Override
    public void login114() {
        if (!isLogin) {
            isLogin = medical114Api.login(medical.getUserName(), medical.getPwd());
            if (isLogin) {
                LogUtil.logView("114 login success");
            } else {
                LogUtil.logView("114 login failed");
            }
        } else {
            LogUtil.logView("114 has already login");
        }
    }

    @Override
    public void listMedicalResource(String hospitalId, String departmentId, String date, Boolean amPm, Consumer<HandleResult> callback) {
        List<MedicalResource> medicalResources = medical114Api.getMedicalResources(hospitalId, departmentId, date, amPm);
        HandleResult handleResult = new HandleResult();
        handleResult.setResourceList(medicalResources);
        callback.apply(handleResult);
    }

    @Override
    public void doAsADealer(TargetDate targetDate, Consumer<HandleResult> stepCallback) {
        String date = targetDate.getDateStr();
        Boolean time = null;
        if (!targetDate.isIfFullDay()) {
            time = targetDate.isAmPm();
        }
        String hospitalId = tempMap.get(Constants.key_intent_selected_hospital_id);
        String departmentId = tempMap.get(Constants.key_intent_selected_department_id);
        String doctorId = tempMap.get(Constants.key_intent_selected_doctor_id);
        boolean isExpertDoctor = isExpertDoctor(doctorId);

        DoctorFilter doctorFilter = null;
        if (isExpertDoctor) {
            Doctor doctor = getDoctorById(doctorId);
            doctorId = null;
            doctorFilter = new DoctorFilter();
            String[] skills = doctor.getSkill().split(Constants.doctor_skill_split_str);
            for (int i = 0; i < skills.length; i++) {
                doctorFilter.add(skills[i]);
            }
            tempMap.put(Constants.temp_doctor_expert, "true");
        }

        // get medical resource
        MedicalResource medicalResource = null;
        int tryTimes = 1;
        while (medicalResource == null) {
            medicalResource = medical114Api.getMedicalResource(hospitalId, departmentId, doctorId, date, time, doctorFilter);
            if (medicalResource == null) {
                LogUtil.log("didn't find medical resource, try times -> {}", ++tryTimes);
                HandleResult handleResult = new HandleResult();
                handleResult.setOccurError(false);
                handleResult.setMessage("try times " + tryTimes);
                stepCallback.apply(handleResult);
                try {
                    Thread.currentThread().sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        if (isExpertDoctor) {
            doctorId = medicalResource.getDoctorId();
            tempMap.put(Constants.key_intent_selected_doctor_id, doctorId);
        }
        long sourceId = medicalResource.getDutySourceId();
        tempMap.put(Constants.key_intent_got_source_id, Long.toString(sourceId));
        LogUtil.log("get medical resource success, sourceId -> {} available -> {}", sourceId, medicalResource.getRemainAvailableNumber());
        HandleResult gotResourceResult = new HandleResult();
        gotResourceResult.setOccurError(false);
        gotResourceResult.setMessage("got resource!! Available -> " + medicalResource.getRemainAvailableNumber());
        stepCallback.apply(gotResourceResult);

        // sent verify code
        medical114Api.sendGetRequestBeforeSendVerifySms(hospitalId, departmentId, doctorId, sourceId);
        boolean sentFlag = medical114Api.sendVerifySms(hospitalId, departmentId, doctorId, sourceId);
        LogUtil.log("verify code sent finish, sent result -> {}", sentFlag);

        String sentResultMsg = "verify code sent result -> " + sentFlag;
        if (!sentFlag) {
            sentResultMsg += " try it again now!!";
        }
        HandleResult sentResult = new HandleResult();
        sentResult.setOccurError(!sentFlag);
        sentResult.setMessage(sentResultMsg);
        stepCallback.apply(sentResult);
    }

    @Override
    public void submit(String verifyCode, Consumer<HandleResult> stepCallback) {
        String hospitalId = tempMap.get(Constants.key_intent_selected_hospital_id);
        String departmentId = tempMap.get(Constants.key_intent_selected_department_id);
        String doctorId = tempMap.get(Constants.key_intent_selected_doctor_id);
        String patientId = tempMap.get(Constants.key_intent_selected_contact_id);
        long sourceId = Long.parseLong(tempMap.get(Constants.key_intent_got_source_id));
        Patient patient = getPatient(patientId);
        MedicalResource resource = new MedicalResource();
        resource.setHospitalId(hospitalId);
        resource.setDepartmentId(departmentId);
        resource.setDoctorId(doctorId);
        resource.setPatient(patient);
        resource.setVerifyCode(verifyCode);
        resource.setDutySourceId(sourceId);

        MResponse mResponse = medical114Api.commit(resource);
        HandleResult result = new HandleResult();
        if (mResponse == null) {
            LogUtil.log("commit occur http exception");
            result.setMessage("commit occur http exception! try again");
            result.setOccurError(false);
            stepCallback.apply(result);
            submit(verifyCode, stepCallback);
        } else if (mResponse.getCode() != 200 && tempMap.containsKey(Constants.temp_doctor_expert)) {
            LogUtil.log("commit failure, code -> {} data -> {}", mResponse.getCode(), mResponse.getData());
            LogUtil.log("is expert doctor, will try again");
            result.setOccurError(true);
            result.setMessage("commit failure and is expert doctor, will try again!! code -> " + mResponse.getCode() + " data -> " + mResponse.getData());
            stepCallback.apply(result);
        } else {
            result.setOccurError(false);
            result.setMessage("commit finish. result is [" + mResponse.getData() + "]");
            stepCallback.apply(result);
            LogUtil.log("commit finish code -> {} data -> {}", mResponse.getCode(), mResponse.getData());
        }
    }

    public void encryptModel(Medical medical) {
        if (medical != null) {
            String pwd = medical.getPwd();
            medical.setPwd(encryptString(pwd));
            List<Patient> patientList = medical.getPatientList();
            if (patientList != null && !patientList.isEmpty()) {
                for (Patient patient : patientList) {
                    String hospitalCard = patient.getHospitalCard();
                    patient.setHospitalCard(encryptString(hospitalCard));
                    String medicareCard = patient.getMedicareCard();
                    patient.setMedicareCard(encryptString(medicareCard));
                }
            }
        }
    }

    public void deEncryptModel(Medical medical) {
        if (medical != null) {
            String pwd = medical.getPwd();
            medical.setPwd(decryptString(pwd));
            List<Patient> patientList = medical.getPatientList();
            if (patientList != null && !patientList.isEmpty()) {
                for (Patient patient : patientList) {
                    String hospitalCard = patient.getHospitalCard();
                    patient.setHospitalCard(decryptString(hospitalCard));
                    String medicareCard = patient.getMedicareCard();
                    patient.setMedicareCard(decryptString(medicareCard));
                }
            }
        }
    }

    private Patient getPatient(String patientId) {
        if (patientId != null && medical != null) {
            List<Patient> patientList = medical.getPatientList();
            for (Patient patient : patientList) {
                if (patient.getId().equals(patientId)) {
                    return patient;
                }
            }
        }
        return null;
    }

    private String encryptString(String string) {
        try {
            DESKeySpec desKeySpec = new DESKeySpec(Base64.decode(encryKey, Base64.DEFAULT));
            SecretKeyFactory factory = SecretKeyFactory.getInstance("DES");
            Key secretKey = factory.generateSecret(desKeySpec);
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return Base64.encodeToString(cipher.doFinal(string.getBytes()), Base64.URL_SAFE);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String decryptString(String encryptString) {
        try {
            DESKeySpec desKeySpec = new DESKeySpec(Base64.decode(encryKey, Base64.DEFAULT));
            SecretKeyFactory factory = SecretKeyFactory.getInstance("DES");
            Key secretKey = factory.generateSecret(desKeySpec);
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return new String(cipher.doFinal(Base64.decode(encryptString, Base64.URL_SAFE)));
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return null;
    }
}

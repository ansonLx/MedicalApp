package anson.std.medical.dealer.service.impl;

import android.content.Context;
import android.util.Base64;

import java.io.File;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

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
import anson.std.medical.dealer.model.Medical;
import anson.std.medical.dealer.model.MedicalResource;
import anson.std.medical.dealer.model.Patient;
import anson.std.medical.dealer.support.Constants;
import anson.std.medical.dealer.support.FileUtil;
import anson.std.medical.dealer.support.LogUtil;

/**
 * Created by anson on 17-5-9.
 */

public class MedicalServiceImpl implements MedicalService {
    private final static String encryKey = "adfjpzcxvkpadpfqreqwer_+)(*&()^*(";

    private Context context;

    private Medical114Api medical114Api;
    private Medical medical;

    public MedicalServiceImpl(Context context, Medical114Api medical114Api) {
        this.context = context;
        this.medical114Api = medical114Api;
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
            if (medicalFile.exists() && medicalFile.isFile()) {
                medical = FileUtil.readFile(medicalFile, Medical.class);
                LogUtil.log("load medical data from file success");
            } else {
                LogUtil.log("no medical data file found, will generate new one");
                medicalFile = FileUtil.createFile(appPrivateDir, Constants.medical_data_file_name);
                medical = new Medical();
                medical.setUserName("18600397835");
                medical.setPwd("51200468q");
                encryptModel(medical);
                FileUtil.flushFileByObject(medicalFile, medical);
                LogUtil.log("write medical data to file success");
            }
            deEncryptModel(medical);
        } else {
            LogUtil.log("medical data has been loaded, need not to reload it");
        }

        HandleResult handleResult = new HandleResult();
        handleResult.setOccurError(false);
        handleResult.setMessage("data load success");
        handleResult.setMedical(medical);
        callback.apply(handleResult);
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
            LogUtil.log("write medical data to file success");
        } else {
            LogUtil.log("medical is null, nothing will write to file");
        }
    }

    @Override
    public void login114() {
        boolean loginFlag = medical114Api.login(medical.getUserName(), medical.getPwd());
        if (loginFlag) {
            LogUtil.logView("114 login success");
        } else {
            LogUtil.logView("114 login failed");
        }
    }

    @Override
    public void listMedicalResource(String hospitalId, String departmentId, String date, Boolean amPm, Consumer<HandleResult> callback) {
        List<MedicalResource> medicalResources = medical114Api.getMedicalResources(hospitalId, departmentId, date, amPm);
        HandleResult handleResult = new HandleResult();
        handleResult.setResourceList(medicalResources);
        callback.apply(handleResult);
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

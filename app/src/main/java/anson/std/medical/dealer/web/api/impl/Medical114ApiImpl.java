package anson.std.medical.dealer.web.api.impl;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import anson.std.medical.dealer.Medical114Api;
import anson.std.medical.dealer.model.MedicalResource;
import anson.std.medical.dealer.support.LogUtil;
import anson.std.medical.dealer.web.api.impl.Support.HttpCommunicator;
import anson.std.medical.dealer.web.api.impl.Support.HttpResponse;
import anson.std.medical.dealer.web.api.impl.Support.NameValuePair;

/**
 * Created by anson on 17-5-9.
 */
public class Medical114ApiImpl implements Medical114Api {

    private static final String medical_host = "http://www.bjguahao.gov.cn";
    private static final String loginUrl = "/quicklogin.htm";
    private static final String queryDutyUrl = "/dpt/partduty.htm";
    private static final String orderSMSUrl = "/v/sendorder.htm";
    private static final String orderUrl = "/order/confirm.htm";
    private static final String confirmUrl = "/order/confirm/";
    private static final String referer_header = "Referer";
    private static final String date_code_am = "1";
    private static final String date_code_pm = "2";

    private HttpCommunicator httpCommunicator;

    public Medical114ApiImpl() {
        httpCommunicator = new HttpCommunicator(medical_host);
    }

    @Override
    public boolean login(String userName, String pwd) {
        HttpResponse response = httpCommunicator.post(loginUrl, null, null,
                new NameValuePair("mobileNo", userName),
                new NameValuePair("yzm", ""),
                new NameValuePair("isAjax", "true"),
                new NameValuePair("password", pwd));
        MResponse mResponse = null;
        if (response.getResponseCode() == 200) {
            mResponse = convert2Medical(response);
        }
        return mResponse.getCode() == 200;
    }

    @Override
    public List<MedicalResource> getMedicalResources(String hospitalId, String departmentId, String date, boolean amPm) {
        String dateCode = amPm ? date_code_am : date_code_pm;
        return fetchMedicalResource(hospitalId, departmentId, date, dateCode);
    }

    @Override
    public MedicalResource getMedicalResource(String hospitalId, String departmentId, String doctorId, String date, Boolean amPm, DoctorFilter doctorFilter) {
        MedicalResource targetResource = null;
        if (amPm != null) {
            String dateCode = amPm ? date_code_am : date_code_pm;
            List<MedicalResource> medicalResources = fetchMedicalResource(hospitalId, departmentId, date, dateCode);
            targetResource = filterMedicalResource(medicalResources, doctorId, doctorFilter);
        } else {
            List<MedicalResource> medicalResources = fetchMedicalResource(hospitalId, departmentId, date, date_code_am);
            targetResource = filterMedicalResource(medicalResources, doctorId, doctorFilter);
            if (targetResource == null) {
                medicalResources = fetchMedicalResource(hospitalId, departmentId, date, date_code_pm);
                targetResource = filterMedicalResource(medicalResources, doctorId, doctorFilter);
            }
        }
        return targetResource;
    }

    @Override
    public void sendGetRequestBeforeSendVerifySms(String hospitalId, String departmentId, String doctorId, long sourceId) {
        StringBuilder refererHeaderBuilder = new StringBuilder(medical_host);
        refererHeaderBuilder.append("/dpt/appoint/").append(hospitalId).append("-").append(departmentId).append(".htm");
        List<NameValuePair> header = Arrays.asList(
                new NameValuePair(referer_header, refererHeaderBuilder.toString()),
                new NameValuePair("Upgrade-Insecure-Requests", "1"));
        StringBuilder urlBuilder = new StringBuilder(confirmUrl);
        urlBuilder.append(hospitalId).append("-").append(departmentId).append("-")
                .append(doctorId).append("-").append(sourceId).append(".htm");
        while (true) {
            HttpResponse response = httpCommunicator.get(urlBuilder.toString(), false, header, Arrays.asList("Origin", "X-Requested-With"));
            if (response.getResponseCode() == 200) {
                break;
            }
        }
    }

    @Override
    public boolean sendVerifySms(String hospitalId, String departmentId, String doctorId, long sourceId) {
        StringBuilder refererHeaderBuilder = new StringBuilder(medical_host);
        refererHeaderBuilder.append("/order/confirm/").append(hospitalId).append("-").append(departmentId)
                .append("-").append(doctorId).append("-").append(sourceId).append(".htm");
        List<NameValuePair> header = Arrays.asList(new NameValuePair(referer_header, refererHeaderBuilder.toString()));
        HttpResponse httpResponse = httpCommunicator.post(orderSMSUrl, header, null);
        return httpResponse.getResponseCode() == 200;
    }

    @Override
    public MResponse commit(MedicalResource medicalResource) {
        String hospitalId = medicalResource.getHospitalId();
        String departmentId = medicalResource.getDepartmentId();
        String doctorId = medicalResource.getDoctorId();
        long sourceId = medicalResource.getDutySourceId();
        String patientId = medicalResource.getPatient().getId();
        String hospitalCardId = medicalResource.getPatient().getHospitalCard();
        String medicareCardId = medicalResource.getPatient().getMedicareCard();
        String verifyCode = medicalResource.getVerifyCode();

        StringBuilder refererHeaderBuilder = new StringBuilder(medical_host);
        refererHeaderBuilder.append("/order/confirm/").append(hospitalId).append("-")
                .append(departmentId).append("-").append(doctorId).append("-").append(sourceId).append(".htm");
        List<NameValuePair> header = Arrays.asList(new NameValuePair(referer_header, refererHeaderBuilder.toString()));
        HttpResponse httpResponse = httpCommunicator.post(orderUrl, header, null,
                new NameValuePair("dutySourceId", Long.toString(sourceId)),
                new NameValuePair("hospitalId", hospitalId),
                new NameValuePair("departmentId", departmentId),
                new NameValuePair("doctorId", doctorId),
                new NameValuePair("patientId", patientId),
                new NameValuePair("hospitalCardId", hospitalCardId),
                new NameValuePair("medicareCardId", medicareCardId),
                new NameValuePair("reimbursementType", "1"),
                new NameValuePair("smsVerifyCode", verifyCode),
                new NameValuePair("childrenBirthday", ""),
                new NameValuePair("isAjax", "true"));
        MResponse mResponse = null;
        if (httpResponse.getResponseCode() == 200) {
            mResponse = convert2Medical(httpResponse);
        }
        return mResponse;
    }

    private MResponse convert2Medical(HttpResponse response) {
        String body = response.getResponseBody();
        return JSON.parseObject(body, MResponse.class);
    }

    private List<MedicalResource> fetchMedicalResource(String hospitalId, String departmentId, String date, String dateCode) {
        StringBuilder refererHeaderBuilder = new StringBuilder(medical_host);
        refererHeaderBuilder.append("/dpt/appoint/").append(hospitalId).append("-").append(departmentId).append(".htm");
        List<NameValuePair> header = Arrays.asList(new NameValuePair(referer_header, refererHeaderBuilder.toString()));
        HttpResponse httpResponse = httpCommunicator.post(queryDutyUrl, header, null,
                new NameValuePair("hospitalId", hospitalId),
                new NameValuePair("departmentId", departmentId),
                new NameValuePair("dutyCode", dateCode),
                new NameValuePair("dutyDate", date),
                new NameValuePair("isAjax", "true"));
        List<MedicalResource> resourceList = new ArrayList<>();
        if (httpResponse.getResponseCode() == 200) {
            MResponse mResponse = convert2Medical(httpResponse);
            if (mResponse.isHasErroe()) {
                LogUtil.logView("fetch response error, code -> {} msg -> {}", mResponse.getCode(), mResponse.getMsg());
            } else {
                String dataString = mResponse.getData();
                resourceList = JSON.parseArray(dataString, MedicalResource.class);
            }
        } else {
            LogUtil.logView("fetch resource failure, code -> {} body -> {}", httpResponse.getResponseCode(), httpResponse.getResponseBody());
        }
        return resourceList;
    }

    private MedicalResource filterMedicalResource(List<MedicalResource> medicalResources, String doctorId, DoctorFilter filter) {
        MedicalResource medicalResource = null;
        if (medicalResources.isEmpty()) {
            return medicalResource;
        }
        if (doctorId != null) {
            for (MedicalResource resource : medicalResources) {
                if (resource.getDoctorId().equals(doctorId) && resource.getRemainAvailableNumber() != 0) {
                    medicalResource = resource;
                    break;
                }
            }
        }
        if (medicalResource == null && filter != null) {
            for (MedicalResource resource : medicalResources) {
                if (resource.getRemainAvailableNumber() != 0 && filter.doFilter(resource.getSkill())) {
                    medicalResource = resource;
                    break;
                }
            }
        }
        return medicalResource;
    }
}

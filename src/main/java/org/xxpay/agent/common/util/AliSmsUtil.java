//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.xxpay.agent.common.util;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.QuerySendDetailsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.QuerySendDetailsResponse;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.dysmsapi.model.v20170525.QuerySendDetailsResponse.SmsSendDetailDTO;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class AliSmsUtil {
    static final String product = "Dysmsapi";
    static final String domain = "dysmsapi.aliyuncs.com";
    static final String accessKeyId = "LTAIf1oqGNYOG2CT";
    static final String accessKeySecret = "WjC582APyOTEnQDwxmMpWzUMJBnIdp";

    public AliSmsUtil() {
    }

    public static SendSmsResponse sendSms(Map<String, String> smsMap) throws ClientException {
        System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
        System.setProperty("sun.net.client.defaultReadTimeout", "10000");
        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", "LTAIf1oqGNYOG2CT", "WjC582APyOTEnQDwxmMpWzUMJBnIdp");
        DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", "Dysmsapi", "dysmsapi.aliyuncs.com");
        IAcsClient acsClient = new DefaultAcsClient(profile);
        SendSmsRequest request = new SendSmsRequest();
        request.setPhoneNumbers((String)smsMap.get("phoneNumbers"));
        request.setSignName((String)smsMap.get("signName"));
        request.setTemplateCode((String)smsMap.get("templateCode"));
        request.setTemplateParam((String)smsMap.get("templateParam"));
        request.setOutId("yourOutId");
        SendSmsResponse sendSmsResponse = (SendSmsResponse)acsClient.getAcsResponse(request);
        return sendSmsResponse;
    }

    public static QuerySendDetailsResponse querySendDetails(String bizId) throws ClientException {
        System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
        System.setProperty("sun.net.client.defaultReadTimeout", "10000");
        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", "LTAIf1oqGNYOG2CT", "WjC582APyOTEnQDwxmMpWzUMJBnIdp");
        DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", "Dysmsapi", "dysmsapi.aliyuncs.com");
        IAcsClient acsClient = new DefaultAcsClient(profile);
        QuerySendDetailsRequest request = new QuerySendDetailsRequest();
        request.setPhoneNumber("15000000000");
        request.setBizId(bizId);
        SimpleDateFormat ft = new SimpleDateFormat("yyyyMMdd");
        request.setSendDate(ft.format(new Date()));
        request.setPageSize(10L);
        request.setCurrentPage(1L);
        QuerySendDetailsResponse querySendDetailsResponse = (QuerySendDetailsResponse)acsClient.getAcsResponse(request);
        return querySendDetailsResponse;
    }

    public static void main(String[] args) throws ClientException, InterruptedException {
        Map smsMap = new HashMap();
        smsMap.put("phoneNumbers", "18610582396");
        smsMap.put("signName", "美诺支付");
        smsMap.put("templateCode", "SMS_125023469");
        smsMap.put("templateParam", "{\"code\":\"903442\"}");
        SendSmsResponse response = sendSms(smsMap);
        System.out.println("短信接口返回的数据----------------");
        System.out.println("Code=" + response.getCode());
        System.out.println("Message=" + response.getMessage());
        System.out.println("RequestId=" + response.getRequestId());
        System.out.println("BizId=" + response.getBizId());
        Thread.sleep(3000L);
        if (response.getCode() != null && response.getCode().equals("OK")) {
            QuerySendDetailsResponse querySendDetailsResponse = querySendDetails(response.getBizId());
            System.out.println("短信明细查询接口返回数据----------------");
            System.out.println("Code=" + querySendDetailsResponse.getCode());
            System.out.println("Message=" + querySendDetailsResponse.getMessage());
            int i = 0;
            Iterator var5 = querySendDetailsResponse.getSmsSendDetailDTOs().iterator();

            while(var5.hasNext()) {
                SmsSendDetailDTO smsSendDetailDTO = (SmsSendDetailDTO)var5.next();
                System.out.println("SmsSendDetailDTO[" + i + "]:");
                System.out.println("Content=" + smsSendDetailDTO.getContent());
                System.out.println("ErrCode=" + smsSendDetailDTO.getErrCode());
                System.out.println("OutId=" + smsSendDetailDTO.getOutId());
                System.out.println("PhoneNum=" + smsSendDetailDTO.getPhoneNum());
                System.out.println("ReceiveDate=" + smsSendDetailDTO.getReceiveDate());
                System.out.println("SendDate=" + smsSendDetailDTO.getSendDate());
                System.out.println("SendStatus=" + smsSendDetailDTO.getSendStatus());
                System.out.println("Template=" + smsSendDetailDTO.getTemplateCode());
            }

            System.out.println("TotalCount=" + querySendDetailsResponse.getTotalCount());
            System.out.println("RequestId=" + querySendDetailsResponse.getRequestId());
        }

    }
}

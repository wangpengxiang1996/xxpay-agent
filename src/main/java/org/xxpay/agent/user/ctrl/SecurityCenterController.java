//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.xxpay.agent.user.ctrl;

import com.alibaba.fastjson.JSONObject;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.xxpay.agent.common.ctrl.BaseController;
import org.xxpay.agent.common.service.RpcCommonService;
import org.xxpay.core.common.annotation.MethodLog;
import org.xxpay.core.common.constant.RetEnum;
import org.xxpay.core.common.domain.BizResponse;
import org.xxpay.core.common.domain.XxPayResponse;
import org.xxpay.core.common.util.GoogleAuthenticator;
import org.xxpay.core.entity.AgentInfo;
import org.xxpay.core.entity.MchInfo;

@Controller
@RequestMapping({"/api/security"})
@PreAuthorize("hasRole('ROLE_AGENT_NORMAL')")
public class SecurityCenterController extends BaseController {
    @Autowired
    private RpcCommonService rpcCommonService;

    public SecurityCenterController() {
    }

    @RequestMapping({"/google_qrcode"})
    @ResponseBody
    public ResponseEntity<?> getGoogleAuthQrCode() {
        AgentInfo agentInfo = this.rpcCommonService.rpcAgentInfoService.findByAgentId(this.getUser().getId());
        Long mobile = agentInfo.getMobile();
        String googleAuthSecretKey = agentInfo.getGoogleAuthSecretKey();
        if (StringUtils.isBlank(googleAuthSecretKey)) {
            googleAuthSecretKey = GoogleAuthenticator.generateSecretKey();
            AgentInfo updateAgentInfo = new AgentInfo();
            updateAgentInfo.setAgentId(agentInfo.getAgentId());
            updateAgentInfo.setGoogleAuthSecretKey(googleAuthSecretKey);
            int count = this.rpcCommonService.rpcAgentInfoService.update(updateAgentInfo);
            if (count != 1) {
                return ResponseEntity.ok(XxPayResponse.build(RetEnum.RET_COMM_OPERATION_FAIL));
            }
        }

        String qrcode = GoogleAuthenticator.getQRBarcode("agent(" + mobile + ")", googleAuthSecretKey);
        String qrcodeUrl = this.mainConfig.getPayUrl() + "/qrcode_img_get?url=" + qrcode + "&widht=200&height=200";
        return ResponseEntity.ok(XxPayResponse.buildSuccess(qrcodeUrl));
    }

    @RequestMapping({"/google_bind"})
    @ResponseBody
    public ResponseEntity<?> bindGoogleAuth(HttpServletRequest request) {
        JSONObject param = this.getJsonParam(request);
        Long code = this.getLongRequired(param, "code");
        if (!this.checkGoogleCode(this.getUser().getId(), code)) {
            return ResponseEntity.ok(BizResponse.build(RetEnum.RET_MCH_GOOGLECODE_NOT_MATCH));
        } else {
            AgentInfo updateAgentInfo = new AgentInfo();
            updateAgentInfo.setAgentId(this.getUser().getId());
            updateAgentInfo.setGoogleAuthStatus((byte)1);
            int count = this.rpcCommonService.rpcAgentInfoService.update(updateAgentInfo);
            return count != 1 ? ResponseEntity.ok(XxPayResponse.build(RetEnum.RET_MCH_GOOGLEAUTH_SECRETKEY_BIND_FAIL)) : ResponseEntity.ok(XxPayResponse.buildSuccess());
        }
    }

    @RequestMapping({"/login_set"})
    @ResponseBody
    @MethodLog(
            remark = "设置登录验证方式"
    )
    public ResponseEntity<?> setLogin(HttpServletRequest request) {
        JSONObject param = this.getJsonParam(request);
        Byte loginSecurityType = this.getByteRequired(param, "loginSecurityType");
        Long code = this.getLongRequired(param, "code");
        if (!this.checkGoogleCode(this.getUser().getId(), code)) {
            return ResponseEntity.ok(BizResponse.build(RetEnum.RET_MCH_GOOGLECODE_NOT_MATCH));
        } else {
            if (loginSecurityType == 1) {
                MchInfo queryMchInfo = this.rpcCommonService.rpcMchInfoService.findByMchId(this.getUser().getId());
                if (queryMchInfo.getGoogleAuthStatus() != 1) {
                    return ResponseEntity.ok(BizResponse.build(RetEnum.RET_MCH_GOOGLEAUTH_NOT_BIND));
                }
            }

            AgentInfo updateAgentInfo = new AgentInfo();
            updateAgentInfo.setAgentId(this.getUser().getId());
            updateAgentInfo.setLoginSecurityType(loginSecurityType);
            int count = this.rpcCommonService.rpcAgentInfoService.update(updateAgentInfo);
            return count != 1 ? ResponseEntity.ok(XxPayResponse.build(RetEnum.RET_COMM_OPERATION_FAIL)) : ResponseEntity.ok(XxPayResponse.buildSuccess());
        }
    }

    @RequestMapping({"/pay_set"})
    @ResponseBody
    @MethodLog(
            remark = "设置支付验证方式"
    )
    public ResponseEntity<?> setPay(HttpServletRequest request) {
        JSONObject param = this.getJsonParam(request);
        Byte paySecurityType = this.getByteRequired(param, "paySecurityType");
        Long code = this.getLongRequired(param, "code");
        if (!this.checkGoogleCode(this.getUser().getId(), code)) {
            return ResponseEntity.ok(BizResponse.build(RetEnum.RET_MCH_GOOGLECODE_NOT_MATCH));
        } else {
            AgentInfo queryAgentInfo;
            if (paySecurityType == 2 || paySecurityType == 3) {
                queryAgentInfo = this.rpcCommonService.rpcAgentInfoService.findByAgentId(this.getUser().getId());
                if (queryAgentInfo.getGoogleAuthStatus() != 1) {
                    return ResponseEntity.ok(BizResponse.build(RetEnum.RET_MCH_GOOGLEAUTH_NOT_BIND));
                }
            }

            queryAgentInfo = new AgentInfo();
            queryAgentInfo.setAgentId(this.getUser().getId());
            queryAgentInfo.setPaySecurityType(paySecurityType);
            int count = this.rpcCommonService.rpcAgentInfoService.update(queryAgentInfo);
            return count != 1 ? ResponseEntity.ok(XxPayResponse.build(RetEnum.RET_COMM_OPERATION_FAIL)) : ResponseEntity.ok(XxPayResponse.buildSuccess());
        }
    }

    boolean checkGoogleCode(Long agentId, Long code) {
        AgentInfo agentInfo = this.rpcCommonService.rpcAgentInfoService.findByAgentId(agentId);
        String googleAuthSecretKey = agentInfo.getGoogleAuthSecretKey();
        return this.checkGoogleCode(googleAuthSecretKey, code);
    }
}

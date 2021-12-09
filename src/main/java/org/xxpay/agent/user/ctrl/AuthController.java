//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.xxpay.agent.user.ctrl;

import com.alibaba.fastjson.JSONObject;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.xxpay.agent.common.ctrl.BaseController;
import org.xxpay.agent.secruity.JwtAuthenticationRequest;
import org.xxpay.agent.secruity.JwtTokenUtil;
import org.xxpay.agent.user.service.UserService;
import org.xxpay.core.common.Exception.ServiceException;
import org.xxpay.core.common.annotation.MethodLog;
import org.xxpay.core.common.constant.RetEnum;
import org.xxpay.core.common.domain.BizResponse;
import org.xxpay.core.common.domain.XxPayResponse;
import org.xxpay.core.common.util.CookieUtil;
import org.xxpay.core.common.util.MD5Util;
import org.xxpay.core.common.util.MyLog;
import org.xxpay.core.common.util.RandomValidateCodeUtil;
import org.xxpay.core.entity.AgentInfo;

@RequestMapping({"/api"})
@RestController
public class AuthController extends BaseController {
    @Value("${jwt.cookie}")
    private String tokenCookie;
    @Value("${jwt.expiration}")
    private Integer expiration;
    @Autowired
    private UserService userService;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    private static final String SMS_VERIFY_CODE = "SMS_VERIFY_CODE";
    private static Map<String, Integer> mobileSendMap = new HashMap();
    private static final MyLog _log = MyLog.getLog(AuthController.class);

    public AuthController() {
    }

    @RequestMapping({"/auth"})
    @MethodLog(
            remark = "登录"
    )
    public ResponseEntity<?> authToken(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        JSONObject param = this.getJsonParam(request);
        String username = this.getStringRequired(param, "username");
        String password = this.getStringRequired(param, "password");
        JwtAuthenticationRequest authenticationRequest = new JwtAuthenticationRequest(username, password);

        String token;
        try {
            token = this.userService.login(authenticationRequest.getUsername(), authenticationRequest.getPassword());
        } catch (ServiceException var10) {
            return ResponseEntity.ok(BizResponse.build(var10.getRetEnum()));
        }

        AgentInfo agentInfo = this.userService.findByLoginName(username);
        JSONObject data = new JSONObject();
        data.put("access_token", token);
        data.put("agentId", agentInfo.getAgentId());
        data.put("loginSecurityType", agentInfo.getLoginSecurityType());
        return ResponseEntity.ok(XxPayResponse.buildSuccess(data));
    }

    @RequestMapping({"/google_auth"})
    public ResponseEntity<?> authGoogle(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        JSONObject param = this.getJsonParam(request);
        Long agentId = this.getLongRequired(param, "agentId");
        Long googleCode = this.getLongRequired(param, "googleCode");
        AgentInfo agentInfo = this.userService.findByAgentId(agentId);
        if (agentInfo == null) {
            return ResponseEntity.ok(BizResponse.build(RetEnum.RET_SERVICE_AGENT_NOT_EXIST));
        } else if (1 != agentInfo.getStatus()) {
            return ResponseEntity.ok(BizResponse.build(RetEnum.RET_AGENT_STATUS_STOP));
        } else {
            boolean checkResult = this.checkGoogleCode(agentInfo.getGoogleAuthSecretKey(), googleCode);
            return !checkResult ? ResponseEntity.ok(BizResponse.build(RetEnum.RET_MCH_GOOGLECODE_NOT_MATCH)) : ResponseEntity.ok(XxPayResponse.buildSuccess());
        }
    }

    @RequestMapping({"/mgr_auth"})
    public ResponseEntity<?> mgrAuthToken(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        JSONObject param = this.getJsonParam(request);
        Long agentId = this.getLongRequired(param, "agentId");
        String token = this.getStringRequired(param, "token");
        AgentInfo agentInfo = this.userService.findByAgentId(agentId);
        if (agentInfo == null) {
            return ResponseEntity.ok(BizResponse.build(RetEnum.RET_SERVICE_AGENT_NOT_EXIST));
        } else if (1 != agentInfo.getStatus()) {
            return ResponseEntity.ok(BizResponse.build(RetEnum.RET_AGENT_STATUS_STOP));
        } else {
            String password = agentInfo.getPassword();
            String secret = "Abc%$G&!!!128G";
            String rawToken = agentId + password + secret;
            String myToken = MD5Util.string2MD5(rawToken).toUpperCase();
            if (!myToken.equals(token)) {
                return ResponseEntity.ok(BizResponse.build(RetEnum.RET_MCH_ILLEGAL_LOGIN));
            } else {
                String jwtToken = this.jwtTokenUtil.generateToken(agentId, String.valueOf(agentId));
                JSONObject data = new JSONObject();
                data.put("access_token", jwtToken);
                return ResponseEntity.ok(XxPayResponse.buildSuccess(data));
            }
        }
    }

    @RequestMapping({"/refresh"})
    public ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        String token = CookieUtil.getCookieByName(request, this.tokenCookie);

        String refreshedToken;
        try {
            refreshedToken = this.userService.refreshToken(token);
        } catch (ServiceException var7) {
            return ResponseEntity.ok(BizResponse.build(var7.getRetEnum()));
        }

        if (refreshedToken == null) {
            return ResponseEntity.ok(BizResponse.build(RetEnum.RET_COMM_OPERATION_FAIL));
        } else {
            JSONObject data = new JSONObject();
            data.put("access_token", token);
            Cookie cookie = new Cookie("token", token);
            cookie.setPath("/");
            cookie.setDomain("xxpay.org");
            cookie.setMaxAge(this.expiration);
            response.addCookie(cookie);
            return ResponseEntity.ok(XxPayResponse.buildSuccess(data));
        }
    }

    @RequestMapping({"/auth/auth_code_get"})
    public void getAuthCode(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map randomMap = RandomValidateCodeUtil.getRandcode(120, 40, 6, 20);
        String randomString = randomMap.get("randomString").toString();
        BufferedImage randomImage = (BufferedImage)randomMap.get("randomImage");
        ImageIO.write(randomImage, "JPEG", response.getOutputStream());
    }
}

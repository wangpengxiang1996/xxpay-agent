//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.xxpay.agent.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.xxpay.agent.common.service.RpcCommonService;
import org.xxpay.agent.secruity.JwtTokenUtil;
import org.xxpay.agent.secruity.JwtUser;
import org.xxpay.core.common.Exception.ServiceException;
import org.xxpay.core.common.constant.RetEnum;
import org.xxpay.core.common.util.MyLog;
import org.xxpay.core.entity.AgentInfo;

@Component
public class UserService {
    @Autowired
    private RpcCommonService rpcCommonService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    private static final MyLog _log = MyLog.getLog(UserService.class);

    public UserService() {
    }

    public AgentInfo findByUserName(String userName) {
        return this.rpcCommonService.rpcAgentInfoService.findByUserName(userName);
    }

    public AgentInfo findByAgentId(Long agentId) {
        return this.rpcCommonService.rpcAgentInfoService.findByAgentId(agentId);
    }

    public AgentInfo findByLoginName(String loginName) {
        return this.rpcCommonService.rpcAgentInfoService.findByLoginName(loginName);
    }

    public AgentInfo findByMobile(Long mobile) {
        return this.rpcCommonService.rpcAgentInfoService.findByMobile(mobile);
    }

    public AgentInfo findByEmail(String email) {
        return this.rpcCommonService.rpcAgentInfoService.findByEmail(email);
    }

    public String login(String username, String password) throws ServiceException {
        UsernamePasswordAuthenticationToken upToken = new UsernamePasswordAuthenticationToken(username, password);

        try {
            Authentication authentication = this.authenticationManager.authenticate(upToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (Exception var8) {
            _log.error(var8, "鉴权失败");
            throw new ServiceException(RetEnum.RET_MCH_AUTH_FAIL);
        }

        UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
        JwtUser jwtUser = (JwtUser)userDetails;
        Byte status = jwtUser.getStatus();
        if (status == -1) {
            throw new ServiceException(RetEnum.RET_MCH_STATUS_AUDIT_ING);
        } else if (status == 0) {
            throw new ServiceException(RetEnum.RET_MCH_STATUS_STOP);
        } else {
            String token = this.jwtTokenUtil.generateToken(userDetails);
            return token;
        }
    }

    public String refreshToken(String oldToken) {
        String username = this.jwtTokenUtil.getUsernameFromToken(oldToken);
        JwtUser user = (JwtUser)this.userDetailsService.loadUserByUsername(username);
        Byte status = user.getStatus();
        if (status == -1) {
            throw new ServiceException(RetEnum.RET_MCH_STATUS_AUDIT_ING);
        } else if (status == 0) {
            throw new ServiceException(RetEnum.RET_MCH_STATUS_STOP);
        } else {
            return this.jwtTokenUtil.canTokenBeRefreshed(oldToken, user.getLastPasswordResetDate()) ? this.jwtTokenUtil.refreshToken(oldToken) : null;
        }
    }
}

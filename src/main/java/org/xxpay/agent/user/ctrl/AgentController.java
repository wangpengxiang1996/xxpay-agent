//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.xxpay.agent.user.ctrl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.xxpay.agent.common.ctrl.BaseController;
import org.xxpay.agent.common.service.RpcCommonService;
import org.xxpay.agent.user.service.UserService;
import org.xxpay.core.common.annotation.MethodLog;
import org.xxpay.core.common.constant.RetEnum;
import org.xxpay.core.common.domain.BizResponse;
import org.xxpay.core.common.domain.MenuTreeBuilder;
import org.xxpay.core.common.domain.XxPayPageRes;
import org.xxpay.core.common.domain.XxPayResponse;
import org.xxpay.core.common.domain.MenuTreeBuilder.Node;
import org.xxpay.core.common.util.MD5Util;
import org.xxpay.core.common.util.MyLog;
import org.xxpay.core.common.util.StrUtil;
import org.xxpay.core.common.util.XXPayUtil;
import org.xxpay.core.entity.AgentAccount;
import org.xxpay.core.entity.AgentInfo;
import org.xxpay.core.entity.SysResource;

@Controller
@RequestMapping({"/api/agent"})
@PreAuthorize("hasRole('ROLE_AGENT_NORMAL')")
public class AgentController extends BaseController {
    private static final MyLog _log = MyLog.getLog(AgentController.class);
    @Autowired
    private UserService userService;
    @Autowired
    private RpcCommonService rpcCommonService;

    public AgentController() {
    }

    @RequestMapping({"/get"})
    @ResponseBody
    public ResponseEntity<?> get() {
        AgentInfo agentInfo = this.userService.findByAgentId(this.getUser().getId());
        agentInfo = this.rpcCommonService.rpcAgentInfoService.reBuildAgentInfoSettConfig(agentInfo);
        return ResponseEntity.ok(XxPayResponse.buildSuccess(agentInfo));
    }

    @RequestMapping({"/list"})
    @ResponseBody
    public ResponseEntity<?> list(HttpServletRequest request) {
        JSONObject param = this.getJsonParam(request);
        AgentInfo agentInfo = (AgentInfo)this.getObject(param, AgentInfo.class);
        agentInfo.setParentAgentId(this.getUser().getId());
        int count = this.rpcCommonService.rpcAgentInfoService.count(agentInfo);
        if (count == 0) {
            return ResponseEntity.ok(XxPayPageRes.buildSuccess());
        } else {
            List<AgentInfo> agentInfoList = this.rpcCommonService.rpcAgentInfoService.select((this.getPageIndex(param) - 1) * this.getPageSize(param), this.getPageSize(param), agentInfo);
            List<JSONObject> objects = new LinkedList();
            Iterator var7 = agentInfoList.iterator();

            while(var7.hasNext()) {
                AgentInfo info = (AgentInfo)var7.next();
                JSONObject object = (JSONObject)JSON.toJSON(info);
                AgentAccount account = this.rpcCommonService.rpcAgentAccountService.findByAgentId(info.getAgentId());
                object.put("agentBalance", account.getBalance());
                objects.add(object);
            }

            Map<String, Object> ps = new HashMap();
            AgentAccount accountRecord = new AgentAccount();
            accountRecord.setAgentId(agentInfo.getAgentId());
            ps.put("allAgentBalance", this.rpcCommonService.rpcAgentAccountService.sumAgentBalance(accountRecord));
            return ResponseEntity.ok(XxPayPageRes.buildSuccess(objects, ps, count));
        }
    }

    @RequestMapping({"/add"})
    @ResponseBody
    @MethodLog(
            remark = "新增代理商"
    )
    public ResponseEntity<?> add(HttpServletRequest request) {
        JSONObject param = this.getJsonParam(request);
        AgentInfo agentInfo = (AgentInfo)this.getObject(param, AgentInfo.class);
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "lzf81959406";
        agentInfo.setPassword(encoder.encode(rawPassword));
        agentInfo.setLastPasswordResetTime(new Date());
        String payPassword = "lzf81959406";
        agentInfo.setPayPassword(MD5Util.string2MD5(payPassword));
        if (this.rpcCommonService.rpcAgentInfoService.findByUserName(agentInfo.getUserName()) != null) {
            return ResponseEntity.ok(BizResponse.build(RetEnum.RET_AGENT_USERNAME_USED));
        } else if (this.rpcCommonService.rpcAgentInfoService.findByMobile(agentInfo.getMobile()) != null) {
            return ResponseEntity.ok(BizResponse.build(RetEnum.RET_MCH_MOBILE_USED));
        } else if (this.rpcCommonService.rpcAgentInfoService.findByEmail(agentInfo.getEmail()) != null) {
            return ResponseEntity.ok(BizResponse.build(RetEnum.RET_MCH_EMAIL_USED));
        } else {
            if (agentInfo.getParentAgentId() == null) {
                agentInfo.setParentAgentId(0L);
            }

            if (agentInfo.getParentAgentId() != 0L) {
                if (this.rpcCommonService.rpcAgentInfoService.findByAgentId(agentInfo.getParentAgentId()) == null) {
                    return ResponseEntity.ok(BizResponse.build(RetEnum.RET_MCH_PARENTAGENTID_NOT_EXIST));
                }

                if (this.rpcCommonService.rpcAgentInfoService.findByAgentId(agentInfo.getParentAgentId()).getAgentLevel() != 1) {
                    return ResponseEntity.ok(BizResponse.build(RetEnum.RET_MCH_PARENTAGENTID_NOT_EXIST));
                }

                agentInfo.setAgentLevel((byte)2);
            } else {
                agentInfo.setAgentLevel((byte)1);
            }

            agentInfo.setMinDrawAmount(XXPayUtil.MIN_SERVICE_CHARGE.longValue());
            agentInfo.setSettMode((byte)1);
            agentInfo.setDayDrawTimes(1000);
            int count = this.rpcCommonService.rpcAgentInfoService.add(agentInfo);
            if (count != 1) {
                ResponseEntity.ok(XxPayResponse.build(RetEnum.RET_COMM_OPERATION_FAIL));
            }

            return ResponseEntity.ok(BizResponse.buildSuccess());
        }
    }

    @RequestMapping({"/menu_get"})
    @ResponseBody
    public ResponseEntity<?> getMenu() {
        Byte agentLevel = this.rpcCommonService.rpcAgentInfoService.findByAgentId(this.getUser().getId()).getAgentLevel();
        List<SysResource> sysResourceList = this.rpcCommonService.rpcSysService.selectAllResource((byte)3);
        List<Node> nodeList = new LinkedList();
        Iterator var4 = sysResourceList.iterator();

        while(var4.hasNext()) {
            SysResource sysResource = (SysResource)var4.next();
            boolean isShow = true;
            String property = sysResource.getProperty();
            if (StringUtils.isNotBlank(property)) {
                isShow = false;
                String[] propertys = property.split(",");
                String[] var9 = propertys;
                int var10 = propertys.length;

                for(int var11 = 0; var11 < var10; ++var11) {
                    String str = var9[var11];
                    if (agentLevel != null && str.equalsIgnoreCase(agentLevel.toString())) {
                        isShow = true;
                        break;
                    }
                }
            }

            if (isShow) {
                Node node = new Node();
                node.setResourceId(sysResource.getResourceId());
                node.setName(sysResource.getName());
                node.setTitle(sysResource.getTitle());
                if (StringUtils.isNotBlank(sysResource.getJump())) {
                    node.setJump(sysResource.getJump());
                }

                if (StringUtils.isNotBlank(sysResource.getIcon())) {
                    node.setIcon(sysResource.getIcon());
                }

                node.setParentId(sysResource.getParentId());
                nodeList.add(node);
            }
        }

        return ResponseEntity.ok(XxPayResponse.buildSuccess(JSONArray.parseArray(MenuTreeBuilder.buildTree(nodeList))));
    }

    @RequestMapping({"/update"})
    @ResponseBody
    @MethodLog(
            remark = "修改代理商信息"
    )
    public ResponseEntity<?> update(HttpServletRequest request) {
        JSONObject param = this.getJsonParam(request);
        AgentInfo agentInfo = new AgentInfo();
        agentInfo.setAgentId(this.getUser().getId());
        agentInfo.setRemark(param.getString("remark"));
        int count = this.rpcCommonService.rpcAgentInfoService.update(agentInfo);
        if (count != 1) {
            ResponseEntity.ok(XxPayResponse.build(RetEnum.RET_COMM_OPERATION_FAIL));
        }

        return ResponseEntity.ok(XxPayResponse.buildSuccess());
    }

    @RequestMapping({"/pwd_update"})
    @ResponseBody
    @MethodLog(
            remark = "修改密码"
    )
    public ResponseEntity<?> updatePassword(HttpServletRequest request) {
        JSONObject param = this.getJsonParam(request);
        String oldRawPassword = this.getStringRequired(param, "oldPassword");
        String rawPassword = this.getStringRequired(param, "password");
        AgentInfo agentInfo = this.userService.findByAgentId(this.getUser().getId());
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (!encoder.matches(oldRawPassword, agentInfo.getPassword())) {
            return ResponseEntity.ok(BizResponse.build(RetEnum.RET_MCH_OLDPASSWORD_NOT_MATCH));
        } else if (!StrUtil.checkPassword(rawPassword)) {
            return ResponseEntity.ok(BizResponse.build(RetEnum.RET_MCH_PASSWORD_FORMAT_FAIL));
        } else {
            agentInfo = new AgentInfo();
            agentInfo.setAgentId(this.getUser().getId());
            agentInfo.setPassword(encoder.encode(rawPassword));
            agentInfo.setLastPasswordResetTime(new Date());
            int count = this.rpcCommonService.rpcAgentInfoService.update(agentInfo);
            if (count != 1) {
                ResponseEntity.ok(XxPayResponse.build(RetEnum.RET_COMM_OPERATION_FAIL));
            }

            return ResponseEntity.ok(XxPayResponse.buildSuccess());
        }
    }

    @RequestMapping({"/paypwd_update"})
    @ResponseBody
    @MethodLog(
            remark = "修改支付密码"
    )
    public ResponseEntity<?> updatePayPassword(HttpServletRequest request) {
        JSONObject param = this.getJsonParam(request);
        String oldPayPassword = this.getStringRequired(param, "oldPayPassword");
        String payPassword = this.getStringRequired(param, "payPassword");
        AgentInfo agentInfo = this.userService.findByAgentId(this.getUser().getId());
        if (!MD5Util.string2MD5(oldPayPassword).equals(agentInfo.getPayPassword())) {
            return ResponseEntity.ok(BizResponse.build(RetEnum.RET_MCH_OLDPASSWORD_NOT_MATCH));
        } else if (!StrUtil.checkPassword(payPassword)) {
            return ResponseEntity.ok(BizResponse.build(RetEnum.RET_MCH_PASSWORD_FORMAT_FAIL));
        } else {
            agentInfo = new AgentInfo();
            agentInfo.setAgentId(this.getUser().getId());
            agentInfo.setPayPassword(MD5Util.string2MD5(payPassword));
            int count = this.rpcCommonService.rpcAgentInfoService.update(agentInfo);
            if (count != 1) {
                ResponseEntity.ok(XxPayResponse.build(RetEnum.RET_COMM_OPERATION_FAIL));
            }

            return ResponseEntity.ok(XxPayResponse.buildSuccess());
        }
    }
}

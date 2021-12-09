//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.xxpay.agent.user.ctrl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.xxpay.agent.common.ctrl.BaseController;
import org.xxpay.agent.common.service.RpcCommonService;
import org.xxpay.core.common.domain.XxPayPageRes;
import org.xxpay.core.common.domain.XxPayResponse;
import org.xxpay.core.entity.AgentAccount;
import org.xxpay.core.entity.AgentAccountHistory;

@RestController
@RequestMapping({"/api/account"})
@PreAuthorize("hasRole('ROLE_AGENT_NORMAL')")
public class AccountController extends BaseController {
    @Autowired
    private RpcCommonService rpcCommonService;

    public AccountController() {
    }

    @RequestMapping({"/get"})
    @ResponseBody
    public ResponseEntity<?> get() {
        AgentAccount agentAccount = this.rpcCommonService.rpcAgentAccountService.findByAgentId(this.getUser().getId());
        JSONObject object = (JSONObject)JSON.toJSON(agentAccount);
        object.put("availableBalance", agentAccount.getAvailableBalance());
        object.put("availableSettAmount", agentAccount.getAvailableSettAmount());
        return ResponseEntity.ok(XxPayResponse.buildSuccess(object));
    }

    @RequestMapping({"/history_list"})
    @ResponseBody
    public ResponseEntity<?> historyList(HttpServletRequest request) {
        JSONObject param = this.getJsonParam(request);
        AgentAccountHistory agentAccountHistory = (AgentAccountHistory)this.getObject(param, AgentAccountHistory.class);
        if (agentAccountHistory == null) {
            agentAccountHistory = new AgentAccountHistory();
        }

        agentAccountHistory.setAgentId(this.getUser().getId());
        int count = this.rpcCommonService.rpcAgentAccountHistoryService.count(agentAccountHistory);
        if (count == 0) {
            return ResponseEntity.ok(XxPayPageRes.buildSuccess());
        } else {
            List<AgentAccountHistory> agentAccountHistoryList = this.rpcCommonService.rpcAgentAccountHistoryService.select((this.getPageIndex(param) - 1) * this.getPageSize(param), this.getPageSize(param), agentAccountHistory);
            return ResponseEntity.ok(XxPayPageRes.buildSuccess(agentAccountHistoryList, count));
        }
    }

    @RequestMapping({"/history_get"})
    @ResponseBody
    public ResponseEntity<?> historyGet(HttpServletRequest request) {
        JSONObject param = this.getJsonParam(request);
        Long id = this.getLongRequired(param, "id");
        AgentAccountHistory agentAccountHistory = this.rpcCommonService.rpcAgentAccountHistoryService.findByAgentIdAndId(this.getUser().getId(), id);
        return ResponseEntity.ok(XxPayResponse.buildSuccess(agentAccountHistory));
    }
}

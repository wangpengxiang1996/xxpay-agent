//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.xxpay.agent.user.ctrl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.xxpay.agent.common.ctrl.BaseController;
import org.xxpay.agent.common.service.RpcCommonService;
import org.xxpay.core.common.domain.XxPayResponse;
import org.xxpay.core.entity.AgentAgentpayPassage;
import org.xxpay.core.entity.AgentpayPassage;

@RestController
@RequestMapping({"/api/agent_agentpay_passage"})
public class AgentAgentpayPassageController extends BaseController {
    @Autowired
    private RpcCommonService rpcCommonService;

    public AgentAgentpayPassageController() {
    }

    @RequestMapping({"/list"})
    @ResponseBody
    public ResponseEntity<?> list(HttpServletRequest request) {
        Long agentId = this.getUser().getId();
        List<AgentAgentpayPassage> agentAgentpayPassageList = this.rpcCommonService.rpcAgentAgentpayPassageService.selectAllByAgentId(agentId);
        AgentpayPassage queryAgentpayPassage = new AgentpayPassage();
        List<AgentpayPassage> agentpayPassageList = this.rpcCommonService.rpcAgentpayPassageService.selectAll(queryAgentpayPassage);
        Map<String, AgentpayPassage> agentpayPassageMap = new HashMap();
        Iterator var7 = agentpayPassageList.iterator();

        while(var7.hasNext()) {
            AgentpayPassage agentpayPassage = (AgentpayPassage)var7.next();
            agentpayPassageMap.put(String.valueOf(agentpayPassage.getId()), agentpayPassage);
        }

        List<JSONObject> objects = new LinkedList();
        Iterator var13 = agentAgentpayPassageList.iterator();

        while(var13.hasNext()) {
            AgentAgentpayPassage agentAgentpayPassage = (AgentAgentpayPassage)var13.next();
            AgentpayPassage ap = (AgentpayPassage)agentpayPassageMap.get(String.valueOf(agentAgentpayPassage.getAgentpayPassageId()));
            if (ap.getStatus() == 1) {
                JSONObject object = (JSONObject)JSON.toJSON(agentAgentpayPassage);
                object.put("passageName", ap.getPassageName());
                objects.add(object);
            }
        }

        return ResponseEntity.ok(XxPayResponse.buildSuccess(objects));
    }
}

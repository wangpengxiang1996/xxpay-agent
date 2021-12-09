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
import org.xxpay.core.entity.AgentPassage;
import org.xxpay.core.entity.PayProduct;

@RestController
@RequestMapping({"/api/agent_passage"})
public class AgentPassageController extends BaseController {
    @Autowired
    private RpcCommonService rpcCommonService;

    public AgentPassageController() {
    }

    @RequestMapping({"/list"})
    @ResponseBody
    public ResponseEntity<?> list(HttpServletRequest request) {
        Long agentId = this.getUser().getId();
        List<AgentPassage> agentPassageList = this.rpcCommonService.rpcAgentPassageService.selectAllByAgentId(agentId);
        List<PayProduct> payProductList = this.rpcCommonService.rpcPayProductService.selectAll();
        Map<String, PayProduct> payProductMap = new HashMap();
        Iterator var6 = payProductList.iterator();

        while(var6.hasNext()) {
            PayProduct product = (PayProduct)var6.next();
            payProductMap.put(String.valueOf(product.getId()), product);
        }

        List<JSONObject> objects = new LinkedList();

        JSONObject object;
        for(Iterator var11 = agentPassageList.iterator(); var11.hasNext(); objects.add(object)) {
            AgentPassage agentPassage = (AgentPassage)var11.next();
            object = (JSONObject)JSON.toJSON(agentPassage);
            if (payProductMap.get(String.valueOf(agentPassage.getProductId())) != null) {
                object.put("productName", ((PayProduct)payProductMap.get(String.valueOf(agentPassage.getProductId()))).getProductName());
            }
        }

        return ResponseEntity.ok(XxPayResponse.buildSuccess(objects));
    }
}

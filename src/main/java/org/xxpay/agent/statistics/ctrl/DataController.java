//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.xxpay.agent.statistics.ctrl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.xxpay.agent.common.ctrl.BaseController;
import org.xxpay.agent.common.service.RpcCommonService;
import org.xxpay.core.common.domain.XxPayResponse;
import org.xxpay.core.entity.AgentAccount;
import org.xxpay.core.entity.MchInfo;

@RestController
@RequestMapping({"/api/data"})
public class DataController extends BaseController {
    @Autowired
    private RpcCommonService rpcCommonService;

    public DataController() {
    }

    @RequestMapping({"/count4Account"})
    @ResponseBody
    public ResponseEntity<?> count4Account(HttpServletRequest request) {
        AgentAccount agentAccount = this.rpcCommonService.rpcAgentAccountService.findByAgentId(this.getUser().getId());
        JSONObject object = (JSONObject)JSON.toJSON(agentAccount);
        MchInfo queryMchInfo = new MchInfo();
        queryMchInfo.setStatus((byte)1);
        queryMchInfo.setAgentId(this.getUser().getId());
        int mchCount = this.rpcCommonService.rpcMchInfoService.count(queryMchInfo);
        object.put("mchCount", mchCount);
        return ResponseEntity.ok(XxPayResponse.buildSuccess(object));
    }

    @RequestMapping({"/count4Income"})
    @ResponseBody
    public ResponseEntity<?> count4Income(HttpServletRequest request) {
        String today = (new SimpleDateFormat("yyyy-MM-dd")).format(new Date());
        String todayStart = today + " 00:00:00";
        String todayEnd = today + " 23:59:59";
        Map todayIncome = this.rpcCommonService.rpcPayOrderService.count4Income(this.getUser().getId(), (Long)null, (byte)1, todayStart, todayEnd);
        Map totalIncome = this.rpcCommonService.rpcPayOrderService.count4Income(this.getUser().getId(), (Long)null, (byte)1, (String)null, (String)null);
        JSONObject object = new JSONObject();
        object.put("todayIncome", this.doMapEmpty(todayIncome));
        object.put("totalIncome", this.doMapEmpty(totalIncome));
        return ResponseEntity.ok(XxPayResponse.buildSuccess(object));
    }

    @RequestMapping({"/count4agent"})
    @ResponseBody
    public ResponseEntity<?> count4Agent(HttpServletRequest request) {
        Map agentProfitObj = new JSONObject();
        Long payProfit = 0L;
        Long agentpayProfit = 0L;
        Long rechargeProfit = 0L;
        Long totalProfit = 0L;
        List<Map> mapList = this.rpcCommonService.rpcAgentAccountHistoryService.count4AgentProfit(this.getUser().getId());
        Iterator var8 = mapList.iterator();

        while(var8.hasNext()) {
            Map map = (Map)var8.next();
            String bizItem = map.get("bizItem").toString();
            Long profilt = Long.parseLong(map.get("totalProfit").toString());
            byte var13 = -1;
            switch(bizItem.hashCode()) {
                case 1598:
                    if (bizItem.equals("20")) {
                        var13 = 0;
                    }
                    break;
                case 1599:
                    if (bizItem.equals("21")) {
                        var13 = 1;
                    }
                    break;
                case 1600:
                    if (bizItem.equals("22")) {
                        var13 = 2;
                    }
                    break;
                case 1601:
                    if (bizItem.equals("23")) {
                        var13 = 3;
                    }
            }

            switch(var13) {
                case 0:
                    totalProfit = totalProfit + profilt;
                    payProfit = payProfit + profilt;
                    break;
                case 1:
                    totalProfit = totalProfit + profilt;
                    agentpayProfit = agentpayProfit + profilt;
                    break;
                case 2:
                    totalProfit = totalProfit + profilt;
                    rechargeProfit = rechargeProfit + profilt;
                    break;
                case 3:
                    totalProfit = totalProfit + profilt;
                    rechargeProfit = rechargeProfit + profilt;
            }
        }

        agentProfitObj.put("agentpayProfit", agentpayProfit);
        agentProfitObj.put("payProfit", payProfit);
        agentProfitObj.put("rechargeProfit", rechargeProfit);
        agentProfitObj.put("totalProfit", totalProfit);
        JSONObject object = new JSONObject();
        object.put("agentProfitObj", this.doMapEmpty(agentProfitObj));
        return ResponseEntity.ok(XxPayResponse.buildSuccess(object));
    }

    @RequestMapping({"/count4MchTop"})
    @ResponseBody
    public ResponseEntity<?> count4mchTop(HttpServletRequest request) {
        JSONObject param = this.getJsonParam(request);
        String createTimeStart = this.getString(param, "createTimeStart");
        String createTimeEnd = this.getString(param, "createTimeEnd");
        Long mchId = this.getLong(param, "mchId");
        Byte productType = this.getByte(param, "productType");
        List<Map> mchTopList = this.rpcCommonService.rpcPayOrderService.count4MchTop(this.getUser().getId(), mchId, productType, createTimeStart, createTimeEnd);
        List<Map> mchTopList2 = (List)mchTopList.stream().map(this::doMapEmpty).collect(Collectors.toCollection(LinkedList::new));
        return ResponseEntity.ok(XxPayResponse.buildSuccess(mchTopList2));
    }

    private Map doMapEmpty(Map map) {
        if (map == null) {
            return map;
        } else {
            if (null == map.get("totalCount")) {
                map.put("totalCount", 0);
            }

            if (null == map.get("totalAmount")) {
                map.put("totalAmount", 0);
            }

            if (null == map.get("totalMchIncome")) {
                map.put("totalMchIncome", 0);
            }

            if (null == map.get("totalAgentProfit")) {
                map.put("totalAgentProfit", 0);
            }

            if (null == map.get("totalPlatProfit")) {
                map.put("totalPlatProfit", 0);
            }

            if (null == map.get("totalChannelCost")) {
                map.put("totalChannelCost", 0);
            }

            if (null == map.get("totalBalance")) {
                map.put("totalBalance", 0);
            }

            if (null == map.get("totalSettAmount")) {
                map.put("totalSettAmount", 0);
            }

            return map;
        }
    }
}

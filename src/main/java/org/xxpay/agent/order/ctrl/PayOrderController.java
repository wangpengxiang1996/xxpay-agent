//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.xxpay.agent.order.ctrl;

import com.alibaba.fastjson.JSONObject;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.xxpay.agent.common.ctrl.BaseController;
import org.xxpay.agent.common.service.RpcCommonService;
import org.xxpay.core.common.domain.XxPayPageRes;
import org.xxpay.core.common.domain.XxPayResponse;
import org.xxpay.core.common.util.DateUtil;
import org.xxpay.core.entity.PayOrder;

@Controller
@RequestMapping({"/api/pay_order"})
public class PayOrderController extends BaseController {
    @Autowired
    private RpcCommonService rpcCommonService;

    public PayOrderController() {
    }

    @RequestMapping({"/get"})
    @ResponseBody
    public ResponseEntity<?> get(HttpServletRequest request) {
        JSONObject param = this.getJsonParam(request);
        String payOrderId = this.getStringRequired(param, "payOrderId");
        Long agentId = this.getUser().getId();
        PayOrder queyrPayOrder = new PayOrder();
        queyrPayOrder.setAgentId(agentId);
        queyrPayOrder.setPayOrderId(payOrderId);
        PayOrder payOrder = this.rpcCommonService.rpcPayOrderService.find(queyrPayOrder);
        return ResponseEntity.ok(XxPayResponse.buildSuccess(payOrder));
    }

    @RequestMapping({"/list"})
    @ResponseBody
    public ResponseEntity<?> list(HttpServletRequest request) {
        JSONObject param = this.getJsonParam(request);
        Integer page = this.getInteger(param, "page");
        Integer limit = this.getInteger(param, "limit");
        PayOrder payOrder = (PayOrder)this.getObject(param, PayOrder.class);
        payOrder.setAgentId(this.getUser().getId());
        Date createTimeStart = null;
        Date createTimeEnd = null;
        String createTimeStartStr = this.getString(param, "createTimeStart");
        if (StringUtils.isNotBlank(createTimeStartStr)) {
            createTimeStart = DateUtil.str2date(createTimeStartStr);
        }

        String createTimeEndStr = this.getString(param, "createTimeEnd");
        if (StringUtils.isNotBlank(createTimeEndStr)) {
            createTimeEnd = DateUtil.str2date(createTimeEndStr);
        }

        int count = this.rpcCommonService.rpcPayOrderService.count(payOrder, createTimeStart, createTimeEnd);
        if (count == 0) {
            return ResponseEntity.ok(XxPayPageRes.buildSuccess());
        } else {
            List<PayOrder> payOrderList = this.rpcCommonService.rpcPayOrderService.select((this.getPageIndex(page) - 1) * this.getPageSize(limit), this.getPageSize(limit), payOrder, createTimeStart, createTimeEnd);
            return ResponseEntity.ok(XxPayPageRes.buildSuccess(payOrderList, count));
        }
    }

    @RequestMapping({"/count"})
    @ResponseBody
    public ResponseEntity<?> count(HttpServletRequest request) {
        JSONObject param = this.getJsonParam(request);
        String payOrderId = this.getString(param, "payOrderId");
        Long passageId = this.getLong(param, "passageId");
        String mchOrderNo = this.getString(param, "mchOrderNo");
        Long productId = this.getLong(param, "productId");
        Long mchId = this.getLong(param, "mchId");
        Byte productType = this.getByte(param, "productType");
        Long angentId = this.getUser().getId();
        String createTimeStartStr = this.getString(param, "createTimeStart");
        String createTimeEndStr = this.getString(param, "createTimeEnd");
        Map allMap = this.rpcCommonService.rpcPayOrderService.count4All(angentId, mchId, productId, payOrderId, passageId, mchOrderNo, productType, createTimeStartStr, createTimeEndStr);
        Map successMap = this.rpcCommonService.rpcPayOrderService.count4Success(angentId, mchId, productId, payOrderId, passageId, mchOrderNo, productType, createTimeStartStr, createTimeEndStr);
        Map failMap = this.rpcCommonService.rpcPayOrderService.count4Fail(angentId, mchId, productId, payOrderId, passageId, mchOrderNo, productType, createTimeStartStr, createTimeEndStr);
        JSONObject obj = new JSONObject();
        obj.put("allTotalCount", allMap.get("totalCount"));
        obj.put("allTotalAmount", allMap.get("totalAmount"));
        obj.put("successTotalCount", successMap.get("totalCount"));
        obj.put("successTotalAmount", successMap.get("totalAmount"));
        obj.put("successTotalMchIncome", successMap.get("totalMchIncome"));
        obj.put("successTotalAgentProfit", successMap.get("totalAgentProfit"));
        obj.put("failTotalCount", failMap.get("totalCount"));
        obj.put("failTotalAmount", failMap.get("totalAmount"));
        return ResponseEntity.ok(XxPayResponse.buildSuccess(obj));
    }
}

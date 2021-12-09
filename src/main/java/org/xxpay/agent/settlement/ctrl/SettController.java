//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.xxpay.agent.settlement.ctrl;

import com.alibaba.fastjson.JSONObject;
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
import org.xxpay.core.common.Exception.ServiceException;
import org.xxpay.core.common.annotation.MethodLog;
import org.xxpay.core.common.constant.RetEnum;
import org.xxpay.core.common.domain.BizResponse;
import org.xxpay.core.common.domain.XxPayPageRes;
import org.xxpay.core.common.domain.XxPayResponse;
import org.xxpay.core.common.util.MyLog;
import org.xxpay.core.entity.AgentInfo;
import org.xxpay.core.entity.SettRecord;

@RestController
@RequestMapping({"/api/sett"})
public class SettController extends BaseController {
    @Autowired
    private RpcCommonService rpcCommonService;
    private static final MyLog _log = MyLog.getLog(SettController.class);

    public SettController() {
    }

    @RequestMapping({"/is_allow_apply"})
    @ResponseBody
    public ResponseEntity<?> allowApply(HttpServletRequest request) {
        AgentInfo agentInfo = this.rpcCommonService.rpcAgentInfoService.findByAgentId(this.getUser().getId());
        agentInfo = this.rpcCommonService.rpcAgentInfoService.reBuildAgentInfoSettConfig(agentInfo);
        String applyTip = this.rpcCommonService.rpcSettRecordService.isAllowApply(agentInfo.getDrawFlag(), agentInfo.getAllowDrawWeekDay(), agentInfo.getDrawDayStartTime(), agentInfo.getDrawDayEndTime());
        return ResponseEntity.ok(BizResponse.build(applyTip));
    }

    @RequestMapping({"/apply"})
    @ResponseBody
    @MethodLog(
            remark = "申请结算"
    )
    public ResponseEntity<?> apply(HttpServletRequest request) {
        JSONObject param = this.getJsonParam(request);
        Long settAmountL = this.getRequiredAmountL(param, "settAmount");
        if (settAmountL <= 0L) {
            return ResponseEntity.ok(BizResponse.build(RetEnum.RET_SERVICE_AMOUNT_ERROR));
        } else {
            String bankName = this.getStringRequired(param, "bankName");
            String bankNetName = this.getStringRequired(param, "bankNetName");
            String accountName = this.getStringRequired(param, "accountName");
            String accountNo = this.getStringRequired(param, "accountNo");
            AgentInfo agentInfo = this.rpcCommonService.rpcAgentInfoService.findByAgentId(this.getUser().getId());
            BizResponse bizResponse = this.verifyPay(agentInfo, param);
            if (bizResponse != null) {
                return ResponseEntity.ok(bizResponse);
            } else {
                try {
                    int count = this.rpcCommonService.rpcSettRecordService.applySett((byte)1, this.getUser().getId(), settAmountL, bankName, bankNetName, accountName, accountNo);
                    return count == 1 ? ResponseEntity.ok(BizResponse.buildSuccess()) : ResponseEntity.ok(BizResponse.build(RetEnum.RET_COMM_OPERATION_FAIL));
                } catch (ServiceException var11) {
                    return ResponseEntity.ok(BizResponse.build(var11.getRetEnum()));
                } catch (Exception var12) {
                    return ResponseEntity.ok(BizResponse.build(RetEnum.RET_COMM_UNKNOWN_ERROR));
                }
            }
        }
    }

    @RequestMapping({"/list"})
    @ResponseBody
    public ResponseEntity<?> list(HttpServletRequest request) {
        JSONObject param = this.getJsonParam(request);
        SettRecord settRecord = (SettRecord)this.getObject(param, SettRecord.class);
        if (settRecord == null) {
            settRecord = new SettRecord();
        }

        settRecord.setInfoType((byte)1);
        settRecord.setInfoId(this.getUser().getId());
        int count = this.rpcCommonService.rpcSettRecordService.count(settRecord, this.getQueryObj(param));
        if (count == 0) {
            return ResponseEntity.ok(XxPayPageRes.buildSuccess());
        } else {
            List<SettRecord> settRecordList = this.rpcCommonService.rpcSettRecordService.select((this.getPageIndex(param) - 1) * this.getPageSize(param), this.getPageSize(param), settRecord, this.getQueryObj(param));
            return ResponseEntity.ok(XxPayPageRes.buildSuccess(settRecordList, count));
        }
    }

    @RequestMapping({"/get"})
    @ResponseBody
    public ResponseEntity<?> get(HttpServletRequest request) {
        JSONObject param = this.getJsonParam(request);
        Long id = this.getLongRequired(param, "id");
        SettRecord querySettRecord = new SettRecord();
        querySettRecord.setInfoType((byte)1);
        querySettRecord.setInfoId(this.getUser().getId());
        querySettRecord.setId(id);
        SettRecord settRecord = this.rpcCommonService.rpcSettRecordService.find(querySettRecord);
        return ResponseEntity.ok(XxPayResponse.buildSuccess(settRecord));
    }

    @RequestMapping({"/count"})
    @ResponseBody
    public ResponseEntity<?> count(HttpServletRequest request) {
        JSONObject param = this.getJsonParam(request);
        Long agentId = this.getUser().getId();
        String settOrderId = this.getString(param, "settOrderId");
        String accountName = this.getString(param, "accountName");
        Byte settStatus = this.getByte(param, "settStatus");
        String createTimeStartStr = this.getString(param, "createTimeStart");
        String createTimeEndStr = this.getString(param, "createTimeEnd");
        Map allMap = this.rpcCommonService.rpcSettRecordService.count4All(agentId, accountName, settOrderId, settStatus, createTimeStartStr, createTimeEndStr);
        JSONObject obj = new JSONObject();
        obj.put("allTotalCount", allMap.get("totalCount"));
        obj.put("allTotalAmount", allMap.get("totalAmount"));
        obj.put("allTotalFee", allMap.get("totalFee"));
        return ResponseEntity.ok(XxPayResponse.buildSuccess(obj));
    }
}

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.xxpay.agent.merchant.ctrl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.xxpay.agent.common.ctrl.BaseController;
import org.xxpay.agent.common.service.RpcCommonService;
import org.xxpay.core.common.annotation.MethodLog;
import org.xxpay.core.common.constant.RetEnum;
import org.xxpay.core.common.domain.BizResponse;
import org.xxpay.core.common.domain.XxPayPageRes;
import org.xxpay.core.common.domain.XxPayResponse;
import org.xxpay.core.common.util.MD5Util;
import org.xxpay.core.entity.AgentInfo;
import org.xxpay.core.entity.AgentPassage;
import org.xxpay.core.entity.MchAccount;
import org.xxpay.core.entity.MchInfo;
import org.xxpay.core.entity.MchPayPassage;
import org.xxpay.core.entity.PayProduct;

@Controller
@RequestMapping({"/api/mch_info"})
public class MchInfoController extends BaseController {
    @Autowired
    private RpcCommonService rpcCommonService;

    public MchInfoController() {
    }

    @RequestMapping({"/get"})
    @ResponseBody
    public ResponseEntity<?> get(HttpServletRequest request) {
        JSONObject param = this.getJsonParam(request);
        Long mchId = this.getLongRequired(param, "mchId");
        MchInfo mchInfo = new MchInfo();
        mchInfo.setMchId(mchId);
        mchInfo.setAgentId(this.getUser().getId());
        return ResponseEntity.ok(XxPayResponse.buildSuccess(this.rpcCommonService.rpcMchInfoService.find(mchInfo)));
    }

    @RequestMapping({"/add"})
    @ResponseBody
    @MethodLog(
            remark = "新增商户"
    )
    public ResponseEntity<?> add(HttpServletRequest request) {
        JSONObject param = this.getJsonParam(request);
        MchInfo mchInfo = (MchInfo)this.getObject(param, MchInfo.class);
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "lzf81959406";
        mchInfo.setPassword(encoder.encode(rawPassword));
        mchInfo.setLastPasswordResetTime(new Date());
        String payPassword = "lzf81959406";
        mchInfo.setPayPassword(MD5Util.string2MD5(payPassword));
        mchInfo.setRole("ROLE_MCH_NORMAL");
        mchInfo.setAgentId(this.getUser().getId());
        mchInfo.setStatus((byte)-1);
        AgentInfo agentInfo = this.rpcCommonService.rpcAgentInfoService.findByAgentId(this.getUser().getId());
        if (agentInfo == null) {
            return ResponseEntity.ok(BizResponse.build(RetEnum.RET_SERVICE_AGENT_NOT_EXIST));
        } else {
            if (agentInfo.getParentAgentId() != null && agentInfo.getParentAgentId() != 0L) {
                mchInfo.setParentAgentId(agentInfo.getParentAgentId());
            } else {
                mchInfo.setParentAgentId(0L);
            }

            if (this.rpcCommonService.rpcMchInfoService.findByMobile(mchInfo.getMobile()) != null) {
                return ResponseEntity.ok(BizResponse.build(RetEnum.RET_MCH_MOBILE_USED));
            } else if (this.rpcCommonService.rpcMchInfoService.findByEmail(mchInfo.getEmail()) != null) {
                return ResponseEntity.ok(BizResponse.build(RetEnum.RET_MCH_EMAIL_USED));
            } else if (this.rpcCommonService.rpcMchInfoService.findByUserName(mchInfo.getUserName()) != null) {
                return ResponseEntity.ok(BizResponse.build(RetEnum.RET_MCH_USERNAME_USED));
            } else if (null != mchInfo.getTag() && this.rpcCommonService.rpcMchInfoService.findByTag(mchInfo.getTag()) != null) {
                return ResponseEntity.ok(BizResponse.build(RetEnum.RET_MCH_TAG_USED));
            } else {
                int count = this.rpcCommonService.rpcMchInfoService.register(mchInfo);
                if (count != 1) {
                    ResponseEntity.ok(XxPayResponse.build(RetEnum.RET_COMM_OPERATION_FAIL));
                }

                return ResponseEntity.ok(BizResponse.buildSuccess());
            }
        }
    }

    @RequestMapping({"/list"})
    @ResponseBody
    public ResponseEntity<?> list(HttpServletRequest request) {
        JSONObject param = this.getJsonParam(request);
        MchInfo mchInfo = (MchInfo)this.getObject(param, MchInfo.class);
        mchInfo.setAgentId(this.getUser().getId());
        int count = this.rpcCommonService.rpcMchInfoService.count(mchInfo);
        if (count == 0) {
            return ResponseEntity.ok(XxPayPageRes.buildSuccess());
        } else {
            List<MchInfo> mchInfoList = this.rpcCommonService.rpcMchInfoService.select((this.getPageIndex(param) - 1) * this.getPageSize(param), this.getPageSize(param), mchInfo);
            return ResponseEntity.ok(XxPayPageRes.buildSuccess(mchInfoList, count));
        }
    }

    @RequestMapping({"/account_get"})
    @ResponseBody
    public ResponseEntity<?> accountGet(HttpServletRequest request) {
        JSONObject param = this.getJsonParam(request);
        Long mchId = this.getLongRequired(param, "mchId");
        this.haveMchPermission(mchId);
        MchAccount mchAccount = this.rpcCommonService.rpcMchAccountService.findByMchId(mchId);
        return ResponseEntity.ok(XxPayResponse.buildSuccess(mchAccount));
    }

    @RequestMapping({"/pay_passage_list"})
    @ResponseBody
    public ResponseEntity<?> payPassagelist(HttpServletRequest request) {
        JSONObject param = this.getJsonParam(request);
        Long mchId = this.getLongRequired(param, "mchId");
        this.haveMchPermission(mchId);
        Long agentId = this.getUser().getId();
        List<AgentPassage> agentPassageList = this.rpcCommonService.rpcAgentPassageService.selectAllByAgentId(agentId);
        List<PayProduct> payProductList = this.rpcCommonService.rpcPayProductService.selectAll();
        Map<String, PayProduct> payProductMap = new HashMap();
        Iterator var8 = payProductList.iterator();

        while(var8.hasNext()) {
            PayProduct product = (PayProduct)var8.next();
            payProductMap.put(String.valueOf(product.getId()), product);
        }

        List<MchPayPassage> mchPayPassageList = this.rpcCommonService.rpcMchPayPassageService.selectAllByMchId(mchId);
        Map<String, MchPayPassage> mchPayPassageMap = new HashMap();
        Iterator var10 = mchPayPassageList.iterator();

        while(var10.hasNext()) {
            MchPayPassage mchPayPassage = (MchPayPassage)var10.next();
            mchPayPassageMap.put(String.valueOf(mchPayPassage.getProductId()), mchPayPassage);
        }

        List<JSONObject> objects = new LinkedList();

        JSONObject object;
        for(Iterator var15 = agentPassageList.iterator(); var15.hasNext(); objects.add(object)) {
            AgentPassage agentPassage = (AgentPassage)var15.next();
            object = (JSONObject)JSON.toJSON(agentPassage);
            if (payProductMap.get(String.valueOf(agentPassage.getProductId())) != null) {
                object.put("productName", ((PayProduct)payProductMap.get(String.valueOf(agentPassage.getProductId()))).getProductName());
            }

            if (mchPayPassageMap.get(String.valueOf(agentPassage.getProductId())) != null) {
                object.put("mchRate", ((MchPayPassage)mchPayPassageMap.get(String.valueOf(agentPassage.getProductId()))).getMchRate());
            }
        }

        return ResponseEntity.ok(XxPayResponse.buildSuccess(objects));
    }
}

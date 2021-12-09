//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.xxpay.agent.message.ctrl;

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
import org.xxpay.core.entity.SysMessage;

@RestController
@RequestMapping({"/api/message"})
@PreAuthorize("hasRole('ROLE_AGENT_NORMAL')")
public class MessageController extends BaseController {
    @Autowired
    private RpcCommonService rpcCommonService;

    public MessageController() {
    }

    @RequestMapping({"/get"})
    @ResponseBody
    public ResponseEntity<?> get(HttpServletRequest request) {
        JSONObject param = this.getJsonParam(request);
        Long id = this.getLongRequired(param, "id");
        SysMessage sysMessage = this.rpcCommonService.rpcSysMessageService.findById(id);
        return ResponseEntity.ok(XxPayResponse.buildSuccess(sysMessage));
    }

    @RequestMapping({"/list"})
    @ResponseBody
    public ResponseEntity<?> list(HttpServletRequest request) {
        JSONObject param = this.getJsonParam(request);
        SysMessage sysMessage = (SysMessage)this.getObject(param, SysMessage.class);
        int count = this.rpcCommonService.rpcSysMessageService.count(sysMessage);
        if (count == 0) {
            return ResponseEntity.ok(XxPayPageRes.buildSuccess());
        } else {
            List<SysMessage> sysMessageList = this.rpcCommonService.rpcSysMessageService.select((this.getPageIndex(param) - 1) * this.getPageSize(param), this.getPageSize(param), sysMessage);
            return ResponseEntity.ok(XxPayPageRes.buildSuccess(sysMessageList, count));
        }
    }
}

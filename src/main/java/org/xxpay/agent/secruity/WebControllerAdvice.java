//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.xxpay.agent.secruity;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.xxpay.core.common.Exception.ServiceException;
import org.xxpay.core.common.domain.BizResponse;
import org.xxpay.core.common.domain.XxPayResponse;
import org.xxpay.core.common.util.MyLog;

@ControllerAdvice
public class WebControllerAdvice {
    private static final MyLog _log = MyLog.getLog(WebControllerAdvice.class);

    public WebControllerAdvice() {
    }

    @ResponseBody
    @ExceptionHandler({Exception.class})
    public ResponseEntity<?> errorHandler(Exception ex) {
        _log.error(ex, "");
        String message = ex.getMessage();
        String eMsg = message;
        if (StringUtils.isNotBlank(message) && message.indexOf(":") > 0) {
            eMsg = message.substring(0, message.indexOf(":"));
        }

        if (eMsg.length() > 200) {
            eMsg = eMsg.substring(0, 200);
        }

        BizResponse bizResponse = new BizResponse(99999, "系统异常[" + eMsg + "]");
        return ResponseEntity.ok(bizResponse);
    }

    @ResponseBody
    @ExceptionHandler({ServiceException.class})
    public ResponseEntity<?> myErrorHandler(ServiceException ex) {
        return ResponseEntity.ok(XxPayResponse.build(ex.getRetEnum()));
    }
}

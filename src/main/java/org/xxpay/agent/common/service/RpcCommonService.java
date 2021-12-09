//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.xxpay.agent.common.service;

import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Service;
import org.xxpay.core.service.IAgentAccountHistoryService;
import org.xxpay.core.service.IAgentAccountService;
import org.xxpay.core.service.IAgentAgentpayPassageService;
import org.xxpay.core.service.IAgentInfoService;
import org.xxpay.core.service.IAgentPassageService;
import org.xxpay.core.service.IAgentpayPassageService;
import org.xxpay.core.service.IMchAccountService;
import org.xxpay.core.service.IMchInfoService;
import org.xxpay.core.service.IMchPayPassageService;
import org.xxpay.core.service.IPayOrderService;
import org.xxpay.core.service.IPayProductService;
import org.xxpay.core.service.ISettRecordService;
import org.xxpay.core.service.ISysLogService;
import org.xxpay.core.service.ISysMessageService;
import org.xxpay.core.service.ISysService;

@Service
public class RpcCommonService {
    @Reference(
            version = "1.0.0",
            timeout = 10000,
            retries = -1
    )
    public IMchInfoService rpcMchInfoService;
    @Reference(
            version = "1.0.0",
            timeout = 10000,
            retries = -1
    )
    public IMchAccountService rpcMchAccountService;
    @Reference(
            version = "1.0.0",
            timeout = 10000,
            retries = -1
    )
    public IAgentAccountHistoryService rpcAgentAccountHistoryService;
    @Reference(
            version = "1.0.0",
            timeout = 10000,
            retries = -1
    )
    public ISysMessageService rpcSysMessageService;
    @Reference(
            version = "1.0.0",
            timeout = 10000,
            retries = -1
    )
    public ISysService rpcSysService;
    @Reference(
            version = "1.0.0",
            timeout = 10000,
            retries = -1
    )
    public IAgentInfoService rpcAgentInfoService;
    @Reference(
            version = "1.0.0",
            timeout = 10000,
            retries = -1
    )
    public IAgentAccountService rpcAgentAccountService;
    @Reference(
            version = "1.0.0",
            timeout = 10000,
            retries = -1
    )
    public IAgentPassageService rpcAgentPassageService;
    @Reference(
            version = "1.0.0",
            timeout = 10000,
            retries = -1
    )
    public IPayProductService rpcPayProductService;
    @Reference(
            version = "1.0.0",
            timeout = 10000,
            retries = -1
    )
    public IMchPayPassageService rpcMchPayPassageService;
    @Reference(
            version = "1.0.0",
            timeout = 10000,
            retries = -1
    )
    public IPayOrderService rpcPayOrderService;
    @Reference(
            version = "1.0.0",
            timeout = 10000,
            retries = -1
    )
    public ISettRecordService rpcSettRecordService;
    @Reference(
            version = "1.0.0",
            timeout = 10000,
            retries = -1
    )
    public IAgentAgentpayPassageService rpcAgentAgentpayPassageService;
    @Reference(
            version = "1.0.0",
            timeout = 10000,
            retries = -1
    )
    public IAgentpayPassageService rpcAgentpayPassageService;
    @Reference(
            version = "1.0.0",
            timeout = 10000,
            retries = -1
    )
    public ISysLogService rpcSysLogService;

    public RpcCommonService() {
    }
}

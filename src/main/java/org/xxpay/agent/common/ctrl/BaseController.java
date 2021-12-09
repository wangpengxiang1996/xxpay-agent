//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.xxpay.agent.common.ctrl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.xxpay.agent.common.config.MainConfig;
import org.xxpay.agent.common.service.RpcCommonService;
import org.xxpay.agent.secruity.JwtUser;
import org.xxpay.core.common.Exception.ServiceException;
import org.xxpay.core.common.constant.RetEnum;
import org.xxpay.core.common.domain.BizResponse;
import org.xxpay.core.common.util.DateUtil;
import org.xxpay.core.common.util.GoogleAuthenticator;
import org.xxpay.core.common.util.MD5Util;
import org.xxpay.core.entity.AgentInfo;
import org.xxpay.core.entity.MchInfo;

@Controller
public class BaseController {
    @Autowired
    private RpcCommonService rpcCommonService;
    @Autowired
    protected MainConfig mainConfig;
    private static final int DEFAULT_PAGE_INDEX = 1;
    private static final int DEFAULT_PAGE_SIZE = 20;
    protected static final String PAGE_COMMON_ERROR = "common/error";
    protected static final String PAGE_COMMON_PC_ERROR = "common/pc_error";

    public BaseController() {
    }

    protected void haveMchPermission(Long mchId) {
        MchInfo queryMchInfo = new MchInfo();
        queryMchInfo.setMchId(mchId);
        queryMchInfo.setAgentId(this.getUser().getId());
        MchInfo mchInfo = this.rpcCommonService.rpcMchInfoService.find(queryMchInfo);
        if (mchInfo == null) {
            throw new ServiceException(RetEnum.RET_AGENT_NOT_MCH_PERMISSION);
        }
    }

    protected JwtUser getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JwtUser jwtUser = (JwtUser)authentication.getPrincipal();
        return jwtUser;
    }

    protected int getPageIndex(JSONObject object) {
        if (object == null) {
            return 1;
        } else {
            Integer pageIndex = object.getInteger("page");
            return pageIndex == null ? 1 : pageIndex;
        }
    }

    protected int getPageSize(JSONObject object) {
        if (object == null) {
            return 20;
        } else {
            Integer pageSize = object.getInteger("limit");
            return pageSize == null ? 20 : pageSize;
        }
    }

    protected int getPageIndex(Integer page) {
        return page == null ? 1 : page;
    }

    protected int getPageSize(Integer limit) {
        return limit == null ? 20 : limit;
    }

    protected JSONObject getJsonParam(HttpServletRequest request) {
        String params = request.getParameter("params");
        if (StringUtils.isNotBlank(params)) {
            return JSON.parseObject(params);
        } else {
            Map properties = request.getParameterMap();
            JSONObject returnObject = new JSONObject();
            Iterator entries = properties.entrySet().iterator();

            String name;
            for(String value = ""; entries.hasNext(); returnObject.put(name, value)) {
                Entry entry = (Entry)entries.next();
                name = (String)entry.getKey();
                Object valueObj = entry.getValue();
                if (null == valueObj) {
                    value = "";
                } else if (!(valueObj instanceof String[])) {
                    value = valueObj.toString();
                } else {
                    String[] values = (String[])((String[])valueObj);

                    for(int i = 0; i < values.length; ++i) {
                        value = values[i] + ",";
                    }

                    value = value.substring(0, value.length() - 1);
                }
            }

            return returnObject;
        }
    }

    protected Boolean getBoolean(JSONObject param, String key) {
        return param == null ? null : param.getBooleanValue(key);
    }

    protected String getString(JSONObject param, String key) {
        return param == null ? null : param.getString(key);
    }

    protected String getStringRequired(JSONObject param, String key) {
        if (param == null) {
            throw new RuntimeException(this.getErrMsg(key));
        } else {
            String value = param.getString(key);
            if (StringUtils.isBlank(value)) {
                throw new RuntimeException(this.getErrMsg(key));
            } else {
                return value;
            }
        }
    }

    protected String getStringDefault(JSONObject param, String key, String defaultValue) {
        String value = this.getString(param, key);
        return value == null ? defaultValue : value;
    }

    protected Byte getByte(JSONObject param, String key) {
        return param == null ? null : param.getByte(key);
    }

    protected Byte getByteRequired(JSONObject param, String key) {
        if (param == null) {
            throw new RuntimeException(this.getErrMsg(key));
        } else {
            Byte value = param.getByte(key);
            if (value == null) {
                throw new RuntimeException(this.getErrMsg(key));
            } else {
                return value;
            }
        }
    }

    protected int getByteDefault(JSONObject param, String key, byte defaultValue) {
        Byte value = this.getByte(param, key);
        return value == null ? defaultValue : value;
    }

    protected Integer getInteger(JSONObject param, String key) {
        return param == null ? null : param.getInteger(key);
    }

    protected Integer getIntegerRequired(JSONObject param, String key) {
        if (param == null) {
            throw new RuntimeException(this.getErrMsg(key));
        } else {
            Integer value = param.getInteger(key);
            if (value == null) {
                throw new RuntimeException(this.getErrMsg(key));
            } else {
                return value;
            }
        }
    }

    protected int getIntegerDefault(JSONObject param, String key, int defaultValue) {
        Integer value = this.getInteger(param, key);
        return value == null ? defaultValue : value;
    }

    protected Long getLong(JSONObject param, String key) {
        return param == null ? null : param.getLong(key);
    }

    protected Long getLongRequired(JSONObject param, String key) {
        if (param == null) {
            throw new RuntimeException(this.getErrMsg(key));
        } else {
            Long value = param.getLong(key);
            if (value == null) {
                throw new RuntimeException(this.getErrMsg(key));
            } else {
                return value;
            }
        }
    }

    protected long getLongDefault(JSONObject param, String key, long defaultValue) {
        Long value = this.getLong(param, key);
        return value == null ? defaultValue : value;
    }

    protected JSONObject getJSONObject(JSONObject param, String key) {
        return param == null ? null : param.getJSONObject(key);
    }

    protected <T> T getObject(JSONObject param, String key, Class<T> clazz) {
        JSONObject object = this.getJSONObject(param, key);
        return object == null ? null : JSON.toJavaObject(object, clazz);
    }

    protected <T> T getObject(JSONObject param, Class<T> clazz) {
        return param == null ? null : JSON.toJavaObject(param, clazz);
    }

    private String getErrMsg(String key) {
        return "参数" + key + "必填";
    }

    protected boolean checkGoogleCode(String googleAuthSecretKey, Long code) {
        if (StringUtils.isBlank(googleAuthSecretKey)) {
            return false;
        } else {
            long t = System.currentTimeMillis();
            GoogleAuthenticator ga = new GoogleAuthenticator();
            ga.setWindowSize(5);
            return ga.check_code(googleAuthSecretKey, code, t);
        }
    }

    protected BizResponse verifyPay(AgentInfo agentInfo, JSONObject param) {
        Byte paySecurityType = agentInfo.getPaySecurityType();
        String payPassword;
        String passwordMd5;
        Long googleCode;
        switch(paySecurityType) {
            case 0:
                break;
            case 1:
                payPassword = this.getStringRequired(param, "payPassword");
                passwordMd5 = MD5Util.string2MD5(payPassword);
                if (!agentInfo.getPayPassword().equals(passwordMd5)) {
                    return BizResponse.build(RetEnum.RET_MCH_PAP_PASSWORD_NOT_MATCH);
                }
                break;
            case 2:
                googleCode = this.getLongRequired(param, "googleCode");
                if (!this.checkGoogleCode(agentInfo.getGoogleAuthSecretKey(), googleCode)) {
                    return BizResponse.build(RetEnum.RET_MCH_GOOGLECODE_NOT_MATCH);
                }
                break;
            case 3:
                payPassword = this.getStringRequired(param, "payPassword");
                passwordMd5 = MD5Util.string2MD5(payPassword);
                if (!agentInfo.getPayPassword().equals(passwordMd5)) {
                    return BizResponse.build(RetEnum.RET_MCH_PAP_PASSWORD_NOT_MATCH);
                }

                googleCode = this.getLongRequired(param, "googleCode");
                if (!this.checkGoogleCode(agentInfo.getGoogleAuthSecretKey(), googleCode)) {
                    return BizResponse.build(RetEnum.RET_MCH_GOOGLECODE_NOT_MATCH);
                }
                break;
            default:
                return null;
        }

        return null;
    }

    protected void checkRequired(JSONObject param, String... keys) {
        if (param == null) {
            throw new RuntimeException(this.getErrMsg(keys[0]));
        } else {
            String[] var3 = keys;
            int var4 = keys.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                String key = var3[var5];
                String value = param.getString(key);
                if (value == null) {
                    throw new RuntimeException(this.getErrMsg(key));
                }
            }

        }
    }

    public Long getRequiredAmountL(JSONObject param, String name) {
        String amountStr = this.getStringRequired(param, name);
        Long amountL = (new BigDecimal(amountStr.trim())).multiply(new BigDecimal(100)).longValue();
        return amountL;
    }

    public void handleParamAmount(JSONObject param, String... names) {
        String[] var3 = names;
        int var4 = names.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            String name = var3[var5];
            String amountStr = this.getString(param, name);
            if (StringUtils.isNotBlank(amountStr)) {
                Long amountL = (new BigDecimal(amountStr.trim())).multiply(new BigDecimal(100)).longValue();
                param.put(name, amountL);
            }
        }

    }

    public JSONObject getQueryObj(JSONObject param) {
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

        JSONObject queryObj = new JSONObject();
        queryObj.put("createTimeStart", createTimeStart);
        queryObj.put("createTimeEnd", createTimeEnd);
        return queryObj;
    }
}

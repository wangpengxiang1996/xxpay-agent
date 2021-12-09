//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.xxpay.agent.sys.aop;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import javax.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.xxpay.agent.common.service.RpcCommonService;
import org.xxpay.agent.common.util.SpringUtil;
import org.xxpay.agent.secruity.JwtUser;
import org.xxpay.core.common.annotation.MethodLog;
import org.xxpay.core.common.util.IPUtility;
import org.xxpay.core.common.util.MyLog;
import org.xxpay.core.common.util.RequestUtils;
import org.xxpay.core.entity.SysLog;

@Component
@Aspect
public class MethodLogAop {
    private static final MyLog logger = MyLog.getLog(MethodLogAop.class);
    private static final ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(10);

    public MethodLogAop() {
    }

    @Pointcut("@annotation(org.xxpay.core.common.annotation.MethodLog)")
    public void methodCachePointcut() {
    }

    @Around("methodCachePointcut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        final SysLog sysLog = new SysLog();
        sysLog.setSystem((byte)3);
        HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
        sysLog.setOptReqParam(RequestUtils.getJsonParam(request, true).toJSONString());
        sysLog.setMethodRemark(getAnnotationRemark(point));
        String methodName = point.getSignature().getName();
        String packageName = point.getThis().getClass().getName();
        if (packageName.indexOf("$$EnhancerByCGLIB$$") > -1 || packageName.indexOf("$$EnhancerBySpringCGLIB$$") > -1) {
            packageName = packageName.substring(0, packageName.indexOf("$$"));
        }

        sysLog.setMethodName(packageName + "." + methodName);
        Object result = point.proceed();

        try {
            sysLog.setUserId(JwtUser.getCurrentUserId());
            sysLog.setUserName(JwtUser.getCurrentUserName());
            sysLog.setUserIp(IPUtility.getClientIp(request));
            sysLog.setOptResInfo(result.toString());
            sysLog.setCreateTime(new Date());
            scheduledThreadPool.execute(new Runnable() {
                public void run() {
                    RpcCommonService rpcCommonService = (RpcCommonService)SpringUtil.getBean("rpcCommonService");
                    rpcCommonService.rpcSysLogService.add(sysLog);
                }
            });
        } catch (Exception var8) {
            logger.error("methodLogError", new Object[]{var8});
        }

        return result;
    }

    public static String getAnnotationRemark(ProceedingJoinPoint joinPoint) throws Exception {
        Signature sig = joinPoint.getSignature();
        Method m = joinPoint.getTarget().getClass().getMethod(joinPoint.getSignature().getName(), ((MethodSignature)sig).getParameterTypes());
        MethodLog methodCache = (MethodLog)m.getAnnotation(MethodLog.class);
        return methodCache != null ? methodCache.remark() : "";
    }
}

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.xxpay.agent.secruity;

import com.alibaba.fastjson.JSON;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.xxpay.core.common.util.MyLog;

@WebFilter(
        filterName = "LogFilter",
        urlPatterns = {"/api/*"}
)
public class LogFilter implements Filter {
    private static final MyLog _log = MyLog.getLog(LogFilter.class);

    public LogFilter() {
    }

    public void destroy() {
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            long startTime = System.currentTimeMillis();
            WrappedHttpServletRequest requestWrapper = new WrappedHttpServletRequest((HttpServletRequest)request);
            HttpServletResponse response1 = (HttpServletResponse)response;
            String uri = requestWrapper.getRequestURI();
            String remoteAddr = requestWrapper.getRemoteAddr();
            String method = requestWrapper.getMethod().toUpperCase();
            String params = "";
            if ("POST".equals(method)) {
                params = JSON.toJSONString(requestWrapper.getParameterMap());
            } else if ("GET".equals(method)) {
                params = JSON.toJSONString(requestWrapper.getParameterMap());
            }

            _log.info("[request] [uri:{}, method:{}, remoteAddr:{}, params:{}]", new Object[]{uri, method, remoteAddr, params});
            chain.doFilter(requestWrapper, response1);
            _log.info("[response] [uri:{}, status:{}, cost:{} ms]", new Object[]{uri, response1.getStatus(), System.currentTimeMillis() - startTime});
        } catch (Exception var12) {
            _log.error(var12, "");
        }

    }

    public void init(FilterConfig config) throws ServletException {
    }
}

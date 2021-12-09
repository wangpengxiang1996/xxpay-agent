//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.xxpay;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.ApplicationListener;

@SpringBootApplication
@ServletComponentScan
public class XxPayAgentApplication extends SpringBootServletInitializer {
    private static Class<XxPayAgentApplication> applicationClass = XxPayAgentApplication.class;

    public XxPayAgentApplication() {
    }

    public static void main(String[] args) {
        SpringApplication.run(XxPayAgentApplication.class, args);
    }

    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        application.listeners(new ApplicationListener[0]);
        return application.sources(new Class[]{applicationClass});
    }
}

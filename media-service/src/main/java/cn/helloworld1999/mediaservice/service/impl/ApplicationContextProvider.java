// 将ApplicationContextProvider移动到单独的文件中
package cn.helloworld1999.mediaservice.service.impl;

import org.springframework.context.ApplicationContext;

public class ApplicationContextProvider {
    private static ApplicationContext context;

    public static void setApplicationContext(ApplicationContext applicationContext) {
        context = applicationContext;
    }

    public static ApplicationContext getApplicationContext() {
        return context;
    }
}
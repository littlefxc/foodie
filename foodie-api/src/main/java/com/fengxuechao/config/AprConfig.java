package com.fengxuechao.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Configuration;

/**
 * @author fengxuechao
 * @date 2021/8/12
 */
@Configuration
public class AprConfig implements WebServerFactoryCustomizer<WebServerFactory> {

    /**
     *
     * 如何启动 tomcat 的 apr 运行模式
     * java -Djava.library.path=/usr/local/opt/tomcat-native/lib -jar app.jar
     * 或者
     * 需要安装 apr apr-util tomcat-native
     * sudo ln -s /usr/local/Cellar/tomcat-native/1.2.28/lib/* /Library/Java/Extensions
     * 然后 java -jar app.jar
     * @param factory
     */
    @Override
    public void customize(WebServerFactory factory) {
        TomcatServletWebServerFactory containerFactory = (TomcatServletWebServerFactory) factory;
        containerFactory.setProtocol("org.apache.coyote.http11.Http11AprProtocol");
    }
}

package xyz.erupt.cloud.client.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import xyz.erupt.core.constant.EruptRestPath;
import xyz.erupt.core.context.MetaContext;
import xyz.erupt.core.context.MetaUser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author YuePeng
 * date 2018-12-20.
 */

@Configuration
@Component
public class EruptCloudInterceptor implements WebMvcConfigurer, AsyncHandlerInterceptor {


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(this).addPathPatterns(EruptRestPath.ERUPT_API + "/**");
    }

    //不接受来自erupt-api的任何http请求
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        //远程加载的erupt允许跨域
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Headers", "*");
        response.setHeader("Access-Control-Allow-Methods", "*");
        MetaContext.register(new MetaUser("1", "test", "测试"));
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        MetaContext.remove();
    }
}

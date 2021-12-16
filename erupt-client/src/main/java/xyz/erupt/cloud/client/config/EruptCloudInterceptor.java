package xyz.erupt.cloud.client.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import xyz.erupt.core.constant.EruptRestPath;

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
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        response.setStatus(HttpStatus.NOT_ACCEPTABLE.value());
        response.sendError(response.getStatus());
        return false;
    }
}

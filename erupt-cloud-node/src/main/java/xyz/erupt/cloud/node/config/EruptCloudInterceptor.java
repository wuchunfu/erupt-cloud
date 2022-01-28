package xyz.erupt.cloud.node.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import xyz.erupt.cloud.common.consts.CloudCommonConst;
import xyz.erupt.core.constant.EruptMutualConst;
import xyz.erupt.core.constant.EruptRestPath;
import xyz.erupt.core.context.MetaContext;
import xyz.erupt.core.context.MetaErupt;
import xyz.erupt.core.context.MetaUser;
import xyz.erupt.core.exception.EruptWebApiRuntimeException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author YuePeng
 * date 2018-12-20.
 */

@Configuration
@Component
public class EruptCloudInterceptor implements WebMvcConfigurer, AsyncHandlerInterceptor {

    @Resource
    private EruptNodeProp eruptNodeProp;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(this).addPathPatterns(EruptRestPath.ERUPT_API + "/**");
    }

    //不接受来自erupt-api的任何http请求
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!eruptNodeProp.getAccessToken().equals(request.getHeader(CloudCommonConst.ACCESS_TOKEN))) {
            throw new EruptWebApiRuntimeException("access token incorrect");
        }
//        request.getHeader(EruptMutualConst.TOKEN);
        MetaContext.register(new MetaUser("1", "test", "测试"));
        MetaContext.register(MetaErupt.builder().name(request.getHeader(EruptMutualConst.ERUPT)).build());
        //node节点管理的erupt类禁止浏览器直接访问
        response.setHeader("Access-Control-Allow-Origin", "");
        response.setHeader("Access-Control-Allow-Headers", "*");
        response.setHeader("Access-Control-Allow-Methods", "*");
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        MetaContext.remove();
    }
}

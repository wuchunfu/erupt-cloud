package xyz.erupt.cloud.server.config;

import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import xyz.erupt.cloud.common.consts.CloudCommonConst;
import xyz.erupt.cloud.server.base.MetaNode;
import xyz.erupt.cloud.server.service.EruptNodeMicroservice;
import xyz.erupt.core.annotation.EruptRouter;
import xyz.erupt.core.constant.EruptMutualConst;
import xyz.erupt.core.constant.EruptRestPath;
import xyz.erupt.core.exception.EruptWebApiRuntimeException;
import xyz.erupt.core.service.EruptCoreService;
import xyz.erupt.upms.service.EruptContextService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * @author YuePeng
 * date 2018-12-20.
 */

@Configuration
@Component
@Order(1)
public class EruptCloudServerInterceptor implements WebMvcConfigurer, AsyncHandlerInterceptor {

    @Resource
    private EruptContextService eruptContextService;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(this).addPathPatterns(EruptRestPath.ERUPT_API + "/**");
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        EruptRouter eruptRouter = null;
        if (handler instanceof HandlerMethod) {
            eruptRouter = ((HandlerMethod) handler).getMethodAnnotation(EruptRouter.class);
        }
        if (null == eruptRouter) return true;
        if (EruptRouter.VerifyType.ERUPT == eruptRouter.verifyType()) {
            if (EruptNodeMicroservice.getMetaNodeNum() <= 0) return true;
            String erupt = request.getHeader(EruptMutualConst.ERUPT);
            if (null != EruptCoreService.getErupt(erupt)) return true;
            if (!erupt.contains(".")) return true;
            int point = erupt.lastIndexOf(".");
            String appName = erupt.substring(0, point);
            String eruptName = erupt.substring(point);
            MetaNode metaNode = EruptNodeMicroservice.getMetaNode(appName);
            if (null == metaNode) {
                throw new EruptWebApiRuntimeException("The " + appName + " service is not registered");
            }
            Map<String, String> headers = new HashMap<>();
            Enumeration<String> headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String name = headerNames.nextElement();
                headers.put(name, request.getHeader(name));
            }
            headers.put(EruptMutualConst.ERUPT, eruptName);
            headers.put(EruptMutualConst.TOKEN, request.getHeader(EruptMutualConst.TOKEN));
            headers.put(CloudCommonConst.ACCESS_TOKEN, metaNode.getAccessToken());
            String location = metaNode.getLocations().toArray(new String[0])[metaNode.getCount() % metaNode.getLocations().size()];
            HttpResponse httpResponse = HttpUtil.createRequest(Method.valueOf(request.getMethod()),
                    "https://www.erupt.xyz" + metaNode.getContextPath() + request.getRequestURI())
                    .body(StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8))
                    .addHeaders(headers).header("token", "VsEjtBmUackiH3cY").execute();
            httpResponse.headers().forEach((k, v) -> response.setHeader(k, v.get(0)));
            response.reset();
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.getWriter().write(httpResponse.body());
            if (httpResponse.getStatus() != HttpStatus.OK.value()) {
                response.sendError(httpResponse.getStatus());
            }
            return false;
        } else {
            return true;
        }
    }

}
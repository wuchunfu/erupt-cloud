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
import xyz.erupt.core.annotation.EruptRouter;
import xyz.erupt.core.constant.EruptRestPath;
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

    //不接受来自erupt-api的任何http请求
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        EruptRouter eruptRouter = null;
        if (handler instanceof HandlerMethod) {
            eruptRouter = ((HandlerMethod) handler).getMethodAnnotation(EruptRouter.class);
        }
        if (null == eruptRouter) {
            return true;
        }
        if (EruptRouter.VerifyType.ERUPT == eruptRouter.verifyType()) {
            if (null != EruptCoreService.getErupt(request.getHeader("erupt"))) {
                return true;
            }
            Map<String, String> headers = new HashMap<>();
            Enumeration<String> headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String name = headerNames.nextElement();
                headers.put(name, request.getHeader(name));
            }
            HttpResponse httpResponse = HttpUtil.createRequest(Method.valueOf(request.getMethod()),
                    "https://www.erupt.xyz/demo" + request.getRequestURI())
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

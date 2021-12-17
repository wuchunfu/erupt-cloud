package xyz.erupt.cloud.server.config;

import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import xyz.erupt.core.annotation.EruptRouter;
import xyz.erupt.core.constant.EruptRestPath;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author YuePeng
 * date 2018-12-20.
 */

@Configuration
@Component
@Order(1)
public class EruptCloudServerInterceptor implements WebMvcConfigurer, AsyncHandlerInterceptor {


    String TOKEN_HEADER = "token";

    String ERUPT_HEADER = "erupt";

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
            HttpResponse httpResponse = HttpUtil.createRequest(Method.valueOf(request.getMethod()),
                    "https://www.erupt.xyz/demo" + request.getRequestURI())
                    .header(TOKEN_HEADER, "9RHHp7hRXWU9sAhz")
                    .header(ERUPT_HEADER, request.getHeader(ERUPT_HEADER))
                    .execute();
            response.setStatus(httpResponse.getStatus());
            response.getWriter().write(httpResponse.body());
            httpResponse.headers().forEach((k, v) -> response.setHeader(k, v.get(0)));
            if (httpResponse.getStatus() == HttpStatus.OK.value()) {
                String body = httpResponse.body();
                response.setCharacterEncoding("utf-8");
                response.reset();
                response.getWriter().write(body);
            } else {
                response.sendError(httpResponse.getStatus());
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        System.out.println(123);
    }
}

package xyz.erupt.cloud.server.config;

import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import lombok.SneakyThrows;
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
import xyz.erupt.cloud.server.base.CloudErrorModel;
import xyz.erupt.cloud.server.base.MetaNode;
import xyz.erupt.cloud.server.node.NodeManager;
import xyz.erupt.cloud.server.service.EruptNodeMicroservice;
import xyz.erupt.core.annotation.EruptRouter;
import xyz.erupt.core.config.GsonFactory;
import xyz.erupt.core.constant.EruptMutualConst;
import xyz.erupt.core.constant.EruptRestPath;
import xyz.erupt.core.exception.EruptWebApiRuntimeException;
import xyz.erupt.core.service.EruptCoreService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.ConnectException;
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

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(this).addPathPatterns(EruptRestPath.ERUPT_API + "/**");
    }

    @Resource
    private EruptNodeMicroservice eruptNodeMicroservice;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        EruptRouter eruptRouter = null;
        if (handler instanceof HandlerMethod) {
            eruptRouter = ((HandlerMethod) handler).getMethodAnnotation(EruptRouter.class);
        }
        if (null == eruptRouter) return true;
        if (EruptRouter.VerifyType.ERUPT == eruptRouter.verifyType()) {
            String erupt = request.getHeader(EruptMutualConst.ERUPT);
            if (erupt == null) {
                erupt = request.getParameter("_" + EruptMutualConst.ERUPT);
            }
            if (erupt == null) {
                return true;
            }
            if (!erupt.contains(".")) return true;
            if (null != EruptCoreService.getErupt(erupt)) return true;
            int point = erupt.lastIndexOf(".");
            String nodeName = erupt.substring(0, point);
            String eruptName = erupt.substring(point + 1);
            MetaNode metaNode = NodeManager.getNode(nodeName);
            if (null == metaNode) {
                //TODO 自定义状态码
                throw new EruptWebApiRuntimeException("'" + nodeName + "' node not ready");
            }
            final Map<String, String> headers = new HashMap<>();
            Enumeration<String> headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String name = headerNames.nextElement();
                headers.put(name, request.getHeader(name));
            }
            headers.put(EruptMutualConst.ERUPT, eruptName);
            headers.put(EruptMutualConst.TOKEN, request.getHeader(EruptMutualConst.TOKEN));
            HttpResponse httpResponse = this.httpProxy(request, metaNode, request.getRequestURI().replace(erupt, eruptName), headers);
            response.setContentType(httpResponse.header("Content-Type"));
//            response.reset();
//            httpResponse.headers().forEach((k, v) -> {
//                System.out.println(v);
//                response.setHeader(k, v.get(0));
//            });
//            response.setHeader("Access-Control-Allow-Origin", "*");
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            if (httpResponse.getStatus() != HttpStatus.OK.value()) {
                //返回统一状态码，前端统一处理
                response.sendError(httpResponse.getStatus());
                response.getWriter().write(GsonFactory.getGson().toJson(
                        new CloudErrorModel(httpResponse.getStatus(), httpResponse.body(), nodeName))
                );
            } else {
                response.getOutputStream().write(httpResponse.bodyBytes());
            }
            return false;
        } else {
            return true;
        }
    }

    @SneakyThrows
    public HttpResponse httpProxy(HttpServletRequest request, MetaNode metaNode, String path, Map<String, String> headers) {
        String location = metaNode.getLocations().toArray(new String[0])[metaNode.getCount() % metaNode.getLocations().size()];
        headers.put(CloudCommonConst.ACCESS_TOKEN, metaNode.getAccessToken());
//        headers.remove("host");
        try {
            return HttpUtil.createRequest(Method.valueOf(request.getMethod()), location + path)
                    .body(StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8))
                    .addHeaders(headers).execute();
        } catch (ConnectException connectException) {
            throw new EruptWebApiRuntimeException(location + " -> " + connectException.getMessage());
//            errorNum.get(location);
//            if (errorNum.containsKey(location)) {
//                errorNum.put(location, errorNum.get(location) + 1);
//            } else {
//                errorNum.put(location, 1);
//            }
//            //如果连续失败五次就从服务ip中移除,重新注册
//            if (errorNum.get(location) >= 5) {
//                errorNum.remove(location);
//                eruptNodeMicroservice.removeNodeByLocation(metaNode, location);
//            }
//            throw new RuntimeException(connectException.getMessage());
        }
    }

}

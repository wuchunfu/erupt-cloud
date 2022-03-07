package xyz.erupt.cloud.server.config;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
import xyz.erupt.cloud.server.node.NodeManager;
import xyz.erupt.core.annotation.EruptRouter;
import xyz.erupt.core.config.GsonFactory;
import xyz.erupt.core.constant.EruptMutualConst;
import xyz.erupt.core.constant.EruptRestPath;
import xyz.erupt.core.exception.EruptWebApiRuntimeException;
import xyz.erupt.core.service.EruptCoreService;
import xyz.erupt.upms.service.EruptContextService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.net.ConnectException;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author YuePeng
 * date 2018-12-20.
 */
@Configuration
@Component
@Slf4j
@Order(Integer.MAX_VALUE - 1)
public class EruptCloudServerInterceptor implements WebMvcConfigurer, AsyncHandlerInterceptor {

    @Resource
    private EruptContextService eruptContextService;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(this).addPathPatterns(EruptRestPath.ERUPT_API + "/**");
    }

    private static final String[] TRANSFER_HEADERS = {
            "Content-Disposition"
    };

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

            HttpResponse httpResponse = this.httpProxy(request, metaNode, request.getRequestURI().replace(erupt, eruptName), eruptName);
            Optional.ofNullable(httpResponse.header("Content-Type")).ifPresent(response::setContentType);
            for (String transferHeader : TRANSFER_HEADERS) {
                Optional.ofNullable(httpResponse.header(transferHeader)).ifPresent(it -> response.addHeader(transferHeader, it));
            }
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            if (httpResponse.getStatus() == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                if (this.isSimpleJson(httpResponse.body())) {
                    throw new EruptWebApiRuntimeException(GsonFactory.getGson().fromJson(httpResponse.body(), Map.class), false);
                } else {
                    throw new EruptWebApiRuntimeException(httpResponse.body(), false);
                }
            }
            if (httpResponse.getStatus() != HttpStatus.OK.value()) {
                response.sendError(httpResponse.getStatus());
            }
            response.getOutputStream().write(httpResponse.bodyBytes());
            return false;
        } else {
            return true;
        }
    }

    private boolean isSimpleJson(String str) {
        if (StringUtils.isBlank(str)) return false;
        str = str.trim();
        return str.startsWith("{") && str.endsWith("}");
    }

    @SneakyThrows
    public HttpResponse httpProxy(HttpServletRequest request, MetaNode metaNode, String path, String eruptName) {
        String location = metaNode.getLocations().toArray(new String[0])[metaNode.getCount() % metaNode.getLocations().size()];
        Map<String, String> headers = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            headers.put(name, request.getHeader(name));
        }
        headers.put(CloudCommonConst.ACCESS_TOKEN, metaNode.getAccessToken());
        headers.put(EruptMutualConst.TOKEN, eruptContextService.getCurrentToken());
        headers.put(EruptMutualConst.ERUPT, eruptName);
        HttpRequest httpRequest = HttpUtil.createRequest(Method.valueOf(request.getMethod()), location + path + "?" + request.getQueryString());
        try {
            if (null != request.getContentType() && request.getContentType().contains("multipart/form-data")) {
                for (Part part : request.getParts()) {
                    httpRequest.form(part.getName(), StreamUtils.copyToByteArray(part.getInputStream()), part.getSubmittedFileName());
                }
            } else {
                httpRequest.body(StreamUtils.copyToByteArray(request.getInputStream()));
            }
            HttpResponse httpResponse = httpRequest.addHeaders(headers).execute();
            if (httpResponse.getStatus() != HttpStatus.OK.value()) {
                log.warn("{} -> {}", location, httpResponse.body());
            }
            return httpResponse;
        } catch (ConnectException connectException) {
            throw new EruptWebApiRuntimeException(location + " -> " + connectException.getMessage());
        }
    }

}

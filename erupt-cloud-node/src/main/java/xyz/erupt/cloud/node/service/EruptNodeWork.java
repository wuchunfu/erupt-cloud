package xyz.erupt.cloud.node.service;

import cn.hutool.core.io.IORuntimeException;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.google.gson.Gson;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.stereotype.Component;
import xyz.erupt.cloud.common.consts.ServerApiConst;
import xyz.erupt.cloud.common.model.NodeInfo;
import xyz.erupt.cloud.node.config.EruptNodeProp;
import xyz.erupt.core.config.GsonFactory;
import xyz.erupt.core.service.EruptCoreService;
import xyz.erupt.core.view.EruptModel;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author YuePeng
 * date 2021/12/17 00:24
 */
@Component
@Slf4j
public class EruptNodeWork implements ApplicationRunner, Runnable, DisposableBean {

    @Resource
    private EruptNodeProp eruptNodeProp;

    private final Gson gson = GsonFactory.getGson();
    @Resource
    private ServerProperties serverProperties;
    private boolean runner = true;

    private int count = 0;

    @Override
    public void run(ApplicationArguments args) {
        Thread register = new Thread(this);
        register.setName("node-register");
        register.setDaemon(true);
        register.start();
    }

    @SneakyThrows
    @Override
    public void run() {
        if (null == eruptNodeProp.getServerAddresses() || eruptNodeProp.getServerAddresses().length <= 0) {
            throw new RuntimeException("erupt-cloud.node.serverAddresses not config");
        }
        while (this.runner) {
            NodeInfo nodeInfo = new NodeInfo();
            nodeInfo.setNodeName(eruptNodeProp.getNodeName());
            nodeInfo.setAccessToken(eruptNodeProp.getAccessToken());
            nodeInfo.setContextPath(serverProperties.getServlet().getContextPath());
            nodeInfo.setErupts(EruptCoreService.getErupts().stream().map(EruptModel::getEruptName).collect(Collectors.toList()));
            String address = eruptNodeProp.getServerAddresses()[count++ % eruptNodeProp.getServerAddresses().length];
            try {
                HttpResponse httpResponse = HttpUtil.createPost(address + ServerApiConst.REGISTER_NODE)
                        .body(gson.toJson(nodeInfo)).execute();
                if (!httpResponse.isOk()) {
                    log.error(httpResponse.body());
                }
                TimeUnit.MILLISECONDS.sleep(eruptNodeProp.getHeartbeatTime());
            } catch (IORuntimeException e) {
                log.error("{}: Connection refused (Connection refused)", address);
                TimeUnit.SECONDS.sleep(5);
            }
        }
    }

    @Override
    public void destroy() {
        this.runner = false;
        // cancel register
        HttpUtil.createPost(eruptNodeProp.getServerAddresses()
                [count++ % eruptNodeProp.getServerAddresses().length] + ServerApiConst.REMOVE_NODE
        ).form(new HashMap<String, Object>() {{
            this.put("nodeName", eruptNodeProp.getNodeName());
            this.put("accessToken", eruptNodeProp.getAccessToken());
        }}).execute();
    }
}

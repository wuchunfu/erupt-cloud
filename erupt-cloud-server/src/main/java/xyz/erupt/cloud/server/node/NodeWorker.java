package xyz.erupt.cloud.server.node;

import cn.hutool.http.HttpUtil;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import xyz.erupt.cloud.common.consts.CloudRestApiConst;
import xyz.erupt.cloud.server.config.EruptCloudServerProp;

import javax.annotation.PostConstruct;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * node 节点定时任务
 *
 * @author YuePeng
 * date 2022/2/3 21:36
 */
@AllArgsConstructor
@Component
public class NodeWorker implements Runnable {

    private final NodeManager nodeManager;

    private final EruptCloudServerProp eruptCloudServerProp;

    @PostConstruct
    public void postConstruct() {
        Executors.newScheduledThreadPool(2).scheduleAtFixedRate(this, 0,
                eruptCloudServerProp.getNodeSurviveCheckTime(), TimeUnit.MILLISECONDS);
    }

    @SneakyThrows
    @Override
    public void run() {
        for (MetaNode node : nodeManager.findAllNodes()) {
            if (node.getLocations().removeIf(location -> !health(location, 2))) {
                nodeManager.putNode(node);
            }
        }
    }

    private boolean health(String location, int retryNum) {
        if (retryNum <= 0) return false;
        if (HttpUtil.createGet(location + CloudRestApiConst.NODE_HEALTH).timeout(1000).execute().isOk()) {
            return true;
        } else {
            return health(location, retryNum - 1);
        }
    }

}

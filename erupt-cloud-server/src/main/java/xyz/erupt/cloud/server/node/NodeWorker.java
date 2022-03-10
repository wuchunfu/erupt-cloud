package xyz.erupt.cloud.server.node;

import cn.hutool.http.HttpUtil;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import xyz.erupt.cloud.common.consts.CloudRestApiConst;
import xyz.erupt.cloud.server.distribute.DistributeFactory;

import javax.annotation.PostConstruct;
import java.util.Date;
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

    private final DistributeFactory distributeFactory;

    @PostConstruct
    public void postConstruct() {
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(this, 0, 60, TimeUnit.SECONDS);
    }

    @SneakyThrows
    @Override
    public void run() {
        NodeManager.consumerNode(node -> {
            if (new Date().getTime() - 1000 * 60 >= node.getRegisterTime().getTime()) {
                distributeFactory.factory().removeNode(node.getNodeName()); //长时间未注册节点从 zk 中移除
            }
            if (node.getLocations().removeIf(location -> !health(location, 2))) {
                distributeFactory.factory().putNode(node);
            }
        });
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

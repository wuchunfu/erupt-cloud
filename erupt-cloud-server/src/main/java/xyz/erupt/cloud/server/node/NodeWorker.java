package xyz.erupt.cloud.server.node;

import cn.hutool.http.HttpUtil;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import xyz.erupt.cloud.common.consts.CloudRestApiConst;
import xyz.erupt.cloud.server.distribute.ZkDistribute;

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
public class NodeWorker implements Runnable {

    private final ZkDistribute zkDistribute;

    @PostConstruct
    public void postConstruct() {
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(this, 0, 60, TimeUnit.SECONDS);
    }

    @SneakyThrows
    @Override
    public void run() {
        NodeManager.consumerNode(node -> {
            if (new Date().getTime() - 1000 * 60 >= node.getRegisterTime().getTime()) {
                zkDistribute.remove(node); //长时间未注册节点从 zk 中移除
            }
            node.getLocations().removeIf(location -> !health(location, 2));
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

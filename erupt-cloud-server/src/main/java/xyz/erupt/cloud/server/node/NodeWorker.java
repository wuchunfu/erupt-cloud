package xyz.erupt.cloud.server.node;

import lombok.SneakyThrows;
import xyz.erupt.cloud.server.service.ZkService;

import java.util.Date;

/**
 * node 节点定时任务
 *
 * @author YuePeng
 * date 2022/2/3 21:36
 */
public class NodeWorker implements Runnable {

    private final ZkService zkService;

    public NodeWorker(ZkService zkService) {
        this.zkService = zkService;
    }

    @SneakyThrows
    @Override
    public void run() {
        NodeManager.consumerNode(node -> {
            if (new Date().getTime() + 1000 * 60 <= node.getRegisterTime().getTime()) {
                NodeManager.removeNode(node.getNodeName());
                zkService.remove(node);
            }
        });
    }

}

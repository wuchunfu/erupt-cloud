package xyz.erupt.cloud.server.node;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import xyz.erupt.cloud.server.service.ZkService;

import java.util.Date;

/**
 * node 节点定时任务
 *
 * @author YuePeng
 * date 2022/2/3 21:36
 */
@AllArgsConstructor
public class NodeWorker implements Runnable {

    private final ZkService zkService;

    @SneakyThrows
    @Override
    public void run() {
        NodeManager.consumerNode(node -> {
            if (new Date().getTime() - 1000 * 60 >= node.getRegisterTime().getTime()) {
                zkService.remove(node); //长时间未注册节点从 zk 中移除
            }
        });
    }

}

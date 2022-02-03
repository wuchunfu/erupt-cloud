package xyz.erupt.cloud.server.node;

import org.springframework.stereotype.Component;
import xyz.erupt.cloud.server.service.ZkService;

import javax.annotation.Resource;
import java.util.Date;

/**
 * node 节点定时任务
 *
 * @author YuePeng
 * date 2022/2/3 21:36
 */
@Component
public class NodeWork implements Runnable {

    @Resource
    private ZkService zkService;

    @Override
    public void run() {
        NodeManager.consumerNode(node -> {
            if (new Date().getTime() + 1000 * 60 < node.getRegisterTime().getTime()) {
                NodeManager.removeNode(node.getNodeName());
                zkService.remove(node);
            }
        });
    }

}

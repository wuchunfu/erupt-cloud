package xyz.erupt.cloud.server.service;

import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.IZkStateListener;
import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.Watcher;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import xyz.erupt.cloud.server.base.MetaNode;
import xyz.erupt.cloud.server.config.EruptCloudServerProp;

/**
 * @author YuePeng
 * date 2022/1/28
 */
@Slf4j
@Service
public class ZkService implements CommandLineRunner {

    public static final String ERUPT_NODE = "/erupt-node";

    private final EruptCloudServerProp eruptCloudServerProp;

    private final ZkClient zkClient;

    public ZkService(EruptCloudServerProp eruptCloudServerProp) {
        this.eruptCloudServerProp = eruptCloudServerProp;
        zkClient = new ZkClient(String.join(",", eruptCloudServerProp.getZkServers()), 20000);
    }

    //向zk推送节点信息
    public void putNode(MetaNode metaNode) {
        zkClient.createEphemeral(ERUPT_NODE + "/" + metaNode.getNodeName(), metaNode);
    }

    public void remove(MetaNode metaNode) {
        zkClient.delete(ERUPT_NODE + "/" + metaNode.getNodeName());
    }

    @Override
    public void run(String... args) {
        if (eruptCloudServerProp.getZkServers().size() <= 0) {
            log.info("Not configured zookeeper cluster");
            return;
        }
        zkClient.createEphemeral(ERUPT_NODE);
        zkClient.subscribeChildChanges(ERUPT_NODE, (path, list) -> list.forEach(it -> {
            log.info(path);
            log.info(ERUPT_NODE + ":children:" + it);
            String info = zkClient.readData(ERUPT_NODE + it);
            log.info(info);
        }));
        // 监听状态变化
        zkClient.subscribeStateChanges(new IZkStateListener() {
            @Override
            public void handleStateChanged(Watcher.Event.KeeperState keeperState) {
                System.out.println("state:" + keeperState);
            }

            @Override
            public void handleNewSession() {
                System.out.println("new session");
            }

            @Override
            public void handleSessionEstablishmentError(Throwable throwable) {
                throwable.printStackTrace();
            }
        });
    }
}
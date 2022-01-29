package xyz.erupt.cloud.server.service;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.IZkStateListener;
import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import xyz.erupt.cloud.server.base.MetaNode;
import xyz.erupt.cloud.server.config.EruptCloudServerProp;
import xyz.erupt.cloud.server.node.NodeManager;
import xyz.erupt.core.config.GsonFactory;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author YuePeng
 * date 2022/1/28
 */
@Slf4j
@Service
public class ZkService implements CommandLineRunner {

    public static final String ERUPT_NODE = "/erupt-node";

    private final Gson gson = GsonFactory.getGson();

    @Resource
    private EruptCloudServerProp eruptCloudServerProp;

    @Resource
    private HttpServletRequest request;

    private ZkClient zkClient;

    //向zk推送节点信息
    public void putNode(MetaNode metaNode) {
        NodeManager.putNode(metaNode);
        zkClient.createEphemeral(ERUPT_NODE + "/" + metaNode.getNodeName(), metaNode);
    }

    //移除节点
    public void remove(MetaNode metaNode) {
        if (metaNode.getLocations().size() == 1) {
            NodeManager.removeNode(metaNode.getNodeName());
            zkClient.delete(ERUPT_NODE + "/" + metaNode.getNodeName());
        } else {
            metaNode.getLocations().removeIf(location -> location.equals(request.getRequestURI()));
            this.putNode(metaNode);
        }
    }

    @Override
    public void run(String... args) {
        if (null == eruptCloudServerProp.getZkServers() || eruptCloudServerProp.getZkServers().size() <= 0) {
            log.info("Not configured zookeeper cluster");
            return;
        }
        zkClient = new ZkClient(String.join(",", eruptCloudServerProp.getZkServers()), 20000);
        zkClient.createEphemeral(ERUPT_NODE);
        zkClient.subscribeChildChanges(ERUPT_NODE, (parent, list) -> list.forEach(it -> {
            log.info(parent);
            log.info("parent:" + parent + "/" + ERUPT_NODE + ":children:" + it);
            String data = zkClient.readData(ERUPT_NODE + it);
            log.info(data);
            if (null == data) {
                NodeManager.removeNode(ERUPT_NODE + it);
            } else {
                NodeManager.putNode(gson.fromJson(data, MetaNode.class));
            }
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
//package xyz.erupt.cloud.server.distribute;
//
//import com.google.gson.Gson;
//import lombok.extern.slf4j.Slf4j;
//import org.I0Itec.zkclient.IZkStateListener;
//import org.I0Itec.zkclient.ZkClient;
//import org.apache.zookeeper.Watcher;
//import org.springframework.stereotype.Service;
//import xyz.erupt.cloud.server.base.MetaNode;
//import xyz.erupt.cloud.server.config.EruptCloudServerProp;
//import xyz.erupt.cloud.server.node.NodeManager;
//import xyz.erupt.core.config.GsonFactory;
//
//import javax.annotation.Resource;
//import java.util.Optional;
//
///**
// * @author YuePeng
// * date 2022/1/28
// */
//@Slf4j
//@Service
//public class ZkDistribute extends DistributeAbstract {
//
//    public static final String ERUPT_NODE = "/erupt-node";
//
//    private final Gson gson = GsonFactory.getGson();
//
//    @Resource
//    private EruptCloudServerProp eruptCloudServerProp;
//
//    private ZkClient zkClient;
//
//    //向zk推送节点信息
//    public void putNode(MetaNode metaNode) {
//        String node = ERUPT_NODE + "/" + metaNode.getNodeName();
//        String metaNodeJson = gson.toJson(metaNode);
//        Optional.ofNullable(zkClient).ifPresent(it -> {
//            if (zkClient.exists(node)) {
//                zkClient.writeData(node, metaNodeJson);
//            } else {
//                zkClient.createPersistent(node, metaNodeJson);
//            }
//        });
//    }
//
//    //移除节点中的某个地址
//    public void removeLocation(MetaNode metaNode, String location) {
//        if (null != metaNode) {
//            if (null == metaNode.getLocations() || metaNode.getLocations().size() == 1) {
//                NodeManager.removeNode(metaNode.getNodeName());
//                zkClient.delete(ERUPT_NODE + "/" + metaNode.getNodeName());
//            } else {
//                metaNode.getLocations().removeIf(it -> it.equals(location));
//                this.putNode(metaNode);
//            }
//        }
//    }
//
//    //移除节点
//    public void remove(MetaNode metaNode) {
//        zkClient.delete(ERUPT_NODE + "/" + metaNode.getNodeName());
//    }
//
//    @Override
//    public void init() {
//        if (null == eruptCloudServerProp.getZkServers() || eruptCloudServerProp.getZkServers().size() <= 0) {
//            log.error("Not configured zookeeper cluster");
//            return;
//        }
//        zkClient = new ZkClient(String.join(",", eruptCloudServerProp.getZkServers()), 20000);
//        if (!zkClient.exists(ERUPT_NODE)) {
//            zkClient.createPersistent(ERUPT_NODE);
//        }
//        for (String child : zkClient.getChildren(ERUPT_NODE)) {
//            String data = zkClient.readData(ERUPT_NODE + "/" + child);
//            NodeManager.putNode(gson.fromJson(data, MetaNode.class));
//        }
//        zkClient.subscribeChildChanges(ERUPT_NODE, (parent, list) -> {
//            if (null == list) {
//                NodeManager.clearNodes();
//            } else {
//                for (String it : list) {
//                    String data = zkClient.readData(ERUPT_NODE + "/" + it);
//                    NodeManager.putNode(gson.fromJson(data, MetaNode.class));
//                }
//            }
//        });
//        // 监听状态变化
//        zkClient.subscribeStateChanges(new IZkStateListener() {
//            @Override
//            public void handleStateChanged(Watcher.Event.KeeperState keeperState) {
//                System.out.println("state:" + keeperState);
//            }
//
//            @Override
//            public void handleNewSession() {
//
//            }
//
//            @Override
//            public void handleSessionEstablishmentError(Throwable throwable) {
//                throwable.printStackTrace();
//            }
//        });
//    }
//
//    @Override
//    protected void distributePut(MetaNode metaNode) {
//
//    }
//
//    @Override
//    protected void distributeRemove(String nodeName) {
//
//    }
//
//}
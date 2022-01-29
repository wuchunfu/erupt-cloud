package xyz.erupt.cloud.server.node;

import xyz.erupt.cloud.server.base.MetaNode;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author YuePeng
 * date 2022/1/29
 */
public class NodeManager {

    private static final Map<String, MetaNode> metaNodeMap = new ConcurrentHashMap<>();

    public static int getMetaNodeNum() {
        return metaNodeMap.size();
    }

    //获取节点
    public static MetaNode getNode(String appName) {
        return metaNodeMap.get(appName);
    }

    //插入节点
    public static void putNode(MetaNode metaNode) {
        metaNodeMap.put(metaNode.getNodeName(), metaNode);
    }

    //移除节点
    public static void removeNode(String nodeName) {
        metaNodeMap.remove(nodeName);
    }

    //清空节点
    public static void clearNodeMap() {
        metaNodeMap.clear();
    }

}

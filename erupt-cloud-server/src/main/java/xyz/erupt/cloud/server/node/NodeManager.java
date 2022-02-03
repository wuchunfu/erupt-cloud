package xyz.erupt.cloud.server.node;

import xyz.erupt.cloud.server.base.MetaNode;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * @author YuePeng
 * date 2022/1/29
 */
public class NodeManager {

    private static Map<String, MetaNode> metaNodeMap = new ConcurrentHashMap<>();

    public static void consumerNode(Consumer<MetaNode> consumer) {
        for (MetaNode value : metaNodeMap.values()) {
            consumer.accept(value);
        }
    }

    public static void removeIf(Consumer<MetaNode> consumer) {
        for (MetaNode value : metaNodeMap.values()) {
            consumer.accept(value);
        }
    }

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
    public static void clearNodes() {
        metaNodeMap.clear();
    }

}

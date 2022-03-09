package xyz.erupt.cloud.server.distribute;

import xyz.erupt.cloud.server.base.MetaNode;

/**
 * 分布式处理
 *
 * @author YuePeng
 * date 2022/3/8 21:25
 */
public abstract class Distribute {

    //更新节点
    protected abstract void put(MetaNode metaNode);

    //移除节点
    protected abstract void remove(String nodeName);


    public void putNode(MetaNode metaNode) {
        this.put(metaNode);
    }

    public void removeNode(String nodeName) {
        this.remove(nodeName);
    }

}

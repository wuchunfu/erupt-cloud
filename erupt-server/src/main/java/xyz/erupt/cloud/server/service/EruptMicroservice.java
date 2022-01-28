package xyz.erupt.cloud.server.service;

import org.springframework.stereotype.Service;
import xyz.erupt.cloud.server.base.MetaNode;
import xyz.erupt.upms.util.IpUtil;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author YuePeng
 * date 2021/12/20 23:54
 */
@Service
public class EruptMicroservice {

    @Resource
    private ZkService zkService;

    private static final Map<String, MetaNode> metaNodeMap = new ConcurrentHashMap<>();

    public static int getMetaNodeNum() {
        return metaNodeMap.size();
    }

    public static MetaNode getMetaNode(String appName) {
        return metaNodeMap.get(appName);
    }

    @Resource
    private HttpServletRequest request;

    public void registerNode(MetaNode metaNode) {
        Optional.ofNullable(metaNodeMap.get(metaNode.getNodeName())).ifPresent(it ->
                metaNode.getLocations().addAll(it.getLocations()));
        metaNode.getLocations().add(new MetaNode.Location(IpUtil.getIpAddr(request), request.getRemotePort()));
        metaNode.getErupts().forEach(it -> metaNode.getEruptMap().put(it, it));
        metaNodeMap.put(metaNode.getNodeName(), metaNode);
    }

}

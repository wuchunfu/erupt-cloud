package xyz.erupt.cloud.server.service;

import org.springframework.stereotype.Service;
import xyz.erupt.cloud.server.base.MetaNode;
import xyz.erupt.cloud.server.model.CloudNode;
import xyz.erupt.core.exception.EruptWebApiRuntimeException;
import xyz.erupt.jpa.dao.EruptDao;
import xyz.erupt.upms.util.IpUtil;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author YuePeng
 * date 2021/12/20 23:54
 */
@Service
public class EruptNodeMicroservice {

    @Resource
    private ZkService zkService;

    @Resource
    private EruptDao eruptDao;

    private static final Map<String, MetaNode> metaNodeMap = new ConcurrentHashMap<>();

    public static int getMetaNodeNum() {
        return metaNodeMap.size();
    }

    public static MetaNode getMetaNode(String appName) {
        return metaNodeMap.get(appName);
    }

    @Resource
    private HttpServletRequest request;

    public CloudNode findNodeByAppName(String appName, String accessToken) {
        CloudNode cloudNode = eruptDao.queryEntity(CloudNode.class, CloudNode.APP_NAME + " = :" + CloudNode.APP_NAME, new HashMap<String, Object>() {{
            this.put(CloudNode.APP_NAME, appName);
        }});
        if (null == cloudNode) {
            throw new EruptWebApiRuntimeException(appName + " not found");
        }
        if (!cloudNode.getAccessToken().equals(accessToken)) {
            throw new EruptWebApiRuntimeException(cloudNode.getNodeName() + " Access token invalid");
        }
        return cloudNode;
    }

    public void registerNode(MetaNode metaNode) {
        Optional.ofNullable(metaNodeMap.get(metaNode.getNodeName())).ifPresent(it -> metaNode.getLocations().addAll(it.getLocations()));
        metaNode.getLocations().add(IpUtil.getIpAddr(request) + request.getRemotePort());
        metaNode.getErupts().forEach(it -> metaNode.getEruptMap().put(it, it));
        metaNodeMap.put(metaNode.getNodeName(), metaNode);
    }

    public void removeNode(String nodeName, String accessToken) {
        CloudNode cloudNode = this.findNodeByAppName(nodeName, accessToken);
        zkService.remove(metaNodeMap.remove(cloudNode.getNodeName()));
    }
}
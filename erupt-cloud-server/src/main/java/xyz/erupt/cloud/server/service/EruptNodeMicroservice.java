package xyz.erupt.cloud.server.service;

import org.springframework.stereotype.Service;
import xyz.erupt.cloud.server.model.CloudNode;
import xyz.erupt.cloud.server.node.MetaNode;
import xyz.erupt.cloud.server.node.NodeManager;
import xyz.erupt.jpa.dao.EruptDao;
import xyz.erupt.upms.util.IpUtil;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Optional;

/**
 * @author YuePeng
 * date 2021/12/20 23:54
 */
@Service
public class EruptNodeMicroservice {

    @Resource
    private EruptDao eruptDao;

    @Resource
    private HttpServletRequest request;

    @Resource
    private NodeManager nodeManager;

    public CloudNode findNodeByAppName(String nodeName, String accessToken) {
        CloudNode cloudNode = eruptDao.queryEntity(CloudNode.class, CloudNode.NODE_NAME + " = :" + CloudNode.NODE_NAME, new HashMap<String, Object>() {{
            this.put(CloudNode.NODE_NAME, nodeName);
        }});
        if (null == cloudNode) {
            throw new RuntimeException(nodeName + " not found");
        }
        if (!cloudNode.getAccessToken().equals(accessToken)) {
            throw new RuntimeException(cloudNode.getNodeName() + " Access token invalid");
        }
        return cloudNode;
    }

    //生成节点地址
    private String geneNodeLocation(MetaNode metaNode) {
        return request.getScheme() + "://" + IpUtil.getIpAddr(request) + ":" + metaNode.getPort() +
                (metaNode.getContextPath() == null ? "" : metaNode.getContextPath());
    }

    public void registerNode(MetaNode metaNode) {
        Optional.ofNullable(nodeManager.getNode(metaNode.getNodeName())).ifPresent(it -> metaNode.getLocations().addAll(it.getLocations()));
        metaNode.getLocations().add(geneNodeLocation(metaNode));
        metaNode.getErupts().forEach(it -> metaNode.getEruptMap().put(it, it));
        metaNode.setRegisterTime(new Date());
        nodeManager.putNode(metaNode);
    }

    public void removeNode(String nodeName, String accessToken) {
        this.findNodeByAppName(nodeName, accessToken);
//        nodeManager.removeNode();
    }

}
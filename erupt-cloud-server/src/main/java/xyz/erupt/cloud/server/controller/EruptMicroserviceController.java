package xyz.erupt.cloud.server.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import xyz.erupt.cloud.common.consts.CloudRestApiConst;
import xyz.erupt.cloud.server.base.R;
import xyz.erupt.cloud.server.model.CloudNode;
import xyz.erupt.cloud.server.node.MetaNode;
import xyz.erupt.cloud.server.service.EruptNodeMicroservice;
import xyz.erupt.core.exception.EruptWebApiRuntimeException;

/**
 * 客户端注册控制器
 *
 * @author YuePeng
 * date 2021/12/17 00:01
 */
@RestController
@AllArgsConstructor
public class EruptMicroserviceController {

    private final EruptNodeMicroservice eruptNodeMicroservice;

    @RequestMapping(CloudRestApiConst.REGISTER_NODE)
    public R registerNode(@RequestBody MetaNode metaNode) {
        CloudNode cloudNode = eruptNodeMicroservice.findNodeByAppName(metaNode.getNodeName(), metaNode.getAccessToken());
        if (!cloudNode.getStatus()) {
            throw new EruptWebApiRuntimeException(metaNode.getNodeName() + " prohibiting the registration");
        }
        eruptNodeMicroservice.registerNode(metaNode);
        return R.success();
    }

    @RequestMapping(CloudRestApiConst.REMOVE_NODE)
    public void removeNode(@RequestParam String nodeName, @RequestParam String accessToken) {
        eruptNodeMicroservice.removeNode(nodeName, accessToken);
    }

}

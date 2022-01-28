package xyz.erupt.cloud.server.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.erupt.cloud.server.base.MetaNode;
import xyz.erupt.cloud.server.base.R;
import xyz.erupt.cloud.server.model.CloudNode;
import xyz.erupt.cloud.server.service.EruptMicroservice;
import xyz.erupt.jpa.dao.EruptDao;

import java.util.HashMap;

/**
 * 客户端注册控制器
 *
 * @author YuePeng
 * date 2021/12/17 00:01
 */
@RestController
@AllArgsConstructor
public class EruptMicroserviceController {

    private final EruptDao eruptDao;

    private final EruptMicroservice eruptMicroservice;

    @RequestMapping("/register-node/{appName}")
    public R registerNode(@PathVariable("appName") final String appName, @RequestBody final MetaNode metaNode) {
        CloudNode cloudNode = eruptDao.queryEntity(CloudNode.class, CloudNode.APP_NAME + " = :" + CloudNode.APP_NAME, new HashMap<String, Object>() {{
            this.put(CloudNode.APP_NAME, appName);
        }});
        if (null == cloudNode) return R.error(appName + " not found");
        if (!cloudNode.getStatus()) return R.error(cloudNode.getName() + " prohibiting the registration");
        if (!cloudNode.getAccessToken().equals(metaNode.getAccessToken())) {
            return R.error(cloudNode.getAppName() + " Access token invalid");
        }
        metaNode.setNodeName(appName);
        eruptMicroservice.registerNode(metaNode);
        return R.success();
    }

}

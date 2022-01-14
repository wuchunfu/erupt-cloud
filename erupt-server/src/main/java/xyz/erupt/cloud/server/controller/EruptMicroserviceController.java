package xyz.erupt.cloud.server.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.erupt.cloud.server.base.MetaClient;
import xyz.erupt.cloud.server.base.R;
import xyz.erupt.cloud.server.model.CloudClient;
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

    @RequestMapping("/register-client/{appName}")
    public R registerClient(@PathVariable("appName") final String appName, @RequestBody final MetaClient metaClient) {
        CloudClient cloudClient = eruptDao.queryEntity(CloudClient.class, CloudClient.APP_NAME + " = :" + CloudClient.APP_NAME, new HashMap<String, Object>() {{
            this.put(CloudClient.APP_NAME, appName);
        }});
        if (null == cloudClient) {
            return R.error(appName + " not found");
        }
        if (!cloudClient.getAccessToken().equals(metaClient.getAccessToken())) {
            return R.error(cloudClient.getAppName() + " Access token invalid");
        }
        if (!cloudClient.getStatus()) {
            return R.error(cloudClient.getName() + " prohibiting the registration");
        }
        metaClient.setClientCode(appName);
        eruptMicroservice.registerClient(metaClient);
        return R.success();
    }

}

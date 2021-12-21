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

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

/**
 * 客户端注册控制器
 *
 * @author YuePeng
 * date 2021/12/17 00:01
 */
@RestController
@AllArgsConstructor
public class ServerController {

    private final EruptDao eruptDao;

    private final EruptMicroservice eruptMicroservice;

    @RequestMapping("/register-client/{code}")
    public R registerClient(@PathVariable("code") String clientCode,
                            @RequestBody MetaClient metaClient,
                            HttpServletRequest request) {
        CloudClient cloudClient = eruptDao.queryEntity(CloudClient.class,
                "code = :code and secret = :secret",
                new HashMap<String, Object>() {{
                    this.put("code", clientCode);
                    this.put("secret", metaClient.getSecret());
                }});

        if (null == cloudClient) {
            return R.error(clientCode + " not found");
        }
        if (!cloudClient.getStatus()) {
            return R.error(cloudClient.getName() + " Prohibiting the registration");
        }
        eruptMicroservice.registerClient(metaClient);
        return R.success();
    }

}

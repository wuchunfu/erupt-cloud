package xyz.erupt.cloud.server.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import xyz.erupt.cloud.server.model.CloudClient;
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
public class RegisterController {

    private final EruptDao eruptDao;

    private void registerClient(String secret, String clientCode) {
        CloudClient cloudClient = eruptDao.queryEntity(CloudClient.class, "code = :code", new HashMap<String, Object>(1) {{
            this.put("code", clientCode);
        }});
    }

}

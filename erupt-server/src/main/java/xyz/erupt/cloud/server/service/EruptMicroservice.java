package xyz.erupt.cloud.server.service;

import org.springframework.stereotype.Service;
import xyz.erupt.cloud.server.base.MetaClient;
import xyz.erupt.jpa.dao.EruptDao;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author YuePeng
 * date 2021/12/20 23:54
 */
@Service
public class EruptMicroservice {

    private static final Map<String, MetaClient> metaClientMap = new HashMap<>();

    @Resource
    private EruptDao eruptDao;


    public void registerClient(MetaClient metaClient) {
//        metaClient.setEruptMap(metaClient.getErupts().stream().collect(Maps));
        metaClientMap.put(metaClient.getClientCode(), metaClient);
    }


}

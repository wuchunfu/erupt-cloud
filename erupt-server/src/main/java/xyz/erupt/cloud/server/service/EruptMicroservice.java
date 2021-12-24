package xyz.erupt.cloud.server.service;

import org.springframework.stereotype.Service;
import xyz.erupt.cloud.server.base.MetaClient;
import xyz.erupt.jpa.dao.EruptDao;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
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

    @Resource
    private HttpServletRequest request;


    public void registerClient(MetaClient metaClient) {

        metaClientMap.put(metaClient.getClientCode(), metaClient);
    }


}

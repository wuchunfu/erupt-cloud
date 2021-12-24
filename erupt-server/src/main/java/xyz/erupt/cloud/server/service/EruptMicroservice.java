package xyz.erupt.cloud.server.service;

import org.springframework.stereotype.Service;
import xyz.erupt.cloud.server.base.MetaClient;
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
public class EruptMicroservice {

    private static final Map<String, MetaClient> metaClientMap = new ConcurrentHashMap<>();

    @Resource
    private HttpServletRequest request;

    public void registerClient(MetaClient metaClient) {
        Optional.ofNullable(metaClientMap.get(metaClient.getClientCode())).ifPresent(it ->
                metaClient.getLocations().addAll(it.getLocations()));
        metaClient.getLocations().add(new MetaClient.Location(IpUtil.getIpAddr(request), request.getRemotePort()));
        metaClient.getErupts().forEach(it -> metaClient.getEruptMap().put(it, it));
        metaClientMap.put(metaClient.getClientCode(), metaClient);
    }


}

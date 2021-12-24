package xyz.erupt.cloud.server.service;

import org.springframework.stereotype.Service;
import xyz.erupt.cloud.server.base.MetaClient;
import xyz.erupt.upms.util.IpUtil;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author YuePeng
 * date 2021/12/20 23:54
 */
@Service
public class EruptMicroservice {

    private static final Map<String, MetaClient> metaClientMap = new HashMap<>();

    @Resource
    private HttpServletRequest request;


    public void registerClient(MetaClient metaClient) {
        Optional.ofNullable(metaClientMap.get(metaClient.getClientCode())).ifPresent(it->
                metaClient.getSourceIp().addAll(it.getSourceIp()));
        MetaClient.Location location = new MetaClient.Location();
        location.setIp(IpUtil.getIpAddr(request));
        location.setPort(request.getRemotePort());
        metaClient.getSourceIp().add(location);
        metaClientMap.put(metaClient.getClientCode(), metaClient);
    }


}

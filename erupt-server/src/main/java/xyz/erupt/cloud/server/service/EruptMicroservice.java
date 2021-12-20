package xyz.erupt.cloud.server.service;

import org.springframework.stereotype.Service;
import xyz.erupt.cloud.server.base.ClientInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author YuePeng
 * date 2021/12/20 23:54
 */
@Service
public class EruptMicroservice {

    private final List<ClientInfo> clientInfos = new ArrayList<>();

    /**
     * @param secret      服务秘钥
     * @param clientName  服务名称
     * @param contextPath 上下文路径
     */
    public void registerClient(String secret, String clientName, String contextPath) {

    }

}

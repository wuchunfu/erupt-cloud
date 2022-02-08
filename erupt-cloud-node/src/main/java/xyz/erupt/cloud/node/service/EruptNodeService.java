package xyz.erupt.cloud.node.service;

import org.springframework.stereotype.Service;
import xyz.erupt.core.service.EruptCoreService;

/**
 * @author YuePeng
 * date 2022/2/7 19:34
 */
@Service
public class EruptNodeService {

    private EruptCoreService eruptCoreService;

    public EruptNodeService(EruptCoreService eruptCoreService) {
        this.eruptCoreService = eruptCoreService;
    }
}

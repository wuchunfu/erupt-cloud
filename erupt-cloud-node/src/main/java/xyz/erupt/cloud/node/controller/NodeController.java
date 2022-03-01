package xyz.erupt.cloud.node.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.erupt.cloud.common.consts.CloudRestApiConst;

/**
 * @author YuePeng
 * date 2022/3/1 23:17
 */
@RestController
public class NodeController {

    @RequestMapping(CloudRestApiConst.NODE_HEALTH)
    public String health() {
        return "pong";
    }

}

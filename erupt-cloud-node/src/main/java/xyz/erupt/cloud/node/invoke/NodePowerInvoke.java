package xyz.erupt.cloud.node.invoke;

import org.springframework.stereotype.Component;
import xyz.erupt.annotation.fun.PowerHandler;
import xyz.erupt.annotation.fun.PowerObject;
import xyz.erupt.cloud.node.config.EruptNodeProp;
import xyz.erupt.core.invoke.PowerInvoke;

import javax.annotation.Resource;

/**
 * @author YuePeng
 * date 2022/2/20 01:06
 */
@Component
public class NodePowerInvoke implements PowerHandler {

    static {
        PowerInvoke.RegisterPowerHandler(NodePowerInvoke.class);
    }

    @Resource
    private EruptNodeProp eruptNodeProp;

    @Override
    public void handler(PowerObject power) {
//        HttpUtil.get()
        eruptNodeProp.getBalanceAddress();
    }

}

package xyz.erupt.cloud.node.handler;

import org.springframework.stereotype.Component;
import xyz.erupt.annotation.expr.ExprBool;
import xyz.erupt.cloud.node.service.CloudServerRemoteService;
import xyz.erupt.core.context.MetaContext;

import javax.annotation.Resource;

/**
 * @author YuePeng
 * date 2022/3/8 22:19
 */
@Component
public class ViaMenuValueNodeCtrl implements ExprBool.ExprHandler {

    @Resource
    private CloudServerRemoteService cloudServerRemoteService;

    @Override
    public boolean handler(boolean expr, String[] params) {
        return cloudServerRemoteService.getMenuCodePermission(params[0], MetaContext.getToken());
    }

}

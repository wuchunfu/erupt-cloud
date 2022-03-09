package xyz.erupt.cloud.node.service;

import cn.hutool.http.HttpUtil;
import org.springframework.stereotype.Service;
import xyz.erupt.cloud.node.config.EruptNodeProp;
import xyz.erupt.core.config.GsonFactory;
import xyz.erupt.core.constant.EruptMutualConst;
import xyz.erupt.core.constant.EruptRestPath;
import xyz.erupt.core.context.MetaContext;
import xyz.erupt.core.module.MetaUserinfo;

import javax.annotation.Resource;

/**
 * @author YuePeng
 * date 2022/3/9 20:11
 */
@Service
public class CloudServerRemoteService {

    @Resource
    private EruptNodeProp eruptNodeProp;

    //校验菜单权限
    public boolean getMenuCodePermission(String menuValue, String token) {
        String permissionResult = HttpUtil.createGet(eruptNodeProp.getBalanceAddress() + EruptRestPath.ERUPT_CODE_PERMISSION)
                .form("menuValue", menuValue)
                .header(EruptMutualConst.TOKEN, token).execute().body();
        return Boolean.parseBoolean(permissionResult);
    }

    public MetaUserinfo getRemoteUserInfo(String token) {
        String userinfo = HttpUtil.createGet(eruptNodeProp.getBalanceAddress() + EruptRestPath.USERINFO)
                .header(EruptMutualConst.TOKEN, MetaContext.getToken()).execute().body();
        return GsonFactory.getGson().fromJson(userinfo, MetaUserinfo.class);
    }


}

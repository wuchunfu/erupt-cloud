package xyz.erupt.cloud.server;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import xyz.erupt.core.module.EruptModule;
import xyz.erupt.core.module.MetaMenu;
import xyz.erupt.core.module.ModuleInfo;

import java.util.List;

/**
 * @author YuePeng
 * date 2021/12/16 00:15
 */
@Configuration
@ComponentScan
@EnableConfigurationProperties
public class EruptCloudServerAutoConfiguration implements EruptModule {

    @Override
    public ModuleInfo info() {
        return ModuleInfo.builder().name("erupt-cloud-server").build();
    }

    @Override
    public List<MetaMenu> initMenus() {
        return EruptModule.super.initMenus();
    }

}

package xyz.erupt.cloud.client;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import xyz.erupt.cloud.client.config.EruptClientProp;

/**
 * @author YuePeng
 * date 2021/12/16 00:15
 */
@Configuration
@ComponentScan
@EnableConfigurationProperties(EruptClientProp.class)
public class EruptCloudClientAutoConfiguration {
}

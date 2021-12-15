package xyz.erupt.cloud.client.model;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import xyz.erupt.jpa.model.BaseModel;

/**
 * @author YuePeng
 * date 2021/12/16 00:28
 */
@Configuration
@ComponentScan
@EnableConfigurationProperties
public class CloudClient extends BaseModel {
}

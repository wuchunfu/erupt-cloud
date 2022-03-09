package xyz.erupt.cloud.server.config;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author YuePeng
 * date 2022/1/28
 */
@Getter
@Setter
@Component
@ConfigurationProperties("erupt-cloud.server")
public class EruptCloudServerProp {

    //实例ID，集群环境下，要保证每台机器不同。默认启动后随机生成，如无特殊情况可不配置。
    private String instanceId = RandomStringUtils.randomAlphabetic(6);

    //多实例集群订阅通道
    private String topicChannel = "erupt-cloud:notify:channel";

    // zookeeper 集群地址
    private List<String> zkServers;

}

package xyz.erupt.cloud.server.config;

import lombok.Getter;
import lombok.Setter;
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

    // zookeeper 集群地址
    private List<String> zkServers;

}

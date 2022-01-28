package xyz.erupt.cloud.node.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author YuePeng
 * date 2019-10-31.
 */
@Getter
@Setter
@Component
@ConfigurationProperties("erupt-cloud.node")
public class EruptNodeProp {

    //接入应用名称，用于分组隔离，推荐填写 本 Java 项目名称
    private String appName;

    //客户端秘钥（在服务端界面生成）
    private String accessToken;

    //服务端地址（支持集群）
    private String[] serverAddresses;

    //心跳时间(毫秒)
    private int heartbeatTime = 30 * 1000;

}

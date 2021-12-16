package xyz.erupt.cloud.client.config;

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
@ConfigurationProperties("erupt-cloud")
public class EruptClientProp {

    //客户端名称
    private String clientName;

    //客户端秘钥（在服务端界面生成）
    private String clientSecret;

    //服务端地址（支持集群）
    private String[] serverAddresses;


}

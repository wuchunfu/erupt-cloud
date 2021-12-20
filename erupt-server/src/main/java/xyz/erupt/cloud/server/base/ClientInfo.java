package xyz.erupt.cloud.server.base;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author YuePeng
 * date 2021/12/20 23:57
 */
@Getter
@Setter
public class ClientInfo {

    //应用名称
    private String clientName;

    //应用上下文
    private String contextPath;

    //服务IP
    private List<String> sourceIp;


}

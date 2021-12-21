package xyz.erupt.cloud.server.base;

import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author YuePeng
 * date 2021/12/22 00:12
 */
@Getter
@Setter
public class MetaClient {

    //秘钥
    private String secret;

    //应用编码
    private String clientCode;

    //应用上下文
    private String contextPath;

    //服务IP
    private Set<String> sourceIp = new HashSet<>();

    //服务所管理的erupt清单
    private List<String> erupts;

    //由erupts转换而来
    private Map<String, String> eruptMap;
}

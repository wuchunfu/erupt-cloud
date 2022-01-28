package xyz.erupt.cloud.server.base;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author YuePeng
 * date 2021/12/22 00:12
 */
@Getter
@Setter
public class MetaNode {

    //节点名
    private String nodeName;

    //访问令牌
    private String accessToken;

    //应用上下文
    private String contextPath;

    private String nodeAddress;

    //服务所管理的erupt清单
    private Set<String> erupts = new HashSet<>();

    //服务地址
    private transient Set<String> locations = new HashSet<>();

    //由erupts转换而来
    private transient Map<String, String> eruptMap = new HashMap<>();

    //调用次数
    private transient int count = 0;

    public int getCount() {
        return ++count;
    }

    @Getter
    @Setter
    public static class Location {

        public Location(String ip, int port) {
            this.ip = ip;
            this.port = port;
        }

        public Location() {
        }

        public String ip;

        public int port;

    }
}

package xyz.erupt.cloud.server.base;

import lombok.Getter;
import lombok.Setter;
import xyz.erupt.cloud.common.model.NodeInfo;

import java.io.Serializable;
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
public class MetaNode extends NodeInfo implements Serializable {

    private String nodeAddress;

    //服务地址
    private transient Set<String> locations = new HashSet<>();

    //由erupts转换而来
    private transient Map<String, String> eruptMap = new HashMap<>();

    //调用次数
    private transient int count = 0;

    public int getCount() {
        return ++count;
    }

}

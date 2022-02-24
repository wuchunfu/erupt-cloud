package xyz.erupt.cloud.server.base;

import lombok.Getter;
import lombok.Setter;

/**
 * @author YuePeng
 * date 2022/2/24 00:58
 */
@Getter
@Setter
public class CloudErrorModel {

    int status;

    String message;

    String node;

    public CloudErrorModel(int status, String message, String node) {
        this.status = status;
        this.message = message;
        this.node = node;
    }
}

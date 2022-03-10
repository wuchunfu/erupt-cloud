package xyz.erupt.cloud.server.distribute;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author YuePeng
 * date 2022/3/10 00:22
 */
@Component
public class DistributeFactory implements CommandLineRunner {

    public DistributeAbstract distributeAbstract;

    @Resource
    private RedisDistribute redisDistribute;

    @Override
    public void run(String... args) throws Exception {
        distributeAbstract = redisDistribute;
        distributeAbstract.init();
    }

    public DistributeAbstract factory() {
        return distributeAbstract;
    }


}

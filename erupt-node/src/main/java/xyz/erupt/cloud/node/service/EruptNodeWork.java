package xyz.erupt.cloud.node.service;

import cn.hutool.http.HttpUtil;
import lombok.SneakyThrows;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import xyz.erupt.cloud.node.config.EruptNodeProp;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @author YuePeng
 * date 2021/12/17 00:24
 */
@Component
public class EruptNodeWork implements ApplicationRunner, Runnable, DisposableBean {

    @Resource
    private EruptNodeProp eruptNodeProp;

    private int count = 0;

    private boolean runner = true;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Thread register = new Thread(this);
        register.start();
        register.setDaemon(true);
    }

    @SneakyThrows
    @Override
    public void run() {
        while (runner) {
            HttpUtil.createPost(eruptNodeProp.getServerAddresses()[count++ % eruptNodeProp.getServerAddresses().length] + "/")
                    .body("").execute();
            TimeUnit.MILLISECONDS.sleep(eruptNodeProp.getHeartbeatTime());
        }
    }

    @Override
    public void destroy() throws Exception {
        runner = false;
        //cancel register
        HttpUtil.createPost(eruptNodeProp.getServerAddresses()[count % eruptNodeProp.getServerAddresses().length] + "/")
                .body("").execute();
    }
}

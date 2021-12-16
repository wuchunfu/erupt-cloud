package xyz.erupt.cloud.client.service;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import xyz.erupt.cloud.client.config.EruptClientProp;

import javax.annotation.Resource;

/**
 * @author YuePeng
 * date 2021/12/17 00:24
 */
@Component
public class EruptClientWork implements ApplicationRunner {

    @Resource
    private EruptClientProp eruptClientProp;

    @Override
    public void run(ApplicationArguments args) throws Exception {

    }

}

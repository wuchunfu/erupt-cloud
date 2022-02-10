package xyz.erupt.cloud.node.aop;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import xyz.erupt.cloud.node.config.EruptNodeProp;
import xyz.erupt.core.view.EruptBuildModel;

import javax.annotation.Resource;

@Aspect
@Component
public class EruptBuildAop {

    @Resource
    private EruptNodeProp eruptNodeProp;

    @AfterReturning(pointcut = "execution(public * xyz.erupt.core.controller.EruptBuildController.getEruptBuild(..))", returning = "eruptBuildModel")
    public void doAfterReturning(EruptBuildModel eruptBuildModel) {
        eruptBuildModel.getEruptModel().setEruptName(eruptNodeProp.getNodeName() + "." + eruptBuildModel.getEruptModel().getEruptName());
    }
}
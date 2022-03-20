package xyz.erupt.cloud.server.distribute;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import xyz.erupt.cloud.server.config.EruptCloudServerProp;
import xyz.erupt.cloud.server.node.MetaNode;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * 分布式处理
 *
 * @author YuePeng
 * date 2022/3/8 21:25
 */
@Configuration
public class RedisDistribute extends DistributeAbstract implements MessageListener {

    public static final String NODE_SPACE = "node:";

    @Resource
    private EruptCloudServerProp eruptCloudServerProp;

    @Resource
    private RedisTemplate<String, MetaNode> redisTemplate;

    @Override
    public void init() {
//        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
//        Optional.ofNullable(redisTemplate.keys(eruptCloudServerProp.getKeyNameSpace() + NODE_SPACE + "*")).flatMap(keys ->
//                Optional.ofNullable(redisTemplate.opsForValue().multiGet(keys))).ifPresent(nodes -> {
//                    for (MetaNode metaNode : nodes) {
//                        NodeManager.putNode(metaNode);
//                    }
//                }
//        );
//        container.setConnectionFactory(redisConnectionFactory);
//        container.addMessageListener(new MessageListenerAdapter(this), new ChannelTopic(eruptCloudServerProp.getTopicChannel()));
    }

    @Override
    protected void distributePut(MetaNode metaNode) {
        redisTemplate.opsForValue().set(eruptCloudServerProp.getCloudNameSpace() + NODE_SPACE + metaNode.getNodeName(),
                metaNode, eruptCloudServerProp.getNodeExpireTime(), TimeUnit.MILLISECONDS);
        this.publishNodeInfo(ChannelSwapModel.Command.PUT, metaNode);
    }

    @Override
    protected void distributeRemove(String nodeName) {
        redisTemplate.delete(eruptCloudServerProp.getCloudNameSpace() + NODE_SPACE + nodeName);
        this.publishNodeInfo(ChannelSwapModel.Command.REMOVE, nodeName);
    }

    //发布节点信息
    private void publishNodeInfo(ChannelSwapModel.Command command, Object data) {
//        redisTemplate.convertAndSend(eruptCloudServerProp.getTopicChannel(), ChannelSwapModel.create(eruptCloudServerProp.getInstanceId(), command, data));
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
//        ChannelSwapModel channelSwapModel = (ChannelSwapModel) redisTemplate.getValueSerializer().deserialize(message.getBody());
//        if (null != channelSwapModel && !eruptCloudServerProp.getInstanceId().equals(channelSwapModel.getInstanceId())) {
//            switch (channelSwapModel.getCommand()) {
//                case PUT:
//                    NodeManager.putNode((MetaNode) channelSwapModel.getData());
//                    break;
//                case REMOVE:
//                    NodeManager.removeNode((String) channelSwapModel.getData());
//                    break;
//            }
//        }
    }

}

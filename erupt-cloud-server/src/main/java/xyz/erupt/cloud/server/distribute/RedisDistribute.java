package xyz.erupt.cloud.server.distribute;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import xyz.erupt.cloud.server.base.MetaNode;
import xyz.erupt.cloud.server.config.EruptCloudServerProp;
import xyz.erupt.cloud.server.model.ChannelSwapModel;

import javax.annotation.Resource;

/**
 * 分布式处理
 *
 * @author YuePeng
 * date 2022/3/8 21:25
 */
@Configuration
public class RedisDistribute extends Distribute implements MessageListener, CommandLineRunner {

    @Resource
    private RedisConnectionFactory redisConnectionFactory;

    @Resource
    private EruptCloudServerProp eruptCloudServerProp;

    @Resource
    private RedisTemplate<String, ChannelSwapModel> redisTemplate;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        ChannelSwapModel channelSwapModel = (ChannelSwapModel) redisTemplate.getValueSerializer().deserialize(message.getBody());
        if (null != channelSwapModel && !eruptCloudServerProp.getInstanceId().equals(channelSwapModel.getInstanceId())) {
            switch (channelSwapModel.getCommand()) {
                case PUT:

                    break;
                case REMOVE:

                    break;
            }
        }
    }

    @Override
    public void run(String... args) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory);
        container.addMessageListener(new MessageListenerAdapter(this), new ChannelTopic(eruptCloudServerProp.getTopicChannel()));
    }

    //更新节点
    @Override
    public void put(MetaNode metaNode) {
        this.publishNodeInfo(ChannelSwapModel.Command.PUT, metaNode);
    }

    //移除节点
    @Override
    public void remove(String nodeName) {
        this.publishNodeInfo(ChannelSwapModel.Command.REMOVE, nodeName);
    }

    //发布节点信息
    private void publishNodeInfo(ChannelSwapModel.Command command, Object data) {
        redisTemplate.convertAndSend(eruptCloudServerProp.getTopicChannel(), ChannelSwapModel.create(eruptCloudServerProp.getInstanceId(), command, data));
    }

}

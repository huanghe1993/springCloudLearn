package springcloud.producer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.support.MessageBuilder;
import springcloud.source.MySource;

/**
 * 这个注解给我们绑定消息通道的，Source是Stream给我们提供的，可以点进去看源码，
 * 这里我们使用自己定义的发射器发送的管道
 * 可以看到output和input,这和配置文件中的output，input对应的。
 */
@EnableBinding(MySource.class)
public class MySendService {

    @Autowired
    private Source source;

    public void sendMsg(String msg){
        source.output().send(MessageBuilder.withPayload(msg).build());
    }
}

package springcloud.source;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

/**
 *
 */
public interface MySource {

    // 管道名称为"myOutput"
    String str = "myOutput";

    @Output(str)
    MessageChannel myOutput();
}

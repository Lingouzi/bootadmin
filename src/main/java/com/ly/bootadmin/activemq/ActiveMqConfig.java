package com.ly.bootadmin.activemq;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;

import javax.jms.ConnectionFactory;
import java.util.concurrent.*;

/**
 * 解决queue和topic共存的问题
 * <p>
 * mq 系统默认的消息类型是queue, 要发送topic类别的,就要开启设置pub-sub-domain: true
 * 但是开启之后,queue不好用了. 所以需要做到2个兼容.
 *
 * @author linyun
 * @date 2018/12/3 12:00
 */
@Configuration
public class ActiveMqConfig {

    private static ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
            .setNameFormat("activemq-pool-%d").setDaemon(true).build();
    private static ExecutorService pool;

    static {
        if (pool == null) {
            pool = new ThreadPoolExecutor(6, 6,
                    0L, TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<>(6), namedThreadFactory, new ThreadPoolExecutor.AbortPolicy());
        }
    }


    @Bean
    public JmsListenerContainerFactory<?> topicListenerFactory(ConnectionFactory connectionFactory) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setPubSubDomain(true);
        factory.setConnectionFactory(connectionFactory);
        factory.setTaskExecutor(pool);
        return factory;
    }

    @Bean
    public JmsListenerContainerFactory<?> queueListenerFactory(ConnectionFactory connectionFactory) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setPubSubDomain(false);
        factory.setConnectionFactory(connectionFactory);
        factory.setTaskExecutor(pool);
        return factory;
    }

}

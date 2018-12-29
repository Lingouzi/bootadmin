package com.ly.bootadmin.activemq;

import com.ly.bootadmin.websocket.bean.WebSocketMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

/**
 * 消费者
 *
 * @author linyun
 * @date 2018/12/3 09:33
 */
@Slf4j
@Component
public class ActiveMqCustomer {

    private final SimpMessagingTemplate template;

    @Autowired
    public ActiveMqCustomer(SimpMessagingTemplate template) {
        this.template = template;
    }

    @JmsListener(destination = "queue01", containerFactory = "queueListenerFactory")
    public void customer(String msg) {
        System.out.println(Thread.currentThread().getName() + ",接收到的消息:" + msg);

        // 结合websocket, 收到消息之后,发送给页面
        WebSocketMessage returnMessage = WebSocketMessage.builder()
                .from("client")
                .content(msg)
                .date(System.currentTimeMillis())
                .build();
        template.convertAndSend("/message/public", returnMessage);
    }

    @JmsListener(destination = "delaySend01", containerFactory = "queueListenerFactory")
    public void customer2(String msg) {
        log.info(Thread.currentThread().getName() + "接收延时消息:" + msg);
    }

    @JmsListener(destination = "topic01", containerFactory = "topicListenerFactory")
    public void customer3(String msg) {
        log.info(Thread.currentThread().getName() + "收到订阅消息:" + msg);
    }
}

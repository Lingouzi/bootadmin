package com.ly.bootadmin.activemq;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 生产者
 *
 * @author linyun
 * @date 2018/12/3 09:33
 */
@RestController
public class ActiveMqProducter {

    private final ActiveMQHandler handler;

    @Autowired
    public ActiveMqProducter(ActiveMQHandler handler) {
        this.handler = handler;
    }

    /**
     * 即时消息
     *
     * @return
     */
    @RequestMapping("/jms/queue")
    public String queue() {
        // 构建一个消息, 名称是 queue01
        String message = "我是消息内容, " + System.currentTimeMillis();
        handler.queue("queue01", message);
        return "success";
    }

    /**
     * 延迟消息
     *
     * @return
     */
    @RequestMapping("/jms/delaySend")
    public String delaySend() {
        // 构建一个消息, 名称是 queue01
        for (int i = 0; i < 5; i++) {
            String message = "我是延迟消息内容, " + i;
            handler.delaySend("delaySend01", message, 10);
        }
        return "success";
    }

    @RequestMapping("/jms/topic")
    public String topic() {
        handler.topic("topic01", "hello world");
        return "success";
    }
}

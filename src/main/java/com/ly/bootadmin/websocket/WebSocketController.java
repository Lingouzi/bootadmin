package com.ly.bootadmin.websocket;

import com.ly.bootadmin.websocket.bean.WebSocketMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Map;

/**
 * @author linyun
 * @date 2018/12/3 15:31
 */
@Slf4j
@RestController
public class WebSocketController {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;


    /**
     * 测试订阅
     *
     * @param message
     * @param messageHeaders
     * @param destination
     * @param headers
     * @param id
     * @param body
     */
    @MessageMapping("/hello/{id}")
    public void hello(WebSocketMessage message,
                      MessageHeaders messageHeaders,
                      @Header("destination") String destination,
                      @Headers Map<String, Object> headers,
                      @DestinationVariable long id,
                      @Payload String body) {
        log.info("message:{}", message);
        log.info("messageHeaders:{}", messageHeaders);
        log.info("destination:{}", destination);
        log.info("headers:{}", headers);
        log.info("id:{}", id);
        log.info("body:{}", body);
    }


    /***  群消息   ***/

    /**
     * 主动返回消息。
     *
     * @param message
     */
    @MessageMapping("/hello")
    public void hello(@Payload WebSocketMessage message) {
        System.out.println(message);
        WebSocketMessage returnMessage = new WebSocketMessage();
        returnMessage.setContent("转发，" + message.getContent());
        simpMessagingTemplate.convertAndSend("/message/public", returnMessage);
    }

    /**
     * 使用注解的方式返回消息
     *
     * @param message
     * @return
     */
    @MessageMapping("/hello1")
    @SendTo("/message/public")
    public WebSocketMessage hello1(@Payload WebSocketMessage message) {
        System.out.println(message);
        WebSocketMessage returnMessage = new WebSocketMessage();
        returnMessage.setContent("转发2，" + message.getContent());
        return returnMessage;
    }

    /***  点对点   ***/

    /**
     * 点对点发送消息。接收消息的人是从消息中获取的。
     *
     * @param message
     * @param principal
     */
    @MessageMapping("/hello2")
    public void hello2(@Payload WebSocketMessage message, Principal principal) {
        System.out.println(message);
        System.out.println(principal);
        WebSocketMessage returnMessage = new WebSocketMessage();
        returnMessage.setContent("转发3，" + message.getContent());
        simpMessagingTemplate.convertAndSendToUser(message.getTo(), "/notice/msg", returnMessage);
    }

}

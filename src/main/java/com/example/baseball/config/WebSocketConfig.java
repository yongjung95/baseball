package com.example.baseball.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS(); // STOMP 엔드포인트
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
//        config.setApplicationDestinationPrefixes("/app") // 클라이언트 메시지 전송 prefix 설정
//                .enableStompBrokerRelay("/topic") // RabbitMQ 브로커 설정
//                .setSystemHeartbeatSendInterval(20000)
//                .setSystemHeartbeatReceiveInterval(20000)  // 20초마다 heartbeat
//                .setRelayHost("localhost")  // RabbitMQ 서버의 호스트
//                .setRelayPort(61613)        // STOMP 포트 (기본 61613)
//                .setClientLogin("guest")    // RabbitMQ 로그인
//                .setClientPasscode("guest");


        config.setApplicationDestinationPrefixes("/app")
                .enableSimpleBroker("/comment", "/chat"); // 메시지 브로커 설정 (인메모리)

    }
}

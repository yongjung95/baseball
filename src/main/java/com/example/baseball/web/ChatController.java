package com.example.baseball.web;

import com.example.baseball.dto.ChatDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChatController {

    private final RabbitTemplate rabbitTemplate;

    @MessageMapping("/sendMessage")
    public void sendMessage(@RequestBody @Valid ChatDto.SendChatDto dto) {
        if (!StringUtils.hasText(dto.getChatAuthorId())) {
            return;
        }

        rabbitTemplate.convertAndSend("amq.topic", "chatQueue", dto);
    }
}

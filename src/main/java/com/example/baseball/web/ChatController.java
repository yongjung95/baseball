package com.example.baseball.web;

import com.example.baseball.dto.ChatDto;
import com.example.baseball.dto.MemberDto;
import com.example.baseball.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final MemberService memberService;

    @MessageMapping("/sendMessage")
    public void sendMessage(@RequestBody @Valid ChatDto.SendChatDto dto) {
        if (!StringUtils.hasText(dto.getChatAuthorId())) {
            return;
        }

        MemberDto.ResponseMemberDto responseMemberDto = memberService.selectMemberByMemberId(dto.getChatAuthorId());
        ChatDto.ReceiveChatDto receiveChatDto = new ChatDto.ReceiveChatDto();
        receiveChatDto.setChatAuthorId(dto.getChatAuthorId());
        receiveChatDto.setChatAuthorNickname(responseMemberDto.getNickname());
        receiveChatDto.setContent(dto.getContent());
        receiveChatDto.setTeamName(StringUtils.hasText(responseMemberDto.getTeamName()) ? responseMemberDto.getTeamName() : "미정");
        receiveChatDto.setTeamLogo(StringUtils.hasText(responseMemberDto.getTeamName()) ? responseMemberDto.getTeamLogo() : "/logos/kbo/baseball.svg");
        messagingTemplate.convertAndSend("/topic/chat", receiveChatDto);
    }
}

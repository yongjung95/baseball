package com.example.baseball.service;

import com.example.baseball.domain.Chat;
import com.example.baseball.domain.Member;
import com.example.baseball.dto.ChatDto;
import com.example.baseball.repository.ChatRepository;
import com.example.baseball.repository.MemberRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Transactional
@RequiredArgsConstructor
public class ChatService {

    private final SimpMessagingTemplate messagingTemplate;

    private final MemberRepository memberRepository;

    private final ChatRepository chatRepository;

    private final EntityManager entityManager;

    @RabbitListener(queues = "chatQueue") // 선언한 큐 이름과 동일하게 설정
    public void receiveMessage(ChatDto.SendChatDto dto) {
        Member findMember = memberRepository.findByMemberId(dto.getChatAuthorId());

        ChatDto.ReceiveChatDto receiveChatDto = new ChatDto.ReceiveChatDto();
        receiveChatDto.setChatAuthorId(findMember.getMemberId());
        receiveChatDto.setChatAuthorNickname(findMember.getNickname());
        receiveChatDto.setContent(dto.getContent());
        receiveChatDto.setTeamName(StringUtils.hasText(findMember.getFollowedTeam().getTeamName()) ? findMember.getFollowedTeam().getTeamName() : "미정");
        receiveChatDto.setTeamLogo(StringUtils.hasText(findMember.getFollowedTeam().getTeamName()) ? findMember.getFollowedTeam().getLogo() : "/logos/kbo/baseball.svg");

        // 메시지 처리 로직

        Chat chat = Chat.builder()
                .memberId(findMember.getMemberId())
                .content(dto.getContent())
                .build();
        chatRepository.save(chat);

        messagingTemplate.convertAndSend("/chat", receiveChatDto);
    }
}

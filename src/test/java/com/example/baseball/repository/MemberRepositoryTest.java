package com.example.baseball.repository;

import com.example.baseball.domain.Member;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void 회원_생성() throws Exception {
        //given
        Member member = Member.builder()
                .memberId(UUID.randomUUID().toString())
                .email("yongjung95@gmail.com")
                .passwd("1234")
                .nickName("정이")
                .build();

        //when
        memberRepository.save(member);

        entityManager.flush();
        entityManager.clear();

        //then
        Member findMember = memberRepository.findById(member.getMemberId()).get();

        assertThat(findMember.getEmail()).isEqualTo(member.getEmail());
    }

    @Test
    void 회원_닉네임으로_조회() throws Exception {
        //given
        Member member = Member.builder()
                .memberId(UUID.randomUUID().toString())
                .email("yongjung95@gmail.com")
                .passwd("1234")
                .nickName("정이")
                .build();

        //when
        memberRepository.save(member);

        entityManager.flush();
        entityManager.clear();

        //then
        Member findMember = memberRepository.findByNickName("정이");

        assertThat(findMember.getEmail()).isEqualTo(member.getEmail());
    }
}
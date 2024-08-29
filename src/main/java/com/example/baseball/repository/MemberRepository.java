package com.example.baseball.repository;

import com.example.baseball.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, String> {

    Member findByNickname(String nickname);
    Member findByEmail(String email);
}

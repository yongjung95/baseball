package com.example.baseball.repository;

import com.example.baseball.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberRepository extends JpaRepository<Member, String> {

    Member findByNickname(String nickname);
    Member findByEmail(String email);
    @Query("select m from Member m left join fetch m.followedTeam where m.memberId = :memberId")
    Member findByMemberId(@Param("memberId") String memberId);
}

package com.example.baseball.repository;

import com.example.baseball.domain.Member;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberRepository extends JpaRepository<Member, String> {

    @Query("select m from Member m left join fetch m.followedTeam where m.nickname = :nickname and m.isUse = true")
    Member findByNickname(@Param("nickname") String nickname);
    @Query("select m from Member m left join fetch m.followedTeam where m.email = :email and m.isUse = true")
    Member findByEmail(@Param("email") String email);
    @Query("select m from Member m left join fetch m.followedTeam where m.id = :id and m.isUse = true")
    Member findMemberById(@Param("id") String id);

    @Cacheable("members")
    @Query("select m from Member m left join fetch m.followedTeam where m.memberId = :memberId and m.isUse = true")
    Member findByMemberId(@Param("memberId") String memberId);
}

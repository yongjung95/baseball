package com.example.baseball.repository;

import com.example.baseball.domain.AttachmentFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AttachmentFileRepository extends JpaRepository<AttachmentFile, Long> {

    @Query("select a from AttachmentFile a join fetch a.post join fetch a.regMember where a.post.postId =:postId and a.isUse = true")
    List<AttachmentFile> selectAttachmentFileListByPostId(@Param("postId") Long postId);

    @Query("SELECT a FROM AttachmentFile a WHERE a.id =:id and a.regMember.memberId =:regMemberId and a.isUse = true")
    AttachmentFile SelectAttachmentFileByIdAndRegMemberId(@Param("id") Long id, @Param("regMemberId") String regMemberId);

}

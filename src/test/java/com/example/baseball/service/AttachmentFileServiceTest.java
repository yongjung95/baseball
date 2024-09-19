package com.example.baseball.service;

import com.example.baseball.domain.AttachmentFile;
import com.example.baseball.domain.Member;
import com.example.baseball.domain.Post;
import com.example.baseball.domain.Team;
import com.example.baseball.dto.AttachmentFileDto;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class AttachmentFileServiceTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private AttachmentFileService attachmentFileService;

    @Test
    void 파일_업로드_테스트() throws Exception {
        //given
        Team team = Team.builder()
                .teamId(UUID.randomUUID().toString())
                .teamName("SSG")
                .build();
        entityManager.persist(team);

        Member member = Member.builder()
                .memberId(UUID.randomUUID().toString())
                .email("yongjung95@gmail.com")
                .nickname("정이")
                .password("123456")
                .followedTeam(team)
                .build();
        entityManager.persist(member);

        Post post = Post.builder()
                .title("게시글 테스트")
                .content("내용")
                .author(member)
                .followedTeam(team)
                .build();

        entityManager.persist(post);

        //when
        MultipartFile mockFile = new MockMultipartFile(
                "image",
                "test-file.img",
                MediaType.IMAGE_JPEG_VALUE,
                "This is a plain text file".getBytes()
        );
        List<MultipartFile> files = new ArrayList<>();
        files.add(mockFile);

        List<AttachmentFileDto.SelectAttachmentFileDto> result = attachmentFileService.saveAttachmentFile(files, member, post);

        //then
        assertThat(result.size()).isEqualTo(1);

        for (AttachmentFileDto.SelectAttachmentFileDto selectAttachmentFileDto : result) {
            System.out.println("selectAttachmentFileDto = " + selectAttachmentFileDto);
        }
    }

    @Test
    void 파일_조회_테스트() throws Exception {
        //given
        Team team = Team.builder()
                .teamId(UUID.randomUUID().toString())
                .teamName("SSG")
                .build();
        entityManager.persist(team);

        Member member = Member.builder()
                .memberId(UUID.randomUUID().toString())
                .email("yongjung95@gmail.com")
                .nickname("정이")
                .password("123456")
                .followedTeam(team)
                .build();
        entityManager.persist(member);

        Post post = Post.builder()
                .title("게시글 테스트")
                .content("내용")
                .author(member)
                .followedTeam(team)
                .build();

        entityManager.persist(post);

        MultipartFile mockFile = new MockMultipartFile(
                "image",
                "test-file.img",
                MediaType.IMAGE_JPEG_VALUE,
                "This is a plain text file".getBytes()
        );

        AttachmentFile attachmentFile = AttachmentFile.builder()
                .fileName(mockFile.getName())
                .fileOriginalName(mockFile.getOriginalFilename())
                .fileSize(mockFile.getSize())
                .fileContentType(mockFile.getContentType())
                .post(post)
                .regMember(member)
                .build();

        entityManager.persist(attachmentFile);

        AttachmentFile attachmentFile2 = AttachmentFile.builder()
                .fileName(mockFile.getName())
                .fileOriginalName(mockFile.getOriginalFilename())
                .fileSize(mockFile.getSize())
                .fileContentType(mockFile.getContentType())
                .post(post)
                .regMember(member)
                .build();

        entityManager.persist(attachmentFile2);
        entityManager.flush();
        entityManager.clear();

        //when

        List<AttachmentFileDto.SelectAttachmentFileDto> result = attachmentFileService.selectAttachmentFileByPostId(post.getPostId());
        //then
        assertThat(result.size()).isEqualTo(2);

        for (AttachmentFileDto.SelectAttachmentFileDto selectAttachmentFileDto : result) {
            System.out.println("selectAttachmentFileDto = " + selectAttachmentFileDto);
        }
    }

    @Test
    void 파일_삭제() throws Exception {
        //given
        Team team = Team.builder()
                .teamId(UUID.randomUUID().toString())
                .teamName("SSG")
                .build();
        entityManager.persist(team);

        Member member = Member.builder()
                .memberId(UUID.randomUUID().toString())
                .email("yongjung95@gmail.com")
                .nickname("정이")
                .password("123456")
                .followedTeam(team)
                .build();
        entityManager.persist(member);

        Post post = Post.builder()
                .title("게시글 테스트")
                .content("내용")
                .author(member)
                .followedTeam(team)
                .build();

        entityManager.persist(post);

        MultipartFile mockFile = new MockMultipartFile(
                "image",
                "test-file.img",
                MediaType.IMAGE_JPEG_VALUE,
                "This is a plain text file".getBytes()
        );

        AttachmentFile attachmentFile = AttachmentFile.builder()
                .fileName(mockFile.getName())
                .fileOriginalName(mockFile.getOriginalFilename())
                .fileSize(mockFile.getSize())
                .fileContentType(mockFile.getContentType())
                .post(post)
                .regMember(member)
                .build();

        entityManager.persist(attachmentFile);
        entityManager.flush();
        entityManager.clear();
        //when

        attachmentFileService.deleteAttachmentFile(attachmentFile.getId(), member.getMemberId());

        //then
        List<AttachmentFileDto.SelectAttachmentFileDto> result = attachmentFileService.selectAttachmentFileByPostId(post.getPostId());

        assertThat(result.size()).isEqualTo(0);
    }
}
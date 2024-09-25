package com.example.baseball.service;

import com.example.baseball.domain.Member;
import com.example.baseball.domain.Post;
import com.example.baseball.domain.PostLike;
import com.example.baseball.dto.AttachmentFileDto;
import com.example.baseball.dto.CommentDto;
import com.example.baseball.dto.PostDto;
import com.example.baseball.repository.MemberRepository;
import com.example.baseball.repository.PostLikeRepository;
import com.example.baseball.repository.PostRepository;
import com.example.baseball.response.error.ErrorCode;
import com.example.baseball.response.exception.ApiException;
import com.example.baseball.util.FormatUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final PostLikeRepository postLikeRepository;
    private final ModelMapper modelMapper;
    private final AttachmentFileService attachmentFileService;
    private final CommentService commentService;

    public PostDto.ResponsePostDto savePost(PostDto.SavePostRequestDto dto) throws IOException {
        Member member = memberRepository.findByMemberId(dto.getAuthorId());
        if (member == null) {
            throw new ApiException(ErrorCode.MEMBER_IS_NOT_FOUND);
        }
        if (member.getFollowedTeam() == null) {
            throw new ApiException(ErrorCode.POST_CREATE_IS_ONLY_SELECTED_TEAM);
        }

        Post post = Post.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .author(member)
                .followedTeam(member.getFollowedTeam())
                .build();

        postRepository.save(post);
        PostDto.ResponsePostDto result = modelMapper.map(post, PostDto.ResponsePostDto.class);
        result.setTeamName(member.getFollowedTeam().getTeamName());
        result.setAuthorNickname(member.getNickname());

        if (!dto.getFiles().isEmpty()) {
            List<AttachmentFileDto.SelectAttachmentFileDto> files = attachmentFileService.saveAttachmentFile(dto.getFiles(), member, post);
            result.setFiles(files);
        }

        return result;
    }

    public void deletePost(PostDto.UpdatePostRequestDto dto) {
        Member member = memberRepository.findByMemberId(dto.getAuthorId());
        if (member == null) {
            throw new ApiException(ErrorCode.MEMBER_IS_NOT_FOUND);
        }

        Post post = postRepository.findByPostId(dto.getPostId());
        if (post == null) {
            throw new ApiException(ErrorCode.POST_IS_NOT_FOUND);
        }

        if (!post.getAuthor().getMemberId().equals(dto.getAuthorId())) {
            throw new ApiException(ErrorCode.AUTHOR_IS_NOT_MATCHED);
        }

        List<AttachmentFileDto.SelectAttachmentFileDto> attachmentFileList = attachmentFileService.selectAttachmentFileByPostId(post.getPostId());

        for (AttachmentFileDto.SelectAttachmentFileDto selectAttachmentFileDto : attachmentFileList) {
            attachmentFileService.deleteAttachmentFile(selectAttachmentFileDto.getId(), dto.getAuthorId());
        }

        post.changeIsUse();
    }

    public PostDto.ResponsePostDto updatePost(PostDto.UpdatePostRequestDto dto) throws IOException {
        Member member = memberRepository.findByMemberId(dto.getAuthorId());
        if (member == null) {
            throw new ApiException(ErrorCode.MEMBER_IS_NOT_FOUND);
        }

        Post post = postRepository.findByPostId(dto.getPostId());
        if (post == null) {
            throw new ApiException(ErrorCode.POST_IS_NOT_FOUND);
        }

        if (!post.getAuthor().getMemberId().equals(dto.getAuthorId())) {
            throw new ApiException(ErrorCode.AUTHOR_IS_NOT_MATCHED);
        }

        List<Long> deleteFileIds = dto.getDeleteFileIds();
        if (deleteFileIds != null && !deleteFileIds.isEmpty()) {
            for (Long deleteFileId : deleteFileIds) {
                attachmentFileService.deleteAttachmentFile(deleteFileId, dto.getAuthorId());

            }
        }

        post.updatePost(dto.getTitle(), dto.getContent());

        PostDto.ResponsePostDto result = convertToDto(post);
        result.setAuthorNickname(post.getAuthor().getNickname());
        result.setTeamName(post.getFollowedTeam().getTeamName());

        List<MultipartFile> files = dto.getFiles();
        if (!files.isEmpty()) {
            List<AttachmentFileDto.SelectAttachmentFileDto> resultFiles = attachmentFileService.saveAttachmentFile(dto.getFiles(), member, post);
            result.setFiles(resultFiles);
        }

        return result;
    }

    public Page<PostDto.SelectPostListDto> selectPostList(String searchText, String teamId, Pageable pageable) {
        Page<PostDto.SelectPostListDto> result = postRepository.selectPostListByTeam(searchText, teamId, pageable);

        List<PostDto.SelectPostListDto> content = result.getContent();
        content.forEach(responsePostDto -> responsePostDto.setCreateDate(FormatUtil.formatTimeAgo(responsePostDto.getCreateTime())));

        return result;
    }

    public PostDto.ResponsePostDto selectPost(Long postId) {
        Post post = postRepository.findByPostId(postId);

        if (post == null) {
            return null;
        }

        post.updateViewCnt();

        return convertToDto(post);
    }

    public boolean savePostLike(String memberId, Long postId) {
        Member member = memberRepository.findByMemberId(memberId);
        if (member == null) {
            throw new ApiException(ErrorCode.MEMBER_IS_NOT_FOUND);
        }

        Post post = postRepository.findByPostId(postId);
        if (post == null) {
            throw new ApiException(ErrorCode.POST_IS_NOT_FOUND);
        }

        PostLike postlike = postLikeRepository.findByMemberAndPost(member, post);

        if (postlike != null) {
            throw new ApiException(ErrorCode.CAN_NOT_POST_LIKE);
        }

        if (post.getAuthor().getMemberId().equals(memberId)) {
            throw new ApiException(ErrorCode.CAN_NOT_POST_LIKE);
        }

        PostLike postLike = PostLike.builder()
                .member(member)
                .post(post)
                .build();
        postLikeRepository.save(postLike);

        return true;
    }

    private PostDto.ResponsePostDto convertToDto(Post post) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.addMappings(new PropertyMap<Post, PostDto.ResponsePostDto>() {
            @Override
            protected void configure() {
                skip(destination.getComments());
                skip(destination.getFiles());
                map().setTeamName(source.getFollowedTeam().getTeamName());
            }
        });
        PostDto.ResponsePostDto responsePostDto = modelMapper.map(post, PostDto.ResponsePostDto.class);
        responsePostDto.setAuthorNickname(post.getAuthor().getNickname());
        responsePostDto.setTeamName(post.getFollowedTeam().getTeamName());
        responsePostDto.setSymbol(post.getFollowedTeam().getSymbol());
        responsePostDto.setAuthorId(post.getAuthor().getMemberId());
        responsePostDto.setCreateDate(FormatUtil.formatTimeAgo(post.getCreatedDate()));
        responsePostDto.setLikeCnt(post.getPostLikes().size());

        // 댓글
        List<CommentDto.ResponseCommentDto> commentDtoList = commentService.selectCommentList(post.getPostId());
        responsePostDto.setCommentCnt(commentDtoList.size());
        responsePostDto.setComments(commentDtoList);
        // 댓글

        // 첨부파일
        List<AttachmentFileDto.SelectAttachmentFileDto> files = attachmentFileService.selectAttachmentFileByPostId(post.getPostId());
        responsePostDto.setFiles(files);
        // 첨부파일

        return responsePostDto;
    }
}

package com.example.baseball.service;

import com.example.baseball.domain.AttachmentFile;
import com.example.baseball.domain.Member;
import com.example.baseball.domain.Post;
import com.example.baseball.dto.AttachmentFileDto;
import com.example.baseball.repository.AttachmentFileRepository;
import com.example.baseball.response.error.ErrorCode;
import com.example.baseball.response.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttachmentFileService {

    private final AttachmentFileRepository attachmentFileRepository;
    private final ModelMapper modelMapper;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public List<AttachmentFileDto.SelectAttachmentFileDto> saveAttachmentFile(List<MultipartFile> fileList,
                                                                              Member member, Post post) throws IOException {

        List<AttachmentFile> files = new ArrayList<>();
        for (MultipartFile file : fileList) {
            if (file.isEmpty()) {
                throw new ApiException(ErrorCode.ATTACHMENT_FILE_IS_EMPTY);
            }

            if (!isImageFile(file)) {
                throw new ApiException(ErrorCode.ATTACHMENT_FILE_IS_ONLY_IMAGE);
            }

            String extension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);

            String fileName = UUID.randomUUID() + "." + extension;
            // 파일 저장 경로 설정
            Path filePath = Paths.get(uploadDir, fileName);

            // 디렉토리가 없으면 생성
            File directory = new File(uploadDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // 파일 저장
            Files.write(filePath, file.getBytes());

            AttachmentFile attachmentFile = AttachmentFile.builder()
                    .fileName(fileName)
                    .fileOriginalName(file.getOriginalFilename())
                    .filePath(filePath.toString())
                    .fileSize(file.getSize())
                    .fileExtension(extension)
                    .fileContentType(file.getContentType())
                    .post(post)
                    .regMember(member)
                    .build();

            files.add(attachmentFile);
        }

        attachmentFileRepository.saveAll(files);

        return files.stream().map(attachmentFile -> {
            AttachmentFileDto.SelectAttachmentFileDto map = modelMapper.map(attachmentFile, AttachmentFileDto.SelectAttachmentFileDto.class);
            map.setPostId(attachmentFile.getPost().getPostId());
            map.setRegMemberId(attachmentFile.getRegMember().getMemberId());
            return map;
        }).collect(Collectors.toList());
    }

    private boolean isImageFile(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && (contentType.equals("image/jpeg") ||
                contentType.equals("image/png") ||
                contentType.equals("image/gif"));
    }

    public List<AttachmentFileDto.SelectAttachmentFileDto> selectAttachmentFileByPostId(Long postId) {
        return attachmentFileRepository.selectAttachmentFileListByPostId(postId).stream().map(attachmentFile -> {
            AttachmentFileDto.SelectAttachmentFileDto map = modelMapper.map(attachmentFile, AttachmentFileDto.SelectAttachmentFileDto.class);
            map.setPostId(attachmentFile.getPost().getPostId());
            map.setRegMemberId(attachmentFile.getRegMember().getMemberId());
            return map;
        }).collect(Collectors.toList());
    }

    public void deleteAttachmentFile(Long attachmentFileId, String regMemberId) {
        AttachmentFile attachmentFile = attachmentFileRepository.SelectAttachmentFileByIdAndRegMemberId(attachmentFileId, regMemberId);

        if (attachmentFile == null) {
            throw new ApiException(ErrorCode.ATTACHMENT_FILE_IS_NOT_FOUND);
        }

        attachmentFile.deleteFile();
    }

}

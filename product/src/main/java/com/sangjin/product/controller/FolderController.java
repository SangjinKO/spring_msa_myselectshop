package com.sangjin.product.controller;

import com.sangjin.product.config.security.CustomUserPrincipal;
import com.sangjin.product.dto.FolderRequestDto;
import com.sangjin.product.dto.FolderResponseDto;
import com.sangjin.product.service.FolderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class FolderController {

    private final FolderService folderService;

    @PostMapping("/folders")
    public void addFolders(@RequestBody FolderRequestDto folderRequestDto,
                           @AuthenticationPrincipal CustomUserPrincipal userPrincipal) {

        List<String> folderNames = folderRequestDto.getFolderNames();

        folderService.addFolders(folderNames, userPrincipal.getUserId());
    }

    // 회원이 등록한 모든 폴더 조회
    @GetMapping("/folders")
    public List<FolderResponseDto> getFolders(@AuthenticationPrincipal CustomUserPrincipal userPrincipal) {
        return folderService.getFolders(userPrincipal.getUserId());
    }

}

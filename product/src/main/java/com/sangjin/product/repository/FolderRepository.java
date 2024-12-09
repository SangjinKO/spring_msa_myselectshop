package com.sangjin.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FolderRepository extends JpaRepository<Folder, Long> {

    List<Folder> findAllByUserIdAndNameIn(Long userId, List<String> folderNames);
    List<Folder> findAllByUserId(Long userId);

}

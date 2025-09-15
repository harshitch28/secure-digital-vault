package com.digitalvault.vault.service;

import com.digitalvault.vault.dto.VaultFileList;
import com.digitalvault.vault.entity.User;
import com.digitalvault.vault.entity.VaultFile;
import com.digitalvault.vault.repository.UserRepository;
import com.digitalvault.vault.repository.VaultFileRepository;
import com.digitalvault.vault.util.FileEncryptionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class VaultFileService {

    private static final String STORAGE_DIR = System.getProperty("user.home") + "/vault-storage/";

    @Autowired
    private VaultFileRepository vaultFileRepository;

    @Autowired
    private UserRepository userRepository;

    public VaultFile storeEncryptedFile(MultipartFile file) throws Exception {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow();

        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        File encryptedFile = new File(STORAGE_DIR + fileName);

        new File(STORAGE_DIR).mkdirs(); // Ensure dir exists

        FileEncryptionUtil.encryptFile(file.getInputStream(), encryptedFile);

        VaultFile vaultFile = new VaultFile();
        vaultFile.setFileName(file.getOriginalFilename());
        vaultFile.setFileType(file.getContentType());
        vaultFile.setFilePath(encryptedFile.getAbsolutePath());
        vaultFile.setUploadTime(LocalDateTime.now());
        vaultFile.setOwner(user);

        return vaultFileRepository.save(vaultFile);
    }
    public VaultFile getFileByIdIfOwner(Long fileId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow();

        VaultFile file = vaultFileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found"));

        if (!file.getOwner().getId().equals(user.getId())) {
            throw new SecurityException("Access denied. You are not the file owner.");
        }

        return file;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<VaultFile> getAllFiles() {
        return vaultFileRepository.findAll();
    }

    public List<VaultFileList> getFilesForOwner() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow();
        return vaultFileRepository.findFileIdsAndNamesByOwner(user);
    }
}

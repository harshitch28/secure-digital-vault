package com.digitalvault.vault.controller;

import com.digitalvault.vault.entity.VaultFile;
import com.digitalvault.vault.service.VaultFileService;
import com.digitalvault.vault.util.FileEncryptionUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.OutputStream;
import java.util.List;

@RestController
@RequestMapping("/api/vault")
public class VaultFileController {

    @Autowired
    private VaultFileService vaultFileService;

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            VaultFile savedFile = vaultFileService.storeEncryptedFile(file);
            return ResponseEntity.ok("✅ File uploaded & encrypted successfully: " + savedFile.getFileName() + " "+ savedFile.getId());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("❌ Upload failed: " + e.getMessage());
        }
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("/download/{id}")
    public ResponseEntity<?> downloadFile(@PathVariable Long id, HttpServletResponse response) {
        try {
            VaultFile file = vaultFileService.getFileByIdIfOwner(id);
            File encryptedFile = new File(file.getFilePath());

            response.setContentType(file.getFileType());
            response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getFileName() + "\"");

            OutputStream outStream = response.getOutputStream();
            FileEncryptionUtil.decryptFile(encryptedFile, outStream);
            outStream.flush();

            return ResponseEntity.ok().build();

        } catch (SecurityException se) {
            return ResponseEntity.status(403).body("❌ Access Denied: " + se.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("❌ Download failed: " + e.getMessage());
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/all")
    public ResponseEntity<List<VaultFile>> getAllFiles() {
        return ResponseEntity.ok(vaultFileService.getAllFiles());
    }

}

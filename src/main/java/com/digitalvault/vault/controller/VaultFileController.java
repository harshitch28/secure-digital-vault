package com.digitalvault.vault.controller;

import com.digitalvault.vault.dto.VaultFileList;
import com.digitalvault.vault.entity.VaultFile;
import com.digitalvault.vault.service.VaultFileService;
import com.digitalvault.vault.util.FileEncryptionUtil;
import jakarta.annotation.Resources;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.util.List;

//@CrossOrigin()
@RestController
@RequestMapping("/api/vault")
public class VaultFileController {

    @Autowired
    private VaultFileService vaultFileService;

    //@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam MultipartFile file) {
        try {
            VaultFile savedFile = vaultFileService.storeEncryptedFile(file);
            return ResponseEntity.ok("✅ File uploaded & encrypted successfully: " + savedFile.getFileName() + " "+ savedFile.getId());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("❌ Upload failed: " + e.getMessage());
        }
    }

    //@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long id) {
        try {
            VaultFile file = vaultFileService.getFileByIdIfOwner(id);
            File encryptedFile = new File(file.getFilePath());

            ByteArrayOutputStream decryptedOutput = new ByteArrayOutputStream();
            FileEncryptionUtil.decryptFile(encryptedFile, decryptedOutput);

            ByteArrayResource resource = new ByteArrayResource(decryptedOutput.toByteArray());

            //System.out.println(file.getFileName());
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFileName() + "\"")
                    .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS,
                            HttpHeaders.CONTENT_DISPOSITION)
                    .contentType(MediaType.parseMediaType(file.getFileType()))
                    .contentLength(resource.contentLength())
                    .body(resource);

        } catch (SecurityException se) {
            return ResponseEntity.status(403).build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/all")
    public ResponseEntity<List<VaultFile>> getAllFiles() {
        return ResponseEntity.ok(vaultFileService.getAllFiles());
    }


    //@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("/files/user")
    public ResponseEntity<List<VaultFileList>> getUserFileList() {
        try {
            List<VaultFileList> files = vaultFileService.getFilesForOwner();
            return ResponseEntity.ok(files);
        } catch (SecurityException se) {
            return ResponseEntity.status(403).build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

}

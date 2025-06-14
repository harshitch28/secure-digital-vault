package com.digitalvault.vault.repository;

import com.digitalvault.vault.entity.VaultFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VaultFileRepository extends JpaRepository<VaultFile, Long> {
    List<VaultFile> findByOwnerId(Long userId);
}

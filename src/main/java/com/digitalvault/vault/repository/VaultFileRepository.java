package com.digitalvault.vault.repository;

import com.digitalvault.vault.dto.VaultFileList;
import com.digitalvault.vault.entity.User;
import com.digitalvault.vault.entity.VaultFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VaultFileRepository extends JpaRepository<VaultFile, Long> {
    @Query("select new com.digitalvault.vault.dto.VaultFileList(v.id, v.fileName) from VaultFile v where v.owner = :user")
    List<VaultFileList> findFileIdsAndNamesByOwner(@Param("user") User user);
}

package com.digitalvault.vault.service;

import com.digitalvault.vault.entity.User;
import com.digitalvault.vault.entity.VaultFile;
import com.digitalvault.vault.repository.UserRepository;
import com.digitalvault.vault.repository.VaultFileRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VaultFileRepository vaultFileRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<VaultFile> getAllFiles() {
        return vaultFileRepository.findAll();
    }

    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}

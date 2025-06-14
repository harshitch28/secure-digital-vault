package com.digitalvault.vault.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/secure-data")
    public String getSecureData() {
        return "🔐 This is protected secure data visible only to authenticated users!";
    }
}

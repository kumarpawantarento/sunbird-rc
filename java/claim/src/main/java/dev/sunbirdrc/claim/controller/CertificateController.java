package dev.sunbirdrc.claim.controller;

import dev.sunbirdrc.claim.entity.Certificate;
import dev.sunbirdrc.claim.service.CertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CertificateController {
    @Autowired
    private CertificateService certificateService;

    @GetMapping("/get")
    public List<Certificate> getAllCertificates() {
        return certificateService.getAllCertificates();
    }

    @GetMapping("get/{id}")
    public Certificate getCertificateById(@PathVariable Long id) {
        return certificateService.getCertificateById(id);
    }

    @PostMapping("/create")
    public Certificate createCertificate(@RequestBody Certificate certificate) {
        return certificateService.createCertificate(certificate);
    }

    @PutMapping("update/{id}")
    public Certificate updateCertificate(@PathVariable Long id, @RequestBody Certificate certificate) {
        return certificateService.updateCertificate(id, certificate);
    }
}

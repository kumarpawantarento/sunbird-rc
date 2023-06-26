package dev.sunbirdrc.claim.controller;

import dev.sunbirdrc.claim.entity.*;
import dev.sunbirdrc.claim.service.CertificateRequestService;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/certificaterequests")
public class CertificateRequestController {
    @Autowired
    private CertificateRequestService certificateRequestService;

    public CertificateRequestController(CertificateRequestService certificateRequestService) {
        this.certificateRequestService = certificateRequestService;
    }

    @PostMapping("/send")
    public ResponseEntity<String> saveCertificateRequest(@RequestBody Candidate candidate) {
        certificateRequestService.saveCertificateRequest(candidate);
        return ResponseEntity.status(HttpStatus.CREATED).body("Certificate request saved successfully");
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<Candidate> getCandidateById(@PathVariable Long id) {
        Candidate candidate = certificateRequestService.getCandidateById(id);
        if (candidate != null) {
            return ResponseEntity.ok(candidate);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/get")
    public ResponseEntity<List<Candidate>> getAllCandidates() {
        List<Candidate> candidates = certificateRequestService.getAllCandidates();
        if (!candidates.isEmpty()) {
            return ResponseEntity.ok(candidates);
        } else {
            return ResponseEntity.noContent().build();
        }
    }
}
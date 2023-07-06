package dev.sunbirdrc.claim.controller;

import dev.sunbirdrc.claim.entity.Candidate;
import dev.sunbirdrc.claim.service.CertificateRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.util.List;

@RestController
@RequestMapping("/certificaterequests")
public class CertificateRequestController {
    @Autowired
    private CertificateRequestService certificateRequestService;

    public CertificateRequestController(CertificateRequestService certificateRequestService) {
        this.certificateRequestService = certificateRequestService;
    }

    @PostMapping("/send")
    @RolesAllowed("api")
    public ResponseEntity<String> saveCertificateRequest(@RequestBody Candidate candidate) {
        certificateRequestService.saveCertificateRequest(candidate);
        return ResponseEntity.status(HttpStatus.CREATED).body("Certificate request saved successfully");
    }

    @GetMapping("/get/{id}")
    @RolesAllowed("api")
    public ResponseEntity<Candidate> getCandidateById(@PathVariable Long id) {
        Candidate candidate = certificateRequestService.getCandidateById(id);
        if (candidate != null) {
            return ResponseEntity.ok(candidate);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/get")
    @RolesAllowed("api")
    public ResponseEntity<List<Candidate>> getAllCandidates() {
        List<Candidate> candidates = certificateRequestService.getAllCandidates();
        if (!candidates.isEmpty()) {
            return ResponseEntity.ok(candidates);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @PutMapping("/update/{id}")
    @RolesAllowed("api")
    public ResponseEntity<Candidate> updateCertificateRequest(@PathVariable Long id, @RequestBody Candidate updatedCandidate) {
        Candidate candidate = certificateRequestService.updateCertificateRequest(id, updatedCandidate);
        return ResponseEntity.ok(candidate);
    }
}
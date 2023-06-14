package dev.sunbirdrc.claim.service;

import dev.sunbirdrc.claim.entity.Certificate;
import dev.sunbirdrc.claim.repository.CertificateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class CertificateService {
    @Autowired
    private  CertificateRepository certificateRepository;

    public CertificateService(CertificateRepository certificateRepository) {
        this.certificateRepository = certificateRepository;
    }

    public List<Certificate> getAllCertificates() {
        return certificateRepository.findAll();
    }

    public Certificate getCertificateById(Long id) {
        Optional<Certificate> certificate = certificateRepository.findById(id);
        if (certificate.isPresent()) {
            return certificate.get();
        } else {
            throw new NoSuchElementException("Certificate not found with id: " + id);
        }
    }

    public Certificate createCertificate(Certificate certificate) {
        return certificateRepository.save(certificate);
    }

    public Certificate updateCertificate(Long id, Certificate updatedCertificate) {
        Certificate existingCertificate = getCertificateById(id);
        existingCertificate.setCredentials(updatedCertificate.getCredentials());
        existingCertificate.setTemplateURL(updatedCertificate.getTemplateURL());
        return certificateRepository.save(existingCertificate);
    }

}

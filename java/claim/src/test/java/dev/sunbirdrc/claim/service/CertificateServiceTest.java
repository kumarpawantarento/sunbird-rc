package dev.sunbirdrc.claim.service;

import dev.sunbirdrc.claim.entity.Certificate;
import dev.sunbirdrc.claim.repository.CertificateRepository;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CertificateServiceTest {
    @Mock
    CertificateRepository certificateRepository;

    @Test
    public void testCreateCertificate() {
        Certificate certificate = new Certificate();
        certificate.setId(1L);
        certificate.setCredentials("some creds");
        certificate.setTemplateURL("http://google.com");
        CertificateRepository certificateRepository = Mockito.mock(CertificateRepository.class);
        when(certificateRepository.save(Mockito.any(Certificate.class))).thenReturn(certificate);
        CertificateService certificateService = new CertificateService(certificateRepository);
        Certificate createdCertificate = certificateService.createCertificate(certificate);
        assertEquals(certificate.getId(), createdCertificate.getId());
        assertEquals(certificate.getCredentials(), createdCertificate.getCredentials());
        assertEquals(certificate.getTemplateURL(), createdCertificate.getTemplateURL());
    }
    @Test
    public void testUpdateCertificate() {
        Certificate certificate = new Certificate();
        certificate.setId(1L);
        certificate.setCredentials("some creds");
        certificate.setTemplateURL("http://google.com");
        Certificate updatedCertificate = new Certificate();
        updatedCertificate.setCredentials("Updated Credentials");
        updatedCertificate.setTemplateURL("http://google.com/template");
        CertificateRepository mockRepository = Mockito.mock(CertificateRepository.class);
        when(mockRepository.findById(certificate.getId())).thenReturn(Optional.ofNullable(certificate));
        when(mockRepository.save(Mockito.any(Certificate.class))).thenReturn(certificate);
        CertificateService certificateService = new CertificateService(mockRepository);
        Certificate resultCertificate = certificateService.updateCertificate(certificate.getId(), updatedCertificate);
        assertEquals(certificate.getId(), resultCertificate.getId());
        assertEquals(updatedCertificate.getCredentials(), resultCertificate.getCredentials());
        assertEquals(updatedCertificate.getTemplateURL(), resultCertificate.getTemplateURL());
        verify(mockRepository, Mockito.times(1)).save(certificate);
    }
    public void testGetCertificateById() {
        Long certificateId = 1L;
        String credentials = "Test Creds";
        String templateURL = "http://google.com";
        Certificate mockCertificate = new Certificate(certificateId, credentials, templateURL);
        when(certificateRepository.findById(certificateId)).thenReturn(Optional.of(mockCertificate));
        CertificateService certificateService = new CertificateService(certificateRepository);
        Certificate resultCertificate = certificateService.getCertificateById(certificateId);
        assertEquals(certificateId, resultCertificate.getId());
        assertEquals(credentials, resultCertificate.getCredentials());
        assertEquals(templateURL, resultCertificate.getTemplateURL());
    }
    @Test
    public void testGetCertificateByIdNotFound() {
        Long nonExistentCertificateId = 2L;
        when(certificateRepository.findById(nonExistentCertificateId)).thenReturn(Optional.empty());
        CertificateService certificateService = new CertificateService(certificateRepository);
        Assertions.assertThrows(NoSuchElementException.class, () -> {certificateService.getCertificateById(nonExistentCertificateId);
        });
    }
}

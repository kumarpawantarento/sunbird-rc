package dev.sunbirdrc.claim.service;

import dev.sunbirdrc.claim.entity.Candidate;
import dev.sunbirdrc.claim.entity.Course;
import dev.sunbirdrc.claim.repository.CandidateRepository;
import dev.sunbirdrc.claim.repository.CourseRepository;
import dev.sunbirdrc.claim.status.Status;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CertificateRequestServiceTest {
    @Mock
    private CandidateRepository candidateRepository;

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private CertificateRequestService certificateRequestService;

    @Test
   public void testSaveCertificateRequest() {
        Candidate candidate = Candidate.builder()
                .emailId("some@example.com")
                .courses(Collections.emptyList())
                .build();
        when(candidateRepository.existsByEmailId(candidate.getEmailId())).thenReturn(false);
        certificateRequestService.saveCertificateRequest(candidate);
        verify(candidateRepository).save(candidate);
        verify(courseRepository, times(0)).save(any(Course.class));
    }

    @Test
    public void testSaveCertificateRequestExistingEmail() {
        Candidate candidate = Candidate.builder()
                .emailId("some@example.com")
                .courses(Collections.emptyList())
                .build();
        when(candidateRepository.existsByEmailId(candidate.getEmailId())).thenReturn(true);
        assertThrows(IllegalArgumentException.class, () -> {
            certificateRequestService.saveCertificateRequest(candidate);
        });
        verify(candidateRepository, never()).save(candidate);
        verify(courseRepository, never()).save(any(Course.class));
    }

    @Test
    public void testGetAllCandidates() {
        List<Candidate> expectedCandidates = Collections.singletonList(new Candidate());
        when(candidateRepository.findAll()).thenReturn(expectedCandidates);
        List<Candidate> result = certificateRequestService.getAllCandidates();
        assertEquals(expectedCandidates, result);
    }

    @Test
    public void testGetCandidateById() {
        Long candidateId = 1L;
        Candidate expectedCandidate = new Candidate();
        expectedCandidate.setId(candidateId);
        when(candidateRepository.findById(candidateId)).thenReturn(Optional.of(expectedCandidate));
        Candidate result = certificateRequestService.getCandidateById(candidateId);
        assertEquals(expectedCandidate, result);
    }

    @Test
   public void testUpdateCertificateRequest() {
        Long candidateId = 1L;
        Candidate existingCandidate = new Candidate();
        existingCandidate.setId(candidateId);
        existingCandidate.setTitle("Mr.");
        Candidate updatedCandidate = new Candidate();
        updatedCandidate.setTitle("Ms.");
        when(candidateRepository.findById(candidateId)).thenReturn(Optional.of(existingCandidate));
        Candidate result = certificateRequestService.updateCertificateRequest(candidateId, updatedCandidate);
        assertEquals(updatedCandidate.getTitle(), result.getTitle());
        verify(candidateRepository).save(existingCandidate);
    }
}


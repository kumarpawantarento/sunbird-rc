package dev.sunbirdrc.claim.service;

import dev.sunbirdrc.claim.controller.CredentialsController;
import dev.sunbirdrc.claim.entity.Credentials;
import dev.sunbirdrc.claim.entity.Learner;
import dev.sunbirdrc.claim.repository.CredentialsRepository;
import dev.sunbirdrc.claim.repository.LearnerRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CredentialsServiceTest {
    @Mock
    private LearnerRepository learnerRepository;

    @InjectMocks
    private CredentialsService credentialsService;

    @Test
    public void testGetLearnerWithCredentialsByName() {
        String learnerName = "abcd";
        Learner learner = new Learner();
        learner.setId(1L);
        learner.setName(learnerName);
        learner.setRollNumber("101");
        learner.setRegistrationNumber("1001");
        List<Learner> expectedLearners = Arrays.asList(learner);
        when(learnerRepository.findByName(learnerName)).thenReturn(expectedLearners);
        List<Learner> actualLearners = credentialsService.getLearnerWithCredentialsByName(learnerName);
        assertEquals(expectedLearners, actualLearners);
    }
    @Test
    public void testGetLearnerWithCredentialsByRollNumberExistingRollNumber() {
        String rollNumber = "123";
        Learner expectedLearner = new Learner();
        when(learnerRepository.findByRollNumber(rollNumber)).thenReturn(Collections.singletonList(expectedLearner));
        Learner result = credentialsService.getLearnerWithCredentialsByRollNumber(rollNumber);
        assertEquals(expectedLearner, result);
    }
    @Test
    public void testGetLearnerWithCredentialsByRollNumberNonExistingRollNumber() {
        String rollNumber = "456";
        when(learnerRepository.findByRollNumber(rollNumber)).thenReturn(Collections.emptyList());
        Learner result = credentialsService.getLearnerWithCredentialsByRollNumber(rollNumber);
        assertNull(result);
    }
    @Test
    public void testUpdateLearnerWithCredentials() {
        Learner learner = new Learner();
        learner.setId(1L);
        learner.setName("John Doe");
        learner.setRollNumber("12345");
        learner.setRegistrationNumber("ABCDE");
        when(learnerRepository.save(learner)).thenReturn(learner);
        Learner updatedLearner = credentialsService.updateLearnerWithCredentials(learner);
        verify(learnerRepository).save(learner);
        assertEquals(learner, updatedLearner);
    }
    @Test
   public void testSaveLearnerWithCredentialsNewRollNumber() {
        Learner learner = new Learner();
        learner.setRollNumber("123456");
        learner.setCredentialsList(new ArrayList<>());
        Mockito.when(learnerRepository.findByRollNumber(Mockito.anyString()))
                .thenReturn(new ArrayList<>());
        try {
            credentialsService.saveLearnerWithCredentials(learner);
            Mockito.verify(learnerRepository, Mockito.times(1)).save(learner);
        } catch (Exception e) {
            Assertions.fail("Unexpected exception: " + e.getMessage());
        }
    }

    @Test
    public void testSaveLearnerWithExistingRollNumber() {
        Learner learner = new Learner();
        learner.setRollNumber("123456");
        learner.setCredentialsList(new ArrayList<>());
        List<Learner> learnersWithSameRollNumber = new ArrayList<>();
        learnersWithSameRollNumber.add(new Learner());
        Mockito.when(learnerRepository.findByRollNumber(Mockito.anyString()))
                .thenReturn(learnersWithSameRollNumber);
        try {
            credentialsService.saveLearnerWithCredentials(learner);
            Assertions.fail("Expected IllegalArgumentException was not thrown.");
        } catch (IllegalArgumentException e) {
            Mockito.verify(learnerRepository, Mockito.never()).save(Mockito.any(Learner.class));
        } catch (Exception e) {
            Assertions.fail("Unexpected exception: " + e.getMessage());
        }
    }
}

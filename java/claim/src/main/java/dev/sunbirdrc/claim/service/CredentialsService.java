package dev.sunbirdrc.claim.service;

import dev.sunbirdrc.claim.entity.Credentials;
import dev.sunbirdrc.claim.entity.Learner;
import dev.sunbirdrc.claim.repository.CredentialsRepository;
import dev.sunbirdrc.claim.repository.LearnerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CredentialsService {
    @Autowired
    private CredentialsRepository credentialsRepository;
    @Autowired
    private LearnerRepository learnerRepository;
    private static final Logger logger = LoggerFactory.getLogger(CredentialsService.class);

    public CredentialsService(LearnerRepository learnerRepository, CredentialsRepository credentialsRepository) {
        this.learnerRepository = learnerRepository;
        this.credentialsRepository = credentialsRepository;
    }

    public void saveLearnerWithCredentials(Learner learner) {
        String rollNumber = learner.getRollNumber();
        List<Learner> learnersWithSameRollNumber = learnerRepository.findByRollNumber(rollNumber);

        if (!learnersWithSameRollNumber.isEmpty()) {
            String errorMessage = "Roll number " + rollNumber + " already exists.";
            logger.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }

        List<Credentials> credentialsList = learner.getCredentialsList();

        for (Credentials credentials : credentialsList) {
            credentials.setLearner(learner);
        }

        learnerRepository.save(learner);

        logger.info("Learner with credentials saved successfully");
    }



    public List<Learner> getAllLearnerWithCredentials() {
        return learnerRepository.findAll();
    }

    public Learner getLearnerWithCredentialsById(Long learnerId) {
        return learnerRepository.findById(learnerId).orElse(null);
    }


    public Learner updateLearnerWithCredentials(Learner learner) {
        return learnerRepository.save(learner);
    }

    public void deleteLearnerWithCredentials(Long learnerId) {
        learnerRepository.deleteById(learnerId);
    }

    public List<Learner> getLearnerWithCredentialsByName(String learnerName) {
        return learnerRepository.findByName(learnerName);
    }
    public Learner getLearnerWithCredentialsByRollNumber(String rollNumber) {
        List<Learner> learners = learnerRepository.findByRollNumber(rollNumber);
        if (!learners.isEmpty()) {
            return learners.get(0);
        } else {
            return null;
        }
    }



}

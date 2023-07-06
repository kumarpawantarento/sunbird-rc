package dev.sunbirdrc.claim.service;

import dev.sunbirdrc.claim.entity.Candidate;
import dev.sunbirdrc.claim.entity.Course;
import dev.sunbirdrc.claim.repository.CandidateRepository;
import dev.sunbirdrc.claim.repository.CourseRepository;
import dev.sunbirdrc.claim.status.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CertificateRequestService {

    @Autowired
    private CandidateRepository candidateRepository;
    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    public CertificateRequestService(CandidateRepository candidateRepository, CourseRepository courseRepository) {
        this.candidateRepository = candidateRepository;
        this.courseRepository = courseRepository;
    }


    public void saveCertificateRequest(Candidate candidate) {
        String email = candidate.getEmailId();
        boolean emailExists = candidateRepository.existsByEmailId(email);
        if (emailExists) {
            throw new IllegalArgumentException("Email already exists: " + email);
        }

        candidate.setStatus(Status.Pending);
        candidateRepository.save(candidate);
        List<Course> courses = candidate.getCourses();
        if (courses != null) {
            for (Course course : courses) {
                course.setCandidate(candidate);
                courseRepository.save(course);
            }
        }
    }
    public Candidate getCandidateById(Long id) {
        return candidateRepository.findById(id).orElse(null);
    }

    public List<Candidate> getAllCandidates() {
        return candidateRepository.findAll();
    }

    public List<Course> getAllCourseDetails() {
        return courseRepository.findAll();
    }
    public Candidate updateCertificateRequest(Long id, Candidate updatedCandidate) {
        Candidate existingCandidate = candidateRepository.findById(id).orElse(null);
        if (existingCandidate != null) {
            existingCandidate.setTitle(updatedCandidate.getTitle());
            existingCandidate.setFirstName(updatedCandidate.getFirstName());
            existingCandidate.setMiddleName(updatedCandidate.getMiddleName());
            existingCandidate.setLastName(updatedCandidate.getLastName());
            existingCandidate.setStatus(Status.Approved);
            candidateRepository.save(existingCandidate);
        }
        return existingCandidate;
    }
}

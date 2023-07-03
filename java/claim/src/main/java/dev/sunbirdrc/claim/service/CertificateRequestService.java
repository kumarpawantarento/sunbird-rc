package dev.sunbirdrc.claim.service;

import dev.sunbirdrc.claim.entity.Candidate;
import dev.sunbirdrc.claim.entity.Course;
import dev.sunbirdrc.claim.entity.StudentDetails;
import dev.sunbirdrc.claim.repository.CandidateRepository;
import dev.sunbirdrc.claim.repository.CourseRepository;
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
}

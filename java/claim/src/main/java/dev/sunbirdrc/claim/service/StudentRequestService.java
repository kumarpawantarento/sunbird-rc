package dev.sunbirdrc.claim.service;

import dev.sunbirdrc.claim.entity.StudentRequest;
import dev.sunbirdrc.claim.repository.StudentRequestRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class StudentRequestService {
    @Autowired
    private StudentRequestRepository studentRequestRepository;

    private static final Logger logger = LoggerFactory.getLogger(StudentRequestService.class);

// ...

    public StudentRequest getStudent(Long id) {
        Optional<StudentRequest> optionalStudent = studentRequestRepository.findById(id);
        if (optionalStudent.isPresent()) {
            logger.info("Request already exists");
            return optionalStudent.get();
        } else {
            logger.info("Request registered successfully");
            throw new IllegalArgumentException("Invalid ID");
        }
    }

    public StudentRequest saveStudent(StudentRequest studentRequest) {
        return studentRequestRepository.save(studentRequest);
    }

    public StudentRequest updateStudent(Long id, StudentRequest updatedStudentRequest) {
        Optional<StudentRequest> optionalExistingStudent = studentRequestRepository.findById(id);
        if (optionalExistingStudent.isPresent()) {
            StudentRequest existingStudent = optionalExistingStudent.get();
            existingStudent.setRollNumber(updatedStudentRequest.getRollNumber());
            existingStudent.setRegNumber(updatedStudentRequest.getRegNumber());
            existingStudent.setDob(updatedStudentRequest.getDob());
            existingStudent.setEmail(updatedStudentRequest.getEmail());
            return studentRequestRepository.save(existingStudent);
        } else {
            throw new IllegalArgumentException("Invalid student ID");
        }
    }

}
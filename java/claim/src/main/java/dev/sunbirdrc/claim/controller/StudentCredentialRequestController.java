package dev.sunbirdrc.claim.controller;

import dev.sunbirdrc.claim.entity.StudentRequest;
import dev.sunbirdrc.claim.service.StudentRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/studentrequest")
public class StudentCredentialRequestController {
    @Autowired
    private StudentRequestService studentRequestService;

    @GetMapping("/get/{id}")
    public StudentRequest getStudent(@PathVariable("id") Long id) {
        return studentRequestService.getStudent(id);
    }

    @PostMapping("/send")
    public StudentRequest saveStudent(@RequestBody StudentRequest studentRequest) {
        return studentRequestService.saveStudent(studentRequest);
    }

    @PutMapping("/update/{id}")
    public StudentRequest updateStudent(@PathVariable("id") Long id, @RequestBody StudentRequest updatedStudentRequest) {
        return studentRequestService.updateStudent(id, updatedStudentRequest);
    }


}
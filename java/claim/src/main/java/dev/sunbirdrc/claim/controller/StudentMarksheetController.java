package dev.sunbirdrc.claim.controller;

import dev.sunbirdrc.claim.entity.StudentDetails;
import dev.sunbirdrc.claim.entity.Subject;
import dev.sunbirdrc.claim.entity.YearsOfCourse;
import dev.sunbirdrc.claim.service.StudentMarksheetService;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/marksheet")
public class StudentMarksheetController {
    @Autowired
    private StudentMarksheetService studentMarksheetService;

    public StudentMarksheetController(StudentMarksheetService studentMarksheetService) {
        this.studentMarksheetService = studentMarksheetService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadMarksheetData(@RequestParam("file") MultipartFile file) {
        try {
            studentMarksheetService.uploadStudentMarksheetData(file);
            return ResponseEntity.status(HttpStatus.OK).body("Marksheet data uploaded successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload marksheet data");
        }
    }

    @GetMapping("/studentDetails/get")
    public ResponseEntity<List<StudentDetails>> getAllStudentDetails() {
        List<StudentDetails> studentDetailsList = studentMarksheetService.getAllStudentDetails();
        return ResponseEntity.ok(studentDetailsList);
    }

    @GetMapping("/rollnumber/{rollNumber}")
    public StudentDetails getByRollNumber(@PathVariable String rollNumber) {
        return studentMarksheetService.getByRollNumber(rollNumber);
    }
    @GetMapping("/regnumber/{regNumber}")
    public StudentDetails getByRegNumber(@PathVariable String regNumber) {
        return studentMarksheetService.getByRegNumber(regNumber);
    }

    @GetMapping("/yearsOfCourse")
    public ResponseEntity<List<YearsOfCourse>> getAllYearsOfCourse() {
        List<YearsOfCourse> yearsOfCourseList = studentMarksheetService.getAllYearsOfCourse();
        return ResponseEntity.ok(yearsOfCourseList);
    }



    @GetMapping("/subjects")
    public ResponseEntity<List<Subject>> getAllSubjects() {
        List<Subject> subjectList = studentMarksheetService.getAllSubjects();
        return ResponseEntity.ok(subjectList);
    }
    @GetMapping("/getstudentmarksheet")
    public ResponseEntity<String> getAllFieldsOfAllClasses() {
        Map<String, Object> result = new HashMap<>();

        List<StudentDetails> studentDetailsList = studentMarksheetService.getAllStudentDetails();
        List<YearsOfCourse> yearsOfCourseList = studentMarksheetService.getAllYearsOfCourse();
        List<Subject> subjectList = studentMarksheetService.getAllSubjects();

        result.put("studentDetailsList", studentDetailsList);
        result.put("yearsOfCourseList", yearsOfCourseList);
        result.put("subjectList", subjectList);

        StringBuilder responseBuilder = new StringBuilder();

        String jsonResponse = new JSONObject(result).toString();
        jsonResponse += "\n" + responseBuilder.toString();

        return ResponseEntity.status(HttpStatus.OK).body(jsonResponse);
    }

}
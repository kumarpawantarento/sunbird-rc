//package dev.sunbirdrc.claim.controller;
//
//import dev.sunbirdrc.claim.entity.StudentDetails;
//import dev.sunbirdrc.claim.service.StudentDetailsService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/studentdetails")
//public class StudentDetailsController {
//    @Autowired
//    private StudentDetailsService studentDetailsService;
//
//    public StudentDetailsController(StudentDetailsService studentDetailsService) {
//        this.studentDetailsService = studentDetailsService;
//    }
//
//    @PostMapping("/upload")
//    public ResponseEntity<String> uploadCSVFile(@RequestParam("file") MultipartFile file) {
//        try {
//            studentDetailsService.uploadStudentDetails(file);
//            return ResponseEntity.ok("File uploaded successfully!");
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload the file: " + e.getMessage());
//        }
//    }
//
//    @GetMapping("/get")
//    public ResponseEntity<List<StudentDetails>> getAllStudentDetails() {
//        List<StudentDetails> studentDetailsList = studentDetailsService.getAllStudentDetails();
//        return ResponseEntity.ok(studentDetailsList);
//    }
//}

//package dev.sunbirdrc.claim.controller;
//
//import dev.sunbirdrc.claim.entity.Subject;
//import dev.sunbirdrc.claim.entity.YearsOfCourse;
//import dev.sunbirdrc.claim.service.SubjectService;
//import dev.sunbirdrc.claim.service.YearOfCourseService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/subjects")
//public class SubjectController {
//    @Autowired
//    private SubjectService subjectService;
//
//    public SubjectController(SubjectService subjectService) {
//        this.subjectService = subjectService;
//    }
//
//    @PostMapping("/upload")
//    public ResponseEntity<String> uploadCSVFile(@RequestParam("file") MultipartFile file) {
//        try {
//            subjectService.uploadSubjects(file);
//            return ResponseEntity.ok("File uploaded successfully!");
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload the file: " + e.getMessage());
//        }
//    }
//
//    @GetMapping("/get")
//    public ResponseEntity<List<Subject>> getAllSubjects() {
//        List<Subject> subjectList = subjectService.getAllSubjects();
//        return ResponseEntity.ok(subjectList);
//    }
//}
//

//package dev.sunbirdrc.claim.controller;
//
//import dev.sunbirdrc.claim.entity.YearsOfCourse;
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
//@RequestMapping("/yearofcourse")
//public class YearOfCourseController {
//    @Autowired
//    private YearOfCourseService yearOfCourseService;
//
//    public YearOfCourseController(YearOfCourseService yearOfCourseService) {
//        this.yearOfCourseService = yearOfCourseService;
//    }
//
//    @PostMapping("/upload")
//    public ResponseEntity<String> uploadCSVFile(@RequestParam("file") MultipartFile file) {
//        try {
//            yearOfCourseService.uploadYearOfCourse(file);
//            return ResponseEntity.ok("File uploaded successfully!");
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload the file: " + e.getMessage());
//        }
//    }
//
//    @GetMapping("/get")
//    public ResponseEntity<List<YearsOfCourse>> getAllYearOfCourseDetails() {
//        List<YearsOfCourse> yearsOfCourseList = yearOfCourseService.getAllYearOfCourseDetails();
//        return ResponseEntity.ok(yearsOfCourseList);
//    }
//}

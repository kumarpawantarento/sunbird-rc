package dev.sunbirdrc.claim.controller;

import dev.sunbirdrc.claim.dto.StudentDTO;
import dev.sunbirdrc.claim.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
public class StudentController {


    @Autowired
    private StudentService studentService;

    @PostMapping("/upload")
    public List<StudentDTO> uploadCsv(@RequestParam("file") MultipartFile file) {
        return studentService.persistStudentDetails(file);
    }

    @GetMapping("/student/get/page")
    public ResponseEntity<List<StudentDTO>> getStudentByRollNo(@RequestParam("rollNo") String rollNo, @RequestParam("page") int page,
                                                               @RequestParam("size") int size) {

        List<StudentDTO> studentList = studentService.getPaginatedStudentList(rollNo, page, size);

        return new ResponseEntity<>(studentList, HttpStatus.ACCEPTED);
    }


    @GetMapping("/student/get")
    public ResponseEntity<List<StudentDTO>> pushEvents() {

        return new ResponseEntity<>(studentService.findAllStudent(), HttpStatus.ACCEPTED);
    }

    @GetMapping("/student/get/dob/page")
    public ResponseEntity<List<StudentDTO>> getStudentByDOB(@RequestParam("startDate") String startDate,
                                                            @RequestParam("endDate") String endDate,
                                                            @RequestParam("page") int page,
                                                            @RequestParam("size") int size) {

        List<StudentDTO> studentList = studentService.getPaginatedListByDate(startDate, endDate, page, size);

        return new ResponseEntity<>(studentList, HttpStatus.ACCEPTED);
    }
}

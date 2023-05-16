package dev.sunbirdrc.registry.controller;

import dev.sunbirdrc.registry.dao.StudentDTO;
import dev.sunbirdrc.registry.handler.StudentHandler;
import dev.sunbirdrc.registry.service.StudentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
public class StudentController {

    private static final Logger logger = LoggerFactory.getLogger(StudentController.class);

    @Autowired(required = false)
    private StudentHandler studentHandler;

    @Autowired
    private StudentService studentService;

    @PostMapping("/upload")
    public List<StudentDTO> uploadCsv(@RequestParam("file") MultipartFile file) {
        logger.info("CSV file uploaded successfully");
        return studentService.persistStudentDetails(file);
    }

    @GetMapping("/student/get/page")
    public List<StudentDTO> getStudentByRollNo(@RequestParam("rollNo") String rollNo, @RequestParam("page") int page,
                                               @RequestParam("size") int size) {

        List<StudentDTO> studentList = studentService.getPaginatedStudentList(rollNo, page, size);

        return studentList;
    }


    @GetMapping("/student/get")
    public List<StudentDTO> pushEvents() {

        return studentService.findAllStudent();
    }

    @GetMapping("/student/get/dob/page")
    public List<StudentDTO> getStudentByDOB(@RequestParam("startDate") String startDate,
                                            @RequestParam("endDate") String endDate,
                                            @RequestParam("page") int page,
                                            @RequestParam("size") int size) {

        List<StudentDTO> studentList = studentService.getPaginatedListByDate(startDate, endDate, page, size);

        return studentList;
    }
}
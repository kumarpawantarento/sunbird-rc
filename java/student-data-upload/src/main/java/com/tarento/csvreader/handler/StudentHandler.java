package com.tarento.csvreader.handler;

import com.tarento.csvreader.dto.StudentDTO;
import com.tarento.csvreader.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@Component
public class StudentHandler {

    @Autowired
    private StudentService studentService;
    public List<StudentDTO> getStudent(MultipartFile file) {
        return studentService.getStudentFromFile(file);

    }
}

package dev.sunbirdrc.registry.handler;

import dev.sunbirdrc.registry.dao.StudentDTO;
import dev.sunbirdrc.registry.service.StudentService;
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

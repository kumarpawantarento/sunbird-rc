package dev.sunbirdrc.claim.service;

import dev.sunbirdrc.claim.dto.StudentDTO;
import dev.sunbirdrc.claim.entity.Student;
import dev.sunbirdrc.claim.repository.StudentRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class StudentServiceTest {
    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private StudentService studentService;

    @Test
    public void testGetStudentFromFile() throws IOException {
        String csvData = "RollNo,DOB\n" +
                "101,01-01-1990\n" +
                "102,02-02-1991\n";
        MockMultipartFile file = new MockMultipartFile("file.csv", csvData.getBytes());
        List<StudentDTO> result = studentService.getStudentFromFile(file);
        assertEquals(2, result.size());
        assertEquals("101", result.get(0).getRollNo());
        assertEquals("1990-01-01", result.get(0).getDob());
        assertEquals("102", result.get(1).getRollNo());
        assertEquals("1991-02-02", result.get(1).getDob());
    }

    @Test
    public void testPersistStudentDetails() throws IOException {
        String csvData = "RollNo,DOB\n" +
                "101,01-01-1990\n" +
                "102,02-02-1991\n" +
                "abc,03-03-1992\n";
        MockMultipartFile file = new MockMultipartFile("file.csv", csvData.getBytes());
        when(studentRepository.saveAll(anyList())).thenReturn(null);
        List<StudentDTO> result = studentService.persistStudentDetails(file);
        assertEquals(2, result.size());
        verify(studentRepository, times(1)).saveAll(anyList());
    }
    @Test
    public void testGetPaginatedStudentList() {
        String rollNo = "123456";
        int page = 0;
        int size = 10;
        List<Student> studentList = new ArrayList<>();
        studentList.add(new Student(1L, "123456", new Date()));
        studentList.add(new Student(2L, "123456", new Date()));
        Pageable pageable = PageRequest.of(page, size);
        when(studentRepository.findStudentRegistrationNo(rollNo, pageable)).thenReturn(studentList);
        List<StudentDTO> studentDTOList = studentService.getPaginatedStudentList(rollNo, page, size);
        assertEquals(studentList.size(), studentDTOList.size());
    }
    @Test
    public void testGetPaginatedListByDate() {
        String startDate = "2023-01-01";
        String endDate = "2023-01-31";
        int page = 0;
        int size = 10;
        List<Student> mockedStudentList = Arrays.asList(
                new Student(1L, "123", new Date()),
                new Student(2L, "456", new Date())
        );
        when(studentRepository.findAllByDobBetween(any(Date.class), any(Date.class), any(Pageable.class))).thenReturn(mockedStudentList);
        List<StudentDTO> result = studentService.getPaginatedListByDate(startDate, endDate, page, size);
        verify(studentRepository, times(1)).findAllByDobBetween(any(Date.class), any(Date.class), eq(PageRequest.of(page, size)));
        assertEquals(mockedStudentList.size(), result.size());
        assertEquals(mockedStudentList.get(0).getRollNo(), result.get(0).getRollNo());
        assertEquals(mockedStudentList.get(1).getDob().toString(), result.get(1).getDob());
    }

}

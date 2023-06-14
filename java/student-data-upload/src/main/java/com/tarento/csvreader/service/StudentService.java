package com.tarento.csvreader.service;


import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import com.tarento.csvreader.dto.StudentDTO;
import com.tarento.csvreader.entity.Student;
import com.tarento.csvreader.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    /**
     * @param file
     * @return
     */
    public List<StudentDTO> persistStudentDetails(MultipartFile file) {
        return saveStudents(getStudentFromFile(file));
    }

    /**
     * @param file
     * @return
     */
    public List<StudentDTO> getStudentFromFile(MultipartFile file) {
        List<StudentDTO> studentList = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        try {
            Reader reader = new InputStreamReader(file.getInputStream());
            CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build();
            List<String[]> rows = csvReader.readAll();

            for (String[] row : rows) {
                String rollNo = row[0];
                String dob = row[1];

                StudentDTO studentDTO = StudentDTO.builder()
                        .dob(LocalDate.parse(dob, formatter).toString())
                        .rollNo(rollNo).build();

                studentList.add(studentDTO);
            }
        }catch (IOException ioException){
            ioException.printStackTrace();
        } catch (CsvException csvException){
            csvException.printStackTrace();
        }
        return studentList;
    }

    /**
     * @param studentList
     * @return
     */
    public List<StudentDTO> saveStudents(List<StudentDTO> studentList) {
        if (studentList != null && !studentList.isEmpty()) {
            List<Student> students = studentList.stream()
                    .map(studentDTO -> Student.builder()
                                    .rollNo(studentDTO.getRollNo())
                                    .dob(getDateFromString(studentDTO.getDob()))
                                    .build()
                    ).collect(Collectors.toList());

            studentRepository.saveAll(students);
        }
        return studentList;
    }

    /**
     * @param date
     * @return
     */
    public Date getDateFromString(String date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return formatter.parse(date);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param rollNo
     * @param page
     * @param size
     * @return
     */
    public List<StudentDTO> getPaginatedStudentList(String rollNo, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<Student> studentList = studentRepository.findStudentRegistrationNo(rollNo, pageable);

        List<StudentDTO> studentDTOList = studentList.stream()
                .map(student -> StudentDTO.builder()
                        .rollNo(student.getRollNo())
                        .dob(student.getDob().toString())
                        .build()
                ).collect(Collectors.toList());

        return studentDTOList;
    }
    public List<StudentDTO> findAllStudent() {
        List<Student> studentList = studentRepository.findAll();

        List<StudentDTO> studentDTOList = studentList.stream()
                .map(student -> StudentDTO.builder()
                        .rollNo(student.getRollNo())
                        .dob(student.getDob().toString())
                        .build()
                ).collect(Collectors.toList());

        return studentDTOList;
    }

    public List<StudentDTO> getPaginatedListByDate(String startDate, String endDate, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<Student> studentList = studentRepository
                .findAllByDobBetween(getDateFromString(startDate), getDateFromString(endDate), pageable);

        List<StudentDTO> studentDTOList = studentList.stream()
                .map(student -> StudentDTO.builder()
                        .rollNo(student.getRollNo())
                        .dob(student.getDob().toString())
                        .build()
                ).collect(Collectors.toList());

        return studentDTOList;
    }
}

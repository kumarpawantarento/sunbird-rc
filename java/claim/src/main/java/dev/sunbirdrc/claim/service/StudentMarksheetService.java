package dev.sunbirdrc.claim.service;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import com.opencsv.exceptions.CsvValidationException;
import dev.sunbirdrc.claim.entity.StudentDetails;
import dev.sunbirdrc.claim.entity.Subject;
import dev.sunbirdrc.claim.entity.YearsOfCourse;
import dev.sunbirdrc.claim.repository.StudentDetailsRepository;
import dev.sunbirdrc.claim.repository.SubjectRepository;
import dev.sunbirdrc.claim.repository.YearOfCourseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class StudentMarksheetService {
    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private StudentDetailsRepository studentDetailsRepository;
    @Autowired
    private YearOfCourseRepository yearsOfCourseRepository;
    @Autowired
    private SubjectRepository subjectRepository;

    public StudentMarksheetService(StudentDetailsRepository studentDetailsRepository,
                                   YearOfCourseRepository yearsOfCourseRepository,
                                   SubjectRepository subjectRepository) {
        this.studentDetailsRepository = studentDetailsRepository;
        this.yearsOfCourseRepository = yearsOfCourseRepository;
        this.subjectRepository = subjectRepository;
    }

    public void uploadStudentMarksheetData(MultipartFile file) {
        List<StudentDetails> studentDetailsList = new ArrayList<>();
        List<YearsOfCourse> yearsOfCourseList = new ArrayList<>();
        List<Subject> subjectList = new ArrayList<>();

        try (Reader reader = new InputStreamReader(file.getInputStream());
             CSVReader csvReader = new CSVReaderBuilder(reader).build()) {

            String[] headers = csvReader.readNext();
            String[] record;

            Set<String> RollNumbers = studentDetailsRepository.findAllRollNumbers();
            Set<String> regNumbers = studentDetailsRepository.findAllRegNumbers();


            while ((record = csvReader.readNext()) != null) {
                StudentDetails studentDetails = new StudentDetails();
                studentDetails.setName(record[0]);
                studentDetails.setFatherName(record[1]);
                studentDetails.setMotherName(record[2]);
                String rollNumber = record[3];
                String regNumber = record[4];

                if (RollNumbers.contains(rollNumber) || regNumbers.contains(regNumbers)) {
                    logger.info("Duplicate roll number and reg number found: " + rollNumber +regNumbers);
                    continue;
                }
                studentDetails.setRollNumber(rollNumber);
                studentDetails.setRegNumber(regNumber);

                studentDetails.setCourse(record[5]);
                studentDetails.setTrainingCenter(record[6]);
                studentDetails.setTrainingPeriod(record[7]);
                studentDetails.setExamBody(record[8]);
                studentDetails.setExamYear(record[9]);
                studentDetails.setExamMonth(record[10]);
                studentDetails.setEnrollNo(record[11]);
                studentDetails.setOrgLogo(record[12]);
                studentDetails.setBarcode(record[13]);
                studentDetails.setCandidatePic(record[14]);
                studentDetails.setSignaturePic(record[15]);
                studentDetails.setTrainingCenterCode(record[16]);
                studentDetails.setDated(record[17]);
                studentDetails.setTrainingTitle(record[18]);
                studentDetails.setTrainingCode(record[19]);
                studentDetails.setCredType(record[20]);

                studentDetailsList.add(studentDetails);

                YearsOfCourse yearsOfCourse = new YearsOfCourse();
                yearsOfCourse.setCourseYear(record[21]);
                yearsOfCourse.setExamYear(record[22]);
                yearsOfCourse.setExamMonth(record[23]);
                yearsOfCourse.setResult(record[24]);
                yearsOfCourse.setTotalMarksObtained(record[25]);
                yearsOfCourse.setTotalMarksObtainedInWord(record[26]);
                yearsOfCourse.setMaxTotal(record[27]);

                yearsOfCourseList.add(yearsOfCourse);

                Subject subject = new Subject();
                subject.setName(record[28]);
                subject.setMaxExt(record[29]);
                subject.setMaxInt(record[30]);
                subject.setObtExt(record[31]);
                subject.setObtInt(record[32]);

                subjectList.add(subject);
            }
            logger.info("CSV file uploaded successfully");
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } catch (CsvException csvException) {
            csvException.printStackTrace();

        }

        studentDetailsRepository.saveAll(studentDetailsList);
        yearsOfCourseRepository.saveAll(yearsOfCourseList);
        subjectRepository.saveAll(subjectList);
    }

    public List<StudentDetails> getAllStudentDetails() {
        return studentDetailsRepository.findAll();
    }

    public List<YearsOfCourse> getAllYearsOfCourse() {
        return yearsOfCourseRepository.findAll();
    }

    public List<Subject> getAllSubjects() {
        return subjectRepository.findAll();
    }
    public StudentDetails getByRollNumber(String rollNumber) {
        return studentDetailsRepository.findByRollNumber(rollNumber);
    }
    public StudentDetails getByRegNumber(String regNumber) {
        return studentDetailsRepository.findByRegNumber(regNumber);
    }

}


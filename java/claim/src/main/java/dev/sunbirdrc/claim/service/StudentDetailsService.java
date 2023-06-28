//package dev.sunbirdrc.claim.service;
//
//import com.opencsv.CSVReader;
//import com.opencsv.CSVReaderBuilder;
//import com.opencsv.exceptions.CsvException;
//import com.opencsv.exceptions.CsvValidationException;
//import dev.sunbirdrc.claim.entity.StudentDetails;
//import dev.sunbirdrc.claim.repository.StudentDetailsRepository;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.io.Reader;
//import java.util.ArrayList;
//import java.util.List;
//
//@Service
//public class StudentDetailsService {
//    Logger logger = LoggerFactory.getLogger(getClass());
//
//    @Autowired
//    private StudentDetailsRepository studentDetailsRepository;
//
//    public void uploadStudentDetails(MultipartFile file) throws IOException {
//        List<StudentDetails> studentDetailsList = getStudentDetailsFromFile(file);
//        saveStudentDetails(studentDetailsList);
//    }
//
//    public List<StudentDetails> getAllStudentDetails() {
//        return studentDetailsRepository.findAll();
//    }
//
//    public void saveStudentDetails(List<StudentDetails> studentDetailsList) {
//        for (StudentDetails studentDetails : studentDetailsList) {
//            studentDetailsRepository.save(studentDetails);
//        }
//        logger.info("Student details saved successfully");
//    }
//
//    public List<StudentDetails> getStudentDetailsFromFile(MultipartFile file) {
//        List<StudentDetails> studentDetailsList = new ArrayList<>();
//
//        try (Reader reader = new InputStreamReader(file.getInputStream());
//             CSVReader csvReader = new CSVReaderBuilder(reader).build()) {
//
//            String[] headers = csvReader.readNext();
//            String[] record;
//
//            while ((record = csvReader.readNext()) != null) {
//                StudentDetails studentDetails = new StudentDetails();
//                studentDetails.setName(record[0]);
//                studentDetails.setFatherName(record[1]);
//                studentDetails.setMotherName(record[2]);
//                studentDetails.setRollNumber(record[3]);
//                studentDetails.setRegNumber(record[4]);
//                studentDetails.setCourse(record[5]);
//                studentDetails.setTrainingCenter(record[6]);
//                studentDetails.setTrainingPeriod(record[7]);
//                studentDetails.setExamBody(record[8]);
//                studentDetails.setExamYear(record[9]);
//                studentDetails.setExamMonth(record[10]);
//                studentDetails.setEnrollNo(record[11]);
//                studentDetails.setOrgLogo(record[12]);
//                studentDetails.setBarcode(record[13]);
//                studentDetails.setCandidatePic(record[14]);
//                studentDetails.setSignaturePic(record[15]);
//                studentDetails.setTrainingCenterCode(record[16]);
//                studentDetails.setDated(record[17]);
//                studentDetails.setTrainingTitle(record[18]);
//                studentDetails.setTrainingCode(record[19]);
//                studentDetails.setCredType(record[20]);
//
//                studentDetailsList.add(studentDetails);
//
//            }
//            logger.info("CSV file uploaded successfully");
//        } catch (IOException ioException) {
//            ioException.printStackTrace();
//        } catch (CsvException csvException) {
//            csvException.printStackTrace();
//        }
//
//        return studentDetailsList;
//    }
//}
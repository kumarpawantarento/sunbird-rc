//package dev.sunbirdrc.claim.service;
//
//import com.opencsv.CSVReader;
//import com.opencsv.CSVReaderBuilder;
//import com.opencsv.exceptions.CsvException;
//import com.opencsv.exceptions.CsvValidationException;
//import dev.sunbirdrc.claim.entity.Subject;
//import dev.sunbirdrc.claim.repository.SubjectRepository;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.io.Reader;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//@Service
//
//public class SubjectService {
//    Logger logger = LoggerFactory.getLogger(getClass());
//
//    @Autowired
//    private SubjectRepository subjectRepository;
//
//    public void uploadSubjects(MultipartFile file) throws IOException {
//        List<Subject> subjectList = getSubjectFromFile(file);
//        saveSubjects(subjectList);
//    }
//
//    public List<Subject> getAllSubjects() {
//        return subjectRepository.findAll();
//    }
//
//    public void saveSubjects(List<Subject> subjectList) {
//        for (Subject subject : subjectList) {
//            subjectRepository.save(subject);
//        }
//        logger.info("Subjects saved successfully");
//    }
//
//    public List<Subject> getSubjectFromFile(MultipartFile file) {
//        List<Subject> subjectList = new ArrayList<>();
//
//        try (Reader reader = new InputStreamReader(file.getInputStream());
//             CSVReader csvReader = new CSVReaderBuilder(reader).build()) {
//
//            String[] headers = csvReader.readNext();
//            String[] record;
//
//            while ((record = csvReader.readNext()) != null) {
//                Subject subject = new Subject();
//                subject.setName(record[0]);
//                subject.setMaxExt(record[1]);
//                subject.setMaxInt(record[2]);
//                subject.setObtExt(record[3]);
//                subject.setObtInt(record[4]);
//                subjectList.add(subject);
//            }
//            logger.info("CSV file uploaded successfully");
//        } catch (IOException ioException) {
//            ioException.printStackTrace();
//        } catch (CsvException csvException) {
//            csvException.printStackTrace();
//        }
//        return subjectList;
//
//    }
//}

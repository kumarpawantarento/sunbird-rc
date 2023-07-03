//package dev.sunbirdrc.claim.service;
//
//import com.opencsv.CSVReader;
//import com.opencsv.CSVReaderBuilder;
//import com.opencsv.exceptions.CsvException;
//import com.opencsv.exceptions.CsvValidationException;
//import dev.sunbirdrc.claim.entity.YearsOfCourse;
//import dev.sunbirdrc.claim.repository.YearOfCourseRepository;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.io.Reader;
//import java.util.ArrayList;
//import java.util.List;
//@Service
//@Transactional
//public class YearOfCourseService {
//    Logger logger = LoggerFactory.getLogger(getClass());
//
//    @Autowired
//    private YearOfCourseRepository yearOfCourseRepository;
//
//    public void uploadYearOfCourse(MultipartFile file) throws IOException {
//        List<YearsOfCourse> yearsOfCourseList = getYearOfCourseFromFile(file);
//        saveYearOfCourse(yearsOfCourseList);
//    }
//
//    public List<YearsOfCourse> getAllYearOfCourseDetails() {
//
//        return yearOfCourseRepository.findAll();
//    }
//
//    public void saveYearOfCourse(List<YearsOfCourse> yearsOfCourseList) {
//        for (YearsOfCourse yearsOfCourse : yearsOfCourseList) {
//            yearOfCourseRepository.save(yearsOfCourse);
//        }
//        logger.info("Year of Course saved successfully");
//    }
//
//    public List<YearsOfCourse> getYearOfCourseFromFile(MultipartFile file) {
//        List<YearsOfCourse> yearsOfCourseList = new ArrayList<>();
//
//        try (Reader reader = new InputStreamReader(file.getInputStream());
//             CSVReader csvReader = new CSVReaderBuilder(reader).build()) {
//
//            String[] headers = csvReader.readNext();
//            String[] record;
//
//            while ((record = csvReader.readNext()) != null) {
//                YearsOfCourse yearsOfCourse = new YearsOfCourse();
//                yearsOfCourse.setCourseYear(record[0]);
//                yearsOfCourse.setExamYear(record[1]);
//                yearsOfCourse.setExamMonth(record[2]);
//                yearsOfCourse.setResult(record[3]);
//                yearsOfCourse.setTotalMarksObtained(record[4]);
//                yearsOfCourse.setTotalMarksObtainedInWord(record[5]);
//                yearsOfCourse.setMaxTotal(record[6]);
//
//                yearsOfCourseList.add(yearsOfCourse);
//            }
//            logger.info("CSV file uploaded successfully");
//        } catch (IOException ioException) {
//            ioException.printStackTrace();
//        } catch (CsvException csvException) {
//            csvException.printStackTrace();
//        }
//
//        return yearsOfCourseList;
//    }
//}

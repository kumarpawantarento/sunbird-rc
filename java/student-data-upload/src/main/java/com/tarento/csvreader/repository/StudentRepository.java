package com.tarento.csvreader.repository;

import com.tarento.csvreader.entity.Student;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    @Query("select std from Student std where std.rollNo = ?1")
    List<Student> findStudentRegistrationNo(String rollNo, Pageable pageable);

    List<Student> findAllByDobBetween(Date startDate,Date endDate, Pageable pageable);

}

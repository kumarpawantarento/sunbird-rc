package dev.sunbirdrc.claim.repository;

import dev.sunbirdrc.claim.entity.StudentDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface StudentDetailsRepository extends JpaRepository<StudentDetails, Long> {
    StudentDetails findByRollNumber(String rollNumber);
    StudentDetails findByRegNumber(String regNumber );
    @Query("SELECT sd.rollNumber FROM StudentDetails sd")
    Set<String> findAllRollNumbers();

    @Query("SELECT sd.regNumber FROM StudentDetails sd")
    Set<String> findAllRegNumbers();
}

package dev.sunbirdrc.claim.repository;

import dev.sunbirdrc.claim.entity.StudentRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRequestRepository extends JpaRepository<StudentRequest, Long> {
}

package com.example.portaluniv.demo.repository;

import com.example.portaluniv.demo.entity.Enrollment;
import com.example.portaluniv.demo.entity.Enrollment.EnrollmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    List<Enrollment> findByUserId(Long userId);
    List<Enrollment> findByKelasId(Long kelasId);
    
    Optional<Enrollment> findByUserIdAndKelasId(Long userId, Long kelasId);
    
    List<Enrollment> findByUserIdAndStatus(Long userId, EnrollmentStatus status);
    List<Enrollment> findByKelasIdAndStatus(Long kelasId, EnrollmentStatus status);
    
    @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.kelas.id = :kelasId AND e.status = 'APPROVED'")
    Long countApprovedEnrollmentsByKelasId(@Param("kelasId") Long kelasId);
    
    boolean existsByUserIdAndKelasId(Long userId, Long kelasId);
    
    @Query("SELECT e FROM Enrollment e WHERE e.status = 'PENDING'")
    List<Enrollment> findPendingEnrollments();
}
package com.example.portaluniv.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.portaluniv.demo.entity.Enrollment;
import com.example.portaluniv.demo.entity.EnrollmentId;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, EnrollmentId> {
    
    // Find by user
    List<Enrollment> findByUserId(Long userId);
    
    // Find by kelas
    List<Enrollment> findByKelasId(Long kelasId);
    
    // Find by user and kelas
    Optional<Enrollment> findByUserIdAndKelasId(Long userId, Long kelasId);
    
    // Check if exists
    boolean existsByUserIdAndKelasId(Long userId, Long kelasId);
    
    // Count by kelas
    long countByKelasId(Long kelasId);
    
    // Order by enrolled date
    List<Enrollment> findByUserIdOrderByEnrolledAtDesc(Long userId);
    
    List<Enrollment> findByKelasIdOrderByEnrolledAtAsc(Long kelasId);
    
    // Delete by composite key
    void deleteByUserIdAndKelasId(Long userId, Long kelasId);
}
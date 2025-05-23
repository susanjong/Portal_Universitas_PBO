package com.example.portaluniv.demo.service;

import com.example.portaluniv.demo.entity.Enrollment;
import com.example.portaluniv.demo.entity.Enrollment.EnrollmentStatus;
import com.example.portaluniv.demo.entity.Kelas;
import com.example.portaluniv.demo.entity.User;
import com.example.portaluniv.demo.repository.EnrollmentRepository;
import com.example.portaluniv.demo.repository.KelasRepository;
import com.example.portaluniv.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class EnrollmentService {
    
    @Autowired
    private EnrollmentRepository enrollmentRepository;
    
    @Autowired
    private KelasRepository kelasRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    public List<Enrollment> findAll() {
        return enrollmentRepository.findAll();
    }
    
    public Optional<Enrollment> findById(Long id) {
        return enrollmentRepository.findById(id);
    }
    
    public List<Enrollment> findByUserId(Long userId) {
        return enrollmentRepository.findByUserId(userId);
    }
    
    public List<Enrollment> findByKelasId(Long kelasId) {
        return enrollmentRepository.findByKelasId(kelasId);
    }
    
    public List<Enrollment> findPendingEnrollments() {
        return enrollmentRepository.findPendingEnrollments();
    }
    
    public Optional<Enrollment> findByUserIdAndKelasId(Long userId, Long kelasId) {
        return enrollmentRepository.findByUserIdAndKelasId(userId, kelasId);
    }
    
    public List<Enrollment> findByUserIdAndStatus(Long userId, EnrollmentStatus status) {
        return enrollmentRepository.findByUserIdAndStatus(userId, status);
    }
    
    public Enrollment enroll(Long userId, Long kelasId) throws Exception {
        // Check if already enrolled
        if (enrollmentRepository.existsByUserIdAndKelasId(userId, kelasId)) {
            throw new Exception("User sudah terdaftar di kelas ini");
        }
        
        // Check class capacity
        Optional<Kelas> kelasOpt = kelasRepository.findById(kelasId);
        if (kelasOpt.isEmpty()) {
            throw new Exception("Kelas tidak ditemukan");
        }
        
        Kelas kelas = kelasOpt.get();
        if (kelas.isFull()) {
            throw new Exception("Kelas sudah penuh");
        }
        
        // Get user
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new Exception("User tidak ditemukan");
        }
        
        Enrollment enrollment = new Enrollment();
        enrollment.setUser(userOpt.get());
        enrollment.setKelas(kelas);
        enrollment.setStatus(EnrollmentStatus.PENDING);
        enrollment.setEnrolledAt(LocalDateTime.now());
        
        return enrollmentRepository.save(enrollment);
    }
    
    public Enrollment approveEnrollment(Long enrollmentId) throws Exception {
        Optional<Enrollment> enrollmentOpt = enrollmentRepository.findById(enrollmentId);
        if (enrollmentOpt.isEmpty()) {
            throw new Exception("Enrollment tidak ditemukan");
        }
        
        Enrollment enrollment = enrollmentOpt.get();
        
        // Check if class is still available
        if (enrollment.getKelas().isFull()) {
            throw new Exception("Kelas sudah penuh");
        }
        
        enrollment.setStatus(EnrollmentStatus.APPROVED);
        return enrollmentRepository.save(enrollment);
    }
    
    public Enrollment rejectEnrollment(Long enrollmentId) throws Exception {
        Optional<Enrollment> enrollmentOpt = enrollmentRepository.findById(enrollmentId);
        if (enrollmentOpt.isEmpty()) {
            throw new Exception("Enrollment tidak ditemukan");
        }
        
        Enrollment enrollment = enrollmentOpt.get();
        enrollment.setStatus(EnrollmentStatus.REJECTED);
        
        return enrollmentRepository.save(enrollment);
    }
    
    public void deleteById(Long id) {
        enrollmentRepository.deleteById(id);
    }
}
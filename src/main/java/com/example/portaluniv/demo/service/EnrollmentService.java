package com.example.portaluniv.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.portaluniv.demo.entity.Enrollment;
import com.example.portaluniv.demo.entity.EnrollmentId;
import com.example.portaluniv.demo.entity.Kelas;
import com.example.portaluniv.demo.entity.User;
import com.example.portaluniv.demo.repository.EnrollmentRepository;
import com.example.portaluniv.demo.repository.KelasRepository;
import com.example.portaluniv.demo.repository.UserRepository;

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
    
    public Optional<Enrollment> findById(EnrollmentId id) {
        return enrollmentRepository.findById(id);
    }
    
    public List<Enrollment> findByUserId(Long userId) {
        return enrollmentRepository.findByUserId(userId);
    }
    
    public List<Enrollment> findByKelasId(Long kelasId) {
        return enrollmentRepository.findByKelasId(kelasId);
    }
    
    public Optional<Enrollment> findByUserIdAndKelasId(Long userId, Long kelasId) {
        return enrollmentRepository.findByUserIdAndKelasId(userId, kelasId);
    }
    
    public boolean isKelasAvailable(Long kelasId) {
        Optional<Kelas> kelasOpt = kelasRepository.findById(kelasId);
        if (kelasOpt.isEmpty()) return false;
        
        Kelas kelas = kelasOpt.get();
        return kelas.getKuotaTersedia() > 0;
    }
    
    public Enrollment enroll(Long userId, Long kelasId) throws Exception {
        // Check if already enrolled
        if (enrollmentRepository.existsByUserIdAndKelasId(userId, kelasId)) {
            throw new Exception("User sudah terdaftar di kelas ini");
        }
        
        // Get and validate kelas
        Optional<Kelas> kelasOpt = kelasRepository.findById(kelasId);
        if (kelasOpt.isEmpty()) {
            throw new Exception("Kelas tidak ditemukan");
        }
        
        Kelas kelas = kelasOpt.get();
        
        // Check availability
        if (!isKelasAvailable(kelasId)) {
            throw new Exception("Kelas sudah penuh");
        }
        
        // Get and validate user
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new Exception("User tidak ditemukan");
        }
        
        // Create enrollment
        Enrollment enrollment = new Enrollment(userOpt.get(), kelas);
        
        return enrollmentRepository.save(enrollment);
    }
    
    public void unenroll(Long userId, Long kelasId) throws Exception {
        if (!enrollmentRepository.existsByUserIdAndKelasId(userId, kelasId)) {
            throw new Exception("Enrollment tidak ditemukan");
        }
        
        enrollmentRepository.deleteByUserIdAndKelasId(userId, kelasId);
    }
    
    public void deleteById(EnrollmentId id) {
        enrollmentRepository.deleteById(id);
    }
    
    public long getEnrollmentCount(Long kelasId) {
        return enrollmentRepository.countByKelasId(kelasId);
    }
    
    public int getKuotaTersedia(Long kelasId) {
        Optional<Kelas> kelasOpt = kelasRepository.findById(kelasId);
        if (kelasOpt.isEmpty()) return 0;
        return kelasOpt.get().getKuotaTersedia();
    }
    
    public String getKuotaInfo(Long kelasId) {
        Optional<Kelas> kelasOpt = kelasRepository.findById(kelasId);
        if (kelasOpt.isEmpty()) return "0/0";
        return kelasOpt.get().getKuotaInfo();
    }
}
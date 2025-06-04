package com.example.portaluniv.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.portaluniv.demo.entity.Enrollment;
import com.example.portaluniv.demo.service.EnrollmentService;

@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {
    
    @Autowired
    private EnrollmentService enrollmentService;
    
    @GetMapping
    public List<Enrollment> getAllEnrollments() {
        return enrollmentService.findAll();
    }

    @DeleteMapping("/{userId}/{kelasId}")
    public ResponseEntity<String> deleteEnrollment(@PathVariable Long userId, @PathVariable Long kelasId) {
        try {
            enrollmentService.unenroll(userId, kelasId);
            return ResponseEntity.ok("Mahasiswa berhasil dihapus dari kelas");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
package com.example.portaluniv.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "enrollments", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "kelas_id"}))
public class Enrollment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kelas_id")
    private Kelas kelas;

    @Enumerated(EnumType.STRING)
    private EnrollmentStatus status;

    private LocalDateTime enrolledAt;
    private LocalDateTime updatedAt;

    public enum EnrollmentStatus {
        PENDING, APPROVED, REJECTED, DROPPED
    }

    // Constructors
    public Enrollment() {}

    public Enrollment(User user, Kelas kelas, EnrollmentStatus status) {
        this.user = user;
        this.kelas = kelas;
        this.status = status;
        this.enrolledAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        enrolledAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Kelas getKelas() { return kelas; }
    public void setKelas(Kelas kelas) { this.kelas = kelas; }

    public EnrollmentStatus getStatus() { return status; }
    public void setStatus(EnrollmentStatus status) { this.status = status; }

    public LocalDateTime getEnrolledAt() { return enrolledAt; }
    public void setEnrolledAt(LocalDateTime enrolledAt) { this.enrolledAt = enrolledAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
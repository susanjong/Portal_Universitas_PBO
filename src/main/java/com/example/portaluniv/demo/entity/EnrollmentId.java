package com.example.portaluniv.demo.entity;

import java.io.Serializable;
import java.util.Objects;

public class EnrollmentId implements Serializable {
    private Long user;
    private Long kelas;

    public EnrollmentId() {}

    public EnrollmentId(Long user, Long kelas) {
        this.user = user;
        this.kelas = kelas;
    }

    // Getters and Setters
    public Long getUser() { return user; }
    public void setUser(Long user) { this.user = user; }

    public Long getKelas() { return kelas; }
    public void setKelas(Long kelas) { this.kelas = kelas; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EnrollmentId that = (EnrollmentId) o;
        return Objects.equals(user, that.user) && Objects.equals(kelas, that.kelas);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, kelas);
    }
}
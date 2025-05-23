package com.example.portaluniv.demo.repository;

import com.example.portaluniv.demo.entity.Mahasiswa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface MahasiswaRepository extends JpaRepository<Mahasiswa, Long> {
    Optional<Mahasiswa> findByNim(String nim);
    boolean existsByNim(String nim);
}
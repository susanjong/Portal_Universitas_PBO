package com.example.portaluniv.demo.repository;

import com.example.portaluniv.demo.entity.Dosen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface DosenRepository extends JpaRepository<Dosen, Long> {
    Optional<Dosen> findByNidn(String nidn);
    boolean existsByNidn(String nidn);
}
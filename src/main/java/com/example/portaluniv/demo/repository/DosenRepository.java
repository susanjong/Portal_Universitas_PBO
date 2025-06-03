package com.example.portaluniv.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.portaluniv.demo.entity.Dosen;

@Repository
public interface DosenRepository extends JpaRepository<Dosen, Long> {
    Optional<Dosen> findByNidn(String nidn);
    Optional<Dosen> findByName(String name);
    boolean existsByNidn(String nidn);
}
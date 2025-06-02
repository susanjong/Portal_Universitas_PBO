package com.example.portaluniv.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.portaluniv.demo.entity.Kelas;

@Repository
public interface KelasRepository extends JpaRepository<Kelas, Long> {
    Optional<Kelas> findByKode(String kode);

    Optional<Kelas> findByKodeAndKelas(String kode, String kelas);

    List<Kelas> findByMataKuliahId(Long mataKuliahId);

    List<Kelas> findByDosenId(Long dosenId);

    List<Kelas> findByKelasContaining(String kelas);

    List<Kelas> findByJadwalContaining(String jadwal);
    
    List<Kelas> findByRuangan(String ruangan);
    
    List<Kelas> findByProdi(String prodi);
    
    List<Kelas> findBySemester(int semester);
    
    List<Kelas> findByProdiAndSemester(String prodi, int semester);
    boolean existsByKode(String kode);
}
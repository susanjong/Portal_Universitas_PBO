package com.example.portaluniv.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.portaluniv.demo.entity.Kelas;

@Repository
public interface KelasRepository extends JpaRepository<Kelas, Long> {
    
    // Spring Data JPA method naming conventions
    List<Kelas> findByMataKuliahKodeMk(String kodeMk);
    
    Optional<Kelas> findByMataKuliahKodeMkAndKelas(String kodeMk, String kelas);

    List<Kelas> findByMataKuliahId(Long mataKuliahId);

    List<Kelas> findByDosenId(Long dosenId);

    List<Kelas> findByKelasContaining(String kelas);

    List<Kelas> findByJadwalContaining(String jadwal);
    
    List<Kelas> findByRuangan(String ruangan);
    
    List<Kelas> findByMataKuliahProgramStudi(String prodi);
    
    List<Kelas> findByMataKuliahSemester(int semester);
    
    List<Kelas> findByMataKuliahProgramStudiAndMataKuliahSemester(String prodi, int semester);
    
    // Check existence
    boolean existsByMataKuliahKodeMk(String kodeMk);
}
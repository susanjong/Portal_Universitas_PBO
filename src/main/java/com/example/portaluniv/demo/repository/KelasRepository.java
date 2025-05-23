package com.example.portaluniv.demo.repository;

import com.example.portaluniv.demo.entity.Kelas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface KelasRepository extends JpaRepository<Kelas, Long> {
    Optional<Kelas> findByKodeKelas(String kodeKelas);
    List<Kelas> findByMataKuliahId(Long mataKuliahId);
    List<Kelas> findByDosenId(Long dosenId);
    
    @Query("SELECT k FROM Kelas k WHERE k.namaKelas LIKE %:nama%")
    List<Kelas> findByNamaKelasContaining(@Param("nama") String nama);
    
    List<Kelas> findByHari(String hari);
    List<Kelas> findByRuangan(String ruangan);
    
    @Query("SELECT k FROM Kelas k WHERE SIZE(k.enrollments) < k.kapasitas")
    List<Kelas> findAvailableClasses();
    
    @Query("SELECT k FROM Kelas k JOIN k.mataKuliah mk WHERE mk.fakultas = :fakultas")
    List<Kelas> findByFakultas(@Param("fakultas") String fakultas);
    
    @Query("SELECT k FROM Kelas k JOIN k.mataKuliah mk WHERE mk.programStudi = :programStudi")
    List<Kelas> findByProgramStudi(@Param("programStudi") String programStudi);
    
    boolean existsByKodeKelas(String kodeKelas);
}
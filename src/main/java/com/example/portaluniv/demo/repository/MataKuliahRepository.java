package com.example.portaluniv.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.portaluniv.demo.entity.MataKuliah;

@Repository
public interface MataKuliahRepository extends JpaRepository<MataKuliah, Long> {
    Optional<MataKuliah> findByKodeMk(String kodeMk);
    
    @Query("SELECT mk FROM MataKuliah mk WHERE mk.namaMk LIKE %:nama%")
    List<MataKuliah> findByNamaMkContaining(@Param("nama") String nama);
    
    List<MataKuliah> findByProgramStudi(String programStudi);
    List<MataKuliah> findBySks(Integer sks);
    boolean existsByKodeMk(String kodeMk);
}
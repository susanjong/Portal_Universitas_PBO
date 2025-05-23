package com.example.portaluniv.demo.repository;

import com.example.portaluniv.demo.entity.MataKuliah;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface MataKuliahRepository extends JpaRepository<MataKuliah, Long> {
    Optional<MataKuliah> findByKodeMataKuliah(String kodeMataKuliah);
    
    @Query("SELECT mk FROM MataKuliah mk WHERE mk.namaMataKuliah LIKE %:nama%")
    List<MataKuliah> findByNamaMataKuliahContaining(@Param("nama") String nama);
    
    List<MataKuliah> findByJurusan(String jurusan);
    List<MataKuliah> findBySks(Integer sks);
    boolean existsByKodeMataKuliah(String kodeMataKuliah);
}
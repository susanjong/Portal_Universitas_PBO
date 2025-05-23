package com.example.portaluniv.demo.service;

import com.example.portaluniv.demo.entity.Kelas;
import com.example.portaluniv.demo.repository.KelasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class KelasService {
    
    @Autowired
    private KelasRepository kelasRepository;
    
    public List<Kelas> findAll() {
        return kelasRepository.findAll();
    }
    
    public Optional<Kelas> findById(Long id) {
        return kelasRepository.findById(id);
    }
    
    public Optional<Kelas> findByKodeKelas(String kodeKelas) {
        return kelasRepository.findByKodeKelas(kodeKelas);
    }
    
    public List<Kelas> findByMataKuliahId(Long mataKuliahId) {
        return kelasRepository.findByMataKuliahId(mataKuliahId);
    }
    
    public List<Kelas> findByDosenId(Long dosenId) {
        return kelasRepository.findByDosenId(dosenId);
    }
    
    public List<Kelas> findAvailableClasses() {
        return kelasRepository.findAvailableClasses();
    }
    
    public List<Kelas> findByFakultas(String fakultas) {
        return kelasRepository.findByFakultas(fakultas);
    }
    
    public List<Kelas> findByProgramStudi(String programStudi) {
        return kelasRepository.findByProgramStudi(programStudi);
    }
    
    public Kelas save(Kelas kelas) {
        return kelasRepository.save(kelas);
    }
    
    public void deleteById(Long id) {
        kelasRepository.deleteById(id);
    }
    
    public boolean existsByKodeKelas(String kodeKelas) {
        return kelasRepository.existsByKodeKelas(kodeKelas);
    }
}
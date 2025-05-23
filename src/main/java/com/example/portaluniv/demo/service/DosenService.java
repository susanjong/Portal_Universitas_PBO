package com.example.portaluniv.demo.service;

import com.example.portaluniv.demo.entity.Dosen;
import com.example.portaluniv.demo.repository.DosenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DosenService {
    
    @Autowired
    private DosenRepository dosenRepository;
    
    public List<Dosen> findAll() {
        return dosenRepository.findAll();
    }
    
    public Optional<Dosen> findById(Long id) {
        return dosenRepository.findById(id);
    }
    
    public Optional<Dosen> findByNidn(String nidn) {
        return dosenRepository.findByNidn(nidn);
    }
    
    public Dosen save(Dosen dosen) {
        return dosenRepository.save(dosen);
    }
    
    public void deleteById(Long id) {
        dosenRepository.deleteById(id);
    }
    
    public boolean existsByNidn(String nidn) {
        return dosenRepository.existsByNidn(nidn);
    }
}
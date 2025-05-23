package com.example.portaluniv.demo.service;

import com.example.portaluniv.demo.entity.MataKuliah;
import com.example.portaluniv.demo.repository.MataKuliahRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MataKuliahService {
    
    @Autowired
    private MataKuliahRepository mataKuliahRepository;
    
    public List<MataKuliah> findAll() {
        return mataKuliahRepository.findAll();
    }
    
    public Optional<MataKuliah> findById(Long id) {
        return mataKuliahRepository.findById(id);
    }
    
    public MataKuliah save(MataKuliah mataKuliah) {
        return mataKuliahRepository.save(mataKuliah);
    }
    
    public void deleteById(Long id) {
        mataKuliahRepository.deleteById(id);
    }
}
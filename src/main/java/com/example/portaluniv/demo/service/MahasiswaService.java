package com.example.portaluniv.demo.service;

import com.example.portaluniv.demo.entity.Mahasiswa;
import com.example.portaluniv.demo.repository.MahasiswaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MahasiswaService {
    
    @Autowired
    private MahasiswaRepository mahasiswaRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public List<Mahasiswa> findAll() {
        return mahasiswaRepository.findAll();
    }
    
    public Optional<Mahasiswa> findById(Long id) {
        return mahasiswaRepository.findById(id);
    }
    
    public Optional<Mahasiswa> findByNim(String nim) {
        return mahasiswaRepository.findByNim(nim);
    }
    
    public Mahasiswa save(Mahasiswa mahasiswa) {
        if (mahasiswa.getPassword() != null && !mahasiswa.getPassword().isEmpty()) {
            mahasiswa.setPassword(passwordEncoder.encode(mahasiswa.getPassword()));
        }
        return mahasiswaRepository.save(mahasiswa);
    }
    
    public void deleteById(Long id) {
        mahasiswaRepository.deleteById(id);
    }
    
    public boolean existsByNim(String nim) {
        return mahasiswaRepository.existsByNim(nim);
    }
}
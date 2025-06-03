package com.example.portaluniv.demo.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.portaluniv.demo.entity.MataKuliah;
import com.example.portaluniv.demo.service.MataKuliahService;

@RestController
@RequestMapping("/api/mata-kuliah")
@CrossOrigin(origins = "*")
public class MataKuliahController {
    
    @Autowired
    private MataKuliahService mataKuliahService;
    
    @GetMapping("/by-kode/{kodeMk}")
    public ResponseEntity<MataKuliah> getMataKuliahByKode(@PathVariable String kodeMk) {
        Optional<MataKuliah> mataKuliah = mataKuliahService.findByKodeMk(kodeMk);
        return mataKuliah.map(ResponseEntity::ok)
                        .orElse(ResponseEntity.notFound().build());
    }
}
package com.example.portaluniv.demo.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.portaluniv.demo.entity.Kelas;
import com.example.portaluniv.demo.service.KelasService;

@RestController
@RequestMapping("/api/kelas")
@CrossOrigin(origins = "*")
public class KelasController {
    
    @Autowired
    private KelasService kelasService;
    
    @GetMapping
    public List<Kelas> getAllKelas() {
        return kelasService.findAll();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Kelas> getKelasById(@PathVariable Long id) {
        Optional<Kelas> kelas = kelasService.findById(id);
        return kelas.map(ResponseEntity::ok)
                   .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public Kelas createKelas(@RequestBody Kelas kelas) {
        return kelasService.save(kelas);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Kelas> updateKelas(@PathVariable Long id, @RequestBody Kelas kelas) {
        Optional<Kelas> existingKelas = kelasService.findById(id);
        if (existingKelas.isPresent()) {
            kelas.setId(id);
            return ResponseEntity.ok(kelasService.save(kelas));
        }
        return ResponseEntity.notFound().build();
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteKelas(@PathVariable Long id) {
        Optional<Kelas> kelas = kelasService.findById(id);
        if (kelas.isPresent()) {
            kelasService.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
    
    @GetMapping("/mata-kuliah/{kodeMk}")
    public List<Kelas> getKelasByKodeMataKuliah(@PathVariable String kodeMk) {
        return kelasService.findByKodeMataKuliah(kodeMk);
    }
    
    @GetMapping("/prodi/{prodi}")
    public List<Kelas> getKelasByProdi(@PathVariable String prodi) {
        return kelasService.findByProdi(prodi);
    }
    
    @GetMapping("/semester/{semester}")
    public List<Kelas> getKelasBySemester(@PathVariable int semester) {
        return kelasService.findBySemester(semester);
    }
}
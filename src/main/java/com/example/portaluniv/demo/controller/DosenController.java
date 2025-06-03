package com.example.portaluniv.demo.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.portaluniv.demo.entity.Dosen;
import com.example.portaluniv.demo.service.DosenService;

@RestController
@RequestMapping("/api/dosen")
@CrossOrigin(origins = "*")
public class DosenController {
    
    @Autowired
    private DosenService dosenService;
    
    @GetMapping
    public ResponseEntity<List<Dosen>> getAllDosen() {
        List<Dosen> dosenList = dosenService.findAll();
        return ResponseEntity.ok(dosenList);
    }

    @GetMapping("/by-name/{name}")
    public ResponseEntity<Dosen> getDosenByName(@PathVariable String name) {
        Optional<Dosen> dosen = dosenService.findByName(name);
        return dosen.map(ResponseEntity::ok)
                   .orElse(ResponseEntity.notFound().build());
    }
}
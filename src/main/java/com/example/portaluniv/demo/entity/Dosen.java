package com.example.portaluniv.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "dosen")
@PrimaryKeyJoinColumn(name = "user_id")
public class Dosen extends User {
    
    @NotBlank(message = "NIDN is required")
    @Column(unique = true)
    private String nidn;

    @NotBlank(message = "Faculty is required")
    private String fakultas;

    @NotBlank(message = "Department is required")
    private String departemen;

    private String spesialisasi;

    @OneToMany(mappedBy = "dosen", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Kelas> kelasSet = new HashSet<>();

    // Constructors
    public Dosen() {
        super();
        this.setRole(Role.DOSEN);
    }

    public Dosen(String username, String password, String email, String name, 
                 String nidn, String fakultas, String departemen, String spesialisasi) {
        super(username, password, email, name, Role.DOSEN);
        this.nidn = nidn;
        this.fakultas = fakultas;
        this.departemen = departemen;
        this.spesialisasi = spesialisasi;
    }

    // Getters and Setters
    public String getNidn() { return nidn; }
    public void setNidn(String nidn) { this.nidn = nidn; }

    public String getFakultas() { return fakultas; }
    public void setFakultas(String fakultas) { this.fakultas = fakultas; }

    public String getDepartemen() { return departemen; }
    public void setDepartemen(String departemen) { this.departemen = departemen; }

    public String getSpesialisasi() { return spesialisasi; }
    public void setSpesialisasi(String spesialisasi) { this.spesialisasi = spesialisasi; }

    public Set<Kelas> getKelasSet() { return kelasSet; }
    public void setKelasSet(Set<Kelas> kelasSet) { this.kelasSet = kelasSet; }
}
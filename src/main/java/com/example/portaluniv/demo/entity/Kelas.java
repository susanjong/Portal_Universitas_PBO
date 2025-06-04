package com.example.portaluniv.demo.entity;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "kelas")
public class Kelas {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Kelas is required")
    private String kelas; 

    private String ruangan; 

    private String jadwal; 

    @Min(value = 1, message = "Kuota must be at least 1")
    private int kuota; 

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "mata_kuliah_id")
    @NotNull(message = "Mata kuliah is required")
    @JsonIgnoreProperties({"kelasSet", "password"})
    private MataKuliah mataKuliah;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "dosen_id")
    @NotNull(message = "Dosen is required")
    private Dosen dosen; 

    @OneToMany(mappedBy = "kelas", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"kelas", "user"}) // Prevent circular reference in JSON
    private Set<Enrollment> enrollments = new HashSet<>();

    // Constructors
    public Kelas() {}

    public Kelas(String kelas, String ruangan, String jadwal, int kuota,
                 MataKuliah mataKuliah, Dosen dosen) {
        this.kelas = kelas;
        this.ruangan = ruangan;
        this.jadwal = jadwal;
        this.kuota = kuota;
        this.mataKuliah = mataKuliah;
        this.dosen = dosen;
    }

    // Helper methods
    public int getCurrentEnrollmentCount() {
        return enrollments != null ? enrollments.size() : 0;
    }

    public boolean isFull() {
        return getCurrentEnrollmentCount() >= kuota;
    }

    public boolean isAvailable() {
        return !isFull();
    }

    // Derived properties from MataKuliah
    public String getProdi() { 
        return mataKuliah != null ? mataKuliah.getProgramStudi() : null; 
    }
    
    public int getSemester() { 
        return mataKuliah != null ? mataKuliah.getSemester() : 0; 
    }
    
    public int getSks() { 
        return mataKuliah != null ? mataKuliah.getSks() : 0; 
    }

    public String getKodeMataKuliah() {
        return mataKuliah != null ? mataKuliah.getKodeMk() : null;
    }
    
    public String getKodeKelasLengkap() {
        return getKodeMataKuliah() + "-" + kelas;
    }

    public int getKuotaTersedia() {
        return kuota - getCurrentEnrollmentCount();
    }

    public String getKuotaInfo() {
        return getKuotaTersedia() + "/" + kuota;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getKelas() { return kelas; }
    public void setKelas(String kelas) { this.kelas = kelas; }

    public String getRuangan() { return ruangan; }
    public void setRuangan(String ruangan) { this.ruangan = ruangan; }

    public String getJadwal() { return jadwal; }
    public void setJadwal(String jadwal) { this.jadwal = jadwal; }

    public int getKuota() { return kuota; }
    public void setKuota(int kuota) { this.kuota = kuota; }

    public MataKuliah getMataKuliah() { return mataKuliah; }
    public void setMataKuliah(MataKuliah mataKuliah) { this.mataKuliah = mataKuliah; }

    public Dosen getDosen() { return dosen; }
    public void setDosen(Dosen dosen) { this.dosen = dosen; }

    public Set<Enrollment> getEnrollments() { return enrollments; }
    public void setEnrollments(Set<Enrollment> enrollments) { this.enrollments = enrollments; }

}
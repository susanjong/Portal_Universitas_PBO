package com.example.portaluniv.demo.entity;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
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

    @NotBlank(message = "Kode is required")
    @Column(unique = true)
    private String kode; 

    @NotBlank(message = "Prodi is required")
    private String prodi; 

    @Min(value = 1, message = "Semester must be at least 1")
    private int semester; 

    @NotBlank(message = "Kelas is required")
    private String kelas; 

    @Min(value = 1, message = "SKS must be at least 1")
    private int sks; 

    private String ruangan; 

    private String jadwal; 

    @Min(value = 1, message = "Kuota must be at least 1")
    private int kuota; 

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mata_kuliah_id")
    @NotNull(message = "Mata kuliah is required")
    private MataKuliah mataKuliah;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dosen_id")
    @NotNull(message = "Dosen is required")
    private Dosen dosen; 

    @OneToMany(mappedBy = "kelas", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Enrollment> enrollments = new HashSet<>();

    // Constructors
    public Kelas() {}

    public Kelas(String kode, String prodi, int semester, String kelas, 
                 int sks, String ruangan, String jadwal, int kuota,
                 MataKuliah mataKuliah, Dosen dosen) {
        this.kode = kode;
        this.prodi = prodi;
        this.semester = semester;
        this.kelas = kelas;
        this.sks = sks;
        this.ruangan = ruangan;
        this.jadwal = jadwal;
        this.kuota = kuota;
        this.mataKuliah = mataKuliah;
        this.dosen = dosen;
    }

    // Helper methods
    public int getCurrentEnrollmentCount() {
        return enrollments.size();
    }

    public boolean isFull() {
        return getCurrentEnrollmentCount() >= kuota;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getKode() { return kode; }
    public void setKode(String kode) { this.kode = kode; }

    public String getProdi() { return prodi; }
    public void setProdi(String prodi) { this.prodi = prodi; }

    public int getSemester() { return semester; }
    public void setSemester(int semester) { this.semester = semester; }

    public String getKelas() { return kelas; }
    public void setKelas(String kelas) { this.kelas = kelas; }

    public int getSks() { return sks; }
    public void setSks(int sks) { this.sks = sks; }

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
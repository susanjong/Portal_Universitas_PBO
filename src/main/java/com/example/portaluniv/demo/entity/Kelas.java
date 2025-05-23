package com.example.portaluniv.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "kelas")
public class Kelas {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Class code is required")
    @Column(unique = true)
    private String kodeKelas;

    @NotBlank(message = "Class name is required")
    private String namaKelas;

    @Min(value = 1, message = "Capacity must be at least 1")
    private int kapasitas;

    private String ruangan;
    private String waktu;
    private String hari;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mata_kuliah_id")
    @NotNull(message = "Subject is required")
    private MataKuliah mataKuliah;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dosen_id")
    @NotNull(message = "Lecturer is required")
    private Dosen dosen;

    @OneToMany(mappedBy = "kelas", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Enrollment> enrollments = new HashSet<>();

    // Constructors
    public Kelas() {}

    public Kelas(String kodeKelas, String namaKelas, int kapasitas, String ruangan, 
                 String waktu, String hari, MataKuliah mataKuliah, Dosen dosen) {
        this.kodeKelas = kodeKelas;
        this.namaKelas = namaKelas;
        this.kapasitas = kapasitas;
        this.ruangan = ruangan;
        this.waktu = waktu;
        this.hari = hari;
        this.mataKuliah = mataKuliah;
        this.dosen = dosen;
    }

    // Helper method to get current enrollment count
    public int getCurrentEnrollmentCount() {
        return enrollments.size();
    }

    public boolean isFull() {
        return getCurrentEnrollmentCount() >= kapasitas;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getKodeKelas() { return kodeKelas; }
    public void setKodeKelas(String kodeKelas) { this.kodeKelas = kodeKelas; }

    public String getNamaKelas() { return namaKelas; }
    public void setNamaKelas(String namaKelas) { this.namaKelas = namaKelas; }

    public int getKapasitas() { return kapasitas; }
    public void setKapasitas(int kapasitas) { this.kapasitas = kapasitas; }

    public String getRuangan() { return ruangan; }
    public void setRuangan(String ruangan) { this.ruangan = ruangan; }

    public String getWaktu() { return waktu; }
    public void setWaktu(String waktu) { this.waktu = waktu; }

    public String getHari() { return hari; }
    public void setHari(String hari) { this.hari = hari; }

    public MataKuliah getMataKuliah() { return mataKuliah; }
    public void setMataKuliah(MataKuliah mataKuliah) { this.mataKuliah = mataKuliah; }

    public Dosen getDosen() { return dosen; }
    public void setDosen(Dosen dosen) { this.dosen = dosen; }

    public Set<Enrollment> getEnrollments() { return enrollments; }
    public void setEnrollments(Set<Enrollment> enrollments) { this.enrollments = enrollments; }
}
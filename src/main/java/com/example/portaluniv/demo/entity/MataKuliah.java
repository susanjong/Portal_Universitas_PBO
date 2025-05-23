package com.example.portaluniv.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "mata_kuliah")
public class MataKuliah {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Subject code is required")
    @Column(unique = true)
    private String kodeMk;

    @NotBlank(message = "Subject name is required")
    private String namaMk;

    @Min(value = 1, message = "Credits must be at least 1")
    private int sks;

    private String deskripsi;

    @NotBlank(message = "Faculty is required")
    private String fakultas;

    @NotBlank(message = "Study program is required")
    private String programStudi;

    private int semester;

    @OneToMany(mappedBy = "mataKuliah", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Kelas> kelasSet = new HashSet<>();

    // Constructors
    public MataKuliah() {}

    public MataKuliah(String kodeMk, String namaMk, int sks, String deskripsi, 
                      String fakultas, String programStudi, int semester) {
        this.kodeMk = kodeMk;
        this.namaMk = namaMk;
        this.sks = sks;
        this.deskripsi = deskripsi;
        this.fakultas = fakultas;
        this.programStudi = programStudi;
        this.semester = semester;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getKodeMk() { return kodeMk; }
    public void setKodeMk(String kodeMk) { this.kodeMk = kodeMk; }

    public String getNamaMk() { return namaMk; }
    public void setNamaMk(String namaMk) { this.namaMk = namaMk; }

    public int getSks() { return sks; }
    public void setSks(int sks) { this.sks = sks; }

    public String getDeskripsi() { return deskripsi; }
    public void setDeskripsi(String deskripsi) { this.deskripsi = deskripsi; }

    public String getFakultas() { return fakultas; }
    public void setFakultas(String fakultas) { this.fakultas = fakultas; }

    public String getProgramStudi() { return programStudi; }
    public void setProgramStudi(String programStudi) { this.programStudi = programStudi; }

    public int getSemester() { return semester; }
    public void setSemester(int semester) { this.semester = semester; }

    public Set<Kelas> getKelasSet() { return kelasSet; }
    public void setKelasSet(Set<Kelas> kelasSet) { this.kelasSet = kelasSet; }
}
package com.example.portaluniv.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "mahasiswa")
@PrimaryKeyJoinColumn(name = "user_id")
public class Mahasiswa extends User {
    
    @NotBlank(message = "NIM is required")
    @Column(unique = true)
    private String nim;

    @NotBlank(message = "Faculty is required")
    private String fakultas;

    @NotBlank(message = "Study program is required")
    private String programStudi;

    private int semester;

    // Constructors
    public Mahasiswa() {
        super();
        this.setRole(Role.MAHASISWA);
    }

    public Mahasiswa(String username, String password, String email, String name, 
                     String nim, String fakultas, String programStudi, int semester) {
        super(username, password, email, name, Role.MAHASISWA);
        this.nim = nim;
        this.fakultas = fakultas;
        this.programStudi = programStudi;
        this.semester = semester;
    }

    // Getters and Setters
    public String getNim() { return nim; }
    public void setNim(String nim) { this.nim = nim; }

    public String getFakultas() { return fakultas; }
    public void setFakultas(String fakultas) { this.fakultas = fakultas; }

    public String getProgramStudi() { return programStudi; }
    public void setProgramStudi(String programStudi) { this.programStudi = programStudi; }

    public int getSemester() { return semester; }
    public void setSemester(int semester) { this.semester = semester; }
}
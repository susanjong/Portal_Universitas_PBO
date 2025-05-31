package com.example.portaluniv.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.portaluniv.demo.dto.RegistrationForm;
import com.example.portaluniv.demo.entity.Dosen;
import com.example.portaluniv.demo.entity.Mahasiswa;
import com.example.portaluniv.demo.entity.User;
import com.example.portaluniv.demo.service.DosenService;
import com.example.portaluniv.demo.service.MahasiswaService;
import com.example.portaluniv.demo.service.UserService;


import jakarta.validation.Valid;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private DosenService dosenService;
    
    @Autowired
    private MahasiswaService mahasiswaService;
    
    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                          @RequestParam(value = "logout", required = false) String logout,
                          Model model) {
        
        if (error != null) {
            model.addAttribute("error", "Username atau password salah!");
        }
        
        if (logout != null) {
            model.addAttribute("message", "Anda telah berhasil logout.");
        }
        
        return "login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("registrationForm", new RegistrationForm());
        return "register";
    }

    @PostMapping("/register")
    public String processRegistration(@Valid @ModelAttribute("registrationForm") RegistrationForm form,
                                    BindingResult bindingResult,
                                    @RequestParam(value = "nim", required = false) String nim,
                                    @RequestParam(value = "nidn", required = false) String nidn,
                                    @RequestParam(value = "adminUsername", required = false) String adminUsername,
                                    @RequestParam(value = "semester", required = false) Integer semester,
                                    @RequestParam(value = "fakultas", required = false) String fakultas,
                                    @RequestParam(value = "programStudi", required = false) String programStudi,
                                    @RequestParam(value = "departemen", required = false) String departemen,
                                    @RequestParam(value = "spesialisasi", required = false) String spesialisasi,
                                    Model model,
                                    RedirectAttributes redirectAttributes) {
        
        System.out.println("=== REGISTRATION DEBUG ===");
        System.out.println("Role: " + form.getRole());
        System.out.println("Name: " + form.getName());
        System.out.println("Email: " + form.getEmail());
        System.out.println("Password: " + (form.getPassword() != null ? "***" : "null"));
        System.out.println("NIM: " + nim);
        System.out.println("NIDN: " + nidn);
        System.out.println("Admin Username: " + adminUsername);
        System.out.println("Fakultas: " + fakultas);
        System.out.println("Program Studi: " + programStudi);
        System.out.println("Departemen: " + departemen);
        System.out.println("Semester: " + semester);
        System.out.println("=========================");

        // Basic validation errors
        if (bindingResult.hasErrors()) {
            System.out.println("Validation errors found:");
            bindingResult.getAllErrors().forEach(error -> 
                System.out.println("- " + error.getDefaultMessage()));
            return "register";
        }

        try {
            // Role-specific validation and processing
            if (form.getRole() == User.Role.MAHASISWA) {
                if (nim == null || nim.trim().isEmpty()) {
                    model.addAttribute("error", "NIM wajib diisi untuk mahasiswa!");
                    return "register";
                }
                if (fakultas == null || fakultas.trim().isEmpty()) {
                    model.addAttribute("error", "Fakultas wajib diisi untuk mahasiswa!");
                    return "register";
                }
                if (programStudi == null || programStudi.trim().isEmpty()) {
                    model.addAttribute("error", "Program Studi wajib diisi untuk mahasiswa!");
                    return "register";
                }

                // Check if NIM already exists
                if (mahasiswaService.existsByNim(nim)) {
                    model.addAttribute("error", "NIM sudah terdaftar!");
                    return "register";
                }

                // Create Mahasiswa
                Mahasiswa mahasiswa = new Mahasiswa();
                mahasiswa.setUsername(nim); // NIM as username
                mahasiswa.setPassword(form.getPassword());
                mahasiswa.setEmail(form.getEmail());
                mahasiswa.setName(form.getName());
                mahasiswa.setNim(nim);
                mahasiswa.setFakultas(fakultas);
                mahasiswa.setProgramStudi(programStudi);
                mahasiswa.setSemester(semester != null ? semester : 1);

                mahasiswaService.save(mahasiswa);
                redirectAttributes.addFlashAttribute("success", 
                    "Pendaftaran mahasiswa berhasil! Silakan login dengan NIM: " + nim);

            } else if (form.getRole() == User.Role.DOSEN) {
                if (nidn == null || nidn.trim().isEmpty()) {
                    model.addAttribute("error", "NIDN wajib diisi untuk dosen!");
                    return "register";
                }
                if (fakultas == null || fakultas.trim().isEmpty()) {
                    model.addAttribute("error", "Fakultas wajib diisi untuk dosen!");
                    return "register";
                }
                if (departemen == null || departemen.trim().isEmpty()) {
                    model.addAttribute("error", "Departemen wajib diisi untuk dosen!");
                    return "register";
                }

                // Check if NIDN already exists
                if (dosenService.existsByNidn(nidn)) {
                    model.addAttribute("error", "NIDN sudah terdaftar!");
                    return "register";
                }

                // Create Dosen
                Dosen dosen = new Dosen();
                dosen.setUsername(nidn); // NIDN as username
                dosen.setPassword(form.getPassword());
                dosen.setEmail(form.getEmail());
                dosen.setName(form.getName());
                dosen.setNidn(nidn);
                dosen.setFakultas(fakultas);
                dosen.setDepartemen(departemen);
                dosen.setSpesialisasi(spesialisasi);

                dosenService.save(dosen);
                redirectAttributes.addFlashAttribute("success", 
                    "Pendaftaran dosen berhasil! Silakan login dengan NIDN: " + nidn);

            } else if (form.getRole() == User.Role.ADMIN) {
                if (adminUsername == null || adminUsername.trim().isEmpty()) {
                    model.addAttribute("error", "Username wajib diisi untuk admin!");
                    return "register";
                }

                // Check if username already exists
                if (userService.existsByUsername(adminUsername)) {
                    model.addAttribute("error", "Username sudah terdaftar!");
                    return "register";
                }

                // Create Admin User
                User admin = new User();
                admin.setUsername(adminUsername);
                admin.setPassword(form.getPassword());
                admin.setEmail(form.getEmail());
                admin.setName(form.getName());
                admin.setRole(User.Role.ADMIN);

                userService.save(admin);
                redirectAttributes.addFlashAttribute("success", 
                    "Pendaftaran admin berhasil! Silakan login dengan username: " + adminUsername);
            }

            return "redirect:/login";

        } catch (Exception e) {
            System.err.println("Registration error: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Terjadi kesalahan saat mendaftar: " + e.getMessage());
            return "register";
        }
    }

}
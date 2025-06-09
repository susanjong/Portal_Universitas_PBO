package com.example.portaluniv.demo.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.portaluniv.demo.config.CustomUserDetails;
import com.example.portaluniv.demo.entity.Enrollment;
import com.example.portaluniv.demo.entity.Kelas;
import com.example.portaluniv.demo.entity.Mahasiswa;
import com.example.portaluniv.demo.entity.MataKuliah;
import com.example.portaluniv.demo.entity.User;
import com.example.portaluniv.demo.service.EnrollmentService;
import com.example.portaluniv.demo.service.KelasService;
import com.example.portaluniv.demo.service.MahasiswaService;
import com.example.portaluniv.demo.service.UserService;

@Controller
public class AdminAturKelasController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private EnrollmentService enrollmentService;

    @Autowired
    private KelasService kelasService;

    @Autowired
    private MahasiswaService mahasiswaService;

    @GetMapping("/Admin_aturkelas")
    public String adminaturkelas(@RequestParam(required = false) String kelasFilter,
                                @RequestParam(required = false) String kodeFilter,
                                @RequestParam(required = false) String matkulFilter,
                                @RequestParam(required = false) String namaFilter,
                                Model model) {
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            
            Optional<User> currentUser = userService.findByUsername(userDetails.getUsername());
            
            if (currentUser.isPresent()) {
                User user = currentUser.get();
                model.addAttribute("username", user.getUsername());
                model.addAttribute("name", user.getName());
                model.addAttribute("email", user.getEmail());
                model.addAttribute("role", user.getRole());
            }
        }
        
        // Load all enrollments dan apply filter di controller
        List<Enrollment> allEnrollments = enrollmentService.findAll();
        List<Enrollment> filteredEnrollments = allEnrollments.stream()
            .filter(enrollment -> {

                if (kelasFilter != null && !kelasFilter.trim().isEmpty()) {
                    if (!enrollment.getKelas().getKelas().toLowerCase().contains(kelasFilter.toLowerCase())) {
                        return false;
                    }
                }

                if (kodeFilter != null && !kodeFilter.trim().isEmpty()) {
                    if (!enrollment.getKelas().getMataKuliah().getKodeMk().toLowerCase().contains(kodeFilter.toLowerCase())) {
                        return false;
                    }
                }

                if (matkulFilter != null && !matkulFilter.trim().isEmpty()) {
                    if (!enrollment.getKelas().getMataKuliah().getNamaMk().toLowerCase().contains(matkulFilter.toLowerCase())) {
                        return false;
                    }
                }

                if (namaFilter != null && !namaFilter.trim().isEmpty()) {
                    if (!enrollment.getUser().getName().toLowerCase().contains(namaFilter.toLowerCase())) {
                        return false;
                    }
                }
                return true;
            })
            .collect(Collectors.toList());
        
        model.addAttribute("enrollments", filteredEnrollments);
        model.addAttribute("kelasFilter", kelasFilter);
        model.addAttribute("kodeFilter", kodeFilter);
        model.addAttribute("matkulFilter", matkulFilter);
        model.addAttribute("namaFilter", namaFilter);
        
        return "Admin_aturkelas";
    }

    @GetMapping("/Admin_aturkelas/add")
    public String showAddForm(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            
            Optional<User> currentUser = userService.findByUsername(userDetails.getUsername());
            
            if (currentUser.isPresent()) {
                User user = currentUser.get();
                model.addAttribute("username", user.getUsername());
                model.addAttribute("name", user.getName());
                model.addAttribute("email", user.getEmail());
                model.addAttribute("role", user.getRole());
            }
        }
        
        // Load enrollments untuk ditampilkan juga
        model.addAttribute("enrollments", enrollmentService.findAll());
        model.addAttribute("showForm", true); // Set true untuk form view
        
        return "Admin_aturkelas";
    }

    @PostMapping("/Admin_aturkelas/add")
    public String addEnrollment(
            @RequestParam String nim,
            @RequestParam String kodeMatKul,
            @RequestParam String kelas,
            Model model,
            RedirectAttributes redirectAttributes) {
        
        try {
            // Validasi kelas dulu
            Optional<Kelas> kelasOpt = kelasService.findByKodeMataKuliahAndKelas(kodeMatKul, kelas);
            if (kelasOpt.isEmpty()) {
                // Kelas tidak valid, kembalikan ke form dengan error
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
                    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
                    Optional<User> currentUser = userService.findByUsername(userDetails.getUsername());
                    if (currentUser.isPresent()) {
                        User user = currentUser.get();
                        model.addAttribute("username", user.getUsername());
                        model.addAttribute("name", user.getName());
                        model.addAttribute("email", user.getEmail());
                        model.addAttribute("role", user.getRole());
                    }
                }
                
                model.addAttribute("enrollments", enrollmentService.findAll());
                model.addAttribute("showForm", true);
                model.addAttribute("errorMessage", "Kelas " + kelas + " untuk mata kuliah " + kodeMatKul + " tidak ditemukan!");
                
                // Keep form values
                model.addAttribute("kodeMatKul", kodeMatKul);
                model.addAttribute("kelas", kelas);
                model.addAttribute("nim", nim);
                
                return "Admin_aturkelas";
            }
            
            // Validasi mahasiswa
            Optional<Mahasiswa> mahasiswaOpt = mahasiswaService.findByNim(nim);
            if (mahasiswaOpt.isEmpty()) {
                // Mahasiswa tidak valid, kembalikan ke form dengan error
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
                    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
                    Optional<User> currentUser = userService.findByUsername(userDetails.getUsername());
                    if (currentUser.isPresent()) {
                        User user = currentUser.get();
                        model.addAttribute("username", user.getUsername());
                        model.addAttribute("name", user.getName());
                        model.addAttribute("email", user.getEmail());
                        model.addAttribute("role", user.getRole());
                    }
                }
                
                model.addAttribute("enrollments", enrollmentService.findAll());
                model.addAttribute("showForm", true);
                model.addAttribute("errorMessage", "Mahasiswa dengan NIM " + nim + " tidak ditemukan!");
                
                // Keep form values + mata kuliah info jika valid
                Kelas foundKelas = kelasOpt.get();
                model.addAttribute("kodeMatKul", kodeMatKul);
                model.addAttribute("kelas", kelas);
                model.addAttribute("nim", nim);
                model.addAttribute("namaMataKuliah", foundKelas.getMataKuliah().getNamaMk());
                model.addAttribute("semester", foundKelas.getMataKuliah().getSemester());
                
                return "Admin_aturkelas";
            }
            
            Kelas selectedKelas = kelasOpt.get();
            enrollmentService.enroll(mahasiswaOpt.get().getId(), selectedKelas.getId());
            
            redirectAttributes.addFlashAttribute("successMessage", "Mahasiswa berhasil ditambahkan ke kelas!");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
        }
        
        return "redirect:/Admin_aturkelas";
    }

    @GetMapping("/Admin_aturkelas/edit")
    public String showEditForm(@RequestParam Long id,
                            @RequestParam String nim,
                            @RequestParam String kodeMatKul,
                            @RequestParam String kelas,
                            Model model) {
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            Optional<User> currentUser = userService.findByUsername(userDetails.getUsername());
            if (currentUser.isPresent()) {
                User user = currentUser.get();
                model.addAttribute("username", user.getUsername());
                model.addAttribute("name", user.getName());
                model.addAttribute("email", user.getEmail());
                model.addAttribute("role", user.getRole());
            }
        }
        
        model.addAttribute("enrollments", enrollmentService.findAll());
        model.addAttribute("showForm", true);
        model.addAttribute("editMode", true);
        model.addAttribute("oldKelasId", id);
        
        // Load data untuk form
        model.addAttribute("kodeMatKul", kodeMatKul);
        model.addAttribute("kelas", kelas);
        model.addAttribute("nim", nim);
        
        // Load mata kuliah data
        List<Kelas> kelasList = kelasService.findByKodeMataKuliah(kodeMatKul);
        if (!kelasList.isEmpty()) {
            MataKuliah mataKuliah = kelasList.get(0).getMataKuliah();
            model.addAttribute("selectedMataKuliah", mataKuliah);
        }
        
        // Load mahasiswa data
        Optional<Mahasiswa> mahasiswa = mahasiswaService.findByNim(nim);
        if (mahasiswa.isPresent()) {
            model.addAttribute("selectedMahasiswa", mahasiswa.get());
        }
        
        return "Admin_aturkelas";
    }

    @PostMapping("/Admin_aturkelas/edit")
    public String editEnrollment(
        @RequestParam Long oldKelasId,
        @RequestParam String nim,
        @RequestParam String kodeMatKul,
        @RequestParam String kelas, // Tambahkan parameter ini
        RedirectAttributes redirectAttributes) {

        try {
            // Find mahasiswa by NIM
            Optional<Mahasiswa> mahasiswaOpt = mahasiswaService.findByNim(nim);
            if (mahasiswaOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Mahasiswa dengan NIM " + nim + " tidak ditemukan!");
                return "redirect:/Admin_aturkelas";
            }
            
            Mahasiswa mahasiswa = mahasiswaOpt.get();
            
            // Find new kelas berdasarkan kodeMatKul dan kelas
            Optional<Kelas> newKelasOpt = kelasService.findByKodeMataKuliahAndKelas(kodeMatKul, kelas);
            if (newKelasOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Kelas " + kelas + " untuk mata kuliah " + kodeMatKul + " tidak ditemukan!");
                return "redirect:/Admin_aturkelas";
            }
            
            Kelas selectedKelas = newKelasOpt.get();
            
            // Remove old enrollment
            enrollmentService.unenroll(mahasiswa.getId(), oldKelasId);
            
            // Create new enrollment
            enrollmentService.enroll(mahasiswa.getId(), selectedKelas.getId());
            
            redirectAttributes.addFlashAttribute("successMessage", "Data mahasiswa berhasil diperbarui!");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
        }
        
        return "redirect:/Admin_aturkelas";
    }

    @PostMapping("/Admin_aturkelas/delete")
    public String deleteEnrollment(
            @RequestParam String nim,
            @RequestParam Long kelasId,
            RedirectAttributes redirectAttributes) {
        
        try {
            // Find mahasiswa by NIM
            Optional<Mahasiswa> mahasiswaOpt = mahasiswaService.findByNim(nim);
            if (mahasiswaOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Mahasiswa dengan NIM " + nim + " tidak ditemukan!");
                return "redirect:/Admin_aturkelas";
            }
            
            Mahasiswa mahasiswa = mahasiswaOpt.get();
            enrollmentService.unenroll(mahasiswa.getId(), kelasId);
            redirectAttributes.addFlashAttribute("successMessage", "Mahasiswa berhasil dihapus dari kelas!");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
        }
        
        return "redirect:/Admin_aturkelas";
    }

    

    @PostMapping("/Admin_aturkelas/fillMataKuliah")
    public String fillMataKuliahData(@RequestParam String kodeMatKul,
                                    @RequestParam(required = false) String kelas,
                                    @RequestParam(required = false) String nim,
                                    Model model) {
        // Add user info
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            Optional<User> currentUser = userService.findByUsername(userDetails.getUsername());
            if (currentUser.isPresent()) {
                User user = currentUser.get();
                model.addAttribute("username", user.getUsername());
                model.addAttribute("name", user.getName());
                model.addAttribute("email", user.getEmail());
                model.addAttribute("role", user.getRole());
            }
        }
        
        model.addAttribute("enrollments", enrollmentService.findAll());
        model.addAttribute("showForm", true);
        
        // Preserve form values
        model.addAttribute("kodeMatKul", kodeMatKul);
        model.addAttribute("kelas", kelas);
        model.addAttribute("nim", nim);
        
        // Cari mata kuliah
        if (kodeMatKul != null && !kodeMatKul.trim().isEmpty()) {
            // Cari berdasarkan kode saja dulu
            List<Kelas> kelasList = kelasService.findByKodeMataKuliah(kodeMatKul.trim());
            if (!kelasList.isEmpty()) {
                MataKuliah mataKuliah = kelasList.get(0).getMataKuliah();
                model.addAttribute("selectedMataKuliah", mataKuliah);
                model.addAttribute("successMessage", "Mata kuliah ditemukan!");
            } else {
                model.addAttribute("errorMessage", "Mata kuliah dengan kode " + kodeMatKul + " tidak ditemukan");
            }
        }
        
        return "Admin_aturkelas";
    }

    @PostMapping("/Admin_aturkelas/fillMahasiswa")
    public String fillMahasiswaData(@RequestParam String kodeMatKul,
                                @RequestParam String kelas,
                                @RequestParam String nim,
                                Model model) {
        // Add user info (sama seperti method sebelumnya)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            Optional<User> currentUser = userService.findByUsername(userDetails.getUsername());
            if (currentUser.isPresent()) {
                User user = currentUser.get();
                model.addAttribute("username", user.getUsername());
                model.addAttribute("name", user.getName());
                model.addAttribute("email", user.getEmail());
                model.addAttribute("role", user.getRole());
            }
        }
        
        model.addAttribute("enrollments", enrollmentService.findAll());
        model.addAttribute("showForm", true);
        
        // Preserve form values
        model.addAttribute("kodeMatKul", kodeMatKul);
        model.addAttribute("kelas", kelas);
        model.addAttribute("nim", nim);
        
        // Load mata kuliah data (harus di-reload)
        if (kodeMatKul != null && !kodeMatKul.trim().isEmpty()) {
            List<Kelas> kelasList = kelasService.findByKodeMataKuliah(kodeMatKul.trim());
            if (!kelasList.isEmpty()) {
                MataKuliah mataKuliah = kelasList.get(0).getMataKuliah();
                model.addAttribute("selectedMataKuliah", mataKuliah);
            }
        }
        
        // Cari mahasiswa
        if (nim != null && !nim.trim().isEmpty()) {
            Optional<Mahasiswa> mahasiswa = mahasiswaService.findByNim(nim.trim());
            if (mahasiswa.isPresent()) {
                model.addAttribute("selectedMahasiswa", mahasiswa.get());
                model.addAttribute("successMessage", "Mahasiswa ditemukan!");
            } else {
                model.addAttribute("errorMessage", "Mahasiswa dengan NIM " + nim + " tidak ditemukan");
            }
        }
        
        return "Admin_aturkelas";
    }
        
}
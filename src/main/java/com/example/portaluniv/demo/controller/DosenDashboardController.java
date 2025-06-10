package com.example.portaluniv.demo.controller;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.portaluniv.demo.config.CustomUserDetails;
import com.example.portaluniv.demo.entity.Dosen;
import com.example.portaluniv.demo.entity.Enrollment;
import com.example.portaluniv.demo.entity.Kelas;
import com.example.portaluniv.demo.entity.Mahasiswa;
import com.example.portaluniv.demo.entity.MataKuliah;
import com.example.portaluniv.demo.entity.User;
import com.example.portaluniv.demo.service.DosenService;
import com.example.portaluniv.demo.service.EnrollmentService;
import com.example.portaluniv.demo.service.KelasService;
import com.example.portaluniv.demo.service.UserService;

@Controller
public class DosenDashboardController {

    @Autowired
    private UserService userService;

    @Autowired
    private DosenService dosenService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EnrollmentService enrollmentService;

    @Autowired
    private KelasService kelasService;

    // Method untuk menambahkan user info ke model (dosen)
    private void addUserInfoToModel(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            
            Optional<User> currentUser = userService.findByUsername(userDetails.getUsername());
            
            if (currentUser.isPresent()) {
                User user = currentUser.get();
                
                // Basic user information
                model.addAttribute("username", user.getUsername());
                model.addAttribute("name", user.getName());
                model.addAttribute("email", user.getEmail());
                model.addAttribute("role", user.getRole());
                
                // Additional dosen-specific data
                if (user instanceof Dosen) {
                    Dosen dosen = (Dosen) user;
                    model.addAttribute("nidn", dosen.getNidn());
                    model.addAttribute("fakultas", dosen.getFakultas());
                    model.addAttribute("departemen", dosen.getDepartemen());
                    model.addAttribute("spesialisasi", dosen.getSpesialisasi());
                }
            }
        }
    }

    @GetMapping("/dosen_dashboard_beranda")
    public String dashboard(Model model) {
        addUserInfoToModel(model);
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            
            Optional<User> currentUser = userService.findByUsername(userDetails.getUsername());
            
            if (currentUser.isPresent()) {
                User user = currentUser.get();
                
                // Show classes that dosen is teaching
                if (user instanceof Dosen) {
                    Dosen dosen = (Dosen) user;
                    
                    // Get classes taught by this dosen
                    List<Kelas> teachingClasses = kelasService.findByDosenId(dosen.getId());
                    model.addAttribute("teachingClasses", teachingClasses);
                    
                    // Get total students enrolled in dosen's classes
                    int totalStudents = teachingClasses.stream()
                            .mapToInt(kelas -> (int) enrollmentService.getEnrollmentCount(kelas.getId()))
                            .sum();
                    model.addAttribute("totalStudents", totalStudents);
                }
            }
        }
        
        return "dosen_dashboard_beranda";
    }

    @GetMapping("/dosen_dashboard_daftarkelas")
    public String daftarkelas(Model model,
                             @RequestParam(required = false) String codeFilter,
                             @RequestParam(required = false) String nameFilter,
                             @RequestParam(required = false) String classFilter,
                             @RequestParam(required = false) String roomFilter) {
        addUserInfoToModel(model);
        
        // Get all available classes that don't have a dosen assigned yet
        List<Kelas> kelasList = kelasService.findAll().stream()
                .filter(kelas -> kelas.getDosen() == null) // Only show unassigned classes
                .collect(Collectors.toList());
        
        // Apply filters
        if (codeFilter != null && !codeFilter.trim().isEmpty()) {
            kelasList = kelasList.stream()
                .filter(kelas -> kelas.getMataKuliah() != null && 
                        kelas.getMataKuliah().getKodeMk().toLowerCase().contains(codeFilter.toLowerCase()))
                .collect(Collectors.toList());
        }
        
        if (nameFilter != null && !nameFilter.trim().isEmpty()) {
            kelasList = kelasList.stream()
                .filter(kelas -> kelas.getMataKuliah() != null && 
                        kelas.getMataKuliah().getNamaMk().toLowerCase().contains(nameFilter.toLowerCase()))
                .collect(Collectors.toList());
        }
        
        if (classFilter != null && !classFilter.trim().isEmpty()) {
            kelasList = kelasList.stream()
                .filter(kelas -> kelas.getKelas().equals(classFilter))
                .collect(Collectors.toList());
        }
        
        if (roomFilter != null && !roomFilter.trim().isEmpty()) {
            kelasList = kelasList.stream()
                .filter(kelas -> kelas.getRuangan().equals(roomFilter))
                .collect(Collectors.toList());
        }
        
        // Add filtered class list to model
        model.addAttribute("kelasList", kelasList);
        
        // Preserve filter values
        model.addAttribute("codeFilter", codeFilter);
        model.addAttribute("nameFilter", nameFilter);
        model.addAttribute("classFilter", classFilter);
        model.addAttribute("roomFilter", roomFilter);
        
        // Set default form state
        model.addAttribute("showForm", false);
        
        return "dosen_dashboard_daftarkelas";
    }

    @GetMapping("/dosen_dashboard_daftarkelas/add")
    public String showAddClassForm(Model model) {
        addUserInfoToModel(model);
        
        // Load all unassigned classes for display
        List<Kelas> kelasList = kelasService.findAll().stream()
                .filter(kelas -> kelas.getDosen() == null)
                .collect(Collectors.toList());
        model.addAttribute("kelasList", kelasList);
        
        model.addAttribute("showForm", true);
        
        return "dosen_dashboard_daftarkelas";
    }

    @PostMapping("/dosen_dashboard_daftarkelas/add")
    public String assignToClass(
            @RequestParam String kodeMatKul,
            @RequestParam String kelas,
            Model model,
            RedirectAttributes redirectAttributes) {
        
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails)) {
                redirectAttributes.addFlashAttribute("errorMessage", "Session tidak valid!");
                return "redirect:/dosen_dashboard_daftarkelas";
            }
            
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            Optional<User> currentUser = userService.findByUsername(userDetails.getUsername());
            
            if (currentUser.isEmpty() || !(currentUser.get() instanceof Dosen)) {
                redirectAttributes.addFlashAttribute("errorMessage", "User tidak valid!");
                return "redirect:/dosen_dashboard_daftarkelas";
            }
            
            Dosen dosen = (Dosen) currentUser.get();
            
            // Find the class
            Optional<Kelas> kelasOpt = kelasService.findByKodeMataKuliahAndKelas(kodeMatKul, kelas);
            if (kelasOpt.isEmpty()) {
                addUserInfoToModel(model);
                
                List<Kelas> kelasList = kelasService.findAll().stream()
                        .filter(k -> k.getDosen() == null)
                        .collect(Collectors.toList());
                model.addAttribute("kelasList", kelasList);
                
                model.addAttribute("showForm", true);
                model.addAttribute("errorMessage", "Kelas " + kelas + " untuk mata kuliah " + kodeMatKul + " tidak ditemukan!");
                model.addAttribute("kodeMatKul", kodeMatKul);
                model.addAttribute("kelas", kelas);
                
                return "dosen_dashboard_daftarkelas";
            }
            
            Kelas selectedKelas = kelasOpt.get();
            
            // Check if class already has a dosen assigned
            if (selectedKelas.getDosen() != null) {
                redirectAttributes.addFlashAttribute("errorMessage", 
                    "Kelas " + kelas + " sudah memiliki dosen pengajar!");
                return "redirect:/dosen_dashboard_daftarkelas";
            }
            
            // Assign dosen to class
            selectedKelas.setDosen(dosen);
            kelasService.save(selectedKelas);
            
            redirectAttributes.addFlashAttribute("successMessage", 
                "Berhasil menjadi pengajar kelas " + kelas + " mata kuliah " + 
                selectedKelas.getMataKuliah().getNamaMk() + "!");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
        }
        
        return "redirect:/dosen_dashboard_daftarkelas";
    }

    @PostMapping("/dosen_dashboard_daftarkelas/fillMataKuliah")
    public String fillMataKuliahInfo(@RequestParam String kodeMatKul,
                                    @RequestParam(required = false) String kelas,
                                    Model model) {
        addUserInfoToModel(model);
        
        // Load all unassigned classes for display
        List<Kelas> kelasList = kelasService.findAll().stream()
                .filter(k -> k.getDosen() == null)
                .collect(Collectors.toList());
        model.addAttribute("kelasList", kelasList);
        
        model.addAttribute("showForm", true);
        model.addAttribute("kodeMatKul", kodeMatKul);
        model.addAttribute("kelas", kelas);
        
        // Search for mata kuliah
        if (kodeMatKul != null && !kodeMatKul.trim().isEmpty()) {
            List<Kelas> kelasListForMataKuliah = kelasService.findByKodeMataKuliah(kodeMatKul.trim())
                    .stream()
                    .filter(k -> k.getDosen() == null) // Only unassigned classes
                    .collect(Collectors.toList());
            
            if (!kelasListForMataKuliah.isEmpty()) {
                MataKuliah mataKuliah = kelasListForMataKuliah.get(0).getMataKuliah();
                model.addAttribute("selectedMataKuliah", mataKuliah);
                
                // Get available classes for this mata kuliah
                model.addAttribute("availableClasses", kelasListForMataKuliah);
                model.addAttribute("successMessage", "Mata kuliah ditemukan!");
            } else {
                model.addAttribute("errorMessage", "Mata kuliah dengan kode " + kodeMatKul + " tidak ditemukan atau semua kelas sudah memiliki dosen");
            }
        }
        
        return "dosen_dashboard_daftarkelas";
    }

    @GetMapping("/dosen_dashboard_kelasterdaftar")
    public String kelasterdaftar(Model model) {
        addUserInfoToModel(model);
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            
            Optional<User> currentUser = userService.findByUsername(userDetails.getUsername());
            
            if (currentUser.isPresent()) {
                User user = currentUser.get();
                
                // Get classes that this dosen is teaching
                if (user instanceof Dosen) {
                    Dosen dosen = (Dosen) user;
                    
                    List<Kelas> teachingClasses = kelasService.findByDosenId(dosen.getId());
                    model.addAttribute("teachingClasses", teachingClasses);
                    
                    // Add enrollment count for each class
                    model.addAttribute("enrollmentService", enrollmentService);
                }
            }
        }
        
        return "dosen_dashboard_kelasterdaftar";
    }

    @PostMapping("/dosen_dashboard_kelasterdaftar/unassign")
    public String unassignFromClass(@RequestParam String kodeMatKul,
                                @RequestParam String kelas,
                                RedirectAttributes redirectAttributes) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails)) {
                redirectAttributes.addFlashAttribute("errorMessage", "Session tidak valid!");
                return "redirect:/dosen_dashboard_kelasterdaftar";
            }
            
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            Optional<User> currentUser = userService.findByUsername(userDetails.getUsername());
            
            if (currentUser.isEmpty() || !(currentUser.get() instanceof Dosen)) {
                redirectAttributes.addFlashAttribute("errorMessage", "User tidak valid!");
                return "redirect:/dosen_dashboard_kelasterdaftar";
            }
            
            Dosen dosen = (Dosen) currentUser.get();
            
            // Find the kelas by kodeMatKul and kelas
            Optional<Kelas> kelasOpt = kelasService.findByKodeMataKuliahAndKelas(kodeMatKul, kelas);
            if (kelasOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", 
                    "Kelas " + kelas + " untuk mata kuliah " + kodeMatKul + " tidak ditemukan!");
                return "redirect:/dosen_dashboard_kelasterdaftar";
            }
            
            Kelas selectedKelas = kelasOpt.get();
            
            // Check if dosen is actually teaching this class
            if (selectedKelas.getDosen() == null || !selectedKelas.getDosen().getId().equals(dosen.getId())) {
                redirectAttributes.addFlashAttribute("errorMessage", 
                    "Anda tidak mengajar kelas " + kelas + " mata kuliah " + kodeMatKul + "!");
                return "redirect:/dosen_dashboard_kelasterdaftar";
            }
            
            // Unassign dosen from class
            selectedKelas.setDosen(null);
            kelasService.save(selectedKelas);
            
            redirectAttributes.addFlashAttribute("successMessage", 
                "Berhasil berhenti mengajar kelas " + kelas + " mata kuliah " + 
                selectedKelas.getMataKuliah().getNamaMk() + "!");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
        }
        
        return "redirect:/dosen_dashboard_kelasterdaftar";
    }

    @GetMapping("/dosen_dashboard_profile")
    public String dosenProfile(Model model) {
        addUserInfoToModel(model);
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            
            Optional<User> currentUser = userService.findByUsername(userDetails.getUsername());
            
            if (currentUser.isPresent()) {
                User user = currentUser.get();
                
                // Profile information
                model.addAttribute("birthDate", user.getBirthDate());
                model.addAttribute("birthPlace", user.getBirthPlace());
                model.addAttribute("gender", user.getGender());
                model.addAttribute("religion", user.getReligion());
                model.addAttribute("address", user.getAddress());
                model.addAttribute("idNumber", user.getIdNumber());
                model.addAttribute("idType", user.getIdType());
                model.addAttribute("phone", user.getPhone());
                model.addAttribute("careerGoal", user.getCareerGoal());
            }
        }

        return "dosen_dashboard_profile";
    }

    @PostMapping("/dosen_dashboard_profile/updatePassword")
    public String updatePassword(@RequestParam String password, 
                                @RequestParam String confirmPassword, 
                                RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            
            if (!password.equals(confirmPassword)) {
                redirectAttributes.addFlashAttribute("error", "Password tidak cocok!");
                return "redirect:/dosen_dashboard_profile";
            }
            
            if (password.length() < 6) {
                redirectAttributes.addFlashAttribute("error", "Password minimal 6 karakter!");
                return "redirect:/dosen_dashboard_profile";
            }
            
            Optional<User> currentUser = userService.findByUsername(userDetails.getUsername());
            if (currentUser.isPresent()) {
                User user = currentUser.get();
                
                user.setPassword(passwordEncoder.encode(password));
                userService.save(user);
                
                redirectAttributes.addFlashAttribute("success", "Password berhasil diupdate!");
            }
        }
        
        return "redirect:/dosen_dashboard_profile";
    }

    @PostMapping("/dosen_dashboard_profile/updateProfile")
    public String updateProfile(@RequestParam(required = false) String birthDate,
                              @RequestParam(required = false) String birthPlace,
                              @RequestParam(required = false) String gender,
                              @RequestParam(required = false) String religion,
                              @RequestParam(required = false) String address,
                              @RequestParam(required = false) String idNumber,
                              @RequestParam(required = false) String idType,
                              @RequestParam(required = false) String phone,
                              @RequestParam(required = false) String careerGoal,
                              @RequestParam(required = false) String password,
                              @RequestParam(required = false) String confirmPassword,
                              RedirectAttributes redirectAttributes) {
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            
            Optional<User> currentUser = userService.findByUsername(userDetails.getUsername());
            if (currentUser.isPresent()) {
                User user = currentUser.get();
                
                try {
                    // Update password jika diisi
                    if (password != null && !password.trim().isEmpty()) {
                        if (confirmPassword == null || !password.equals(confirmPassword)) {
                            redirectAttributes.addFlashAttribute("error", "Password dan konfirmasi password tidak cocok!");
                            return "redirect:/dosen_dashboard_profile";
                        }
                        
                        if (password.length() < 6) {
                            redirectAttributes.addFlashAttribute("error", "Password minimal 6 karakter!");
                            return "redirect:/dosen_dashboard_profile";
                        }
                        
                        user.setPassword(passwordEncoder.encode(password));
                    }
                    
                    // Update profile fields
                    if (birthDate != null && !birthDate.trim().isEmpty()) {
                        try {
                            user.setBirthDate(LocalDate.parse(birthDate));
                        } catch (DateTimeParseException e) {
                            redirectAttributes.addFlashAttribute("error", "Format tanggal lahir tidak valid!");
                            return "redirect:/dosen_dashboard_profile";
                        }
                    }
                    
                    if (birthPlace != null && !birthPlace.trim().isEmpty()) {
                        user.setBirthPlace(birthPlace.trim());
                    }
                    
                    if (gender != null && !gender.trim().isEmpty()) {
                        user.setGender(gender);
                    }
                    
                    if (religion != null && !religion.trim().isEmpty()) {
                        user.setReligion(religion);
                    }
                    
                    if (address != null && !address.trim().isEmpty()) {
                        user.setAddress(address.trim());
                    }
                    
                    if (idNumber != null && !idNumber.trim().isEmpty()) {
                        user.setIdNumber(idNumber.trim());
                    }
                    
                    if (idType != null && !idType.trim().isEmpty()) {
                        user.setIdType(idType);
                    }
                    
                    if (phone != null && !phone.trim().isEmpty()) {
                        user.setPhone(phone.trim());
                    }
                    
                    if (careerGoal != null && !careerGoal.trim().isEmpty()) {
                        user.setCareerGoal(careerGoal.trim());
                    }
                    
                    userService.save(user);
                    redirectAttributes.addFlashAttribute("success", "Profil berhasil diupdate!");
                    
                } catch (Exception e) {
                    redirectAttributes.addFlashAttribute("error", "Terjadi kesalahan saat mengupdate profil: " + e.getMessage());
                }
            }
        }
        
        return "redirect:/dosen_dashboard_profile";
    }
}
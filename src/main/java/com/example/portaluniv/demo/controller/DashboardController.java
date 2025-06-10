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
import com.example.portaluniv.demo.entity.Enrollment;
import com.example.portaluniv.demo.entity.Kelas;
import com.example.portaluniv.demo.entity.Mahasiswa;
import com.example.portaluniv.demo.entity.MataKuliah;
import com.example.portaluniv.demo.entity.User;
import com.example.portaluniv.demo.service.EnrollmentService;
import com.example.portaluniv.demo.service.KelasService;
import com.example.portaluniv.demo.service.UserService;

@Controller
public class DashboardController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EnrollmentService enrollmentService;

    @Autowired
    private KelasService kelasService;

    // Method untuk menambahkan user info ke model (mahasiswa)
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
                
                // Additional student-specific data
                if (user instanceof Mahasiswa) {
                    Mahasiswa mahasiswa = (Mahasiswa) user;
                    model.addAttribute("nim", mahasiswa.getNim());
                    model.addAttribute("fakultas", mahasiswa.getFakultas());
                    model.addAttribute("programStudi", mahasiswa.getProgramStudi());
                    model.addAttribute("currentSemester", mahasiswa.getSemester());
                }
            }
        }
    }

    @GetMapping("/dashboard_mahasiswa_beranda")
    public String dashboard(Model model) {
        addUserInfoToModel(model);
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            
            Optional<User> currentUser = userService.findByUsername(userDetails.getUsername());
            
            if (currentUser.isPresent()) {
                User user = currentUser.get();
                
                // Additional student-specific data
                if (user instanceof Mahasiswa) {

                    // Get enrolled classes for this student
                    List<Enrollment> enrollments = enrollmentService.findByUserId(user.getId());
                    List<Kelas> enrolledClasses = enrollments.stream()
                            .map(Enrollment::getKelas)
                            .collect(Collectors.toList());
                    
                    model.addAttribute("enrolledClasses", enrolledClasses);
                }
            }
        }
        
        return "dashboard_mahasiswa_beranda";
    }

    @GetMapping("/dashboard_mahasiswa_daftarkelas")
    public String daftarkelas(Model model,
                             @RequestParam(required = false) String codeFilter,
                             @RequestParam(required = false) String nameFilter,
                             @RequestParam(required = false) String classFilter,
                             @RequestParam(required = false) String roomFilter) {
        addUserInfoToModel(model);
        
        // Get all available classes
        List<Kelas> kelasList = kelasService.findAll();
        
        // Apply filters (same as admin controller)
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
        
        return "dashboard_mahasiswa_daftarkelas";
    }

    @GetMapping("/dashboard_mahasiswa_daftarkelas/add")
    public String showAddClassForm(Model model) {
        addUserInfoToModel(model);
        
        // Also load all classes for display
        List<Kelas> kelasList = kelasService.findAll();
        model.addAttribute("kelasList", kelasList);
        
        model.addAttribute("showForm", true);
        
        return "dashboard_mahasiswa_daftarkelas";
    }

    @PostMapping("/dashboard_mahasiswa_daftarkelas/add")
    public String addClassEnrollment(
            @RequestParam String kodeMatKul,
            @RequestParam String kelas,
            Model model,
            RedirectAttributes redirectAttributes) {
        
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails)) {
                redirectAttributes.addFlashAttribute("errorMessage", "Session tidak valid!");
                return "redirect:/dashboard_mahasiswa_daftarkelas";
            }
            
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            Optional<User> currentUser = userService.findByUsername(userDetails.getUsername());
            
            if (currentUser.isEmpty() || !(currentUser.get() instanceof Mahasiswa)) {
                redirectAttributes.addFlashAttribute("errorMessage", "User tidak valid!");
                return "redirect:/dashboard_mahasiswa_daftarkelas";
            }
            
            Mahasiswa mahasiswa = (Mahasiswa) currentUser.get();
            
            // Validasi kelas
            Optional<Kelas> kelasOpt = kelasService.findByKodeMataKuliahAndKelas(kodeMatKul, kelas);
            if (kelasOpt.isEmpty()) {
                // Return to form with error
                addUserInfoToModel(model);
                
                // Load all classes for display
                List<Kelas> kelasList = kelasService.findAll();
                model.addAttribute("kelasList", kelasList);
                
                model.addAttribute("showForm", true);
                model.addAttribute("errorMessage", "Kelas " + kelas + " untuk mata kuliah " + kodeMatKul + " tidak ditemukan!");
                model.addAttribute("kodeMatKul", kodeMatKul);
                model.addAttribute("kelas", kelas);
                
                return "dashboard_mahasiswa_daftarkelas";
            }
            
            // Check if already enrolled
            List<Enrollment> existingEnrollments = enrollmentService.findByUserId(mahasiswa.getId());
            boolean alreadyEnrolled = existingEnrollments.stream()
                    .anyMatch(enrollment -> 
                        enrollment.getKelas().getMataKuliah().getKodeMk().equals(kodeMatKul));
            
            if (alreadyEnrolled) {
                redirectAttributes.addFlashAttribute("errorMessage", 
                    "Anda sudah terdaftar di mata kuliah " + kodeMatKul + "!");
                return "redirect:/dashboard_mahasiswa_daftarkelas";
            }
            
            Kelas selectedKelas = kelasOpt.get();
            enrollmentService.enroll(mahasiswa.getId(), selectedKelas.getId());
            
            redirectAttributes.addFlashAttribute("successMessage", 
                "Berhasil mendaftar ke kelas " + kelas + " mata kuliah " + 
                selectedKelas.getMataKuliah().getNamaMk() + "!");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
        }
        
        return "redirect:/dashboard_mahasiswa_daftarkelas";
    }

    @PostMapping("/dashboard_mahasiswa_daftarkelas/fillMataKuliah")
    public String fillMataKuliahInfo(@RequestParam String kodeMatKul,
                                    @RequestParam(required = false) String kelas,
                                    Model model) {
        addUserInfoToModel(model);
        
        // Load all classes for display
        List<Kelas> kelasList = kelasService.findAll();
        model.addAttribute("kelasList", kelasList);
        
        model.addAttribute("showForm", true);
        model.addAttribute("kodeMatKul", kodeMatKul);
        model.addAttribute("kelas", kelas);
        
        // Search for mata kuliah
        if (kodeMatKul != null && !kodeMatKul.trim().isEmpty()) {
            List<Kelas> kelasListForMataKuliah = kelasService.findByKodeMataKuliah(kodeMatKul.trim());
            if (!kelasListForMataKuliah.isEmpty()) {
                MataKuliah mataKuliah = kelasListForMataKuliah.get(0).getMataKuliah();
                model.addAttribute("selectedMataKuliah", mataKuliah);
                
                // Get available classes for this mata kuliah
                model.addAttribute("availableClasses", kelasListForMataKuliah);
                model.addAttribute("successMessage", "Mata kuliah ditemukan!");
            } else {
                model.addAttribute("errorMessage", "Mata kuliah dengan kode " + kodeMatKul + " tidak ditemukan");
            }
        }
        
        return "dashboard_mahasiswa_daftarkelas";
    }

    @GetMapping("/dashboard_mahasiswa_kelasterdaftar")
    public String kelasterdaftar(Model model) {
        addUserInfoToModel(model);
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            
            Optional<User> currentUser = userService.findByUsername(userDetails.getUsername());
            
            if (currentUser.isPresent()) {
                User user = currentUser.get();
                
                // Additional student-specific data
                if (user instanceof Mahasiswa) {

                    // Get enrolled classes for this student
                    List<Enrollment> enrollments = enrollmentService.findByUserId(user.getId());
                    List<Kelas> enrolledClasses = enrollments.stream()
                            .map(Enrollment::getKelas)
                            .collect(Collectors.toList());
                    
                    model.addAttribute("enrolledClasses", enrolledClasses);
                }
            }
        }
        
        return "dashboard_mahasiswa_kelasterdaftar";
    }

    @PostMapping("/dashboard_mahasiswa_kelasterdaftar/unenroll")
    public String unenrollFromClass(@RequestParam String kodeMatKul,
                                @RequestParam String kelas,
                                RedirectAttributes redirectAttributes) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails)) {
                redirectAttributes.addFlashAttribute("errorMessage", "Session tidak valid!");
                return "redirect:/dashboard_mahasiswa_kelasterdaftar";
            }
            
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            Optional<User> currentUser = userService.findByUsername(userDetails.getUsername());
            
            if (currentUser.isEmpty() || !(currentUser.get() instanceof Mahasiswa)) {
                redirectAttributes.addFlashAttribute("errorMessage", "User tidak valid!");
                return "redirect:/dashboard_mahasiswa_kelasterdaftar";
            }
            
            Mahasiswa mahasiswa = (Mahasiswa) currentUser.get();
            
            // Find the kelas by kodeMatKul and kelas
            Optional<Kelas> kelasOpt = kelasService.findByKodeMataKuliahAndKelas(kodeMatKul, kelas);
            if (kelasOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", 
                    "Kelas " + kelas + " untuk mata kuliah " + kodeMatKul + " tidak ditemukan!");
                return "redirect:/dashboard_mahasiswa_kelasterdaftar";
            }
            
            Kelas selectedKelas = kelasOpt.get();
            
            // Check if student is actually enrolled in this class
            List<Enrollment> enrollments = enrollmentService.findByUserId(mahasiswa.getId());
            boolean isEnrolled = enrollments.stream()
                    .anyMatch(enrollment -> enrollment.getKelas().getId().equals(selectedKelas.getId()));
            
            if (!isEnrolled) {
                redirectAttributes.addFlashAttribute("errorMessage", 
                    "Anda tidak terdaftar di kelas " + kelas + " mata kuliah " + kodeMatKul + "!");
                return "redirect:/dashboard_mahasiswa_kelasterdaftar";
            }
            
            // Unenroll using the kelas ID
            enrollmentService.unenroll(mahasiswa.getId(), selectedKelas.getId());
            
            redirectAttributes.addFlashAttribute("successMessage", 
                "Berhasil membatalkan pendaftaran kelas " + kelas + " mata kuliah " + 
                selectedKelas.getMataKuliah().getNamaMk() + "!");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
        }
        
        return "redirect:/dashboard_mahasiswa_kelasterdaftar";
    }

    @GetMapping("/dashboard_mahasiswa_profile")
    public String mahasiswaProfile(Model model) {
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

        return "dashboard_mahasiswa_profile";
    }

    @PostMapping("/dashboard_mahasiswa_profile/updatePassword")
    public String updatePassword(@RequestParam String password, 
                                @RequestParam String confirmPassword, 
                                RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            
            if (!password.equals(confirmPassword)) {
                redirectAttributes.addFlashAttribute("error", "Password tidak cocok!");
                return "redirect:/dashboard_mahasiswa_profile";
            }
            
            if (password.length() < 6) {
                redirectAttributes.addFlashAttribute("error", "Password minimal 6 karakter!");
                return "redirect:/dashboard_mahasiswa_profile";
            }
            
            Optional<User> currentUser = userService.findByUsername(userDetails.getUsername());
            if (currentUser.isPresent()) {
                User user = currentUser.get();
                
                // Encode password sebelum menyimpan
                user.setPassword(passwordEncoder.encode(password));
                userService.save(user);
                
                redirectAttributes.addFlashAttribute("success", "Password berhasil diupdate!");
            }
        }
        
        return "redirect:/dashboard_mahasiswa_profile";
    }

    @PostMapping("/dashboard_mahasiswa_profile/updateProfile")
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
                            return "redirect:/dashboard_mahasiswa_profile";
                        }
                        
                        if (password.length() < 6) {
                            redirectAttributes.addFlashAttribute("error", "Password minimal 6 karakter!");
                            return "redirect:/dashboard_mahasiswa_profile";
                        }
                        
                        user.setPassword(passwordEncoder.encode(password));
                    }
                    
                    // Update profile fields
                    if (birthDate != null && !birthDate.trim().isEmpty()) {
                        try {
                            user.setBirthDate(LocalDate.parse(birthDate));
                        } catch (DateTimeParseException e) {
                            redirectAttributes.addFlashAttribute("error", "Format tanggal lahir tidak valid!");
                            return "redirect:/dashboard_mahasiswa_profile";
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
        
        return "redirect:/dashboard_mahasiswa_profile";
    }

    @GetMapping("/Admin_daftarmahasiswa")
    public String admindaftarmahasiswa(Model model) {
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
        
        return "Admin_daftarmahasiswa";
    }
}
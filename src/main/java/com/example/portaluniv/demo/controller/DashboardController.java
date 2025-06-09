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
import com.example.portaluniv.demo.entity.User;
import com.example.portaluniv.demo.service.EnrollmentService;
import com.example.portaluniv.demo.service.UserService;

@Controller
public class DashboardController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

     @Autowired
    private EnrollmentService enrollmentService;

    @GetMapping("/dashboard_mahasiswa_beranda")
    public String dashboard(Model model) {
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
    public String daftarkelas(Model model) {
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
        return "dashboard_mahasiswa_daftarkelas";
    }

    @GetMapping("/dashboard_mahasiswa_kelasterdaftar")
    public String kelasterdaftar(Model model) {
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
        return "dashboard_mahasiswa_kelasterdaftar";
    }

    @GetMapping("/dashboard_mahasiswa_profile")
    public String mahasiswaProfile(Model model) {
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
}
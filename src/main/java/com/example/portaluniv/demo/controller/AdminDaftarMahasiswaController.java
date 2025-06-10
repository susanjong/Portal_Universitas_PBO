package com.example.portaluniv.demo.controller;

import java.time.LocalDate;
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
import com.example.portaluniv.demo.entity.User;
import com.example.portaluniv.demo.entity.Mahasiswa;
import com.example.portaluniv.demo.service.UserService;
import com.example.portaluniv.demo.service.MahasiswaService;

@Controller
public class AdminDaftarMahasiswaController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private MahasiswaService mahasiswaService;

    @GetMapping("/Admin_daftarmahasiswa")
    public String adminDaftarMahasiswa(Model model, 
                                     @RequestParam(required = false) String showAddForm,
                                     @RequestParam(required = false) Long editStudentId,
                                     @RequestParam(required = false) String nimFilter,
                                     @RequestParam(required = false) String nameFilter,
                                     @RequestParam(required = false) String semesterFilter,
                                     @RequestParam(required = false) String fakultasFilter,
                                     @RequestParam(required = false) String prodiFilter) {

        // Get current authenticated user info
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

        // Get all students (Mahasiswa entities)
        List<Mahasiswa> allStudents = mahasiswaService.findAll();

        // Apply filters
        if (nimFilter != null && !nimFilter.trim().isEmpty()) {
            allStudents = allStudents.stream()
                .filter(student -> student.getNim() != null && 
                        student.getNim().toLowerCase().contains(nimFilter.toLowerCase()))
                .collect(Collectors.toList());
        }
        
        if (nameFilter != null && !nameFilter.trim().isEmpty()) {
            allStudents = allStudents.stream()
                .filter(student -> student.getName() != null && 
                        student.getName().toLowerCase().contains(nameFilter.toLowerCase()))
                .collect(Collectors.toList());
        }
        
        if (semesterFilter != null && !semesterFilter.trim().isEmpty()) {
            allStudents = allStudents.stream()
                .filter(student -> String.valueOf(student.getSemester()).equals(semesterFilter))
                .collect(Collectors.toList());
        }
        
        if (fakultasFilter != null && !fakultasFilter.trim().isEmpty()) {
            allStudents = allStudents.stream()
                .filter(student -> student.getFakultas() != null && 
                        student.getFakultas().toLowerCase().contains(fakultasFilter.toLowerCase()))
                .collect(Collectors.toList());
        }
        
        if (prodiFilter != null && !prodiFilter.trim().isEmpty()) {
            allStudents = allStudents.stream()
                .filter(student -> student.getProgramStudi() != null && 
                        student.getProgramStudi().toLowerCase().contains(prodiFilter.toLowerCase()))
                .collect(Collectors.toList());
        }

        model.addAttribute("students", allStudents);
        
        // Always add boolean attributes to prevent null values
        model.addAttribute("showAddForm", "true".equals(showAddForm));

        // Handle edit student
        boolean isEditMode = false;
        if (editStudentId != null) {
            Optional<Mahasiswa> studentToEdit = mahasiswaService.findById(editStudentId);
            if (studentToEdit.isPresent()) {
                model.addAttribute("editStudent", studentToEdit.get());
                isEditMode = true;
            }
        }
        model.addAttribute("showEditForm", isEditMode);

        // Add filter values to model for preserving form state (with null-safe defaults)
        model.addAttribute("nimFilter", nimFilter != null ? nimFilter : "");
        model.addAttribute("nameFilter", nameFilter != null ? nameFilter : "");
        model.addAttribute("semesterFilter", semesterFilter != null ? semesterFilter : "");
        model.addAttribute("fakultasFilter", fakultasFilter != null ? fakultasFilter : "");
        model.addAttribute("prodiFilter", prodiFilter != null ? prodiFilter : "");
        
        return "Admin_daftarmahasiswa";
    }

    // ADD THIS NEW MAPPING FOR EDIT FORM
    @GetMapping("/Admin_daftarmahasiswa/edit-student")
    public String showEditStudentForm(@RequestParam Long editUserId, Model model) {
        return adminDaftarMahasiswa(model, null, editUserId, null, null, null, null, null);
    }

    @PostMapping("/Admin_daftarmahasiswa/add-student")
    public String addStudent(@RequestParam String nim,
                           @RequestParam String name,
                           @RequestParam String email,
                           @RequestParam String password,
                           @RequestParam String semester,
                           @RequestParam String fakultas,
                           @RequestParam String prodi,
                           @RequestParam(required = false) String birthDate,
                           @RequestParam(required = false) String birthPlace,
                           @RequestParam(required = false) String gender,
                           @RequestParam(required = false) String religion,
                           @RequestParam(required = false) String address,
                           @RequestParam(required = false) String idNumber,
                           @RequestParam(required = false) String idType,
                           @RequestParam(required = false) String phone,
                           @RequestParam(required = false) String careerGoal,
                           RedirectAttributes redirectAttributes) {
        try {
            // Validate NIM already exists
            if (mahasiswaService.existsByNim(nim)) {
                redirectAttributes.addFlashAttribute("error", "NIM sudah digunakan");
                return "redirect:/Admin_daftarmahasiswa";
            }
            
            // Validate username (NIM) already exists in User table
            if (userService.existsByUsername(nim)) {
                redirectAttributes.addFlashAttribute("error", "NIM sudah digunakan sebagai username");
                return "redirect:/Admin_daftarmahasiswa";
            }
            
            // Validate email already exists
            if (userService.existsByEmail(email)) {
                redirectAttributes.addFlashAttribute("error", "Email sudah digunakan");
                return "redirect:/Admin_daftarmahasiswa";
            }
            
            Mahasiswa newStudent = new Mahasiswa();
            
            // Set basic User fields
            newStudent.setUsername(nim); // NIM as username
            newStudent.setName(name);
            newStudent.setEmail(email);
            newStudent.setPassword(password); // Should be encoded in service
            newStudent.setRole(User.Role.MAHASISWA);
            
            // Set profile fields from User entity
            if (birthDate != null && !birthDate.trim().isEmpty()) {
                newStudent.setBirthDate(LocalDate.parse(birthDate));
            }
            newStudent.setBirthPlace(birthPlace != null ? birthPlace : "");
            newStudent.setGender(gender != null ? gender : "");
            newStudent.setReligion(religion != null ? religion : "");
            newStudent.setAddress(address != null ? address : "");
            newStudent.setIdNumber(idNumber != null ? idNumber : "");
            newStudent.setIdType(idType != null ? idType : "");
            newStudent.setPhone(phone != null ? phone : "");
            newStudent.setCareerGoal(careerGoal != null ? careerGoal : "");
            
            // Set Mahasiswa-specific fields
            newStudent.setNim(nim);
            newStudent.setFakultas(fakultas);
            newStudent.setProgramStudi(prodi);
            newStudent.setSemester(Integer.valueOf(semester));
            
            mahasiswaService.save(newStudent);
            redirectAttributes.addFlashAttribute("success", "Mahasiswa berhasil ditambahkan");
            
        } catch (Exception e) {
            e.printStackTrace(); // ADD THIS FOR DEBUGGING
            redirectAttributes.addFlashAttribute("error", "Terjadi kesalahan: " + e.getMessage());
        }
        
        return "redirect:/Admin_daftarmahasiswa";
    }

    @PostMapping("/Admin_daftarmahasiswa/edit-student")
    public String editStudent(@RequestParam Long id,
                            @RequestParam String nim,
                            @RequestParam String name,
                            @RequestParam String email,
                            @RequestParam String semester,
                            @RequestParam String fakultas,
                            @RequestParam String prodi,
                            @RequestParam(required = false) String birthDate,
                            @RequestParam(required = false) String birthPlace,
                            @RequestParam(required = false) String gender,
                            @RequestParam(required = false) String religion,
                            @RequestParam(required = false) String address,
                            @RequestParam(required = false) String idNumber,
                            @RequestParam(required = false) String idType,
                            @RequestParam(required = false) String phone,
                            @RequestParam(required = false) String careerGoal,
                            RedirectAttributes redirectAttributes) {
        try {
            Optional<Mahasiswa> existingStudentOpt = mahasiswaService.findById(id);
            if (!existingStudentOpt.isPresent()) {
                redirectAttributes.addFlashAttribute("error", "Mahasiswa tidak ditemukan");
                return "redirect:/Admin_daftarmahasiswa";
            }
            
            Mahasiswa existingStudent = existingStudentOpt.get();
            
            // Check if NIM is being changed and if it's already taken by another mahasiswa
            if (!existingStudent.getNim().equals(nim) && mahasiswaService.existsByNim(nim)) {
                redirectAttributes.addFlashAttribute("error", "NIM sudah digunakan oleh mahasiswa lain");
                return "redirect:/Admin_daftarmahasiswa";
            }
            
            // Check if username (NIM) is being changed and if it's already taken by another user
            if (!existingStudent.getUsername().equals(nim) && userService.existsByUsername(nim)) {
                redirectAttributes.addFlashAttribute("error", "NIM sudah digunakan sebagai username oleh pengguna lain");
                return "redirect:/Admin_daftarmahasiswa";
            }
            
            // Check if email is being changed and if it's already taken by another user
            if (!existingStudent.getEmail().equals(email) && userService.existsByEmail(email)) {
                redirectAttributes.addFlashAttribute("error", "Email sudah digunakan oleh pengguna lain");
                return "redirect:/Admin_daftarmahasiswa";
            }
            
            // Update basic User fields
            existingStudent.setUsername(nim);
            existingStudent.setName(name);
            existingStudent.setEmail(email);
            
            // Update profile fields from User entity
            if (birthDate != null && !birthDate.trim().isEmpty()) {
                existingStudent.setBirthDate(LocalDate.parse(birthDate));
            }
            existingStudent.setBirthPlace(birthPlace != null ? birthPlace : "");
            existingStudent.setGender(gender != null ? gender : "");
            existingStudent.setReligion(religion != null ? religion : "");
            existingStudent.setAddress(address != null ? address : "");
            existingStudent.setIdNumber(idNumber != null ? idNumber : "");
            existingStudent.setIdType(idType != null ? idType : "");
            existingStudent.setPhone(phone != null ? phone : "");
            existingStudent.setCareerGoal(careerGoal != null ? careerGoal : "");
            
            // Update Mahasiswa-specific fields
            existingStudent.setNim(nim);
            existingStudent.setFakultas(fakultas);
            existingStudent.setProgramStudi(prodi);
            existingStudent.setSemester(Integer.valueOf(semester));
            
            mahasiswaService.save(existingStudent);
            redirectAttributes.addFlashAttribute("success", "Data mahasiswa berhasil diupdate");
            
        } catch (Exception e) {
            e.printStackTrace(); // ADD THIS FOR DEBUGGING
            redirectAttributes.addFlashAttribute("error", "Terjadi kesalahan: " + e.getMessage());
        }
        
        return "redirect:/Admin_daftarmahasiswa";
    }

    @PostMapping("/Admin_daftarmahasiswa/delete-student")
    public String deleteStudent(@RequestParam Long id, RedirectAttributes redirectAttributes) {
        try {
            Optional<Mahasiswa> studentOpt = mahasiswaService.findById(id);
            if (!studentOpt.isPresent()) {
                redirectAttributes.addFlashAttribute("error", "Mahasiswa tidak ditemukan");
                return "redirect:/Admin_daftarmahasiswa";
            }
            
            mahasiswaService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Mahasiswa berhasil dihapus");
            
        } catch (Exception e) {
            e.printStackTrace(); // ADD THIS FOR DEBUGGING
            redirectAttributes.addFlashAttribute("error", "Terjadi kesalahan: " + e.getMessage());
        }
        
        return "redirect:/Admin_daftarmahasiswa";
    }
}
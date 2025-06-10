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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.portaluniv.demo.config.CustomUserDetails;
import com.example.portaluniv.demo.entity.Dosen;
import com.example.portaluniv.demo.entity.User;
import com.example.portaluniv.demo.service.DosenService;
import com.example.portaluniv.demo.service.UserService;

@Controller
public class AdminDaftarDosenController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private DosenService dosenService;

    @GetMapping("/Admin_daftardosen")
    public String admindaftardosen(
            @RequestParam(value = "nidn", required = false) String nidnFilter,
            @RequestParam(value = "name", required = false) String nameFilter,
            @RequestParam(value = "fakultas", required = false) String fakultasFilter,
            @RequestParam(value = "departemen", required = false) String departemenFilter,
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
        
        // Ambil semua data dosen
        List<Dosen> dosenList = dosenService.findAll();
        
        if (nidnFilter != null && !nidnFilter.trim().isEmpty()) {
            dosenList = dosenList.stream()
                    .filter(dosen -> dosen.getNidn().toLowerCase().contains(nidnFilter.toLowerCase()))
                    .collect(Collectors.toList());
        }
        
        if (nameFilter != null && !nameFilter.trim().isEmpty()) {
            dosenList = dosenList.stream()
                    .filter(dosen -> dosen.getName().toLowerCase().contains(nameFilter.toLowerCase()))
                    .collect(Collectors.toList());
        }
        
        if (fakultasFilter != null && !fakultasFilter.trim().isEmpty()) {
            dosenList = dosenList.stream()
                    .filter(dosen -> dosen.getFakultas().equals(fakultasFilter))
                    .collect(Collectors.toList());
        }
        
        if (departemenFilter != null && !departemenFilter.trim().isEmpty()) {
            dosenList = dosenList.stream()
                    .filter(dosen -> dosen.getDepartemen().toLowerCase().contains(departemenFilter.toLowerCase()))
                    .collect(Collectors.toList());
        }
        
        model.addAttribute("dosenList", dosenList);
        
        // Add filter values to model to maintain form state
        model.addAttribute("nidnFilter", nidnFilter);
        model.addAttribute("nameFilter", nameFilter);
        model.addAttribute("fakultasFilter", fakultasFilter);
        model.addAttribute("departemenFilter", departemenFilter);
        
        return "Admin_daftardosen";
    }
    
    @GetMapping("/Admin_daftardosen/add-form")
    public String showAddDosenForm(Model model) {
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
        
        // Tambahkan objek dosen kosong untuk form
        model.addAttribute("dosen", new Dosen());
        model.addAttribute("showForm", true);
        model.addAttribute("isEdit", false); // Flag untuk menandakan ini form tambah
        
        // Ambil semua data dosen untuk tetap ditampilkan
        List<Dosen> dosenList = dosenService.findAll();
        model.addAttribute("dosenList", dosenList);
        
        return "Admin_daftardosen";
    }
    
    @GetMapping("/Admin_daftardosen/edit")
    public String showEditDosenForm(@RequestParam("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
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
            
            // Cari dosen berdasarkan ID
            Optional<Dosen> dosenOptional = dosenService.findById(id);
            
            if (dosenOptional.isPresent()) {
                Dosen dosen = dosenOptional.get();
                model.addAttribute("dosen", dosen);
                model.addAttribute("showForm", true);
                model.addAttribute("isEdit", true); // Flag untuk menandakan ini form edit
                
                // Ambil semua data dosen untuk tetap ditampilkan
                List<Dosen> dosenList = dosenService.findAll();
                model.addAttribute("dosenList", dosenList);
                
                return "Admin_daftardosen";
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Dosen tidak ditemukan!");
                return "redirect:/Admin_daftardosen";
            }
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Terjadi kesalahan: " + e.getMessage());
            return "redirect:/Admin_daftardosen";
        }
    }
    
    @PostMapping("/Admin_daftardosen/save")
    public String saveDosenForm(@ModelAttribute Dosen dosen, RedirectAttributes redirectAttributes) {
        try {
            boolean isEdit = (dosen.getId() != null);
            
            if (!isEdit) {
                // Validasi untuk tambah dosen baru
                if (dosenService.existsByNidn(dosen.getNidn())) {
                    redirectAttributes.addFlashAttribute("errorMessage", "NIDN sudah terdaftar!");
                    return "redirect:/Admin_daftardosen/add-form";
                }
                
                if (userService.existsByUsername(dosen.getUsername())) {
                    redirectAttributes.addFlashAttribute("errorMessage", "Username sudah terdaftar!");
                    return "redirect:/Admin_daftardosen/add-form";
                }
                
                if (userService.existsByEmail(dosen.getEmail())) {
                    redirectAttributes.addFlashAttribute("errorMessage", "Email sudah terdaftar!");
                    return "redirect:/Admin_daftardosen/add-form";
                }
                
                // Set role sebagai DOSEN
                dosen.setRole(User.Role.DOSEN);
            } else {
                // Untuk edit, ambil data dosen yang sudah ada
                Optional<Dosen> existingDosenOpt = dosenService.findById(dosen.getId());
                if (existingDosenOpt.isPresent()) {
                    Dosen existingDosen = existingDosenOpt.get();
                    
                    // Validasi NIDN jika diubah
                    if (!existingDosen.getNidn().equals(dosen.getNidn()) && 
                        dosenService.existsByNidn(dosen.getNidn())) {
                        redirectAttributes.addFlashAttribute("errorMessage", "NIDN sudah terdaftar!");
                        return "redirect:/Admin_daftardosen/edit?id=" + dosen.getId();
                    }
                    
                    // Validasi username jika diubah
                    if (!existingDosen.getUsername().equals(dosen.getUsername()) && 
                        userService.existsByUsername(dosen.getUsername())) {
                        redirectAttributes.addFlashAttribute("errorMessage", "Username sudah terdaftar!");
                        return "redirect:/Admin_daftardosen/edit?id=" + dosen.getId();
                    }
                    
                    // Validasi email jika diubah
                    if (!existingDosen.getEmail().equals(dosen.getEmail()) && 
                        userService.existsByEmail(dosen.getEmail())) {
                        redirectAttributes.addFlashAttribute("errorMessage", "Email sudah terdaftar!");
                        return "redirect:/Admin_daftardosen/edit?id=" + dosen.getId();
                    }
                    
                    // Pertahankan password lama jika tidak diubah
                    if (dosen.getPassword() == null || dosen.getPassword().isEmpty()) {
                        dosen.setPassword(existingDosen.getPassword());
                    }
                    
                    // Pertahankan role
                    dosen.setRole(existingDosen.getRole());
                }
            }
            
            // Simpan dosen
            dosenService.save(dosen);
            
            String message = isEdit ? "Dosen berhasil diperbarui!" : "Dosen berhasil ditambahkan!";
            redirectAttributes.addFlashAttribute("successMessage", message);
            return "redirect:/Admin_daftardosen";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Terjadi kesalahan: " + e.getMessage());
            if (dosen.getId() != null) {
                return "redirect:/Admin_daftardosen/edit?id=" + dosen.getId();
            } else {
                return "redirect:/Admin_daftardosen/add-form";
            }
        }
    }
    
    @PostMapping("/Admin_daftardosen/delete")
    public String deleteDosenForm(@RequestParam("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            // Cari dosen berdasarkan ID
            Optional<Dosen> dosenOptional = dosenService.findById(id);
            
            if (dosenOptional.isPresent()) {
                dosenService.deleteById(id);
                redirectAttributes.addFlashAttribute("successMessage", "Dosen berhasil dihapus!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Dosen tidak ditemukan!");
            }
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Terjadi kesalahan saat menghapus dosen: " + e.getMessage());
        }
        
        return "redirect:/Admin_daftardosen";
    }
}
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
import com.example.portaluniv.demo.entity.MataKuliah;
import com.example.portaluniv.demo.entity.User;
import com.example.portaluniv.demo.service.MataKuliahService;
import com.example.portaluniv.demo.service.UserService;

@Controller
public class AdminMataKuliahController {

    @Autowired
    private MataKuliahService mataKuliahService; 

    @Autowired
    private UserService userService;

    @GetMapping("/Admin_matakuliah")
    public String adminmatakuliah(@RequestParam(value = "codeFilter", required = false) String codeFilter,
                                @RequestParam(value = "nameFilter", required = false) String nameFilter,
                                @RequestParam(value = "prodiFilter", required = false) String prodiFilter,
                                @RequestParam(value = "semesterFilter", required = false) String semesterFilter,
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
        
        // Apply filters directly in controller
        List<MataKuliah> allMataKuliah = mataKuliahService.findAll();
        List<MataKuliah> matakuliahList = allMataKuliah.stream()
            .filter(mk -> codeFilter == null || codeFilter.isEmpty() || 
                        mk.getKodeMk().toLowerCase().contains(codeFilter.toLowerCase()))
            .filter(mk -> nameFilter == null || nameFilter.isEmpty() || 
                        mk.getNamaMk().toLowerCase().contains(nameFilter.toLowerCase()))
            .filter(mk -> prodiFilter == null || prodiFilter.isEmpty() || 
                        mk.getProgramStudi().equals(prodiFilter))
            .filter(mk -> semesterFilter == null || semesterFilter.isEmpty() || 
                        mk.getSemester() == Integer.parseInt(semesterFilter))
            .collect(Collectors.toList());
        
        // Add attributes to model
        model.addAttribute("matakuliahList", matakuliahList);
        model.addAttribute("showForm", false);
        
        // Keep filter values in form
        model.addAttribute("codeFilter", codeFilter);
        model.addAttribute("nameFilter", nameFilter);
        model.addAttribute("prodiFilter", prodiFilter);
        model.addAttribute("semesterFilter", semesterFilter);
        
        return "Admin_matakuliah";
    }

    @GetMapping("/Admin_matakuliah/add-form")
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
        
        // Load mata kuliah list untuk ditampilkan di tabel
        List<MataKuliah> matakuliahList = mataKuliahService.findAll();
        model.addAttribute("matakuliahList", matakuliahList);
        
        // Set form attributes
        model.addAttribute("showForm", true);
        model.addAttribute("isEdit", false);
        
        return "Admin_matakuliah";
    }

    @PostMapping("/Admin_matakuliah/add")
    public String addMataKuliah(@RequestParam("code") String kodeMk,
                            @RequestParam("name") String namaMk,
                            @RequestParam("faculty") String fakultas,
                            @RequestParam("prodi") String programStudi,
                            @RequestParam("semester") int semester,
                            @RequestParam("sks") int sks,
                            RedirectAttributes redirectAttributes) {
        try {
            // Create new MataKuliah object
            MataKuliah mataKuliah = new MataKuliah();
            mataKuliah.setKodeMk(kodeMk);
            mataKuliah.setNamaMk(namaMk);
            mataKuliah.setFakultas(fakultas);
            mataKuliah.setProgramStudi(programStudi);
            mataKuliah.setSemester(semester);
            mataKuliah.setSks(sks);
            
            // Save to database
            mataKuliahService.save(mataKuliah);
            
            redirectAttributes.addFlashAttribute("successMessage", "Mata kuliah berhasil ditambahkan!");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Gagal menambahkan mata kuliah: " + e.getMessage());
        }
        
        return "redirect:/Admin_matakuliah";
    }

    // Method untuk menampilkan form edit
    @GetMapping("/Admin_matakuliah/edit")
    public String showEditForm(@RequestParam("id") Long id, Model model) {
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
        
        // Load mata kuliah untuk edit
        Optional<MataKuliah> mataKuliah = mataKuliahService.findById(id);
        if (mataKuliah.isPresent()) {
            model.addAttribute("editMatakuliah", mataKuliah.get());
            model.addAttribute("isEdit", true);
            model.addAttribute("showForm", true);
        }
        
        // Load mata kuliah list untuk tabel
        List<MataKuliah> matakuliahList = mataKuliahService.findAll();
        model.addAttribute("matakuliahList", matakuliahList);
        
        return "Admin_matakuliah";
    }

    // Method untuk proses update
    @PostMapping("/Admin_matakuliah/edit")
    public String updateMataKuliah(@RequestParam("id") Long id,
                                @RequestParam("code") String kodeMk,
                                @RequestParam("name") String namaMk,
                                @RequestParam("faculty") String fakultas,
                                @RequestParam("prodi") String programStudi,
                                @RequestParam("semester") int semester,
                                @RequestParam("sks") int sks,
                                RedirectAttributes redirectAttributes) {
        try {
            Optional<MataKuliah> optionalMataKuliah = mataKuliahService.findById(id);
            if (optionalMataKuliah.isPresent()) {
                MataKuliah mataKuliah = optionalMataKuliah.get();
                mataKuliah.setKodeMk(kodeMk);
                mataKuliah.setNamaMk(namaMk);
                mataKuliah.setFakultas(fakultas);
                mataKuliah.setProgramStudi(programStudi);
                mataKuliah.setSemester(semester);
                mataKuliah.setSks(sks);
                
                mataKuliahService.save(mataKuliah);
                redirectAttributes.addFlashAttribute("successMessage", "Mata kuliah berhasil diupdate!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Gagal mengupdate mata kuliah: " + e.getMessage());
        }
        
        return "redirect:/Admin_matakuliah";
    }

    // Method untuk delete
    @PostMapping("/Admin_matakuliah/delete")
    public String deleteMataKuliah(@RequestParam("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            mataKuliahService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Mata kuliah berhasil dihapus!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Gagal menghapus mata kuliah: " + e.getMessage());
        }
        
        return "redirect:/Admin_matakuliah";
    }
}
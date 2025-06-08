package com.example.portaluniv.demo.controller;

import java.util.List;
import java.util.Optional;

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
import com.example.portaluniv.demo.entity.Dosen;
import com.example.portaluniv.demo.entity.Kelas;
import com.example.portaluniv.demo.entity.MataKuliah;
import com.example.portaluniv.demo.entity.User;
import com.example.portaluniv.demo.service.DosenService;
import com.example.portaluniv.demo.service.KelasService;
import com.example.portaluniv.demo.service.MataKuliahService;
import com.example.portaluniv.demo.service.UserService;

@Controller
public class AdminDaftarKelas {

    @Autowired
    private UserService userService;
    
    @Autowired
    private KelasService kelasService;
    
    @Autowired
    private MataKuliahService mataKuliahService;
    
    @Autowired 
    private DosenService dosenService;    

    // Method untuk menambahkan user info ke model
    private void addUserInfoToModel(Model model) {
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
    }

    // Method untuk menambahkan data dasar ke model
    private void addBasicDataToModel(Model model) {
        List<Kelas> kelasList = kelasService.findAll();
        List<MataKuliah> mataKuliahList = mataKuliahService.findAll();
        List<Dosen> dosenList = dosenService.findAll();

        model.addAttribute("kelasList", kelasList);
        model.addAttribute("mataKuliahList", mataKuliahList);
        model.addAttribute("dosenList", dosenList);
    }

    @GetMapping("/Admin_daftarkelas")
    public String admindaftarkelas(Model model) {
        addUserInfoToModel(model);
        addBasicDataToModel(model);
        
        // Default tampilkan list kelas, sembunyikan form
        model.addAttribute("showForm", false);
        
        return "Admin_daftarkelas";
    }

    // Tampilkan form untuk menambah kelas baru
    @GetMapping("/Admin_daftarkelas/add-form")
    public String showAddForm(Model model) {
        addUserInfoToModel(model);
        addBasicDataToModel(model);
        
        model.addAttribute("showForm", true);
        model.addAttribute("isEdit", false);
        
        return "Admin_daftarkelas";
    }

    // Cari mata kuliah berdasarkan kode untuk form tambah/edit
    @PostMapping("/Admin_daftarkelas/getMataKuliah")
    public String getMataKuliahInfo(@RequestParam String kodeMk, Model model, RedirectAttributes redirectAttributes) {
        addUserInfoToModel(model);
        addBasicDataToModel(model);
        
        // Cari mata kuliah berdasarkan kode
        Optional<MataKuliah> mataKuliah = mataKuliahService.findByKodeMk(kodeMk);
        if (mataKuliah.isPresent()) {
            model.addAttribute("selectedMataKuliah", mataKuliah.get());
            model.addAttribute("showForm", true);
            model.addAttribute("formKodeMk", kodeMk);
        } else {
            model.addAttribute("errorMessage", "Mata kuliah dengan kode " + kodeMk + " tidak ditemukan");
            model.addAttribute("showForm", true);
            model.addAttribute("formKodeMk", kodeMk);
        }
        
        return "Admin_daftarkelas";
    }
    
    @PostMapping("/Admin_daftarkelas/add")
    public String addKelas(@RequestParam String code,  
                        @RequestParam String section, 
                        @RequestParam Integer quota,  
                        @RequestParam String room,    
                        @RequestParam String schedule,
                        @RequestParam String lecturer,
                        Model model,
                        RedirectAttributes redirectAttributes) {
        try {
            Optional<MataKuliah> mataKuliah = mataKuliahService.findByKodeMk(code);
            if (!mataKuliah.isPresent()) {
                // error handling...
                model.addAttribute("formKodeMk", code);
                model.addAttribute("error", "Mata kuliah dengan kode " + code + " tidak ditemukan");
                return "Admin_daftarkelas";
            }
            
            Dosen dosenObj = null;
            if (lecturer != null && !lecturer.trim().isEmpty() && !lecturer.equals("Dosen Belum Terdaftar")) {
                Optional<Dosen> dosen = dosenService.findByName(lecturer);
                if (!dosen.isPresent()) {
                    model.addAttribute("selectedMataKuliah", mataKuliah.get());
                    model.addAttribute("formKodeMk", code);
                    model.addAttribute("error", "Dosen dengan nama " + lecturer + " tidak ditemukan");
                    return "Admin_daftarkelas";
                }
                dosenObj = dosen.get();
            } else if (lecturer != null && lecturer.equals("Dosen Belum Terdaftar")) {
                dosenObj = null; // Explicitly set to null
            }
                
            // Buat kelas baru - simpan ID bukan kode/nama
            Kelas newKelas = new Kelas();
            newKelas.setKelas(section);
            newKelas.setKuota(quota);
            newKelas.setRuangan(room);
            newKelas.setJadwal(schedule);
            newKelas.setMataKuliah(mataKuliah.get()); // Simpan object MataKuliah (berisi ID)
            newKelas.setDosen(dosenObj);           // Simpan object Dosen (berisi ID)
            
            kelasService.save(newKelas);
            redirectAttributes.addFlashAttribute("success", "Kelas berhasil ditambahkan!");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Gagal menambahkan kelas: " + e.getMessage());
        }
        
        return "redirect:/Admin_daftarkelas";
    }

    // Method untuk edit - menampilkan data kelas yang akan diedit
    @GetMapping("/Admin_daftarkelas/edit")
    public String showEditForm(@RequestParam Long id, Model model) {
        addUserInfoToModel(model);
        addBasicDataToModel(model);
        
        // Ambil data kelas yang akan diedit
        Optional<Kelas> kelas = kelasService.findById(id);
        if (kelas.isPresent()) {
            Kelas kelasData = kelas.get();
            model.addAttribute("editKelas", kelasData);
            model.addAttribute("selectedMataKuliah", kelasData.getMataKuliah());
            model.addAttribute("showForm", true);
            model.addAttribute("isEdit", true);
            model.addAttribute("formKodeMk", kelasData.getMataKuliah().getKodeMk());
        } else {
            model.addAttribute("error", "Kelas tidak ditemukan");
            model.addAttribute("showForm", false);
        }
        
        return "Admin_daftarkelas";
    }

    @PostMapping("/Admin_daftarkelas/edit")
    public String editKelas(@RequestParam Long id,
                        @RequestParam String code,        // Ubah dari kodeMk ke code
                        @RequestParam String section,     // Ubah dari kelas ke section  
                        @RequestParam Integer quota,      // Ubah dari kuota ke quota
                        @RequestParam String room,        // Ubah dari ruangan ke room
                        @RequestParam String schedule,    // Ubah dari jadwal ke schedule
                        @RequestParam String lecturer,    // Ubah dari dosenName ke lecturer
                        Model model,
                        RedirectAttributes redirectAttributes) {
        try {
            Optional<Kelas> existingKelas = kelasService.findById(id);
            if (!existingKelas.isPresent()) {
                redirectAttributes.addFlashAttribute("error", "Kelas tidak ditemukan");
                return "redirect:/Admin_daftarkelas";
            }
            
            // Cari mata kuliah berdasarkan kode
            Optional<MataKuliah> mataKuliah = mataKuliahService.findByKodeMk(code);
            if (!mataKuliah.isPresent()) {
                // Error handling sama seperti sebelumnya
                addUserInfoToModel(model);
                addBasicDataToModel(model);
                model.addAttribute("editKelas", existingKelas.get());
                model.addAttribute("showForm", true);
                model.addAttribute("isEdit", true);
                model.addAttribute("formKodeMk", code);
                model.addAttribute("error", "Mata kuliah dengan kode " + code + " tidak ditemukan");
                return "Admin_daftarkelas";
            }
            
            // Cari dosen berdasarkan nama
            Dosen dosenObj = null;
            if (lecturer != null && !lecturer.trim().isEmpty() && !lecturer.equals("Dosen Belum Terdaftar")) {
                Optional<Dosen> dosen = dosenService.findByName(lecturer);
                if (!dosen.isPresent()) {
                    addUserInfoToModel(model);
                    addBasicDataToModel(model);
                    model.addAttribute("editKelas", existingKelas.get());
                    model.addAttribute("selectedMataKuliah", mataKuliah.get());
                    model.addAttribute("showForm", true);
                    model.addAttribute("isEdit", true);
                    model.addAttribute("formKodeMk", code);
                    model.addAttribute("error", "Dosen dengan nama " + lecturer + " tidak ditemukan");
                    return "Admin_daftarkelas";
                }
                dosenObj = dosen.get();
            } else if (lecturer != null && lecturer.equals("Dosen Belum Terdaftar")) {
                dosenObj = null; // Explicitly set to null for "Dosen Belum Terdaftar"
            }
            
            // Update kelas
            Kelas kelasToUpdate = existingKelas.get();
            kelasToUpdate.setKelas(section);           // section bukan kelas
            kelasToUpdate.setKuota(quota);            // quota bukan kuota
            kelasToUpdate.setRuangan(room);           // room bukan ruangan
            kelasToUpdate.setJadwal(schedule);        // schedule bukan jadwal
            kelasToUpdate.setMataKuliah(mataKuliah.get());
            kelasToUpdate.setDosen(dosenObj);
            
            kelasService.save(kelasToUpdate);
            
            redirectAttributes.addFlashAttribute("success", "Kelas berhasil diperbarui!");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Gagal memperbarui kelas: " + e.getMessage());
        }
        
        return "redirect:/Admin_daftarkelas";
    }
    
    @PostMapping("/Admin_daftarkelas/delete")
    public String deleteKelas(@RequestParam Long id, RedirectAttributes redirectAttributes) {
        try {
            Optional<Kelas> kelas = kelasService.findById(id);
            if (!kelas.isPresent()) {
                redirectAttributes.addFlashAttribute("error", "Kelas tidak ditemukan");
                return "redirect:/Admin_daftarkelas";
            }
            
            kelasService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Kelas berhasil dihapus!");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Gagal menghapus kelas: " + e.getMessage());
        }
        
        return "redirect:/Admin_daftarkelas";
    }

    @PostMapping("/Admin_daftarkelas/fillMataKuliah")
    public String fillMataKuliahData(@RequestParam String code, 
                                    @RequestParam(required = false) Long editId,
                                    @RequestParam(required = false) Integer quota,
                                    @RequestParam(required = false) String section,
                                    @RequestParam(required = false) String room,
                                    @RequestParam(required = false) String schedule,
                                    @RequestParam(required = false) String lecturer,
                                    Model model) {
        System.out.println("DEBUG: Received code = " + code);
        addUserInfoToModel(model);
        addBasicDataToModel(model);
        
        model.addAttribute("showForm", true);
        model.addAttribute("formKodeMk", code);
        
        // Preserve semua data form yang sudah diisi
        model.addAttribute("formQuota", quota);
        model.addAttribute("formSection", section);
        model.addAttribute("formRoom", room);
        model.addAttribute("formSchedule", schedule);
        model.addAttribute("selectedLecturer", lecturer);
        
        // Set mode edit jika editId ada
        if (editId != null) {
            Optional<Kelas> editKelas = kelasService.findById(editId);
            if (editKelas.isPresent()) {
                model.addAttribute("editKelas", editKelas.get());
                model.addAttribute("isEdit", true);
            }
        } else {
            model.addAttribute("isEdit", false);
        }
        
        // Cari mata kuliah berdasarkan kode
        if (code != null && !code.trim().isEmpty()) {
            Optional<MataKuliah> mataKuliah = mataKuliahService.findByKodeMk(code.trim());
            if (mataKuliah.isPresent()) {
                model.addAttribute("selectedMataKuliah", mataKuliah.get());
                model.addAttribute("successMessage", "Mata kuliah berhasil ditemukan!");
            } else {
                model.addAttribute("errorMessage", "Mata kuliah dengan kode " + code + " tidak ditemukan");
            }
        }
        
        // Jika lecturer sudah dipilih sebelumnya, load NIDN-nya juga
        if (lecturer != null && !lecturer.trim().isEmpty()) {
            Optional<Dosen> dosen = dosenService.findByName(lecturer.trim());
            if (dosen.isPresent()) {
                model.addAttribute("selectedDosen", dosen.get());
            }
        }
        
        return "Admin_daftarkelas";
    }

    @PostMapping("/Admin_daftarkelas/fillDosen")
    public String fillDosenData(@RequestParam String code,
                            @RequestParam String lecturer,
                            @RequestParam(required = false) Long editId,
                            @RequestParam(required = false) Integer quota,
                            @RequestParam(required = false) String section,
                            @RequestParam(required = false) String room,
                            @RequestParam(required = false) String schedule,
                            Model model) {
        addUserInfoToModel(model);
        addBasicDataToModel(model);
        
        model.addAttribute("showForm", true);
        model.addAttribute("formKodeMk", code);
        
        // Preserve semua data form
        model.addAttribute("formQuota", quota);
        model.addAttribute("formSection", section);
        model.addAttribute("formRoom", room);
        model.addAttribute("formSchedule", schedule);
        model.addAttribute("selectedLecturer", lecturer);
        
        // Set mode edit
        if (editId != null) {
            Optional<Kelas> editKelas = kelasService.findById(editId);
            if (editKelas.isPresent()) {
                model.addAttribute("editKelas", editKelas.get());
                model.addAttribute("isEdit", true);
            }
        } else {
            model.addAttribute("isEdit", false);
        }
        
        // Load mata kuliah data (harus di-reload)
        if (code != null && !code.trim().isEmpty()) {
            Optional<MataKuliah> mataKuliah = mataKuliahService.findByKodeMk(code.trim());
            if (mataKuliah.isPresent()) {
                model.addAttribute("selectedMataKuliah", mataKuliah.get());
            }
        }
        
        // Load dosen NIDN
        if (lecturer != null && !lecturer.trim().isEmpty()) {
            Optional<Dosen> dosen = dosenService.findByName(lecturer.trim());
            if (dosen.isPresent()) {
                model.addAttribute("selectedDosen", dosen.get());
                model.addAttribute("successMessage", "NIDN dosen berhasil ditemukan!");
            } else {
                model.addAttribute("errorMessage", "Dosen dengan nama " + lecturer + " tidak ditemukan");
            }
        }
        
        return "Admin_daftarkelas";
    }
}
package com.example.portaluniv.demo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "admins")
public class Admin extends User {
   
   // Constructor
   public Admin() {
       super();
       this.setRole(Role.ADMIN);
   }
   
   public Admin(String username, String password, String email, String name) {
       super(username, password, email, name, Role.ADMIN);
   }
}
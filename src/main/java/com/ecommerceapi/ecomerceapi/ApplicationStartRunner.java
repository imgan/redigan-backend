package com.ecommerceapi.ecomerceapi;

import com.ecommerceapi.ecomerceapi.model.Admin;
import com.ecommerceapi.ecomerceapi.model.Role;
import com.ecommerceapi.ecomerceapi.repositories.AdminRepository;
import com.ecommerceapi.ecomerceapi.repositories.RoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class ApplicationStartRunner implements CommandLineRunner {

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    AdminRepository adminRepository;

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void run(String... args) throws Exception {
        loadRoleData();
        loadOfficerData();
    }

    private void loadRoleData() {
        if (roleRepository.count() == 0) {
            Role role = new Role(); role.setName("admin");
            role.setStatus(true); role.setCreatedDate(new Date());
            roleRepository.save(role);
        }
    }

    private void loadOfficerData() {
        if (adminRepository.count() == 0) {
            Admin admin = new Admin();
            admin.setCreatedBy("Self"); admin.setUsername("admin");
            admin.setEmail("admin@cranium.com"); admin.setPhone("000000000000");
            admin.setOfficerName("Administrator"); admin.setPassword("cranium2021");
            admin.setRoleId(1); admin.setStatus(1); admin.setCreatedDate(new Date());
            adminRepository.save(admin);
        }
    }
}
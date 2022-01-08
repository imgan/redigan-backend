package com.ecommerceapi.ecomerceapi.repositories;

import com.ecommerceapi.ecomerceapi.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
    Admin findAllByUsername(String username);
    Admin findAllByEmailOrUsernameOrPhone(String email, String username, String phone);
    Admin findOneByUsername(String username);
    Admin findOneByEmail(String email);

    @Query(value = "SELECT * FROM admin WHERE (username LIKE %?1% OR officer_name LIKE %?1% OR email LIKE %?1%) " +
            "AND status = 1 ORDER BY officer_name ASC limit ?2 offset ?3 " ,
            nativeQuery = true)
    List<Admin> findAllOffsetLimit(String search, Integer limit, Integer offset);

    @Query(value = "SELECT count(id) FROM admin WHERE (username LIKE %?1% OR officer_name LIKE %?1% OR email LIKE %?1%) " +
            "AND status = 1 ORDER BY officer_name ASC" ,
            nativeQuery = true)
    Integer findAllOffsetLimitCount(String search);
}

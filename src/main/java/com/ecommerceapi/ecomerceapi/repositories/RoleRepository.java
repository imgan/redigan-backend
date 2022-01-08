package com.ecommerceapi.ecomerceapi.repositories;

import com.ecommerceapi.ecomerceapi.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {

    Role findOneById(Integer id);
    Role findOneByName(String name);

    @Query(value = "SELECT * FROM Role WHERE name = ?1 AND status = 1 limit 1" , nativeQuery = true)
    Role findOneByNameActive(String name);

    @Query(value = "SELECT * FROM Role WHERE id != ?1 AND name = ?2 AND status = 1 limit 1" , nativeQuery = true)
    Role findOneByNameActiveEx(Integer id, String name);

    @Query(value = "SELECT * FROM Role WHERE name LIKE %?1% AND status = 1 ORDER BY name ASC limit ?2 offset ?3 " ,
            nativeQuery = true)
    List<Role> findAllOffsetLimit(String search, Integer limit, Integer offset);

    @Query(value = "SELECT count(id) FROM Role WHERE name LIKE %?1% AND status = 1 ORDER BY name ASC" , nativeQuery = true)
    Integer findAllCount(String search);

}

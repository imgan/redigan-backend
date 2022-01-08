package com.ecommerceapi.ecomerceapi.repositories;

import com.ecommerceapi.ecomerceapi.model.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MerchantRepository extends JpaRepository<Merchant, Long> {
    Merchant findAllByUsername(String username);

    List<Merchant> findAllByEmailOrUsernameOrPhone(String email, String username, String phone);

    Merchant findOneByUsername(String username);

    Merchant findOneByEmail(String email);

    @Query(value = "SELECT * FROM merchant where (phone LIKE %?3% OR email LIKE %?3% OR store_name LIKE %?3% OR username LIKE %?3%) AND is_deleted = 0 order by created_date desc limit ?2 offset ?1 ", nativeQuery = true)
    List<Merchant> findAllOffsetLimit(Integer offset, Integer limit, String search);

    @Query(value = "SELECT count(id) FROM merchant where (phone LIKE %?1% OR email LIKE %?1% OR store_name LIKE %?1% OR username LIKE %?1%) AND is_deleted = 0", nativeQuery = true)
    Integer findAllCount(String search);

    Merchant findOneById(Long customerId);
}

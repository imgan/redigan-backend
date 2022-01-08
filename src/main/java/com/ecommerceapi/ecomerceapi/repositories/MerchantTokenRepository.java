package com.ecommerceapi.ecomerceapi.repositories;

import com.ecommerceapi.ecomerceapi.model.MerchantToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MerchantTokenRepository extends JpaRepository<MerchantToken,Long > {
    MerchantToken findByMerchantId(Long MerchantId);
}

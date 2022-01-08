package com.ecommerceapi.ecomerceapi.repositories;

import com.ecommerceapi.ecomerceapi.model.QontakConfig;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QontakRepository  extends JpaRepository<QontakConfig, Long> {

    QontakConfig findOneByName(String name);
}

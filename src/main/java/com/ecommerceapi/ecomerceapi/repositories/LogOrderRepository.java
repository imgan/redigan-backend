package com.ecommerceapi.ecomerceapi.repositories;

import com.ecommerceapi.ecomerceapi.model.LogOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogOrderRepository extends JpaRepository<LogOrder, Long> {
}

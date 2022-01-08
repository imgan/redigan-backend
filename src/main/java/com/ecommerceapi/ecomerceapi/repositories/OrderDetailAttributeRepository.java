package com.ecommerceapi.ecomerceapi.repositories;

import com.ecommerceapi.ecomerceapi.model.OrderAttributeDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderDetailAttributeRepository extends JpaRepository<OrderAttributeDetail,Long > {

    List<OrderAttributeDetail> findByOrderNumber(String orderNumber);

    List<OrderAttributeDetail> findByOrderNumberAndItemId(String orderNumber, Long itemId);

    List<OrderAttributeDetail> findByOrderNumberAndItemIdAndSequence(String orderNumber, Long itemId, Integer sequence);
}

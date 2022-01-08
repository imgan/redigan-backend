package com.ecommerceapi.ecomerceapi.repositories;

import com.ecommerceapi.ecomerceapi.model.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {

    @Query(value = "SELECT IFNULL(SUM(a.qty),0) as total  from order_detail a" +
            " JOIN order_transaction b on a.order_number = b.order_number" +
            " where a.item_id = ?1" +
            " AND b.order_status IN(1,2,3) AND b.delivery_date > ?2 ", nativeQuery = true)
    Integer getTotalOrderItemPerDay(Long itemId, String now);

    @Query(value = "SELECT a.qty, b.name FROM order_detail a " +
            "JOIN item b on a.item_id = b.id WHERE a.order_number =?1", nativeQuery = true)
    List<Map> findAllByOrderNumberItemName(String orderNumber);

}

package com.ecommerceapi.ecomerceapi.repositories;

import com.ecommerceapi.ecomerceapi.model.Admin;
import com.ecommerceapi.ecomerceapi.model.AvailableStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AvailableStockRepository extends JpaRepository<AvailableStock, Long> {
    @Query(value = "SELECT IFNULL(SUM(a.qty),0) as total  from available_stock a" +
            " where a.item_id = ?1" +
            " AND a.date = ?2", nativeQuery = true)
    Integer getQtyCanBuy(Long itemId, String date);

    @Modifying
    @Query(value = "DELETE FROM available_stock WHERE order_number = ?1", nativeQuery = true)
    void deleteByOrderNumberStock(String orderNumber);
}

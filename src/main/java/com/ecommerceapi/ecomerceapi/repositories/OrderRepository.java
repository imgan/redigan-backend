package com.ecommerceapi.ecomerceapi.repositories;

import com.ecommerceapi.ecomerceapi.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query(value = "SELECT count(id) as total FROM order_transaction ot where ot.merchant_id = ?1 AND order_status = 1", nativeQuery = true)
    Integer countAllOrderIncomingByMerchant(Long id);

    @Query(value = "SELECT count(id) as total FROM order_transaction ot where ot.merchant_id = ?1 AND order_status = 2", nativeQuery = true)
    Integer countAllOrderOngoingByMerchant(Long id);

    @Query(value = "SELECT count(id) as total FROM order_transaction ot where ot.merchant_id = ?1 AND order_status = 3", nativeQuery = true)
    Integer countAllOrderSettledByMerchant(Long id);

    @Query(value = "SELECT IFNULL(SUM(amount+delivery_fee),0) as total  from order_transaction where merchant_id = ?1  AND order_status = 3 ", nativeQuery = true)
    Integer findPrevRevenueByMerchant(Long id);

    @Query(value = "SELECT IFNULL(SUM(amount+delivery_fee),0) as total from order_transaction where merchant_id = ?1  AND order_status = 3 AND month(created_date)=month(now())", nativeQuery = true)
    Integer findThisRevenueByMerchant(Long id);

    @Query(value = "SELECT * from order_transaction where merchant_id = ?1 AND order_status = 1 AND " +
            "(customer_name LIKE %?4% OR order_number LIKE %?4%) AND (?5 is null or delivery_date between ?5 AND ?6) " +
            "order by delivery_date asc limit ?3 offset ?2", nativeQuery = true)
    List<Order> getIncomingOrderByMerchant(Long merchantId, Integer offset, Integer limit, String search, String startDate, String endDate);

    @Query(value = "SELECT count(id) from order_transaction where merchant_id = ?1 AND order_status = 1 AND " +
            "(customer_name LIKE %?2% OR order_number LIKE %?2%) AND (?3 is null or delivery_date between ?3 AND ?4)", nativeQuery = true)
    Integer getIncomingOrderByMerchantCount(Long merchantId, String search, String startDate, String endDate);

    @Query(value = "SELECT * from order_transaction where merchant_id = ?1 AND order_status = 2 AND is_paid =?5 AND " +
            "(customer_name LIKE %?4% OR order_number LIKE %?4%) AND (?6 is null or delivery_date between ?6 AND ?7) " +
            "order by delivery_date asc limit ?3 offset ?2", nativeQuery = true)
    List<Order> getOngoingOrderByMerchant(Long merchantId, Integer offset, Integer limity, String search, Integer is_paid, String startDate, String endDate);

    @Query(value = "SELECT count(id) from order_transaction where merchant_id = ?1 AND order_status = 2 AND is_paid =?3 AND " +
            "(customer_name LIKE %?2% OR order_number LIKE %?2%) AND (?4 is null or delivery_date between ?4 AND ?5) ", nativeQuery = true)
    Integer getOngoingOrderByMerchantCount(Long merchantId, String search, Integer is_paid, String startDate, String endDate);

    @Query(value = "SELECT * from order_transaction where merchant_id = ?1 AND order_status IN(3,4) AND (customer_name " +
            "LIKE %?4% OR order_number LIKE %?4%) AND (?5 is null or delivery_date between ?5 AND ?6) " +
            "order by delivery_date asc limit ?3 offset ?2", nativeQuery = true)
    List<Order> getSettleOrderByMerchant(Long merchantId, Integer offset, Integer limit, String search, String startDate, String endDate);

    @Query(value = "SELECT count(id) from order_transaction where merchant_id = ?1 AND order_status IN(3,4) AND (customer_name " +
            "LIKE %?2% OR order_number LIKE %?2%) AND (?3 is null or delivery_date between ?3 AND ?4) ", nativeQuery = true)
    Integer getSettleOrderByMerchantCount(Long merchantId, String search, String startDate, String endDate);

    @Query(value = "SELECT * from order_transaction  where (?1 = 0 or merchant_id = ?1) AND order_status IN ?7 AND " +
            "(customer_name LIKE %?4% OR order_number LIKE %?4%) AND (?5 is null or delivery_date between ?5 AND ?6) " +
            "order by created_date desc limit ?3 offset ?2", nativeQuery = true)
    List<Order> getAllOrder(Long merchantId, Integer offset, Integer limit, String search, String startDate, String endDate, List<Integer> status);

    @Query(value = "SELECT count(id) from order_transaction where (?1 = 0 or merchant_id = ?1) AND order_status IN ?5 AND " +
            "(customer_name LIKE %?2% OR order_number LIKE %?2%) AND (?3 is null or delivery_date between ?3 AND ?4)", nativeQuery = true)
    Integer getAllOrderCount(Long merchantId, String search, String startDate, String endDate, List<Integer> status);

    @Query(value = "SELECT * from order_transaction where (?1 = 0 or merchant_id = ?1) AND order_status IN ?5 AND " +
            "(customer_name LIKE %?2% OR order_number LIKE %?2%) AND (?3 is null or delivery_date between ?3 AND ?4) " +
            "AND (?6 = 0 or is_paid = 1) order by delivery_date asc", nativeQuery = true)
    List<Order> getAllOrderList(Long merchantId, String search, String startDate, String endDate, List<Integer> status, Integer isOngoing);

    @Query(value = "SELECT * from order_transaction where id = ?1", nativeQuery = true)
    Order findOneById(Long id);

    Order findOneByOrderNumber(String orderNumber);

    @Query(value = "SELECT b.id as itemId, b.price, a.qty, a.additional_info as info, b.name as itemName , b.is_miscellaneous from order_detail a " +
            "JOIN item b on a.item_id = b.id where a.order_number =?1", nativeQuery = true)
    List<Map> findTrackOneByOrderNumber(String orderNumber);

    @Query(value = "SELECT * FROM order_transaction ot where order_status = 1 or order_status = 2 AND is_paid = 0  ", nativeQuery = true)
    List<Order> findOrderByStatusPending();
}

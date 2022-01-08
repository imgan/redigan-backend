package com.ecommerceapi.ecomerceapi.repositories;

import com.ecommerceapi.ecomerceapi.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    @Query(value = "SELECT * FROM customer WHERE phone =?1 and status =?2" , nativeQuery = true)
    Customer getOneByCustomerNameAndStatusQ(String username, Integer status);

    Customer findOneByPhone(String phone);

    List<Customer> findAllByPhone(String phone);

    @Query(value = "SELECT * FROM customer WHERE (phone LIKE %?3% OR email LIKE %?3% OR customer_name LIKE %?3%) AND is_deleted = 0 order by created_date desc limit ?2 offset ?1" , nativeQuery = true)
    List<Customer> getAllOffsetLimit(Integer offset, Integer limit, String search);

    @Query(value = "SELECT count(id) FROM customer WHERE (phone LIKE %?1% OR email LIKE %?1% OR customer_name LIKE %?1%) AND is_deleted = 0" , nativeQuery = true)
    Integer getAllOffsetLimitCount(String search);

    Customer findOneById(Long id);

}

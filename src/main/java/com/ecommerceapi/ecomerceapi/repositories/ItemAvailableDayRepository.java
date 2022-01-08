package com.ecommerceapi.ecomerceapi.repositories;

import com.ecommerceapi.ecomerceapi.model.Customer;
import com.ecommerceapi.ecomerceapi.model.ItemAvailableDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface ItemAvailableDayRepository extends JpaRepository<ItemAvailableDay, Long> {

    @Query(value = "SELECT day_index FROM item_available_day where item_id =?1 AND is_available = true", nativeQuery = true)
    List<Integer> getAvailableDayByItem(Long itemId);

    ItemAvailableDay findOneByItemIdAndDayIndex(Long id, Integer index);

    void deleteAllByItemId(Long id);
}

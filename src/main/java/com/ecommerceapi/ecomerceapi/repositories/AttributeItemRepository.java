package com.ecommerceapi.ecomerceapi.repositories;

import com.ecommerceapi.ecomerceapi.model.AttributeItem;
import com.ecommerceapi.ecomerceapi.model.AvailableStock;
import com.ecommerceapi.ecomerceapi.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttributeItemRepository extends JpaRepository<AttributeItem, Long> {

    @Query(value = "SELECT * from attribute_item a" +
            " where a.item_id = ?1" +
            " AND a.enabled = 1 ", nativeQuery = true)
    List<AttributeItem> findAllByItemIdAndEnabledAtt(Long id, Boolean enabled);

    AttributeItem findOneByIdAndItemId(Long id, Long itemId);

    AttributeItem findOneById(Long id);
}

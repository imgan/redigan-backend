package com.ecommerceapi.ecomerceapi.repositories;

import com.ecommerceapi.ecomerceapi.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query(value = "SELECT COUNT(id) FROM item WHERE merchant_id = ?1 and enabled = 1", nativeQuery = true)
    Integer countAllItemByMerchant(Long id);

    @Query(value = "SELECT price FROM item  where id  = ?1 and enabled = 1" , nativeQuery = true)
    Integer findPriceItemById(Long item_id);

    Item findOneByIdAndMerchantId(Long id, Long merchantId);

    @Query(value = "SELECT * FROM item WHERE merchant_id = ?1 AND name LIKE %?2% AND enabled = 1 AND is_miscellaneous = 0 ORDER BY name ASC limit ?3 offset ?4 " ,
            nativeQuery = true)
    List<Item> findAllOffsetLimitByMerchantIdNon(Long id, String search, Integer limit, Integer offset);

    @Query(value = "SELECT count(id) FROM item WHERE merchant_id = ?1 AND name LIKE %?2% AND enabled = 1 AND is_miscellaneous = 0 ORDER BY name ASC",
            nativeQuery = true)
    Integer findAllOffsetLimitByMerchantIdNonCount(Long id, String search);

    @Query(value = "SELECT * FROM item WHERE (?1 = 0 or merchant_id = ?1) AND name LIKE %?2% AND enabled = 1 limit ?3 offset ?4 " ,
            nativeQuery = true)
    List<Item> findAllOffsetLimitByMerchantId(Long id, String search, Integer limit, Integer offset);

    @Query(value = "SELECT count(id) FROM item WHERE (?1 = 0 or merchant_id = ?1) AND name LIKE %?2% AND enabled = 1 ORDER BY is_miscellaneous ASC" ,
            nativeQuery = true)
    Integer findAllCountByMerchantId(Long id, String search);

    @Query(value = "SELECT * FROM item WHERE merchant_id = ?1 AND name LIKE %?2% AND enabled = 1 AND is_miscellaneous = 1 ORDER BY name ASC limit ?3 offset ?4 " ,
            nativeQuery = true)
    List<Item> findAllOffsetLimitByMerchantIdMisc(Long id, String search, Integer limit, Integer offset);

    @Query(value = "SELECT count(id) FROM item WHERE merchant_id = ?1 AND name LIKE %?2% AND enabled = 1 AND is_miscellaneous = 1 ORDER BY name ASC",
            nativeQuery = true)
    Integer findAllOffsetLimitByMerchantIdMiscCount(Long id, String search);

    @Query(value = "SELECT * FROM item WHERE (?1 = 0 or merchant_id = ?1) AND name LIKE %?2% AND enabled = 1 AND is_miscellaneous = ?5 ORDER BY name ASC limit ?3 offset ?4 " ,
            nativeQuery = true)
    List<Item> findAllOffsetLimitAdmin(Long id, String search, Integer limit, Integer offset, Integer misc);

    @Query(value = "SELECT count(id) FROM item WHERE (?1 = 0 or merchant_id = ?1) AND name LIKE %?2% AND enabled = 1 AND is_miscellaneous = ?3 ORDER BY name ASC",
            nativeQuery = true)
    Integer findAllOffsetLimitAdminCount(Long id, String search, Integer misc);

    Item findOneById(Long id);

    @Query(value = "SELECT * FROM item WHERE id IN (?1)",
            nativeQuery = true)
    List<Item> findAllItemIn(List<Integer> listItemId);

    List<Item> findAllByEnabled(Boolean status);

    Item findOneByNameAndTypeAndCreatedDateAndCreatedBy(String name, Short type, Date createdDate, String createdBy);
}

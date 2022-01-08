package com.ecommerceapi.ecomerceapi.services.impl;

import com.ecommerceapi.ecomerceapi.dto.params.FileNameDTO;

import com.ecommerceapi.ecomerceapi.dto.request.AttributeItem.AttributeItemDTO;
import com.ecommerceapi.ecomerceapi.dto.request.Item.*;
import com.ecommerceapi.ecomerceapi.dto.request.AttributeItem.AttributeCreateDTO;
import com.ecommerceapi.ecomerceapi.dto.request.AttributeItem.AttributeUpdateDTO;
import com.ecommerceapi.ecomerceapi.dto.request.FilterListRequestDTO;
import com.ecommerceapi.ecomerceapi.dto.response.Item.ItemResImportDTO;
import com.ecommerceapi.ecomerceapi.dto.response.Item.ItemResponseDTO;
import com.ecommerceapi.ecomerceapi.dto.response.ResponseListDTO;
import com.ecommerceapi.ecomerceapi.exception.ResultNotFoundException;
import com.ecommerceapi.ecomerceapi.exception.ResultServiceException;
import com.ecommerceapi.ecomerceapi.helpers.ExcelHelper;
import com.ecommerceapi.ecomerceapi.model.*;
import com.ecommerceapi.ecomerceapi.filters.AuthFilter;
import com.ecommerceapi.ecomerceapi.repositories.AttributeItemRepository;
import com.ecommerceapi.ecomerceapi.repositories.ItemAvailableDayRepository;
import com.ecommerceapi.ecomerceapi.repositories.ItemRepository;
import com.ecommerceapi.ecomerceapi.repositories.MerchantRepository;
import com.ecommerceapi.ecomerceapi.services.AmazonS3Service;
import com.ecommerceapi.ecomerceapi.services.AttributeItemService;
import com.ecommerceapi.ecomerceapi.services.ItemServices;
import com.ecommerceapi.ecomerceapi.util.ConstantUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.ValidationException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class ItemServiceImplements extends BaseServices implements ItemServices {
    @Autowired
    ItemRepository itemRepository;

    @Autowired
    MerchantRepository merchantRepository;

    @Autowired
    ItemAvailableDayRepository itemAvailableDayRepository;

    @Autowired
    AttributeItemRepository attributeItemRepository;

    @Autowired
    AuthFilter authFilter;

    @Autowired
    AttributeItemService attributeItemService;

    @Autowired
    private AmazonS3Service amazonS3Service;

    @Value("${amazonProperties.endpointUrl}")
    private String endpointUrl;

    @Value("${amazonProperties.bucketName}")
    private String bucketName;

    @Value("${app.max.sizeImage}")
    private Integer maximumSize;

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @PersistenceContext
    private EntityManager entityManager;

    /** Create Item */
    @Override
    @Transactional
    public ItemResponseDTO itemCreate(ItemFormRequestDTO itemFormRequestDTO, String token) {

        /** Initialize */
        List<String> typeList = new ArrayList<>( List.of("jpg", "jpeg", "png") );
        ItemResponseDTO itemResponseDTO = new ItemResponseDTO();
        Item item = new Item();
        Merchant merchant;

        if(itemFormRequestDTO.getUserType().compareTo("admin") == 0) {
            /** Check Admin Exist */
            Admin admin = authFilter.getAdminFromToken(token);
            if (admin == null) throw new ResultNotFoundException("Admin is not found");
            merchant = merchantRepository.findOneByUsername(itemFormRequestDTO.getMerchant());
        } else {
            /** Check Merchant Exist */
            merchant = authFilter.getMerchantFromToken(token);
        }
        if (merchant == null) throw new ResultNotFoundException("Merchant is not found");

        try {
            /** Check Image Not Empty */
            MultipartFile image = itemFormRequestDTO.getImage();
            if (image != null) {
                if (!image.isEmpty()) {
                    /** Check Image Size */
                    if (image.getSize() > maximumSize) {
                        throw new ValidationException("Image size is too big. Maximum size is 5 MB");
                    }

                    /** Check Image Extension */
                    String typeFile = image.getOriginalFilename().substring(image.getOriginalFilename().lastIndexOf(".") + 1);
                    if (!typeList.contains(typeFile.toLowerCase())) {
                        throw new ValidationException("Image extension must be jpg, jpeg, png");
                    }

                    /** AWS S3 Upload */
                    FileNameDTO fileNameDTO = new FileNameDTO();
                    fileNameDTO.setFileModel("newitem");
                    fileNameDTO.setId(merchant.getId());
                    fileNameDTO.setImage(image);
                    String fileUrl = amazonS3Service.uploadFile(fileNameDTO);
                    logger.info("url: " + fileUrl);
                    String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
                    item.setPicture(fileName);
                }
            }

            /** Save Item */
            item.setName(itemFormRequestDTO.getName());
            item.setPrice(itemFormRequestDTO.getPrice() != null ? itemFormRequestDTO.getPrice() : 0L);
            item.setMaxItem(itemFormRequestDTO.getMaxItemDay() != null ? itemFormRequestDTO.getMaxItemDay() : 0);
            item.setDescription(itemFormRequestDTO.getDescription());
            item.setAssemblyTime(itemFormRequestDTO.getAssemblyTime() != null ? itemFormRequestDTO.getAssemblyTime() : 0);
            item.setMerchant(merchant);
            item.setEnabled(true);
            item.setOutStock(itemFormRequestDTO.getOutStock() != null ? itemFormRequestDTO.getOutStock() : true);
            item.setType(itemFormRequestDTO.getType() != null ? itemFormRequestDTO.getType() : 1);
            item.setMiscellaneous(itemFormRequestDTO.getMiscellaneous() != null ? itemFormRequestDTO.getMiscellaneous() : false);
            item.setCreatedBy(merchant.getUsername());
            item.setCreatedDate(new Date());
            Item itemRepo = itemRepository.saveAndFlush(item);

            /** Check Item Exist */
            if (itemRepo == null) throw new ResultNotFoundException("Item is not found");

            /** Check Item Type ( Available or Specific ) */
            List<Integer> availableDay = new ArrayList<Integer>();
            if (itemRepo.getType() > 1) {
                for (int i = 1; i <= 7; i++) {
                    /** Save Item Available Day */
                    ItemAvailableDay itemAvailableDay = new ItemAvailableDay();
                    itemAvailableDay.setItemId(itemRepo.getId());
                    itemAvailableDay.setDayIndex(i);
                    itemAvailableDay.setCreatedAt(new Date());
                    itemAvailableDay.setAvailable(itemFormRequestDTO.getAvailableDay().contains(i));
                    itemAvailableDayRepository.save(itemAvailableDay);
                }
                availableDay = itemFormRequestDTO.getAvailableDay();
            }
            for(Map map : itemFormRequestDTO.getAttributeItems()){
                /** Save Attribute Item */
                AttributeCreateDTO attributeCreateDTO = new AttributeCreateDTO();
                attributeCreateDTO.setItemId(itemRepo.getId());
                attributeCreateDTO.setName(map.get("name").toString());
                attributeCreateDTO.setQty(Long.valueOf(map.get("qty").toString()));
                attributeCreateDTO.setPrice(Long.valueOf(map.get("price").toString()));
                attributeItemService.createAttributeItem(attributeCreateDTO);
            }

            /** Item Object Result */
            String picture = item.getPicture() != null ? endpointUrl + "/" + bucketName + "/" + item.getPicture() : "";
            itemResponseDTO.setId(itemRepo.getId());
            itemResponseDTO.setName(itemRepo.getName());
            itemResponseDTO.setPrice(itemRepo.getPrice());
            itemResponseDTO.setMaxItemDay(itemRepo.getMaxItem());
            itemResponseDTO.setDescription(itemRepo.getDescription());
            itemResponseDTO.setAssemblyTime(itemRepo.getAssemblyTime());
            itemResponseDTO.setImage(picture);
            itemResponseDTO.setEnabled(itemRepo.getEnabled());
            itemResponseDTO.setOutStock(itemRepo.getOutStock());
            itemResponseDTO.setType(itemRepo.getType());
            itemResponseDTO.setMiscellaneous(itemRepo.getMiscellaneous());
            itemResponseDTO.setAvailableDay(availableDay);

            return itemResponseDTO;
        } catch (Exception e) {
            logger.error("[FATAL]" + e);
            throw new ResultServiceException(e.getMessage());
        }
    }

    /** Update Item */
    @Override
    @Transactional
    public ItemResponseDTO itemUpdate(ItemFormRequestDTO itemFormRequestDTO, String token) {

        /** Initialize */
        List<String> typeList = new ArrayList<>( List.of("jpg", "jpeg", "png") );
        ItemResponseDTO itemResponseDTO = new ItemResponseDTO();
        Merchant merchant;
        Item item;

        if(itemFormRequestDTO.getUserType().compareTo("admin") == 0) {
            /** Check Admin Exist */
            Admin admin = authFilter.getAdminFromToken(token);
            if (admin == null) throw new ResultNotFoundException("Admin is not found");
            merchant = new Merchant();
            merchant.setUsername("admin-" + admin.getUsername());
            /** Check Item Exist */
            item = itemRepository.findOneById(itemFormRequestDTO.getId());
        } else {
            /** Check Merchant Exist */
            merchant = authFilter.getMerchantFromToken(token);
            if (merchant == null) throw new ResultNotFoundException("Merchant is not found");
            /** Check Item Exist */
            item = itemRepository.findOneByIdAndMerchantId(itemFormRequestDTO.getId(), merchant.getId());
        }
        if (item == null) throw new ResultNotFoundException("Item is not found");

        try {
            /** Check Image Not Empty */
            MultipartFile image = itemFormRequestDTO.getImage();
            if (image != null) {
                if (!image.isEmpty()) {
                    /** Check Image Size */
                    if (image.getSize() > maximumSize) {
                        throw new ValidationException("Image size is too big. Maximum size is 5 MB");
                    }

                    /** Check Image Extension */
                    String typeFile = image.getOriginalFilename().substring(image.getOriginalFilename().lastIndexOf(".") + 1);
                    if (!typeList.contains(typeFile.toLowerCase())) {
                        throw new ValidationException("Image extension must be jpg, jpeg, png");
                    }

                    /** AWS S3 Upload */
                    if (item.getPicture() != null) amazonS3Service.deleteFileFromS3Bucket(endpointUrl +
                            "/" + bucketName + "/" + item.getPicture());
                    FileNameDTO fileNameDTO = new FileNameDTO();
                    fileNameDTO.setFileModel("item");
                    fileNameDTO.setId(item.getId());
                    fileNameDTO.setImage(image);
                    String fileUrl = amazonS3Service.uploadFile(fileNameDTO);
                    logger.info("url: " + fileUrl);
                    String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
                    item.setPicture(fileName);
                }
            }

            /** Update Item */
            item.setName(itemFormRequestDTO.getName());
            if (itemFormRequestDTO.getPrice() != null) item.setPrice(itemFormRequestDTO.getPrice());
            if (itemFormRequestDTO.getMaxItemDay() != null) item.setMaxItem(itemFormRequestDTO.getMaxItemDay());
            if (itemFormRequestDTO.getDescription() != null) item.setDescription(itemFormRequestDTO.getDescription());
            if (itemFormRequestDTO.getAssemblyTime() != null) item.setAssemblyTime(itemFormRequestDTO.getAssemblyTime());
            if (itemFormRequestDTO.getOutStock() != null) item.setOutStock(itemFormRequestDTO.getOutStock());
            if (itemFormRequestDTO.getType() != null) item.setType(itemFormRequestDTO.getType());
            if (itemFormRequestDTO.getMiscellaneous() != null) item.setMiscellaneous(itemFormRequestDTO.getMiscellaneous());
            item.setUpdatedBy(merchant.getUsername());
            item.setUpdatedDate(new Date());
            Item itemRepo = itemRepository.saveAndFlush(item);

            /** Check Item Type ( Available or Specific ) */
            List<Integer> availableDay = new ArrayList<Integer>();
            itemAvailableDayRepository.deleteAllByItemId(itemRepo.getId());
            if (itemRepo.getType() > 1) {
                for (int i = 1; i <= 7; i++) {
                    ItemAvailableDay itemAvailableDayRepo = itemAvailableDayRepository.findOneByItemIdAndDayIndex(itemRepo.getId(), i);
                    if (itemAvailableDayRepo == null) {
                        /** Save Item Available Day */
                        ItemAvailableDay itemAvailableDay = new ItemAvailableDay();
                        itemAvailableDay.setItemId(itemRepo.getId());
                        itemAvailableDay.setDayIndex(i);
                        itemAvailableDay.setCreatedAt(new Date());
                        itemAvailableDay.setAvailable(itemFormRequestDTO.getAvailableDay().contains(i));
                        itemAvailableDayRepository.save(itemAvailableDay);
                    } else {
                        itemAvailableDayRepo.setAvailable(itemFormRequestDTO.getAvailableDay().contains(i));
                        itemAvailableDayRepository.save(itemAvailableDayRepo);
                    }
                }
                availableDay = itemFormRequestDTO.getAvailableDay();
            }
            Boolean enabled;
            for(Map map : itemFormRequestDTO.getAttributeItems()){
                /** Save Attribute Item */
                Boolean id = isExistingDataAndStringValue(map.get("id").toString());
                if (Long.valueOf(map.get("id").toString()) == 0){
                    logger.info("insert");
                    AttributeCreateDTO attributeCreateDTO = new AttributeCreateDTO();
                    attributeCreateDTO.setItemId(itemRepo.getId());
                    attributeCreateDTO.setName(map.get("name").toString());
                    attributeCreateDTO.setQty(Long.valueOf(map.get("qty").toString()));
                    attributeCreateDTO.setPrice(Long.valueOf(map.get("price").toString()));
                    attributeItemService.createAttributeItem(attributeCreateDTO);
                } else {
                    logger.info("update");
                    AttributeUpdateDTO attributeUpdateDTO = new AttributeUpdateDTO();
                    attributeUpdateDTO.setId(Long.valueOf(map.get("id").toString()));
                    attributeUpdateDTO.setItemId(itemRepo.getId());
                    attributeUpdateDTO.setName(map.get("name").toString());
                    attributeUpdateDTO.setQty(Long.valueOf(map.get("qty").toString()));
                    attributeUpdateDTO.setPrice(Long.valueOf(map.get("price").toString()));
                    attributeUpdateDTO.setEnabled(Boolean.valueOf(map.get("enabled").toString()));
                    attributeItemService.updateAttributeItem(attributeUpdateDTO);
                }

            }
            List<AttributeItem> attributeItems = attributeItemService.viewByItemId(item.getId());
            List<Map> attributeItemMap = new ArrayList<>();
            for(AttributeItem attributeItem : attributeItems){
                Map attribute = new HashMap();
                /** Maping */
                attribute.put("itemId",item.getId());
                attribute.put("attributeId" , attributeItem.getId());
                attribute.put("price", attributeItem.getPrice());
                attribute.put("name", attributeItem.getName());
                attribute.put("stock", attributeItem.getStock());
                attribute.put("createdAt", attributeItem.getCreatedAt());
                attribute.put("updatedAt", attributeItem.getUpdateAt());
                attribute.put("enabled", attributeItem.getEnabled());
                attributeItemMap.add(attribute);
            }

            /** Item Object Result */
            String picture = item.getPicture() != null ? endpointUrl + "/" + bucketName + "/" + item.getPicture() : "";
            itemResponseDTO.setId(itemRepo.getId());
            itemResponseDTO.setName(itemRepo.getName());
            itemResponseDTO.setMerchantName(itemRepo.getMerchant().getUsername());
            itemResponseDTO.setStoreName(itemRepo.getMerchant().getStoreName());
            itemResponseDTO.setPrice(itemRepo.getPrice());
            itemResponseDTO.setMaxItemDay(itemRepo.getMaxItem());
            itemResponseDTO.setDescription(itemRepo.getDescription());
            itemResponseDTO.setAssemblyTime(itemRepo.getAssemblyTime());
            itemResponseDTO.setImage(picture);
            itemResponseDTO.setEnabled(itemRepo.getEnabled());
            itemResponseDTO.setOutStock(itemRepo.getOutStock());
            itemResponseDTO.setType(itemRepo.getType());
            itemResponseDTO.setMiscellaneous(itemRepo.getMiscellaneous());
            itemResponseDTO.setAvailableDay(availableDay);
            itemResponseDTO.setAttributeItems(attributeItemMap);
            return itemResponseDTO;
        } catch (Exception e) {
            logger.error("[FATAL]" + e);
            throw new ResultServiceException(e.getMessage());
        }
    }

    /** Detail Item */
    @Override
    public ItemResponseDTO itemView(ItemDetailDTO itemDetailDTO, String token) {

        /** Initialize */
        ItemResponseDTO itemResponseDTO = new ItemResponseDTO();
        Item item;

        if(itemDetailDTO.getUserType().compareTo("admin") == 0) {
            /** Check Admin Exist */
            Admin admin = authFilter.getAdminFromToken(token);
            if (admin == null) throw new ResultNotFoundException("Admin is not found");
            /** Check Item Exist */
            item = itemRepository.findOneById(itemDetailDTO.getId());
        } else {
            /** Check Merchant Exist */
            Merchant merchant = authFilter.getMerchantFromToken(token);
            if (merchant == null) throw new ResultNotFoundException("Merchant is not found");
            /** Check Item Exist */
            item = itemRepository.findOneByIdAndMerchantId(itemDetailDTO.getId(), merchant.getId());
        }
        if (item == null) throw new ResultNotFoundException("Item is not found");

        try {
            /** Item Object Result */
            String picture = item.getPicture() != null ? endpointUrl + "/" + bucketName + "/" + item.getPicture() : "";
            List<Integer> availableDay = item.getType() > 1 ? item.getDayIndexList() : new ArrayList<Integer>();
            List<AttributeItem> attributeItems = attributeItemService.viewByItemId(item.getId());
            List<Map> attributeItemMap = new ArrayList<>();
            for(AttributeItem attributeItem : attributeItems){
                Map attribute = new HashMap();
                /** Maping */
                attribute.put("itemId",item.getId());
                attribute.put("attributeId" , attributeItem.getId());
                attribute.put("price", attributeItem.getPrice());
                attribute.put("name", attributeItem.getName());
                attribute.put("qty", attributeItem.getStock());
                attribute.put("createdAt", attributeItem.getCreatedAt());
                attribute.put("updatedAt", attributeItem.getUpdateAt());
                attribute.put("enabled", attributeItem.getEnabled());
                attributeItemMap.add(attribute);
            }


            itemResponseDTO.setId(item.getId());
            itemResponseDTO.setName(item.getName());
            itemResponseDTO.setPrice(item.getPrice());
            itemResponseDTO.setStoreName(item.getMerchant().getStoreName());
            itemResponseDTO.setMerchantName(item.getMerchant().getUsername());
            itemResponseDTO.setMaxItemDay(item.getMaxItem());
            itemResponseDTO.setDescription(item.getDescription());
            itemResponseDTO.setAssemblyTime(item.getAssemblyTime());
            itemResponseDTO.setImage(picture);
            itemResponseDTO.setEnabled(item.getEnabled());
            itemResponseDTO.setOutStock(item.getOutStock());
            itemResponseDTO.setType(item.getType());
            itemResponseDTO.setMiscellaneous(item.getMiscellaneous());
            itemResponseDTO.setAvailableDay(availableDay);
            itemResponseDTO.setAttributeItems(attributeItemMap);
            return itemResponseDTO;
        } catch (Exception e) {
            logger.error("[FATAL]" + e);
            throw new ResultServiceException(e.getMessage());
        }
    }

    /** List item */
    @Override
    public List<ItemResponseDTO> itemList(FilterListRequestDTO filterListRequestDTO, String token) {

        /** Initialize */
        List<ItemResponseDTO> listItemResponseDTO = new ArrayList<>();
        Merchant merchant;

        if(filterListRequestDTO.getUserType().compareTo("admin") == 0) {
            /** Check Admin Exist */
            Admin admin = authFilter.getAdminFromToken(token);
            if (admin == null) throw new ResultNotFoundException("Admin is not found");
            if(isExistingDataAndStringValue(filterListRequestDTO.getMerchant())) {
                merchant = merchantRepository.findOneByUsername(filterListRequestDTO.getMerchant());
            } else {
                merchant = new Merchant();
                merchant.setId(0L);
            }
        } else {
            /** Check Merchant Exist */
            merchant = authFilter.getMerchantFromToken(token);
        }
        if (merchant == null) throw new ResultNotFoundException("Merchant is not found");

        try {
            /** Find Items by Merchant */
            List<Item> listItem = itemRepository.findAllOffsetLimitByMerchantId(merchant.getId(), filterListRequestDTO.getSearch(),
                    filterListRequestDTO.getLimit(), filterListRequestDTO.getOffset());

            /** Item List Result */
            for (Item item : listItem) {
                ItemResponseDTO itemResponseDTO = new ItemResponseDTO();
                String picture = item.getPicture() != null ? endpointUrl + "/" + bucketName + "/" + item.getPicture() : "";
                List<Integer> availableDay = item.getType() > 1 ? item.getDayIndexList() : new ArrayList<Integer>();
                List<AttributeItem> attributeItems = attributeItemService.viewByItemId(item.getId());
                /** Initialize */
                List<Map> listAttr = new ArrayList<>();
                for (AttributeItem attributeItem : attributeItems) {
                    Map attrItem = new HashMap();
                    attrItem.put("id",attributeItem.getId());
                    attrItem.put("name",attributeItem.getName());
                    attrItem.put("stock",attributeItem.getStock());
                    attrItem.put("price", attributeItem.getPrice());
                    listAttr.add(attrItem);
                }
                itemResponseDTO.setAttributeItems(listAttr);
                itemResponseDTO.setId(item.getId());
                itemResponseDTO.setName(item.getName());
                itemResponseDTO.setPrice(item.getPrice());
                itemResponseDTO.setMaxItemDay(item.getMaxItem());
                itemResponseDTO.setDescription(item.getDescription());
                itemResponseDTO.setAssemblyTime(item.getAssemblyTime());
                itemResponseDTO.setImage(picture);
                itemResponseDTO.setMerchantName(item.getMerchant().getUsername());
                itemResponseDTO.setStoreName(item.getMerchant().getStoreName());
                itemResponseDTO.setEnabled(item.getEnabled());
                itemResponseDTO.setOutStock(item.getOutStock());
                itemResponseDTO.setType(item.getType());
                itemResponseDTO.setMiscellaneous(item.getMiscellaneous());
                itemResponseDTO.setAvailableDay(availableDay);
                listItemResponseDTO.add(itemResponseDTO);
            }

            return listItemResponseDTO;
        } catch (Exception e) {
            logger.error("[FATAL]" + e);
            throw new ResultServiceException(e.getMessage());
        }
    }

    /** Delete Item */
    @Override
    public ItemResponseDTO itemDelete(ItemDetailDTO itemDetailDTO, String token) {

        /** Initialize */
        ItemResponseDTO itemResponseDTO = new ItemResponseDTO();
        Item item;

        if(itemDetailDTO.getUserType().compareTo("admin") == 0) {
            /** Check Admin Exist */
            Admin admin = authFilter.getAdminFromToken(token);
            if (admin == null) throw new ResultNotFoundException("Admin is not found");
            /** Check Item Exist */
            item = itemRepository.findOneById(itemDetailDTO.getId());
        } else {
            /** Check Merchant Exist */
            Merchant merchant = authFilter.getMerchantFromToken(token);
            if (merchant == null) throw new ResultNotFoundException("Merchant is not found");
            /** Check Item Exist */
            item = itemRepository.findOneByIdAndMerchantId(itemDetailDTO.getId(), merchant.getId());
        }
        if (item == null) throw new ResultNotFoundException("Item is not found");

        try {
            /** Item Object Result */
            String picture = item.getPicture() != null ? endpointUrl + "/" + bucketName + "/" + item.getPicture() : "";
            List<Integer> availableDay = item.getType() > 1 ? item.getDayIndexList() : new ArrayList<Integer>();

            itemResponseDTO.setId(item.getId());
            itemResponseDTO.setName(item.getName());
            itemResponseDTO.setPrice(item.getPrice());
            itemResponseDTO.setMaxItemDay(item.getMaxItem());
            itemResponseDTO.setDescription(item.getDescription());
            itemResponseDTO.setAssemblyTime(item.getAssemblyTime());
            itemResponseDTO.setImage(picture);
            itemResponseDTO.setEnabled(false);
            itemResponseDTO.setOutStock(item.getOutStock());
            itemResponseDTO.setType(item.getType());
            itemResponseDTO.setMiscellaneous(item.getMiscellaneous());
            itemResponseDTO.setAvailableDay(availableDay);

            /** Delete Item with Change Status */
            item.setEnabled(false);
            itemRepository.save(item);

            return itemResponseDTO;
        } catch (Exception e) {
            logger.error("[FATAL]" + e);
            throw new ResultServiceException(e.getMessage());
        }
    }

    /** Total Page item */
    @Override
    public Map totalPage(FilterListRequestDTO filterListRequestDTO, String token) {

        /** Initialize */
        Map data = new HashMap();
        Merchant merchant;

        if(filterListRequestDTO.getUserType().compareTo("admin") == 0) {
            /** Check Admin Exist */
            Admin admin = authFilter.getAdminFromToken(token);
            if (admin == null) throw new ResultNotFoundException("Admin is not found");
            if(isExistingDataAndStringValue(filterListRequestDTO.getMerchant())) {
                merchant = merchantRepository.findOneByUsername(filterListRequestDTO.getMerchant());
            } else {
                merchant = new Merchant();
                merchant.setId(0L);
            }
        } else {
            /** Check Merchant Exist */
            merchant = authFilter.getMerchantFromToken(token);
        }
        if (merchant == null) throw new ResultNotFoundException("Merchant is not found");

        try {
            /** Find Total Items by Merchant */
            Integer countListItem = itemRepository.findAllCountByMerchantId(merchant.getId(), filterListRequestDTO.getSearch());

            data.put("limit", filterListRequestDTO.getLimit());
            data.put("total", countListItem);
            data.put("totalPage", (int) Math.ceil((double) countListItem / filterListRequestDTO.getLimit()));

            return data;
        } catch (Exception e) {
            logger.error("[FATAL]" + e);
            throw new ResultServiceException(e.getMessage());
        }
    }

    @Override @Transactional
    public Boolean itemImport(MultipartFile file, Merchant merchant) {
        try {
            ItemResImportDTO itemResImportDTO = ExcelHelper.excelToTutorials(file.getInputStream(), merchant);
            List<Item> items = itemResImportDTO.getItems();
            List<ItemDayImportDTO> itemDayImportDtos = itemResImportDTO.getItemDayImports();
            List<ItemAvailableDay> itemAvailableDays = new ArrayList<>();

            List<Item> itemRepo = itemRepository.saveAllAndFlush(items);
            for (ItemDayImportDTO itemDayImportDTO : itemDayImportDtos) {
                SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                List<Item> _items = itemRepo.stream().filter(i -> (i.getName().equals(itemDayImportDTO.getName())
                        && i.getType() == 2 && i.getCreatedBy().equals("upload")
                        && date.format(i.getCreatedDate()).equals(date.format(itemDayImportDTO.getCreatedAt()))))
                        .collect(Collectors.toList());
                if (_items.size() > 0) {
                    Item item = _items.get(0);
                    if (item != null) {
                        for (int i = 1; i <= 7; i++) {
                            ItemAvailableDay itemAvailableDay = new ItemAvailableDay();
                            itemAvailableDay.setItemId(item.getId());
                            itemAvailableDay.setDayIndex(i);
                            itemAvailableDay.setCreatedAt(new Date());
                            itemAvailableDay.setAvailable(itemDayImportDTO.getItemDayList().contains(i));
                            itemAvailableDays.add(itemAvailableDay);
                        }
                    }
                }
            }
            if (itemAvailableDays.size() > 0) {
                itemAvailableDayRepository.saveAll(itemAvailableDays);
            }
            return true;
        } catch (IOException e) {
            throw new ResultServiceException("failed upload excel");
        }
    }

    /** List Item Merchant for Admin */
    @Override
    public ResponseListDTO itemsList(ItemFilterListDTO itemFilterListDTO, String token) {
        ResponseListDTO responseListDTO = new ResponseListDTO();
        List<Map> items = new ArrayList<>();
        Map detail = new HashMap();
        Merchant merchant;

        Admin admin = authFilter.getAdminFromToken(token);
        if (admin == null) throw new ResultNotFoundException("Admin is not found");
        if(isExistingDataAndStringValue(itemFilterListDTO.getUsername())) {
            merchant = merchantRepository.findOneByUsername(itemFilterListDTO.getUsername());
        } else {
            merchant = new Merchant();
            merchant.setId(0L);
        }
        if (merchant == null) throw new ResultNotFoundException("Merchant is not found");
        try {
            List<Item> listData = itemRepository.findAllOffsetLimitAdmin(merchant.getId(), itemFilterListDTO.getSearch(),
                    itemFilterListDTO.getLimit(), itemFilterListDTO.getOffset(), itemFilterListDTO.getMiscellaneous());
            Integer countListData = itemRepository.findAllOffsetLimitAdminCount(merchant.getId(), itemFilterListDTO.getSearch(),
                    itemFilterListDTO.getMiscellaneous());

            /** Item List Result */
            for (Item item : listData) {
                Map itemMap = new HashMap<>();
                String picture = item.getPicture() != null ? endpointUrl + "/" + bucketName + "/" + item.getPicture() : "";
                List<Integer> availableDay = item.getType() > 1 ? item.getDayIndexList() : new ArrayList<Integer>();
                List<AttributeItem> attributeItems = attributeItemService.viewByItemId(item.getId());
                List<Map> listAttr = new ArrayList<>();
                for (AttributeItem attributeItem : attributeItems) {
                    Map attrItem = new HashMap();
                    attrItem.put("id",attributeItem.getId());
                    attrItem.put("name",attributeItem.getName());
                    attrItem.put("stock",attributeItem.getStock());
                    attrItem.put("price", attributeItem.getPrice());
                    listAttr.add(attrItem);
                }
                itemMap.put("id",item.getId());
                itemMap.put("itemName",item.getName());
                itemMap.put("price",item.getPrice());
                itemMap.put("maxItemPerDay",item.getMaxItem());
                itemMap.put("description",item.getDescription());
                itemMap.put("merchantName",item.getMerchant().getUsername());
                itemMap.put("storeName",item.getMerchant().getStoreName());
                itemMap.put("assemblyTime",item.getAssemblyTime());
                itemMap.put("image",picture);
                itemMap.put("outStock",item.getOutStock());
                itemMap.put("type",item.getType());
                itemMap.put("availableDay",availableDay);
                itemMap.put("attributeItem",listAttr);
                items.add(itemMap);
            }
            detail.put("limit", itemFilterListDTO.getLimit());
            detail.put("total", countListData);
            detail.put("totalPage", (int) Math.ceil((double) countListData / itemFilterListDTO.getLimit()));

            responseListDTO.setCode(ConstantUtil.STATUS_SUCCESS);
            responseListDTO.setInfo(ConstantUtil.MESSAGE_SUCCESS);
            responseListDTO.setDetail(detail);
            responseListDTO.setData(items);
            return responseListDTO;
        } catch (Exception e) {
            logger.error("[FATAL]" + e);
            throw new ResultServiceException(e.getMessage());
        }
    }

    @Override @Transactional
    public Boolean itemAttributeImport(MultipartFile file) {
        try {
            ItemResImportDTO itemResImportDTO = ExcelHelper.excelItemAttribute(file.getInputStream());
            List<AttributeItemDTO> attributeItemDtos = itemResImportDTO.getAttributeItems();

            List<Item> items = itemRepository.findAllById(itemResImportDTO.getItemIds());
            List<AttributeItem> attributeItems = new ArrayList<>();

            for (AttributeItemDTO attributeItemDTO : attributeItemDtos) {
                List<Item> _items = items.stream().filter(i -> i.getId().equals(attributeItemDTO.getItemId()))
                        .collect(Collectors.toList());
                if (_items.size() > 0) {
                    Item item = _items.get(0);
                    if (item != null) {
                        AttributeItem attributeItem = new AttributeItem();
                        attributeItem.setItem(item);
                        attributeItem.setName(attributeItemDTO.getName());
                        attributeItem.setPrice(attributeItemDTO.getPrice());
                        attributeItem.setStock(attributeItemDTO.getStock());
                        attributeItem.setEnabled(true);
                        attributeItem.setCreatedAt(new Date());
                        attributeItems.add(attributeItem);
                    }
                }
            }

            if (attributeItems.size() > 0) {
                attributeItemRepository.saveAll(attributeItems);
            }
            return true;
        } catch (IOException e) {
            throw new ResultServiceException("failed upload excel");
        }
    }

}

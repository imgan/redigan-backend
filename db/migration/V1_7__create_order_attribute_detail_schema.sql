CREATE TABLE IF NOT EXISTS `order_attribute_detail` (
  `id` bigint(20) NOT NULL,
  `additional_info` varchar(255) DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `order_number` varchar(255) NOT NULL,
  `attribute_id` bigint(20) DEFAULT NULL,
  `item_id` bigint(20) DEFAULT NULL,
  `sequence` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
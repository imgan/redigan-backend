CREATE TABLE IF NOT EXISTS `order_detail` (
  `id` bigint(20) NOT NULL,
  `created_date` datetime DEFAULT NULL,
  `item_id` bigint(20) NOT NULL,
  `order_number` varchar(255) NOT NULL,
  `qty` bigint(20) NOT NULL,
  `additional_info` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
CREATE TABLE IF NOT EXISTS `item` (
  `id` bigint(20) NOT NULL,
  `assembly_time` int(11) NOT NULL DEFAULT 0,
  `created_by` varchar(100) DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `enabled` bit(1) NOT NULL DEFAULT b'1',
  `is_miscellaneous` bit(1) DEFAULT b'0',
  `max_item` int(11) NOT NULL DEFAULT 0,
  `merchant_id` bigint(20) NOT NULL,
  `name` varchar(100) DEFAULT NULL,
  `out_stock` bit(1) NOT NULL DEFAULT b'1',
  `picture` varchar(255) DEFAULT NULL,
  `price` bigint(20) NOT NULL DEFAULT 0,
  `type_order` smallint(6) NOT NULL DEFAULT 1,
  `updated_by` varchar(100) DEFAULT NULL,
  `updated_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
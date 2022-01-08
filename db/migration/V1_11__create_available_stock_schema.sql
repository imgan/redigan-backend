CREATE TABLE IF NOT EXISTS `available_stock` (
  `id` bigint(20) NOT NULL,
  `date` datetime NOT NULL,
  `order_number` varchar(255) NOT NULL,
  `qty` int(11) NOT NULL,
  `item_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
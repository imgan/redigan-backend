CREATE TABLE IF NOT EXISTS `remain_stock` (
  `id` bigint(20) NOT NULL,
  `date` datetime NOT NULL,
  `stock_qty` int(11) NOT NULL,
  `item_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
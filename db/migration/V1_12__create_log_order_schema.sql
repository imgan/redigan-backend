CREATE TABLE IF NOT EXISTS `log_order` (
  `id` bigint(20) NOT NULL,
  `created_date` datetime DEFAULT NULL,
  `data` text DEFAULT NULL,
  `order_number` varchar(255) DEFAULT NULL,
  `status` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
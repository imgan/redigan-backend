CREATE TABLE IF NOT EXISTS `merchant_token` (
  `id` bigint(20) NOT NULL,
  `created_at` datetime DEFAULT NULL,
  `is_use` bit(1) DEFAULT NULL,
  `merchant_id` bigint(20) DEFAULT NULL,
  `token` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
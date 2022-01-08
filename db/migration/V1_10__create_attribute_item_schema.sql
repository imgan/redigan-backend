CREATE TABLE IF NOT EXISTS `attribute_item` (
  `id` bigint(20) NOT NULL,
  `name` varchar(255) NOT NULL,
  `price` bigint(20) NOT NULL,
  `stock` bigint(20) NOT NULL,
  `created_at` datetime DEFAULT NULL,
  `enabled` bit(1) DEFAULT NULL,
  `update_at` datetime DEFAULT NULL,
  `item_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
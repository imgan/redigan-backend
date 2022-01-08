CREATE TABLE IF NOT EXISTS `item_available_day` (
  `id` bigint(20) NOT NULL,
  `day_index` int(11) DEFAULT NULL,
  `is_available` bit(1) DEFAULT b'0',
  `item_id` bigint(20) NOT NULL,
  `created_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
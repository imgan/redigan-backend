CREATE TABLE IF NOT EXISTS `role` (
  `id` int(11) NOT NULL,
  `created_date` datetime DEFAULT NULL,
  `name` varchar(50) DEFAULT NULL,
  `status` bit(1) DEFAULT NULL,
  `updated_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
CREATE TABLE IF NOT EXISTS `admin` (
  `id` bigint(20) NOT NULL,
  `created_by` varchar(100) DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `officer_name` varchar(100) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `role_id` int(11) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `updated_by` varchar(100) DEFAULT NULL,
  `updated_date` datetime DEFAULT NULL,
  `username` varchar(100) DEFAULT NULL,
  `picture` varchar(255) DEFAULT NULL,
  `token_update` text DEFAULT NULL,
  `pin` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
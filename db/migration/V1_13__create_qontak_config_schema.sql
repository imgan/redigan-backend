CREATE TABLE IF NOT EXISTS `qontak_config` (
  `id` bigint(20) NOT NULL,
  `category` varchar(255) NOT NULL,
  `channel_integration_id` varchar(255) NOT NULL,
  `message_template_id` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `token` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
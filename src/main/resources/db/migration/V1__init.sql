-- 2026-10-11 운영 DB(맥미니 mysql8의 upbrella) 스키마. Flyway 도입 전까지 손으로 바꾼 내용이 모두 들어 있다.
-- 이미 테이블이 있는 DB는 이 버전을 기준(baseline)으로 삼고 건너뛴다. 빈 DB에서만 실행된다.

CREATE TABLE `black_list` (
  `id` int NOT NULL AUTO_INCREMENT,
  `social_id` bigint DEFAULT NULL,
  `blocked_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `business_hour` (
  `id` int NOT NULL AUTO_INCREMENT,
  `store_meta_id` int DEFAULT NULL,
  `date` varchar(45) DEFAULT NULL,
  `open_at` time DEFAULT NULL,
  `close_at` time DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `classification` (
  `id` int NOT NULL AUTO_INCREMENT,
  `type` varchar(45) DEFAULT NULL,
  `name` varchar(45) DEFAULT NULL,
  `latitude` double DEFAULT NULL,
  `longitude` double DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `condition_report` (
  `id` int NOT NULL AUTO_INCREMENT,
  `content` varchar(401) DEFAULT NULL,
  `etc` varchar(255) DEFAULT NULL,
  `history_id` int DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `history` (
  `id` int NOT NULL AUTO_INCREMENT,
  `etc` varchar(255) DEFAULT NULL,
  `rented_at` datetime DEFAULT NULL,
  `returned_at` datetime DEFAULT NULL,
  `rent_store_meta_id` int DEFAULT NULL,
  `return_store_meta_id` int DEFAULT NULL,
  `umbrella_id` int DEFAULT NULL,
  `user_id` int DEFAULT NULL,
  `bank` varchar(45) DEFAULT NULL,
  `account_number` varchar(45) DEFAULT NULL,
  `refunded_at` datetime DEFAULT NULL,
  `refunded_by` int DEFAULT NULL,
  `paid_at` datetime DEFAULT NULL,
  `paid_by` int DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `improvement_report` (
  `id` int NOT NULL AUTO_INCREMENT,
  `content` varchar(401) DEFAULT NULL,
  `etc` varchar(255) DEFAULT NULL,
  `history_id` int DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `locker` (
  `id` int NOT NULL AUTO_INCREMENT,
  `count` int DEFAULT NULL,
  `secret_key` varchar(2000) DEFAULT NULL,
  `last_access` timestamp NULL DEFAULT NULL,
  `store_meta_id` int DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `store_detail` (
  `id` int NOT NULL AUTO_INCREMENT,
  `address` varchar(255) DEFAULT NULL,
  `address_detail` varchar(255) DEFAULT NULL,
  `contact_info` varchar(255) DEFAULT NULL,
  `content` varchar(255) DEFAULT NULL,
  `insta_url` varchar(255) DEFAULT NULL,
  `working_hour` varchar(255) DEFAULT NULL,
  `store_meta_id` int DEFAULT NULL,
  `umbrella_location` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `store_image` (
  `id` int NOT NULL AUTO_INCREMENT,
  `image_url` varchar(2000) DEFAULT NULL,
  `store_detail_id` int DEFAULT NULL,
  `image_urls` text COMMENT '다양한 포맷 및 크기의 이미지 URL (JSON)',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `store_meta` (
  `id` int NOT NULL AUTO_INCREMENT,
  `classification_id` bigint DEFAULT NULL,
  `sub_classification_id` bigint DEFAULT NULL,
  `activated` tinyint DEFAULT NULL,
  `deleted` tinyint DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `category` varchar(255) DEFAULT NULL,
  `latitude` double DEFAULT NULL,
  `longitude` double DEFAULT NULL,
  `password` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `umbrella` (
  `id` int NOT NULL AUTO_INCREMENT,
  `deleted` tinyint DEFAULT NULL,
  `rentable` tinyint DEFAULT NULL,
  `uuid` bigint DEFAULT NULL,
  `store_meta_id` int DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `etc` varchar(255) DEFAULT NULL,
  `missed` tinyint DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `user` (
  `id` int NOT NULL AUTO_INCREMENT,
  `admin_status` tinyint DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `phone_number` varchar(255) DEFAULT NULL,
  `social_id` varchar(500) DEFAULT NULL,
  `bank` varchar(500) DEFAULT NULL,
  `account_number` varchar(500) DEFAULT NULL,
  `email` varchar(500) DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일',
  `deleted_at` datetime DEFAULT NULL COMMENT '삭제일',
  `provider` varchar(100) NOT NULL DEFAULT 'KAKAO',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_social_id` (`social_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


Tools

MySQL 8.0.26
Java Spring Boot 2.5.8
Java 8
Redis 

Graddle Wrapper is ready, just clone to local and run the program,
every configuration are in application.properties, for some setting just need a lil fine tuning such as Redis port or username

MySQL Query for Databases

CREATE DATABASE IF NOT EXISTS `cicada`
DEFAULT CHARACTER SET = 'utf8' DEFAULT COLLATE 'utf8_general_ci'

CREATE TABLE `staff` (
	id INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
	`first_name` VARCHAR(100) NOT NULL,
	`last_name` VARCHAR(100),
	`email` VARCHAR(255) NOT NULL,
	`username` VARCHAR(255) NOT NULL,
	`password` VARCHAR(255) NOT NULL,
	`picture` TEXT,
	`last_update` VARCHAR(100),
	`active` TINYINT UNSIGNED NOT NULL,
	`enabled` TINYINT UNSIGNED NOT NULL
);

CREATE TABLE `product` (
	`id` INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
	`name` VARCHAR(255) NOT NULL,
	`price` DECIMAL(16,2) NOT NULL,
	`description` TEXT,
	`created_at` BIGINT UNSIGNED, 
	`updated_at` BIGINT UNSIGNED,
	`deleted` TINYINT UNSIGNED 
);


CREATE TABLE `authority` (
	`id` INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
	`name` VARCHAR(100) NOT NULL
);

INSERT INTO `authority` (`name`) 
VALUES
('PRODUCT_CREATE'), ('PRODUCT_UPDATE'), ('PRODUCT_DELETE');

CREATE TABLE staff_authority (
    staff_id BIGINT NOT NULL,
    authority_id BIGINT NOT NULL,
    PRIMARY KEY (staff_id, authority_id)
);

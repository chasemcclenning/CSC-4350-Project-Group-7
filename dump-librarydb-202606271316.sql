-- Libris database backup
CREATE DATABASE IF NOT EXISTS `librarydb`;
USE `librarydb`;
SET FOREIGN_KEY_CHECKS=0;

DROP TABLE IF EXISTS `audit_log`;
CREATE TABLE `audit_log` (
  `id` char(36) NOT NULL DEFAULT (uuid()),
  `user_id` char(36) DEFAULT NULL,
  `action` varchar(50) NOT NULL,
  `entity_type` varchar(50) NOT NULL,
  `entity_id` char(36) NOT NULL,
  `notes` json DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_audit_entity` (`entity_type`,`entity_id`),
  KEY `idx_audit_user` (`user_id`),
  KEY `idx_audit_action` (`action`),
  CONSTRAINT `fk_audit_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

DROP TABLE IF EXISTS `checkout`;
CREATE TABLE `checkout` (
  `id` char(36) NOT NULL DEFAULT (uuid()),
  `user_id` char(36) NOT NULL,
  `copy_id` char(36) NOT NULL,
  `checked_out_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `due_at` timestamp NOT NULL,
  `returned_at` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_checkout_user` (`user_id`),
  KEY `fk_checkout_copy` (`copy_id`),
  CONSTRAINT `fk_checkout_copy` FOREIGN KEY (`copy_id`) REFERENCES `copy` (`id`) ON DELETE RESTRICT,
  CONSTRAINT `fk_checkout_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

DROP TABLE IF EXISTS `copy`;
CREATE TABLE `copy` (
  `id` char(36) NOT NULL DEFAULT (uuid()),
  `title_id` char(36) NOT NULL,
  `status` enum('available','checked_out','reserved','lost','damaged') NOT NULL DEFAULT 'available',
  `condition` enum('new','good','fair','poor') NOT NULL DEFAULT 'good',
  PRIMARY KEY (`id`),
  KEY `fk_copy_title` (`title_id`),
  CONSTRAINT `fk_copy_title` FOREIGN KEY (`title_id`) REFERENCES `title` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

DROP TABLE IF EXISTS `fine`;
CREATE TABLE `fine` (
  `id` char(36) NOT NULL DEFAULT (uuid()),
  `checkout_id` char(36) NOT NULL,
  `user_id` char(36) NOT NULL,
  `amount` decimal(8,2) NOT NULL,
  `status` enum('outstanding','paid','waived') NOT NULL DEFAULT 'outstanding',
  PRIMARY KEY (`id`),
  KEY `fk_fine_checkout` (`checkout_id`),
  KEY `fk_fine_user` (`user_id`),
  CONSTRAINT `fk_fine_checkout` FOREIGN KEY (`checkout_id`) REFERENCES `checkout` (`id`) ON DELETE RESTRICT,
  CONSTRAINT `fk_fine_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

DROP TABLE IF EXISTS `hold`;
CREATE TABLE `hold` (
  `id` char(36) NOT NULL DEFAULT (uuid()),
  `user_id` char(36) NOT NULL,
  `title_id` char(36) NOT NULL,
  `queue_position` int NOT NULL,
  `status` enum('waiting','ready','claimed','expired','cancelled') NOT NULL DEFAULT 'waiting',
  `placed_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `expires_at` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_hold_user_title` (`user_id`,`title_id`),
  KEY `fk_hold_title` (`title_id`),
  CONSTRAINT `fk_hold_title` FOREIGN KEY (`title_id`) REFERENCES `title` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_hold_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

DROP TABLE IF EXISTS `title`;
CREATE TABLE `title` (
  `id` char(36) NOT NULL DEFAULT (uuid()),
  `name` varchar(255) NOT NULL,
  `author` varchar(100) NOT NULL,
  `genre` varchar(50) DEFAULT NULL,
  `isbn` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `isbn` (`isbn`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` char(36) NOT NULL DEFAULT (uuid()),
  `name` varchar(100) NOT NULL,
  `email` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `role` enum('patron','librarian','admin') NOT NULL DEFAULT 'patron',
  `fines_owed` decimal(8,2) NOT NULL DEFAULT '0.00',
  PRIMARY KEY (`id`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `audit_log` VALUES ('log-001','u-005','CHECKOUT','checkout','co-002','{"message": "Book checked out to u-001 using copy 8ffae335-7ad2-4f18-9135-48babb610195"}','2026-07-17 18:31:38.0');
INSERT INTO `audit_log` VALUES ('log-002','u-005','CHECKOUT','checkout','co-003','{"message": "Book checked out to 2b52f38e-8226-11f1-b8bf-25cd99951fda using copy f061cb3e-9f65-424f-ad7f-667c0b8066dc"}','2026-07-17 18:32:53.0');
INSERT INTO `audit_log` VALUES ('log-003','u-005','RETURN','checkout','co-003','{"message": "Book returned"}','2026-07-17 18:33:04.0');
INSERT INTO `audit_log` VALUES ('log-004','u-005','RETURN','checkout','co-002','{"message": "Book returned"}','2026-07-17 18:33:10.0');
INSERT INTO `audit_log` VALUES ('log-005','u-001','CHECKOUT','checkout','co-004','{"message": "Book checked out to u-001 using copy c-003"}','2026-07-17 18:34:04.0');
INSERT INTO `audit_log` VALUES ('log-006','u-005','CREATE','user','u-006','{"message": "Librarian account created"}','2026-07-17 19:05:30.0');
INSERT INTO `audit_log` VALUES ('log-007','u-005','CREATE','user','u-007','{"message": "Member account created"}','2026-07-17 19:07:31.0');
INSERT INTO `audit_log` VALUES ('log-008','u-005','DELETE','title','43e0779e-8228-11f1-b8bf-25cd99951fda','{"message": "Book title deleted"}','2026-07-17 19:08:51.0');
INSERT INTO `audit_log` VALUES ('log-009','u-006','CHECKOUT','checkout','co-005','{"message": "Book checked out to Bob Chen"}','2026-07-17 19:23:09.0');
INSERT INTO `audit_log` VALUES ('log-010','u-007','CREATE','hold','h-002','{"message": "Hold placed for u-007"}','2026-07-17 19:23:59.0');
INSERT INTO `audit_log` VALUES ('log-011','u-007','CHECKOUT','checkout','co-006','{"message": "Book checked out to Member Sample"}','2026-07-17 19:24:02.0');
INSERT INTO `audit_log` VALUES ('log-012','u-007','RETURN','checkout','co-006','{"message": "Book returned"}','2026-07-17 19:24:12.0');
INSERT INTO `audit_log` VALUES ('log-013','u-006','CHECKOUT','checkout','co-007','{"message": "Book checked out to Bob Chen"}','2026-07-17 19:26:53.0');
INSERT INTO `audit_log` VALUES ('log-014','u-006','RETURN','checkout','co-007','{"message": "Book returned"}','2026-07-17 19:27:00.0');
INSERT INTO `audit_log` VALUES ('log-015','u-007','CHECKOUT','checkout','co-008','{"message": "Book checked out to Member Sample"}','2026-07-17 19:28:11.0');
INSERT INTO `audit_log` VALUES ('log-016','u-007','RETURN','checkout','co-008','{"message": "Book returned"}','2026-07-17 19:28:16.0');
INSERT INTO `audit_log` VALUES ('log-017','u-007','CANCEL','hold','h-002','{"message": "Hold cancelled"}','2026-07-17 19:28:21.0');

INSERT INTO `checkout` VALUES ('27322b4c-822d-11f1-b8bf-25cd99951fda','2b52f38e-8226-11f1-b8bf-25cd99951fda','8ffae335-7ad2-4f18-9135-48babb610195','2026-07-17 18:16:26.0','2026-07-31 18:16:27.0','2026-07-17 18:21:01.0');
INSERT INTO `checkout` VALUES ('co-001','u-002','c-002','2026-06-27 10:56:58.0','2026-07-11 10:56:58.0',NULL);
INSERT INTO `checkout` VALUES ('co-002','u-001','8ffae335-7ad2-4f18-9135-48babb610195','2026-07-17 18:31:38.0','2026-07-31 18:31:38.0','2026-07-17 18:33:10.0');
INSERT INTO `checkout` VALUES ('co-003','2b52f38e-8226-11f1-b8bf-25cd99951fda','f061cb3e-9f65-424f-ad7f-667c0b8066dc','2026-07-17 18:32:53.0','2026-07-31 18:32:53.0','2026-07-17 18:33:04.0');
INSERT INTO `checkout` VALUES ('co-004','u-001','c-003','2026-07-17 18:34:04.0','2026-07-31 18:34:05.0',NULL);
INSERT INTO `checkout` VALUES ('co-005','u-002','c-001','2026-07-17 19:23:09.0','2026-07-31 19:23:09.0',NULL);
INSERT INTO `checkout` VALUES ('co-006','u-007','c-004','2026-07-17 19:24:02.0','2026-07-31 19:24:03.0','2026-07-17 19:24:12.0');
INSERT INTO `checkout` VALUES ('co-007','u-002','c-004','2026-07-17 19:26:53.0','2026-07-31 19:26:53.0','2026-07-17 19:27:00.0');
INSERT INTO `checkout` VALUES ('co-008','u-007','c-004','2026-07-17 19:28:11.0','2026-07-31 19:28:11.0','2026-07-17 19:28:16.0');

INSERT INTO `copy` VALUES ('8ffae335-7ad2-4f18-9135-48babb610195','43e0779e-8228-11f1-b8bf-25cd99951fda','available','good');
INSERT INTO `copy` VALUES ('c-001','t-001','checked_out','good');
INSERT INTO `copy` VALUES ('c-002','t-001','checked_out','fair');
INSERT INTO `copy` VALUES ('c-003','t-002','checked_out','new');
INSERT INTO `copy` VALUES ('c-004','t-003','available','good');
INSERT INTO `copy` VALUES ('f061cb3e-9f65-424f-ad7f-667c0b8066dc','43e0779e-8228-11f1-b8bf-25cd99951fda','available','good');


INSERT INTO `hold` VALUES ('5c22eb3e-8228-11f1-b8bf-25cd99951fda','2b52f38e-8226-11f1-b8bf-25cd99951fda','43e0779e-8228-11f1-b8bf-25cd99951fda',1,'cancelled','2026-07-17 17:42:08.0',NULL);
INSERT INTO `hold` VALUES ('h-001','u-001','t-001',1,'waiting','2026-06-27 10:56:58.0',NULL);
INSERT INTO `hold` VALUES ('h-002','u-007','t-002',1,'cancelled','2026-07-17 19:23:59.0',NULL);

INSERT INTO `title` VALUES ('43e0779e-8228-11f1-b8bf-25cd99951fda','HP','JKR','SF','12345');
INSERT INTO `title` VALUES ('t-001','The Pragmatic Programmer','David Thomas','Technology','978-0135957059');
INSERT INTO `title` VALUES ('t-002','Clean Code','Robert C. Martin','Technology','978-0132350884');
INSERT INTO `title` VALUES ('t-003','Dune','Frank Herbert','Science Fiction','978-0441013593');

INSERT INTO `user` VALUES ('2b52f38e-8226-11f1-b8bf-25cd99951fda','Mark Allen','mark1@example.com','libmember123','patron',0.00);
INSERT INTO `user` VALUES ('u-001','Alice Morgan','alice1@example.com','libmember1','patron',0.00);
INSERT INTO `user` VALUES ('u-002','Bob Chen','bob@example.com','hashed_pw_2','patron',0.00);
INSERT INTO `user` VALUES ('u-003','Carol Reyes','carol@example.com','librarian1','librarian',0.00);
INSERT INTO `user` VALUES ('u-004','David Park','david@example.com','hashed_pw_4','admin',0.00);
INSERT INTO `user` VALUES ('u-005','Emmanuel G','emmanuel@library.com','librarian2','librarian',0.00);
INSERT INTO `user` VALUES ('u-006','Librarian Sample','librarian@example.com','libexample1','librarian',0.00);
INSERT INTO `user` VALUES ('u-007','Member Sample','member@example.com','memexample1','patron',0.00);

DROP TRIGGER IF EXISTS `trg_checkout_created`;
DELIMITER $$
CREATE TRIGGER `trg_checkout_created` AFTER INSERT ON `checkout` FOR EACH ROW BEGIN
    UPDATE copy SET status = 'checked_out' WHERE id = NEW.copy_id;
END$$
DELIMITER ;

DROP TRIGGER IF EXISTS `trg_checkout_returned`;
DELIMITER $$
CREATE TRIGGER `trg_checkout_returned` AFTER UPDATE ON `checkout` FOR EACH ROW BEGIN
  IF OLD.returned_at IS NULL AND NEW.returned_at IS NOT NULL THEN
    UPDATE copy SET status='available' WHERE id=NEW.copy_id;
    UPDATE hold SET status='ready', expires_at=DATE_ADD(NOW(), INTERVAL 3 DAY)
    WHERE id=(SELECT id FROM (SELECT h.id FROM hold h
      JOIN copy c ON c.title_id=h.title_id
      WHERE c.id=NEW.copy_id AND h.status='waiting'
      ORDER BY h.queue_position LIMIT 1) AS next_hold);
  END IF;
END$$
DELIMITER ;

DROP TRIGGER IF EXISTS `trg_fine_created`;
DELIMITER $$
CREATE TRIGGER `trg_fine_created` AFTER INSERT ON `fine` FOR EACH ROW BEGIN
    UPDATE user SET fines_owed = fines_owed + NEW.amount WHERE id = NEW.user_id;
END$$
DELIMITER ;

DROP TRIGGER IF EXISTS `trg_fine_resolved`;
DELIMITER $$
CREATE TRIGGER `trg_fine_resolved` AFTER UPDATE ON `fine` FOR EACH ROW BEGIN
    IF OLD.status = 'outstanding'
       AND NEW.status IN ('paid', 'waived') THEN
        UPDATE user SET fines_owed = fines_owed - NEW.amount WHERE id = NEW.user_id;
    END IF;
END$$
DELIMITER ;

SET FOREIGN_KEY_CHECKS=1;

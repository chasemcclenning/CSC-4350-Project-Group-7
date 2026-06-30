-- MySQL dump 10.13  Distrib 8.0.19, for Win64 (x86_64)
--
-- Host: localhost    Database: librarydb
-- ------------------------------------------------------
-- Server version	9.4.0

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `audit_log`
--

DROP TABLE IF EXISTS `audit_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `audit_log`
--

LOCK TABLES `audit_log` WRITE;
/*!40000 ALTER TABLE `audit_log` DISABLE KEYS */;
/*!40000 ALTER TABLE `audit_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `checkout`
--

DROP TABLE IF EXISTS `checkout`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `checkout`
--

LOCK TABLES `checkout` WRITE;
/*!40000 ALTER TABLE `checkout` DISABLE KEYS */;
INSERT INTO `checkout` VALUES ('co-001','u-002','c-002','2026-06-27 14:56:58','2026-07-11 14:56:58',NULL);
/*!40000 ALTER TABLE `checkout` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `trg_checkout_created` AFTER INSERT ON `checkout` FOR EACH ROW BEGIN
    UPDATE copy SET status = 'checked_out' WHERE id = NEW.copy_id;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `trg_checkout_returned` AFTER UPDATE ON `checkout` FOR EACH ROW BEGIN
    IF OLD.returned_at IS NULL AND NEW.returned_at IS NOT NULL THEN
        UPDATE copy SET status = 'available' WHERE id = NEW.copy_id;

        -- Notify the next patron in the hold queue for this title
        UPDATE hold
        SET    status     = 'ready',
               expires_at = DATE_ADD(NOW(), INTERVAL 3 DAY)
        WHERE  title_id = (SELECT title_id FROM copy WHERE id = NEW.copy_id)
          AND  status   = 'waiting'
          AND  queue_position = (
                   SELECT MIN(queue_position)
                   FROM   hold h2
                   WHERE  h2.title_id = (SELECT title_id FROM copy WHERE id = NEW.copy_id)
                     AND  h2.status   = 'waiting'
               );
    END IF;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `copy`
--

DROP TABLE IF EXISTS `copy`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `copy` (
  `id` char(36) NOT NULL DEFAULT (uuid()),
  `title_id` char(36) NOT NULL,
  `status` enum('available','checked_out','reserved','lost','damaged') NOT NULL DEFAULT 'available',
  `condition` enum('new','good','fair','poor') NOT NULL DEFAULT 'good',
  PRIMARY KEY (`id`),
  KEY `fk_copy_title` (`title_id`),
  CONSTRAINT `fk_copy_title` FOREIGN KEY (`title_id`) REFERENCES `title` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `copy`
--

LOCK TABLES `copy` WRITE;
/*!40000 ALTER TABLE `copy` DISABLE KEYS */;
INSERT INTO `copy` VALUES ('c-001','t-001','available','good'),('c-002','t-001','checked_out','fair'),('c-003','t-002','available','new'),('c-004','t-003','available','good');
/*!40000 ALTER TABLE `copy` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `fine`
--

DROP TABLE IF EXISTS `fine`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `fine`
--

LOCK TABLES `fine` WRITE;
/*!40000 ALTER TABLE `fine` DISABLE KEYS */;
/*!40000 ALTER TABLE `fine` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `trg_fine_created` AFTER INSERT ON `fine` FOR EACH ROW BEGIN
    UPDATE user SET fines_owed = fines_owed + NEW.amount WHERE id = NEW.user_id;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `trg_fine_resolved` AFTER UPDATE ON `fine` FOR EACH ROW BEGIN
    IF OLD.status = 'outstanding'
       AND NEW.status IN ('paid', 'waived') THEN
        UPDATE user SET fines_owed = fines_owed - NEW.amount WHERE id = NEW.user_id;
    END IF;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `hold`
--

DROP TABLE IF EXISTS `hold`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hold`
--

LOCK TABLES `hold` WRITE;
/*!40000 ALTER TABLE `hold` DISABLE KEYS */;
INSERT INTO `hold` VALUES ('h-001','u-001','t-001',1,'waiting','2026-06-27 14:56:58',NULL);
/*!40000 ALTER TABLE `hold` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `title`
--

DROP TABLE IF EXISTS `title`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `title` (
  `id` char(36) NOT NULL DEFAULT (uuid()),
  `name` varchar(255) NOT NULL,
  `author` varchar(100) NOT NULL,
  `genre` varchar(50) DEFAULT NULL,
  `isbn` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `isbn` (`isbn`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `title`
--

LOCK TABLES `title` WRITE;
/*!40000 ALTER TABLE `title` DISABLE KEYS */;
INSERT INTO `title` VALUES ('t-001','The Pragmatic Programmer','David Thomas','Technology','978-0135957059'),('t-002','Clean Code','Robert C. Martin','Technology','978-0132350884'),('t-003','Dune','Frank Herbert','Science Fiction','978-0441013593');
/*!40000 ALTER TABLE `title` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES ('u-001','Alice Morgan','alice@example.com','hashed_pw_1','patron',0.00),('u-002','Bob Chen','bob@example.com','hashed_pw_2','patron',0.00),('u-003','Carol Reyes','carol@example.com','hashed_pw_3','librarian',0.00),('u-004','David Park','david@example.com','hashed_pw_4','admin',0.00);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'librarydb'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-06-27 13:16:42

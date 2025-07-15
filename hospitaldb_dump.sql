-- MySQL dump 10.13  Distrib 8.0.42, for Win64 (x86_64)
--
-- Host: localhost    Database: HospitalDB
-- ------------------------------------------------------
-- Server version	8.0.42

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
-- Table structure for table `appointments`
--

DROP TABLE IF EXISTS `appointments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `appointments` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `patient_id` bigint NOT NULL,
  `doctor_id` bigint NOT NULL,
  `appointment_time` datetime NOT NULL,
  `status` enum('SCHEDULED','COMPLETED','CANCELLED','NO_SHOW') NOT NULL,
  `reason_for_visit` varchar(255) DEFAULT NULL,
  `notes` text,
  `is_deleted` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `doctor_id` (`doctor_id`),
  KEY `appointments_ibfk_1` (`patient_id`),
  CONSTRAINT `appointments_ibfk_1` FOREIGN KEY (`patient_id`) REFERENCES `patients` (`id`) ON DELETE CASCADE,
  CONSTRAINT `appointments_ibfk_2` FOREIGN KEY (`doctor_id`) REFERENCES `doctors` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=30 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `appointments`
--

LOCK TABLES `appointments` WRITE;
/*!40000 ALTER TABLE `appointments` DISABLE KEYS */;
INSERT INTO `appointments` VALUES (4,6,6,'2025-07-12 00:00:00','SCHEDULED','Braces adjustment',NULL,1),(5,8,6,'2025-07-12 12:20:00','SCHEDULED','Teeth whitening',NULL,1),(7,10,6,'2025-09-18 01:00:00','SCHEDULED','Teeth whitening',NULL,1),(8,17,10,'2025-07-25 18:23:00','SCHEDULED','Dental',NULL,0),(9,12,9,'2025-08-30 14:30:00','SCHEDULED','Annual checkup',NULL,1),(10,6,6,'2025-06-08 10:00:00','SCHEDULED','ufuy',NULL,0),(11,6,6,'2025-06-28 12:00:00','SCHEDULED','hjijppjpkl',NULL,0),(12,6,6,'2025-06-28 00:00:00','SCHEDULED','guvgy',NULL,0),(13,6,6,'2025-06-30 12:00:00','SCHEDULED','ytruiyyui',NULL,0),(14,6,6,'2025-06-30 16:00:00','SCHEDULED','qqqqq',NULL,1),(15,14,8,'2025-06-21 10:00:00','SCHEDULED','rfifgyf',NULL,0),(16,14,8,'2025-06-16 00:00:00','SCHEDULED','effewewew',NULL,1),(17,6,6,'2025-06-28 12:00:00','SCHEDULED','werewr',NULL,1),(18,6,6,'2025-06-18 00:00:00','SCHEDULED','asfsdf',NULL,1),(19,6,6,'2025-06-04 00:00:00','SCHEDULED','wetrewrwtr',NULL,1),(20,16,6,'2025-06-28 00:42:00','SCHEDULED','sfsdfdsf',NULL,0),(21,10,6,'2025-06-28 10:00:00','COMPLETED','sefsdf',NULL,0),(22,13,7,'2025-06-28 17:00:00','SCHEDULED','Eyes',NULL,0),(23,6,6,'2025-06-11 00:00:00','COMPLETED','rererwrt',NULL,0),(24,6,6,'2025-06-12 00:00:00','SCHEDULED','ewrewre',NULL,1),(25,12,11,'2025-06-28 15:00:00','SCHEDULED','rewrrewew',NULL,1),(26,19,12,'2025-08-30 13:00:00','SCHEDULED','Checkup',NULL,0),(27,6,13,'2025-07-17 00:00:00','CANCELLED','asdasdsadsa',NULL,0),(28,13,12,'2025-07-06 00:00:00','SCHEDULED','weewfw',NULL,1),(29,14,11,'2025-07-31 11:00:00','SCHEDULED','dgydias',NULL,0);
/*!40000 ALTER TABLE `appointments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `audit_log`
--

DROP TABLE IF EXISTS `audit_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `audit_log` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `actionType` varchar(32) DEFAULT NULL,
  `entityName` varchar(64) DEFAULT NULL,
  `entityId` varchar(64) DEFAULT NULL,
  `oldValue` longtext,
  `newValue` longtext,
  `timestamp` datetime DEFAULT NULL,
  `staffId` bigint DEFAULT NULL,
  `staffName` varchar(128) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=153 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `audit_log`
--

LOCK TABLES `audit_log` WRITE;
/*!40000 ALTER TABLE `audit_log` DISABLE KEYS */;
INSERT INTO `audit_log` VALUES (1,'CREATE','Patient','21',NULL,'Patient{id=21, firstName=\'Walter\', lastName=\'Ocan\', email=\'wocan223@gmail.com\'}','2025-07-01 15:54:41',5,'Tom Dangote'),(2,'LOGIN','Staff','7',NULL,'Staff{id=7, firstName=\'dummy\', lastName=\'staff\', role=\'OTHER\', department=\'other\'}','2025-07-01 16:02:45',7,'dummy staff'),(3,'LOGIN','Staff','5',NULL,'Staff{id=5, firstName=\'Tom\', lastName=\'Dangote\', role=\'IT_SUPPORT\', department=\'System admin\'}','2025-07-01 16:44:19',5,'Tom Dangote'),(4,'LOGIN','Staff','5',NULL,'Staff{id=5, firstName=\'Tom\', lastName=\'Dangote\', role=\'IT_SUPPORT\', department=\'System admin\'}','2025-07-01 16:47:37',5,'Tom Dangote'),(5,'LOGIN','Staff','5',NULL,'Staff{id=5, firstName=\'Tom\', lastName=\'Dangote\', role=\'IT_SUPPORT\', department=\'System admin\'}','2025-07-01 16:51:51',5,'Tom Dangote'),(6,'LOGIN','Staff','5',NULL,'Staff{id=5, firstName=\'Tom\', lastName=\'Dangote\', role=\'IT_SUPPORT\', department=\'System admin\'}','2025-07-01 17:13:39',5,'Tom Dangote'),(7,'LOGIN','Staff','5',NULL,'Staff{id=5, firstName=\'Tom\', lastName=\'Dangote\', role=\'IT_SUPPORT\', department=\'System admin\'}','2025-07-01 17:18:21',5,'Tom Dangote'),(8,'CREATE','Staff','N/A',NULL,'Staff{id=null, firstName=\'Donald\', lastName=\'Sebabi\', role=\'PHARMACIST\', department=\'Pharmacy\'}','2025-07-01 17:22:54',NULL,NULL),(9,'LOGIN','Staff','7',NULL,'Staff{id=7, firstName=\'dummy\', lastName=\'staff\', role=\'OTHER\', department=\'other\'}','2025-07-01 17:24:56',7,'dummy staff'),(10,'UPDATE','Staff','12','Staff{id=12, firstName=\'Donald\', lastName=\'Sebabi\', role=\'PHARMACIST\', department=\'Pharmacy\'}','Staff{id=12, firstName=\'Donald\', lastName=\'Sebabi\', role=\'PHARMACIST\', department=\'Pharmacy\'}','2025-07-01 17:25:34',12,'Donald Sebabi'),(11,'LOGIN','Staff','7',NULL,'Staff{id=7, firstName=\'dummy\', lastName=\'staff\', role=\'OTHER\', department=\'other\'}','2025-07-02 07:57:32',7,'dummy staff'),(12,'LOGIN','Staff','5',NULL,'Staff{id=5, firstName=\'Tom\', lastName=\'Dangote\', role=\'IT_SUPPORT\', department=\'System admin\'}','2025-07-02 08:03:10',5,'Tom Dangote'),(13,'LOGOUT','Staff','5','Staff{id=5, firstName=\'Tom\', lastName=\'Dangote\', role=\'IT_SUPPORT\', department=\'System admin\'}',NULL,'2025-07-02 08:04:13',5,'Tom Dangote'),(14,'LOGIN','Staff','5',NULL,'Staff{id=5, firstName=\'Tom\', lastName=\'Dangote\', role=\'IT_SUPPORT\', department=\'System admin\'}','2025-07-02 08:06:34',5,'Tom Dangote'),(15,'LOGIN','Staff','7',NULL,'Staff{id=7, firstName=\'dummy\', lastName=\'staff\', role=\'OTHER\', department=\'other\'}','2025-07-02 08:57:21',7,'dummy staff'),(16,'LOGIN','Staff','5',NULL,'Staff{id=5, firstName=\'Tom\', lastName=\'Dangote\', role=\'IT_SUPPORT\', department=\'System admin\'}','2025-07-02 09:16:37',5,'Tom Dangote'),(17,'LOGIN','Staff','5',NULL,'Staff{id=5, firstName=\'Tom\', lastName=\'Dangote\', role=\'IT_SUPPORT\', department=\'System admin\'}','2025-07-02 10:04:04',5,'Tom Dangote'),(18,'UPDATE','Doctor','6','Doctor{id=6, firstName=\'Gordon\', lastName=\'Virunga\', specialization=\'ORTHOPEDICS\'}','Doctor{id=6, firstName=\'Gordon\', lastName=\'Virunga\', specialization=\'ORTHOPEDICS\'}','2025-07-02 10:04:51',NULL,NULL),(19,'UPDATE','Doctor','7','Doctor{id=7, firstName=\'Hakimi\', lastName=\'Yuvraj\', specialization=\'OPTOMETRY\'}','Doctor{id=7, firstName=\'Hakimi\', lastName=\'Yuvraj\', specialization=\'OPTOMETRY\'}','2025-07-02 10:05:11',NULL,NULL),(20,'UPDATE','Doctor','8','Doctor{id=8, firstName=\'Chris\', lastName=\'Balaam\', specialization=\'CARDIOLOGY\'}','Doctor{id=8, firstName=\'Chris\', lastName=\'Balaam\', specialization=\'CARDIOLOGY\'}','2025-07-02 10:05:21',NULL,NULL),(21,'UPDATE','Doctor','9','Doctor{id=9, firstName=\'Daniel\', lastName=\'Kaluuya\', specialization=\'NEUROLOGY\'}','Doctor{id=9, firstName=\'Daniel\', lastName=\'Kaluuya\', specialization=\'NEUROLOGY\'}','2025-07-02 10:05:31',NULL,NULL),(22,'UPDATE','Doctor','10','Doctor{id=10, firstName=\'George\', lastName=\'Funke\', specialization=\'CARDIOLOGY\'}','Doctor{id=10, firstName=\'George\', lastName=\'Funke\', specialization=\'CARDIOLOGY\'}','2025-07-02 10:05:41',NULL,NULL),(23,'UPDATE','Doctor','11','Doctor{id=11, firstName=\'Erica\', lastName=\'Ssozi\', specialization=\'PEDIATRICS\'}','Doctor{id=11, firstName=\'Erica\', lastName=\'Ssozi\', specialization=\'PEDIATRICS\'}','2025-07-02 10:05:52',NULL,NULL),(24,'UPDATE','Doctor','12','Doctor{id=12, firstName=\'Derrick\', lastName=\'Ddungu\', specialization=\'PSYCHIATRY\'}','Doctor{id=12, firstName=\'Derrick\', lastName=\'Ddungu\', specialization=\'PSYCHIATRY\'}','2025-07-02 10:06:01',NULL,NULL),(25,'LOGIN','Staff','5',NULL,'Staff{id=5, firstName=\'Tom\', lastName=\'Dangote\', role=\'IT_SUPPORT\', department=\'System admin\'}','2025-07-02 10:09:15',5,'Tom Dangote'),(26,'UPDATE','Doctor','6','Doctor{id=6, firstName=\'Gordon\', lastName=\'Virunga\', specialization=\'ORTHOPEDICS\'}','Doctor{id=6, firstName=\'Gordon\', lastName=\'Virunga\', specialization=\'ORTHOPEDICS\'}','2025-07-02 10:09:51',NULL,NULL),(27,'UPDATE','Doctor','7','Doctor{id=7, firstName=\'Hakimi\', lastName=\'Yuvraj\', specialization=\'OPTOMETRY\'}','Doctor{id=7, firstName=\'Hakimi\', lastName=\'Yuvraj\', specialization=\'OPTOMETRY\'}','2025-07-02 10:10:09',NULL,NULL),(28,'UPDATE','Doctor','7','Doctor{id=7, firstName=\'Hakimi\', lastName=\'Yuvraj\', specialization=\'OPTOMETRY\'}','Doctor{id=7, firstName=\'Hakimi\', lastName=\'Yuvraj\', specialization=\'OPTOMETRY\'}','2025-07-02 10:15:16',NULL,NULL),(29,'UPDATE','Doctor','8','Doctor{id=8, firstName=\'Chris\', lastName=\'Balaam\', specialization=\'CARDIOLOGY\'}','Doctor{id=8, firstName=\'Chris\', lastName=\'Balaam\', specialization=\'CARDIOLOGY\'}','2025-07-02 10:15:25',NULL,NULL),(30,'UPDATE','Doctor','9','Doctor{id=9, firstName=\'Daniel\', lastName=\'Kaluuya\', specialization=\'NEUROLOGY\'}','Doctor{id=9, firstName=\'Daniel\', lastName=\'Kaluuya\', specialization=\'NEUROLOGY\'}','2025-07-02 10:15:36',NULL,NULL),(31,'UPDATE','Doctor','10','Doctor{id=10, firstName=\'George\', lastName=\'Funke\', specialization=\'CARDIOLOGY\'}','Doctor{id=10, firstName=\'George\', lastName=\'Funke\', specialization=\'CARDIOLOGY\'}','2025-07-02 10:15:45',NULL,NULL),(32,'UPDATE','Doctor','11','Doctor{id=11, firstName=\'Erica\', lastName=\'Ssozi\', specialization=\'PEDIATRICS\'}','Doctor{id=11, firstName=\'Erica\', lastName=\'Ssozi\', specialization=\'PEDIATRICS\'}','2025-07-02 10:15:53',NULL,NULL),(33,'UPDATE','Doctor','12','Doctor{id=12, firstName=\'Derrick\', lastName=\'Ddungu\', specialization=\'PSYCHIATRY\'}','Doctor{id=12, firstName=\'Derrick\', lastName=\'Ddungu\', specialization=\'PSYCHIATRY\'}','2025-07-02 10:16:02',NULL,NULL),(34,'LOGOUT','Staff','5','Staff{id=5, firstName=\'Tom\', lastName=\'Dangote\', role=\'IT_SUPPORT\', department=\'System admin\'}',NULL,'2025-07-02 10:16:22',5,'Tom Dangote'),(35,'LOGIN','Staff','5',NULL,'Staff{id=5, firstName=\'Tom\', lastName=\'Dangote\', role=\'IT_SUPPORT\', department=\'System admin\'}','2025-07-02 10:24:24',5,'Tom Dangote'),(36,'LOGOUT','Staff','5','Staff{id=5, firstName=\'Tom\', lastName=\'Dangote\', role=\'IT_SUPPORT\', department=\'System admin\'}',NULL,'2025-07-02 10:24:27',5,'Tom Dangote'),(37,'LOGIN','Doctor','11',NULL,'Doctor{id=11, firstName=\'Erica\', lastName=\'Ssozi\', specialization=\'PEDIATRICS\'}','2025-07-02 10:36:39',11,'Dr. Erica Ssozi'),(38,'LOGIN','Doctor','11',NULL,'Doctor{id=11, firstName=\'Erica\', lastName=\'Ssozi\', specialization=\'PEDIATRICS\'}','2025-07-02 10:41:04',11,'Dr. Erica Ssozi'),(39,'LOGIN','Doctor','11',NULL,'Doctor{id=11, firstName=\'Erica\', lastName=\'Ssozi\', specialization=\'PEDIATRICS\'}','2025-07-02 10:58:52',11,'Dr. Erica Ssozi'),(40,'LOGIN','Doctor','11',NULL,'Doctor{id=11, firstName=\'Erica\', lastName=\'Ssozi\', specialization=\'PEDIATRICS\'}','2025-07-02 12:02:06',11,'Dr. Erica Ssozi'),(41,'LOGIN','Staff','5',NULL,'Staff{id=5, firstName=\'Tom\', lastName=\'Dangote\', role=\'IT_SUPPORT\', department=\'System admin\'}','2025-07-02 12:02:30',5,'Tom Dangote'),(42,'LOGOUT','Staff','5','Staff{id=5, firstName=\'Tom\', lastName=\'Dangote\', role=\'IT_SUPPORT\', department=\'System admin\'}',NULL,'2025-07-02 12:08:44',5,'Tom Dangote'),(43,'LOGIN','Doctor','11',NULL,'Doctor{id=11, firstName=\'Erica\', lastName=\'Ssozi\', specialization=\'PEDIATRICS\'}','2025-07-02 12:08:59',11,'Dr. Erica Ssozi'),(44,'LOGIN','Staff','5',NULL,'Staff{id=5, firstName=\'Tom\', lastName=\'Dangote\', role=\'IT_SUPPORT\', department=\'System admin\'}','2025-07-02 12:14:55',5,'Tom Dangote'),(45,'LOGIN','Staff','5',NULL,'Staff{id=5, firstName=\'Tom\', lastName=\'Dangote\', role=\'IT_SUPPORT\', department=\'System admin\'}','2025-07-02 12:15:37',5,'Tom Dangote'),(46,'LOGIN','Staff','5',NULL,'Staff{id=5, firstName=\'Tom\', lastName=\'Dangote\', role=\'IT_SUPPORT\', department=\'System admin\'}','2025-07-02 12:17:00',5,'Tom Dangote'),(47,'LOGIN','Staff','5',NULL,'Staff{id=5, firstName=\'Tom\', lastName=\'Dangote\', role=\'IT_SUPPORT\', department=\'System admin\'}','2025-07-02 12:17:19',5,'Tom Dangote'),(48,'LOGIN','Staff','5',NULL,'Staff{id=5, firstName=\'Tom\', lastName=\'Dangote\', role=\'IT_SUPPORT\', department=\'System admin\'}','2025-07-02 12:19:32',5,'Tom Dangote'),(49,'LOGIN','Staff','5',NULL,'Staff{id=5, firstName=\'Tom\', lastName=\'Dangote\', role=\'IT_SUPPORT\', department=\'System admin\'}','2025-07-02 12:19:39',5,'Tom Dangote'),(50,'LOGOUT','Staff','5','Staff{id=5, firstName=\'Tom\', lastName=\'Dangote\', role=\'IT_SUPPORT\', department=\'System admin\'}',NULL,'2025-07-02 13:01:59',5,'Tom Dangote'),(51,'LOGIN','Doctor','11',NULL,'Doctor{id=11, firstName=\'Erica\', lastName=\'Ssozi\', specialization=\'PEDIATRICS\'}','2025-07-02 13:02:39',11,'Dr. Erica Ssozi'),(52,'LOGIN','Doctor','11',NULL,'Doctor{id=11, firstName=\'Erica\', lastName=\'Ssozi\', specialization=\'PEDIATRICS\'}','2025-07-02 13:07:41',11,'Dr. Erica Ssozi'),(53,'LOGIN','Doctor','11',NULL,'Doctor{id=11, firstName=\'Erica\', lastName=\'Ssozi\', specialization=\'PEDIATRICS\'}','2025-07-02 13:29:07',11,'Dr. Erica Ssozi'),(54,'LOGIN','Staff','5',NULL,'Staff{id=5, firstName=\'Tom\', lastName=\'Dangote\', role=\'IT_SUPPORT\', department=\'System admin\'}','2025-07-02 13:34:31',5,'Tom Dangote'),(55,'LOGIN','Staff','5',NULL,'Staff{id=5, firstName=\'Tom\', lastName=\'Dangote\', role=\'IT_SUPPORT\', department=\'System admin\'}','2025-07-02 13:39:00',5,'Tom Dangote'),(56,'LOGOUT','Staff','5','Staff{id=5, firstName=\'Tom\', lastName=\'Dangote\', role=\'IT_SUPPORT\', department=\'System admin\'}',NULL,'2025-07-02 13:40:48',5,'Tom Dangote'),(57,'LOGIN','Doctor','11',NULL,'Doctor{id=11, firstName=\'Erica\', lastName=\'Ssozi\', specialization=\'GENERAL_MEDICINE\'}','2025-07-02 13:41:03',11,'Dr. Erica Ssozi'),(58,'LOGIN','Staff','5',NULL,'Staff{id=5, firstName=\'Tom\', lastName=\'Dangote\', role=\'IT_SUPPORT\', department=\'System admin\'}','2025-07-02 13:41:24',5,'Tom Dangote'),(59,'LOGIN','Staff','5',NULL,'Staff{id=5, firstName=\'Tom\', lastName=\'Dangote\', role=\'IT_SUPPORT\', department=\'System admin\'}','2025-07-02 14:38:19',5,'Tom Dangote'),(60,'UPDATE','Doctor','11','Doctor{id=11, firstName=\'Erica\', lastName=\'Ssozi\', specialization=\'OPTOMETRY\'}','Doctor{id=11, firstName=\'Erica\', lastName=\'Ssozi\', specialization=\'OPTOMETRY\'}','2025-07-02 15:49:25',NULL,NULL),(61,'LOGIN','Staff','5',NULL,'Staff{id=5, firstName=\'Tom\', lastName=\'Dangote\', role=\'IT_SUPPORT\', department=\'System admin\'}','2025-07-02 16:00:18',5,'Tom Dangote'),(62,'CREATE','Doctor','14',NULL,'Doctor{id=14, firstName=\'John\', lastName=\'Sanya\', specialization=\'GENERAL MEDICINE\'}','2025-07-02 16:01:22',NULL,NULL),(63,'UPDATE','Doctor','14','Doctor{id=14, firstName=\'John\', lastName=\'Sanya\', specialization=\'GENERAL MEDICINE\'}','Doctor{id=14, firstName=\'John\', lastName=\'Sanya\', specialization=\'GENERAL MEDICINE\'}','2025-07-02 16:06:23',NULL,NULL),(64,'LOGIN','Staff','5',NULL,'Staff{id=5, firstName=\'Tom\', lastName=\'Dangote\', role=\'IT_SUPPORT\', department=\'System admin\'}','2025-07-02 16:48:59',5,'Tom Dangote'),(65,'LOGIN','Staff','5',NULL,'Staff{id=5, firstName=\'Tom\', lastName=\'Dangote\', role=\'IT_SUPPORT\', department=\'System admin\'}','2025-07-02 16:57:56',5,'Tom Dangote'),(66,'LOGIN','Staff','5',NULL,'Staff{id=5, firstName=\'Tom\', lastName=\'Dangote\', role=\'IT_SUPPORT\', department=\'System admin\'}','2025-07-02 17:03:00',5,'Tom Dangote'),(67,'LOGIN','Staff','5',NULL,'Staff{id=5, firstName=\'Tom\', lastName=\'Dangote\', role=\'IT_SUPPORT\', department=\'System admin\'}','2025-07-02 17:30:44',5,'Tom Dangote'),(68,'LOGIN','Staff','5',NULL,'Staff{id=5, firstName=\'Tom\', lastName=\'Dangote\', role=\'IT_SUPPORT\', department=\'System admin\'}','2025-07-02 18:18:44',5,'Tom Dangote'),(69,'LOGIN','Staff','5',NULL,'Staff{id=5, firstName=\'Tom\', lastName=\'Dangote\', role=\'IT_SUPPORT\', department=\'System admin\'}','2025-07-02 18:21:40',5,'Tom Dangote'),(70,'LOGIN','Staff','5',NULL,'Staff{id=5, firstName=\'Tom\', lastName=\'Dangote\', role=\'IT_SUPPORT\', department=\'System admin\'}','2025-07-02 18:23:39',5,'Tom Dangote'),(71,'LOGOUT','Staff','5','Staff{id=5, firstName=\'Tom\', lastName=\'Dangote\', role=\'IT_SUPPORT\', department=\'System admin\'}',NULL,'2025-07-02 18:24:15',5,'Tom Dangote'),(72,'LOGIN','Doctor','11',NULL,'Doctor{id=11, firstName=\'Erica\', lastName=\'Ssozi\', specialization=\'OPTOMETRY\'}','2025-07-02 18:24:28',11,'Dr. Erica Ssozi'),(73,'LOGIN','Staff','6',NULL,'Staff{id=6, firstName=\'Grace\', lastName=\'Nakiwala\', role=\'RECEPTIONIST\', department=\'reception\'}','2025-07-02 18:25:47',6,'Grace Nakiwala'),(74,'LOGIN','Staff','5',NULL,'Staff{id=5, firstName=\'Tom\', lastName=\'Dangote\', role=\'IT_SUPPORT\', department=\'System admin\'}','2025-07-02 18:56:03',5,'Tom Dangote'),(75,'LOGIN','Staff','5',NULL,'Staff{id=5, firstName=\'Tom\', lastName=\'Dangote\', role=\'IT_SUPPORT\', department=\'System admin\'}','2025-07-03 07:47:22',5,'Tom Dangote'),(76,'LOGIN','Staff','5',NULL,'Staff{id=5, firstName=\'Tom\', lastName=\'Dangote\', role=\'IT_SUPPORT\', department=\'System admin\'}','2025-07-03 07:47:46',5,'Tom Dangote'),(77,'LOGIN','Staff','5',NULL,'Staff{id=5, firstName=\'Tom\', lastName=\'Dangote\', role=\'IT_SUPPORT\', department=\'System admin\'}','2025-07-03 07:48:06',5,'Tom Dangote'),(78,'LOGIN','Staff','5',NULL,'Staff{id=5, firstName=\'Tom\', lastName=\'Dangote\', role=\'IT_SUPPORT\', department=\'System admin\'}','2025-07-03 07:48:48',5,'Tom Dangote'),(79,'LOGIN','Staff','5',NULL,'Staff{id=5, firstName=\'Tom\', lastName=\'Dangote\', role=\'IT_SUPPORT\', department=\'System admin\'}','2025-07-03 07:48:58',5,'Tom Dangote'),(80,'LOGIN','Staff','5',NULL,'Staff{id=5, firstName=\'Tom\', lastName=\'Dangote\', role=\'IT_SUPPORT\', department=\'System admin\'}','2025-07-03 07:49:36',5,'Tom Dangote'),(81,'LOGIN','Staff','5',NULL,'Staff{id=5, firstName=\'Tom\', lastName=\'Dangote\', role=\'IT_SUPPORT\', department=\'System admin\'}','2025-07-03 07:49:45',5,'Tom Dangote'),(82,'LOGIN','Staff','5',NULL,'Staff{id=5, firstName=\'Tom\', lastName=\'Dangote\', role=\'IT_SUPPORT\', department=\'System admin\'}','2025-07-03 07:49:55',5,'Tom Dangote'),(83,'LOGIN','Staff','5',NULL,'Staff{id=5, firstName=\'Tom\', lastName=\'Dangote\', role=\'IT_SUPPORT\', department=\'System admin\'}','2025-07-03 07:50:55',5,'Tom Dangote'),(84,'LOGIN','Staff','5',NULL,'Staff{id=5, firstName=\'Tom\', lastName=\'Dangote\', role=\'IT_SUPPORT\', department=\'System admin\'}','2025-07-03 07:51:07',5,'Tom Dangote'),(85,'LOGIN','Staff','5',NULL,'Staff{id=5, firstName=\'Tom\', lastName=\'Dangote\', role=\'IT_SUPPORT\', department=\'System admin\'}','2025-07-03 07:51:44',5,'Tom Dangote'),(86,'LOGIN','Staff','5',NULL,'Staff{id=5, firstName=\'Tom\', lastName=\'Dangote\', role=\'IT_SUPPORT\', department=\'System admin\'}','2025-07-03 07:51:52',5,'Tom Dangote'),(87,'LOGIN','Staff','5',NULL,'Staff{id=5, firstName=\'Tom\', lastName=\'Dangote\', role=\'IT_SUPPORT\', department=\'System admin\'}','2025-07-03 07:52:05',5,'Tom Dangote'),(88,'LOGIN','Staff','5',NULL,'Staff{id=5, firstName=\'Tom\', lastName=\'Dangote\', role=\'IT_SUPPORT\', department=\'System admin\'}','2025-07-03 07:52:41',5,'Tom Dangote'),(89,'LOGIN','Staff','5',NULL,'Staff{id=5, firstName=\'Tom\', lastName=\'Dangote\', role=\'IT_SUPPORT\', department=\'System admin\'}','2025-07-03 07:54:12',5,'Tom Dangote'),(90,'LOGIN','Staff','5',NULL,'Staff{id=5, firstName=\'Tom\', lastName=\'Dangote\', role=\'IT_SUPPORT\', department=\'System admin\'}','2025-07-03 07:55:05',5,'Tom Dangote'),(91,'LOGIN','Staff','5',NULL,'Staff{id=5, firstName=\'Tom\', lastName=\'Dangote\', role=\'IT_SUPPORT\', department=\'System admin\'}','2025-07-03 07:56:08',5,'Tom Dangote'),(92,'LOGIN','Staff','5',NULL,'Staff{id=5, firstName=\'Tom\', lastName=\'Dangote\', role=\'IT_SUPPORT\', department=\'System admin\'}','2025-07-03 07:56:31',5,'Tom Dangote'),(93,'LOGIN','Staff','5',NULL,'Staff{id=5, firstName=\'Tom\', lastName=\'Dangote\', role=\'IT_SUPPORT\', department=\'System admin\'}','2025-07-03 07:57:00',5,'Tom Dangote'),(94,'LOGIN','Staff','5',NULL,'Staff{id=5, firstName=\'Tom\', lastName=\'Dangote\', role=\'IT_SUPPORT\', department=\'System admin\'}','2025-07-03 07:58:03',5,'Tom Dangote'),(95,'LOGIN','Staff','5',NULL,'Staff{id=5, firstName=\'Tom\', lastName=\'Dangote\', role=\'IT_SUPPORT\', department=\'System admin\'}','2025-07-03 07:58:44',5,'Tom Dangote'),(96,'LOGIN','Staff','5',NULL,'Staff{id=5, firstName=\'Tom\', lastName=\'Dangote\', role=\'IT_SUPPORT\', department=\'System admin\'}','2025-07-03 07:59:13',5,'Tom Dangote'),(97,'LOGIN','Staff','5',NULL,'Staff{id=5, firstName=\'Tom\', lastName=\'Dangote\', role=\'IT_SUPPORT\', department=\'System admin\'}','2025-07-03 07:59:23',5,'Tom Dangote'),(98,'LOGIN','Staff','5',NULL,'Staff{id=5, firstName=\'Tom\', lastName=\'Dangote\', role=\'IT_SUPPORT\', department=\'System admin\'}','2025-07-03 08:00:29',5,'Tom Dangote'),(99,'LOGIN','Staff','5',NULL,'Staff{id=5, firstName=\'Tom\', lastName=\'Dangote\', role=\'IT_SUPPORT\', department=\'System admin\'}','2025-07-03 08:00:40',5,'Tom Dangote'),(100,'LOGIN','Staff','5',NULL,'Staff{id=5, firstName=\'Tom\', lastName=\'Dangote\', role=\'IT_SUPPORT\', department=\'System admin\'}','2025-07-03 08:00:53',5,'Tom Dangote'),(101,'LOGIN','Staff','5',NULL,'Staff{id=5, firstName=\'Tom\', lastName=\'Dangote\', role=\'IT_SUPPORT\', department=\'System admin\'}','2025-07-03 08:01:19',5,'Tom Dangote'),(102,'LOGIN','Staff','5',NULL,'Staff{id=5, firstName=\'Tom\', lastName=\'Dangote\', role=\'IT_SUPPORT\', department=\'System admin\'}','2025-07-03 08:01:30',5,'Tom Dangote'),(103,'LOGIN','Staff','5',NULL,'Staff{id=5, firstName=\'Tom\', lastName=\'Dangote\', role=\'IT_SUPPORT\', department=\'System admin\'}','2025-07-03 08:01:47',5,'Tom Dangote'),(104,'LOGIN','Staff','5',NULL,'Staff{id=5, firstName=\'Tom\', lastName=\'Dangote\', role=\'IT_SUPPORT\', department=\'System admin\'}','2025-07-03 08:02:05',5,'Tom Dangote'),(105,'LOGIN','Staff','5',NULL,'Staff{id=5, firstName=\'Tom\', lastName=\'Dangote\', role=\'IT_SUPPORT\', department=\'System admin\'}','2025-07-03 08:02:16',5,'Tom Dangote'),(106,'LOGIN','Staff','5',NULL,'Staff{id=5, firstName=\'Tom\', lastName=\'Dangote\', role=\'IT_SUPPORT\', department=\'System admin\'}','2025-07-03 08:03:22',5,'Tom Dangote'),(107,'LOGIN','Staff','5',NULL,'Staff{id=5, firstName=\'Tom\', lastName=\'Dangote\', role=\'IT_SUPPORT\', department=\'System admin\'}','2025-07-03 08:03:44',5,'Tom Dangote'),(108,'LOGIN','Staff','5',NULL,'Staff{id=5, firstName=\'Tom\', lastName=\'Dangote\', role=\'IT_SUPPORT\', department=\'System admin\'}','2025-07-03 08:03:59',5,'Tom Dangote'),(109,'LOGIN','Staff','5',NULL,'Staff{id=5, firstName=\'Tom\', lastName=\'Dangote\', role=\'IT_SUPPORT\', department=\'System admin\'}','2025-07-03 08:04:13',5,'Tom Dangote'),(110,'LOGIN','Staff','5',NULL,'Staff{id=5, firstName=\'Tom\', lastName=\'Dangote\', role=\'IT_SUPPORT\', department=\'System admin\'}','2025-07-03 08:04:41',5,'Tom Dangote'),(111,'LOGIN','Staff','5',NULL,'Staff{id=5, firstName=\'Tom\', lastName=\'Dangote\', role=\'IT_SUPPORT\', department=\'System admin\'}','2025-07-03 08:09:19',5,'Tom Dangote'),(112,'LOGIN','Staff','5',NULL,'Staff{id=5, firstName=\'Tom\', lastName=\'Dangote\', role=\'IT_SUPPORT\', department=\'System admin\'}','2025-07-03 08:11:12',5,'Tom Dangote'),(113,'LOGIN','Staff','5',NULL,'Staff{id=5, firstName=\'Tom\', lastName=\'Dangote\', role=\'IT_SUPPORT\', department=\'System admin\'}','2025-07-03 08:16:06',5,'Tom Dangote'),(114,'LOGIN','Staff','5',NULL,'Staff{id=5, firstName=\'Tom\', lastName=\'Dangote\', role=\'IT_SUPPORT\', department=\'System admin\'}','2025-07-03 08:16:46',5,'Tom Dangote'),(115,'LOGIN','Staff','5',NULL,'Staff{id=5, firstName=\'Tom\', lastName=\'Dangote\', role=\'IT_SUPPORT\', department=\'System admin\'}','2025-07-03 08:17:00',5,'Tom Dangote'),(116,'LOGIN','Staff','5',NULL,'Staff{id=5, firstName=\'Tom\', lastName=\'Dangote\', role=\'IT_SUPPORT\', department=\'System admin\'}','2025-07-03 08:17:23',5,'Tom Dangote'),(117,'LOGIN','Staff','5',NULL,'Staff{id=5, firstName=\'Tom\', lastName=\'Dangote\', role=\'IT_SUPPORT\', department=\'System admin\'}','2025-07-03 08:17:41',5,'Tom Dangote'),(118,'LOGIN','Staff','5',NULL,'Staff{id=5, firstName=\'Tom\', lastName=\'Dangote\', role=\'IT_SUPPORT\', department=\'System admin\'}','2025-07-03 08:18:43',5,'Tom Dangote'),(119,'LOGIN','Staff','5',NULL,'Staff{id=5, firstName=\'Tom\', lastName=\'Dangote\', role=\'IT_SUPPORT\', department=\'System admin\'}','2025-07-03 08:19:00',5,'Tom Dangote'),(120,'LOGIN','Staff','5',NULL,'Staff{id=5, firstName=\'Tom\', lastName=\'Dangote\', role=\'IT_SUPPORT\', department=\'System admin\'}','2025-07-03 08:19:11',5,'Tom Dangote'),(121,'LOGIN','Staff','5',NULL,'Staff{id=5, firstName=\'Tom\', lastName=\'Dangote\', role=\'IT_SUPPORT\', department=\'System admin\'}','2025-07-03 08:19:15',5,'Tom Dangote'),(122,'LOGIN','Staff','5',NULL,'Staff{id=5, firstName=\'Tom\', lastName=\'Dangote\', role=\'IT_SUPPORT\', department=\'System admin\'}','2025-07-03 08:21:54',5,'Tom Dangote'),(123,'LOGIN','Staff','5',NULL,'Staff{id=5, firstName=\'Tom\', lastName=\'Dangote\', role=\'IT_SUPPORT\', department=\'System admin\'}','2025-07-03 08:25:20',5,'Tom Dangote'),(124,'LOGIN','Staff','5',NULL,'Staff{id=5, firstName=\'Tom\', lastName=\'Dangote\', role=\'IT_SUPPORT\', department=\'System admin\'}','2025-07-03 08:25:35',5,'Tom Dangote'),(125,'LOGIN','Staff','5',NULL,'Staff{id=5, firstName=\'Tom\', lastName=\'Dangote\', role=\'IT_SUPPORT\', department=\'System admin\'}','2025-07-03 08:25:48',5,'Tom Dangote'),(126,'LOGIN','Staff','5',NULL,'Staff{id=5, firstName=\'Tom\', lastName=\'Dangote\', role=\'IT_SUPPORT\', department=\'System admin\'}','2025-07-03 08:26:46',5,'Tom Dangote'),(127,'LOGIN','Staff','5',NULL,'Staff{id=5, firstName=\'Tom\', lastName=\'Dangote\', role=\'IT_SUPPORT\', department=\'System admin\'}','2025-07-03 08:28:12',5,'Tom Dangote'),(128,'LOGIN','Staff','5',NULL,'Staff{id=5, firstName=\'Tom\', lastName=\'Dangote\', role=\'IT_SUPPORT\', department=\'System admin\'}','2025-07-03 08:29:20',5,'Tom Dangote'),(129,'LOGIN','Staff','5',NULL,'Staff{id=5, firstName=\'Tom\', lastName=\'Dangote\', role=\'IT_SUPPORT\', department=\'System admin\'}','2025-07-03 08:29:32',5,'Tom Dangote'),(130,'LOGIN','Staff','5',NULL,'Staff{id=5, firstName=\'Tom\', lastName=\'Dangote\', role=\'IT_SUPPORT\', department=\'System admin\'}','2025-07-03 08:30:55',5,'Tom Dangote'),(131,'LOGIN','Staff','5',NULL,'Staff{id=5, firstName=\'Tom\', lastName=\'Dangote\', role=\'IT_SUPPORT\', department=\'System admin\'}','2025-07-03 08:31:33',5,'Tom Dangote'),(132,'LOGIN','Staff','5',NULL,'Staff{id=5, firstName=\'Tom\', lastName=\'Dangote\', role=\'IT_SUPPORT\', department=\'System admin\'}','2025-07-03 08:31:44',5,'Tom Dangote'),(133,'LOGIN','Staff','5',NULL,'Staff{id=5, firstName=\'Tom\', lastName=\'Dangote\', role=\'IT_SUPPORT\', department=\'System admin\'}','2025-07-03 08:32:56',5,'Tom Dangote'),(134,'LOGIN','Staff','5',NULL,'Staff{id=5, firstName=\'Tom\', lastName=\'Dangote\', role=\'IT_SUPPORT\', department=\'System admin\'}','2025-07-03 08:34:07',5,'Tom Dangote'),(135,'LOGIN','Staff','5',NULL,'Staff{id=5, firstName=\'Tom\', lastName=\'Dangote\', role=\'IT_SUPPORT\', department=\'System admin\'}','2025-07-03 08:39:05',5,'Tom Dangote'),(136,'UPDATE','Patient','8','Patient{id=8, firstName=\'Ponsiano\', lastName=\'Kagwa\', email=\'pkagwa@gmail.com\'}','Patient{id=8, firstName=\'Ponsiano\', lastName=\'Kagwa\', email=\'pkagwa@gmail.com\'}','2025-07-03 09:01:16',5,'Tom Dangote'),(137,'LOGIN','Staff','5',NULL,'Staff{id=5, firstName=\'Tom\', lastName=\'Dangote\', role=\'IT_SUPPORT\', department=\'System admin\'}','2025-07-03 09:10:11',5,'Tom Dangote'),(138,'UPDATE','Patient','6','Patient{id=6, firstName=\'Pierre\', lastName=\'Fulani\', email=\'petefulani@gmail.com\'}','Patient{id=6, firstName=\'Pierre\', lastName=\'Fulani\', email=\'petefulani@gmail.com\'}','2025-07-03 09:10:29',5,'Tom Dangote'),(139,'LOGIN','Staff','5',NULL,'Staff{id=5, firstName=\'Tom\', lastName=\'Dangote\', role=\'IT_SUPPORT\', department=\'System admin\'}','2025-07-03 09:12:42',5,'Tom Dangote'),(140,'UPDATE','Patient','6','Patient{id=6, firstName=\'Pierre\', lastName=\'Fulani\', email=\'petefulani@gmail.com\'}','Patient{id=6, firstName=\'Pierre\', lastName=\'Fulani\', email=\'petefulani@gmail.com\'}','2025-07-03 09:12:54',5,'Tom Dangote'),(141,'UPDATE','Patient','8','Patient{id=8, firstName=\'Ponsiano\', lastName=\'Kagwa\', email=\'pkagwa@gmail.com\'}','Patient{id=8, firstName=\'Ponsiano\', lastName=\'Kagwa\', email=\'pkagwa@gmail.com\'}','2025-07-03 09:17:01',5,'Tom Dangote'),(142,'UPDATE','Patient','10','Patient{id=10, firstName=\'Peter\', lastName=\'Juuko\', email=\'kamapala@gmail.com\'}','Patient{id=10, firstName=\'Peter\', lastName=\'Juuko\', email=\'kamapala@gmail.com\'}','2025-07-03 09:34:55',5,'Tom Dangote'),(143,'UPDATE','Patient','11','Patient{id=11, firstName=\'Deo\', lastName=\'Segirinya\', email=\'tomsegi@gmail.com\'}','Patient{id=11, firstName=\'Deo\', lastName=\'Segirinya\', email=\'tomsegi@gmail.com\'}','2025-07-03 09:35:08',5,'Tom Dangote'),(144,'UPDATE','Patient','16','Patient{id=16, firstName=\'Jovan\', lastName=\'Kitayimbwa\', email=\'jovan12kita@gmail.com\'}','Patient{id=16, firstName=\'Jovan\', lastName=\'Kitayimbwa\', email=\'jovan12kita@gmail.com\'}','2025-07-03 09:35:35',5,'Tom Dangote'),(145,'CREATE','Patient','22',NULL,'Patient{id=22, firstName=\'dsadfdasf\', lastName=\'asfsaf\', email=\'sdafsa@gmfm.com\'}','2025-07-03 09:38:56',5,'Tom Dangote'),(146,'SOFT_DELETE','Patient','22','Patient{id=22, firstName=\'dsadfdasf\', lastName=\'asfsaf\', email=\'sdafsa@gmfm.com\'}',NULL,'2025-07-03 09:39:10',5,'Tom Dangote'),(147,'UPDATE','Patient','19','Patient{id=19, firstName=\'Xander\', lastName=\'duku\', email=\'xanduku@gmail.com\'}','Patient{id=19, firstName=\'Xander\', lastName=\'duku\', email=\'xanduku@gmail.com\'}','2025-07-03 09:40:16',5,'Tom Dangote'),(148,'UPDATE','Patient','20','Patient{id=20, firstName=\'Damian\', lastName=\'Sabiiti\', email=\'dsabiiti@gmail.com\'}','Patient{id=20, firstName=\'Damian\', lastName=\'Sabiiti\', email=\'dsabiiti@gmail.com\'}','2025-07-03 09:48:58',5,'Tom Dangote'),(149,'LOGIN','Staff','5',NULL,'Staff{id=5, firstName=\'Tom\', lastName=\'Dangote\', role=\'IT_SUPPORT\', department=\'System admin\'}','2025-07-03 10:35:36',5,'Tom Dangote'),(150,'LOGIN','Staff','5',NULL,'Staff{id=5, firstName=\'Tom\', lastName=\'Dangote\', role=\'IT_SUPPORT\', department=\'System admin\'}','2025-07-03 11:20:49',5,'Tom Dangote'),(151,'CREATE','Doctor','15',NULL,'Doctor{id=15, firstName=\'Thomas\', lastName=\'Kanye\', specialization=\'OPTOMETRY\'}','2025-07-03 11:23:59',NULL,NULL),(152,'LOGIN','Staff','5',NULL,'Staff{id=5, firstName=\'Tom\', lastName=\'Dangote\', role=\'IT_SUPPORT\', department=\'System admin\'}','2025-07-03 11:54:23',5,'Tom Dangote');
/*!40000 ALTER TABLE `audit_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `billings`
--

DROP TABLE IF EXISTS `billings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `billings` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `patient_id` bigint NOT NULL,
  `bill_date` date NOT NULL,
  `amount` double NOT NULL,
  `status` enum('PENDING','PAID','OVERDUE','CANCELLED') NOT NULL,
  `payment_method` varchar(20) DEFAULT NULL,
  `insurance_claim_id` varchar(50) DEFAULT NULL,
  `service_description` varchar(255) DEFAULT NULL,
  `tax_amount` double DEFAULT NULL,
  `discount_amount` double DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `patient_id` (`patient_id`),
  CONSTRAINT `billings_ibfk_1` FOREIGN KEY (`patient_id`) REFERENCES `patients` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `billings`
--

LOCK TABLES `billings` WRITE;
/*!40000 ALTER TABLE `billings` DISABLE KEYS */;
INSERT INTO `billings` VALUES (4,6,'2025-06-19',6000,'PAID','CASH',NULL,'Brace checkup appointment',NULL,NULL,0),(5,10,'2025-06-20',200000,'PAID','MOBILE_MONEY',NULL,'Teeth whitening',NULL,NULL,0),(6,10,'2025-06-20',-20000,'PENDING',NULL,NULL,'dgfdsyf',NULL,NULL,1),(7,12,'2025-06-26',60000,'PAID','CASH',NULL,'test bill',NULL,NULL,0),(8,11,'2025-06-26',20000,'PAID','CASH',NULL,'sadsdsa',NULL,NULL,0),(9,12,'2025-06-26',33333,'PENDING',NULL,NULL,'test2',NULL,NULL,0),(10,12,'2025-06-27',43244,'PAID','CASH',NULL,'sdfdsfdsfdf',NULL,NULL,1),(11,15,'2025-06-27',42344,'PENDING',NULL,NULL,'Eyes',NULL,NULL,1),(12,6,'2025-06-27',23545,'PENDING',NULL,NULL,'sdfsggg',NULL,NULL,1),(13,13,'2025-06-30',34000,'PAID','INSURANCE',NULL,'Next appointment',NULL,NULL,0),(14,6,'2025-06-30',32432,'PENDING',NULL,NULL,'qwrwqrew',NULL,NULL,1),(15,6,'2025-07-01',3143224532,'PENDING',NULL,NULL,'ewfsdfsf',NULL,NULL,1),(16,6,'2025-07-01',42335,'PENDING',NULL,NULL,'werwer',NULL,NULL,1),(17,12,'2025-07-01',53,'PENDING',NULL,NULL,'yteyt',NULL,NULL,1),(18,6,'2025-07-01',2312,'PENDING',NULL,NULL,'saddss',NULL,NULL,1),(19,6,'2025-07-01',322,'PENDING',NULL,NULL,'saf2ewqe',NULL,NULL,1),(20,8,'2025-07-01',20000,'PENDING',NULL,NULL,'Society',NULL,NULL,0),(21,19,'2025-07-01',30000,'PENDING',NULL,NULL,'New braces',NULL,NULL,0),(22,21,'2025-07-02',50000,'PENDING',NULL,NULL,'Checkup',NULL,NULL,0);
/*!40000 ALTER TABLE `billings` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `doctors`
--

DROP TABLE IF EXISTS `doctors`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `doctors` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `first_name` varchar(50) NOT NULL,
  `last_name` varchar(50) NOT NULL,
  `specialization` varchar(255) NOT NULL,
  `contact_number` varchar(20) NOT NULL,
  `email` varchar(100) NOT NULL,
  `is_deleted` tinyint(1) DEFAULT '0',
  `password` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `contact_number` (`contact_number`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `doctors`
--

LOCK TABLES `doctors` WRITE;
/*!40000 ALTER TABLE `doctors` DISABLE KEYS */;
INSERT INTO `doctors` VALUES (6,'Gordon','Virunga','ORTHOPEDICS','0786475843','gvirunga@hospital.com',0,'virunga123'),(7,'Hakimi','Yuvraj','OPTOMETRY','0785954894','hakimraj@hospital.com',0,'yuvraj123'),(8,'Chris','Balaam','CARDIOLOGY','0748393023','cbalaam@hospital.com',0,'balaam123'),(9,'Daniel','Kaluuya','NEUROLOGY','0722364781','dkaluuya@hospital.com',0,'kaluuya123'),(10,'George','Funke','CARDIOLOGY','0777382746','ggfunke@hospital.com',0,'funke123'),(11,'Erica','Ssozi','PAEDIATRICS','0795759455','ericassozi@hospital.com',0,'ssozi123'),(12,'Derrick','Ddungu','PSYCHIATRY','0797660554','dddungu@hospital.com',0,'ddungu123'),(13,'Cathy','Marunga','PSYCHIATRY','078595749','cmarunga',1,''),(14,'John','Sanya','GENERAL MEDICINE','0789404322','jsanya@hospital.com',0,'sanya123'),(15,'Thomas','Kanye','OPTOMETRY','0772869483','tomkanye@hospital.com',0,'kanye123');
/*!40000 ALTER TABLE `doctors` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `patients`
--

DROP TABLE IF EXISTS `patients`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `patients` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `first_name` varchar(50) NOT NULL,
  `last_name` varchar(50) NOT NULL,
  `date_of_birth` date NOT NULL,
  `contact_number` varchar(20) NOT NULL,
  `address` varchar(255) NOT NULL,
  `medical_history` text,
  `email` varchar(100) NOT NULL,
  `insurance_number` varchar(255) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT '0',
  `created_by` bigint DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  `updated_by` bigint DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `contact_number` (`contact_number`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `patients`
--

LOCK TABLES `patients` WRITE;
/*!40000 ALTER TABLE `patients` DISABLE KEYS */;
INSERT INTO `patients` VALUES (6,'Pierre','Fulani','2000-05-12','0780204837','1 Hopkins Close, Naalya','Back pain','petefulani@gmail.com',NULL,0,NULL,NULL,NULL,NULL),(8,'Ponsiano','Kagwa','1977-09-22','0795859909','P.O.Box 223 Wamala close, Nakivubo','Chest palpitations','pkagwa@gmail.com',NULL,0,NULL,NULL,NULL,NULL),(10,'Peter','Juuko','2010-12-12','0786965442','Kampala Road','Toothache','kamapala@gmail.com',NULL,0,NULL,NULL,NULL,NULL),(11,'Deo','Segirinya','2020-03-30','0786958594','1 Numaro close, Adjumani','Stomach pain','tomsegi@gmail.com',NULL,0,NULL,NULL,NULL,NULL),(12,'test','patient','2002-06-30','0786905950','123 Drive',NULL,'testpatient@gmail.com',NULL,1,NULL,NULL,NULL,NULL),(13,'Cordon','hamis','2001-01-01','0837457865','123 Close',NULL,'cordonhamis@gmail.com',NULL,0,NULL,NULL,NULL,NULL),(14,'Tendo','Mukasa','1986-10-29','0775839201','#45 Busulwa close, Misingo',NULL,'tmukasa@gmail.com',NULL,0,NULL,NULL,NULL,NULL),(15,'Xavier','Lubwama','1999-03-03','0784930332','Plot 12 Reginald close',NULL,'xavlubwama@gmail.com',NULL,0,NULL,NULL,NULL,NULL),(16,'Jovan','Kitayimbwa','2000-09-30','0758758622','Kampala','Broken arm','jovan12kita@gmail.com',NULL,0,NULL,NULL,NULL,NULL),(17,'Yokanna','Domingo','1955-12-25','0575809409','233 Bullion drive',NULL,'yokdomingo@gmail.com',NULL,0,NULL,NULL,NULL,NULL),(19,'Xander','duku','2005-06-28','0798292102','1244 Fulham path, Nsambya','Swollen jaw','xanduku@gmail.com',NULL,0,NULL,NULL,NULL,NULL),(20,'Damian','Sabiiti','2009-03-22','0738372393','Bombo','Painful urinations','dsabiiti@gmail.com',NULL,0,7,'2025-06-30 10:56:02',NULL,NULL),(21,'Walter','Ocan','1940-02-03','0785985944','Plot 345, Wambuzi road, Kampala',NULL,'wocan223@gmail.com',NULL,0,NULL,NULL,NULL,NULL),(22,'dsadfdasf','asfsaf','2025-07-01','0464335262','asfdsasds','safsaf','sdafsa@gmfm.com',NULL,1,NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `patients` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `staff`
--

DROP TABLE IF EXISTS `staff`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `staff` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `first_name` varchar(50) NOT NULL,
  `last_name` varchar(50) NOT NULL,
  `email` varchar(100) NOT NULL,
  `contact_number` varchar(20) NOT NULL,
  `role` enum('RECEPTIONIST','NURSE','PHARMACIST','ADMIN','CLEANER','ACCOUNTANT','SECURITY','LAB_TECHNICIAN','IT_SUPPORT','OTHER') NOT NULL,
  `department` varchar(100) NOT NULL,
  `hire_date` date NOT NULL,
  `password` varchar(255) NOT NULL,
  `is_deleted` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `staff`
--

LOCK TABLES `staff` WRITE;
/*!40000 ALTER TABLE `staff` DISABLE KEYS */;
INSERT INTO `staff` VALUES (4,'John','Mufujo','jmufu@hospital.com','0748549038','CLEANER','SUPPORT','2007-06-06','jmufu123',0),(5,'Tom','Dangote','tomdango@hospital.com','0785968494','IT_SUPPORT','System admin','2016-03-24','admin123',0),(6,'Grace','Nakiwala','gracenaki@hospital.com','0773445674','RECEPTIONIST','reception','2022-05-01','gracie123',0),(7,'dummy','staff','teststaff@hospital.com','0796869500','OTHER','other','2025-06-25','dummy123',1),(8,'Harry','John','hjohn@hospital.com','0777328293','SECURITY','Security','2019-08-09','hjohn123',0),(10,'Theo','Funke','theofunke@hospital.com','0775867459','PHARMACIST','Pharmacy','2009-05-15','theo123',0),(12,'Donald','Sebabi','dsebabi@gmail.com','07847483202','PHARMACIST','Pharmacy','2022-12-01','dsebabi123',0);
/*!40000 ALTER TABLE `staff` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-07-03 15:07:39

CREATE DATABASE IF NOT EXISTS `epicerie` /*!40100 DEFAULT CHARACTER SET utf8
  COLLATE utf8_unicode_ci */;
USE `epicerie`;
-- MySQL dump 10.13  Distrib 5.6.19, for osx10.7 (i386)
--
-- Host: 45.55.144.190    Database: epicerie
-- ------------------------------------------------------
-- Server version	5.5.41-0ubuntu0.14.04.1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT = @@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS = @@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION = @@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE = @@TIME_ZONE */;
/*!40103 SET TIME_ZONE = '+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS = @@UNIQUE_CHECKS, UNIQUE_CHECKS = 0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS = @@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS = 0 */;
/*!40101 SET @OLD_SQL_MODE = @@SQL_MODE, SQL_MODE = 'NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES = @@SQL_NOTES, SQL_NOTES = 0 */;

--
-- Table structure for table `crawler`
--

DROP TABLE IF EXISTS `crawler`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `crawler` (
  `id`          INT(11)    NOT NULL AUTO_INCREMENT,
  `multithread` TINYINT(1) NOT NULL,
  `website_id`  INT(11)    NOT NULL,
  `enabled`     TINYINT(1) NOT NULL,
  `name`        VARCHAR(45)         DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_crawler_website_id_idx` (`website_id`),
  CONSTRAINT `fk_crawler_website_id` FOREIGN KEY (`website_id`) REFERENCES `website` (`website_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
)
  ENGINE =InnoDB
  AUTO_INCREMENT =5
  DEFAULT CHARSET =utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `crawler`
--

LOCK TABLES `crawler` WRITE;
/*!40000 ALTER TABLE `crawler` DISABLE KEYS */;
INSERT INTO `crawler` VALUES (3, 0, 1, 1, 'Supermarches'), (4, 1, 2, 0, 'Walmart');
/*!40000 ALTER TABLE `crawler` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product`
--

DROP TABLE IF EXISTS `product`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `product` (
  `product_id`          INT(11)                 NOT NULL AUTO_INCREMENT,
  `product_description` VARCHAR(256)
                        COLLATE utf8_unicode_ci NOT NULL,
  `product_size`        VARCHAR(100)
                        COLLATE utf8_unicode_ci NOT NULL,
  `product_origin`      VARCHAR(100)
                        COLLATE utf8_unicode_ci          DEFAULT NULL,
  `product_introduced`  DATE                    NOT NULL,
  `product_thumbnail`   VARCHAR(256)
                        COLLATE utf8_unicode_ci NOT NULL,
  `product_sku`         INT(11)                 NOT NULL,
  `product_store_id`    INT(11)                 NOT NULL,
  `product_category_id` INT(11)                 NOT NULL,
  PRIMARY KEY (`product_id`),
  KEY `fk_product_store_id` (`product_store_id`),
  KEY `fk_product_category_id` (`product_category_id`),
  CONSTRAINT `fk_product_category_id` FOREIGN KEY (`product_category_id`) REFERENCES `product_category` (`category_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_product_store_id` FOREIGN KEY (`product_store_id`) REFERENCES `product_store` (`store_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
)
  ENGINE =InnoDB
  AUTO_INCREMENT =903
  DEFAULT CHARSET =utf8
  COLLATE =utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product`
--

LOCK TABLES `product` WRITE;
/*!40000 ALTER TABLE `product` DISABLE KEYS */;
/*!40000 ALTER TABLE `product` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product_category`
--

DROP TABLE IF EXISTS `product_category`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `product_category` (
  `category_id`   INT(11)                 NOT NULL AUTO_INCREMENT,
  `category_name` VARCHAR(100)
                  COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`category_id`)
)
  ENGINE =InnoDB
  AUTO_INCREMENT =2
  DEFAULT CHARSET =utf8
  COLLATE =utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product_category`
--

LOCK TABLES `product_category` WRITE;
/*!40000 ALTER TABLE `product_category` DISABLE KEYS */;
INSERT INTO `product_category` VALUES (1, 'Temporary');
/*!40000 ALTER TABLE `product_category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product_price`
--

DROP TABLE IF EXISTS `product_price`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `product_price` (
  `price_id`             INT(11)    NOT NULL     AUTO_INCREMENT,
  `price_price`          DOUBLE     NOT NULL,
  `price_rebate`         DOUBLE     NOT NULL,
  `price_rebate_percent` INT(11)    NOT NULL,
  `price_start`          DATE       NOT NULL,
  `price_end`            DATE       NOT NULL,
  `price_active`         TINYINT(1) NOT NULL,
  `price_note`           VARCHAR(256)
                         COLLATE utf8_unicode_ci DEFAULT NULL,
  `price_quantity`       INT(11)    NOT NULL,
  `product_id`           INT(11)    NOT NULL,
  PRIMARY KEY (`price_id`),
  KEY `fk_product_id` (`product_id`),
  CONSTRAINT `fk_product_price_product` FOREIGN KEY (`product_id`) REFERENCES `product` (`product_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
)
  ENGINE =InnoDB
  AUTO_INCREMENT =903
  DEFAULT CHARSET =utf8
  COLLATE =utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product_price`
--

LOCK TABLES `product_price` WRITE;
/*!40000 ALTER TABLE `product_price` DISABLE KEYS */;
/*!40000 ALTER TABLE `product_price` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product_store`
--

DROP TABLE IF EXISTS `product_store`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `product_store` (
  `store_id`   INT(11)                 NOT NULL AUTO_INCREMENT,
  `store_name` VARCHAR(100)
               COLLATE utf8_unicode_ci NOT NULL,
  `store_slug` VARCHAR(100)
               COLLATE utf8_unicode_ci          DEFAULT NULL,
  `website_id` INT(11)                 NOT NULL,
  PRIMARY KEY (`store_id`),
  KEY `fk_website_id` (`website_id`),
  CONSTRAINT `fk_website_id` FOREIGN KEY (`website_id`) REFERENCES `website` (`website_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
)
  ENGINE =InnoDB
  AUTO_INCREMENT =7
  DEFAULT CHARSET =utf8
  COLLATE =utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product_store`
--

LOCK TABLES `product_store` WRITE;
/*!40000 ALTER TABLE `product_store` DISABLE KEYS */;
INSERT INTO `product_store`
VALUES (1, 'IGA', 'IGA', 1), (2, 'Metro', 'METRO', 1), (3, 'Loblaws', 'LOBLAWS', 1), (4, 'Maxi', 'MAXI', 1),
  (5, 'Super C', 'SUPER C', 1), (6, 'Walmart', 'WALMART', 2);
/*!40000 ALTER TABLE `product_store` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `website`
--

DROP TABLE IF EXISTS `website`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `website` (
  `website_id`   INT(11)      NOT NULL AUTO_INCREMENT,
  `website_url`  VARCHAR(255) NOT NULL,
  `website_name` VARCHAR(100) NOT NULL,
  PRIMARY KEY (`website_id`)
)
  ENGINE =InnoDB
  AUTO_INCREMENT =3
  DEFAULT CHARSET =utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `website`
--

LOCK TABLES `website` WRITE;
/*!40000 ALTER TABLE `website` DISABLE KEYS */;
INSERT INTO `website` VALUES (1, 'www.supermarches.ca', 'Supermarchés'), (2, 'www.walmart.ca/fr/', 'Walmart');
/*!40000 ALTER TABLE `website` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `website_category`
--

DROP TABLE IF EXISTS `website_category`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `website_category` (
  `category_id`         INT(11)      NOT NULL AUTO_INCREMENT,
  `category_name`       VARCHAR(100) NOT NULL,
  `category_slug`       VARCHAR(100) NOT NULL,
  `website_id`          INT(11)      NOT NULL,
  `product_category_id` INT(11)      NOT NULL,
  PRIMARY KEY (`category_id`),
  KEY `fk_category_website_id` (`website_id`),
  KEY `fk_website_product_category_id` (`product_category_id`),
  CONSTRAINT `fk_category_website_id` FOREIGN KEY (`website_id`) REFERENCES `website` (`website_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_website_product_category_id` FOREIGN KEY (`product_category_id`) REFERENCES `product_category` (`category_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
)
  ENGINE =InnoDB
  AUTO_INCREMENT =65
  DEFAULT CHARSET =utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `website_category`
--

LOCK TABLES `website_category` WRITE;
/*!40000 ALTER TABLE `website_category` DISABLE KEYS */;
INSERT INTO `website_category`
VALUES (23, 'Bières et vins', '3', 1, 1), (24, 'Biscuits, collations et friandises', '4', 1, 1),
  (25, 'Boissons, eau et jus', '5', 1, 1), (26, 'Boulangerie et pâtisserie', '6', 1, 1),
  (27, 'Café, thé et tisanes', '8', 1, 1), (28, 'Charcuterie', '9', 1, 1),
  (29, 'Confitures, tartinades et sirops', '10', 1, 1), (30, 'Fruits de mer et poissons', '11', 1, 1),
  (31, 'Fruits, légumes et noix', '12', 1, 1), (32, 'Pâtes, farine et oeufs', '13', 1, 1),
  (33, 'Produits laitiers', '15', 1, 1), (34, 'Céréales, gruau et riz', '16', 1, 1), (35, 'Viandes', '17', 1, 1),
  (36, 'Volaille', '18', 1, 1), (37, 'Produits pour les animaux', '20', 1, 1), (38, 'Soins du corps', '21', 1, 1),
  (39, 'Produits ménagers', '22', 1, 1), (40, 'Contenants, outils et papiers', '23', 1, 1),
  (41, 'Condiments, huiles et sauces', '340', 1, 1), (42, 'Conserves, prêt-à-manger et soupes', '452', 1, 1),
  (43, 'Divers', '520', 1, 1), (44, 'Aliments surgelés', '612', 1, 1), (45, 'Produits pour les bébés', '648', 1, 1),
  (46, 'Articles pour bébés', 'articles-pour-bebes/N-2113', 2, 1),
  (47, 'Boulangerie et pâtisserie', 'boulangerie-patisserie/N-333', 2, 1),
  (48, 'Articles de cuisson, épices et assaisonnements', 'articles-de-cuisson-epices-et-assaisonnements/N-328', 2, 1),
  (49, 'Boissons', 'boissons/N-2114', 2, 1), (50, 'Aliments en conserve', 'aliments-en-conserve/N-329', 2, 1),
  (51, 'Céréales et articles pour le déjeuner', 'cereales-et-articles-pour-le-dejeuner/N-330', 2, 1),
  (52, 'Collations', 'collations/N-1022', 2, 1),
  (53, 'Produits nettoyants et articles en papier', 'produits-nettoyants-et-articles-en-papier/N-1023', 2, 1),
  (54, 'Condiments et cornichons', 'condiments-et-cornichons/N-331', 2, 1),
  (55, 'Contenants et articles de table jetables', 'contenants-et-articles-de-table-jetables/N-2115', 2, 1),
  (56, 'Produits laitiers et oeufs', 'produits-laitiers-et-oeufs/N-334', 2, 1),
  (57, 'Aliments surgeles', 'aliments-surgeles/N-1021', 2, 1),
  (58, 'Aliments internationaux', 'aliments-internationaux/N-3206', 2, 1),
  (59, 'Viande et fruits de mer', 'viande-et-fruits-de-mer/N-2117', 2, 1),
  (60, 'Produit naturels et biologiques', 'produits-naturels-et-biologiques/N-2842', 2, 1),
  (61, 'Huiles vinaigres et vinaigrettes', 'huiles-vinaigres-et-vinaigrettes/N-3042', 2, 1),
  (62, 'Fruits et légumes frais', 'fruits-et-legumes-frais/N-337', 2, 1),
  (63, 'Riz, pâtes et nouilles', 'riz-pates-et-nouilles/N-3043', 2, 1),
  (64, 'Tartinades, confitures et miel', 'tartinades-confitures-et-miel/N-3208', 2, 1);
/*!40000 ALTER TABLE `website_category` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE = @OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE = @OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS = @OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS = @OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT = @OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS = @OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION = @OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES = @OLD_SQL_NOTES */;

-- Dump completed on 2015-04-05 23:24:22

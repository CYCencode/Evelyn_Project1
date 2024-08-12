-- MySQL dump 10.13  Distrib 8.0.38, for Win64 (x86_64)
--
-- Host: localhost    Database: stylish
-- ------------------------------------------------------
-- Server version	8.0.38

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
-- Table structure for table `campaigns`
--

DROP TABLE IF EXISTS `campaigns`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `campaigns` (
                             `id` int NOT NULL AUTO_INCREMENT,
                             `product_id` int NOT NULL,
                             `picture` varchar(255) NOT NULL,
                             `story` text,
                             PRIMARY KEY (`id`),
                             KEY `product_id` (`product_id`),
                             CONSTRAINT `campaigns_ibfk_1` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `campaigns`
--

LOCK TABLES `campaigns` WRITE;
/*!40000 ALTER TABLE `campaigns` DISABLE KEYS */;
INSERT INTO `campaigns` VALUES (1,13,'13-camp.jpg','獻給這個夏天最耀眼的你。');
/*!40000 ALTER TABLE `campaigns` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `categories`
--

DROP TABLE IF EXISTS `categories`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `categories` (
                              `id` int NOT NULL AUTO_INCREMENT,
                              `name` varchar(255) NOT NULL,
                              PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `categories`
--

LOCK TABLES `categories` WRITE;
/*!40000 ALTER TABLE `categories` DISABLE KEYS */;
INSERT INTO `categories` VALUES (1,'women'),(2,'accessories'),(3,'men');
/*!40000 ALTER TABLE `categories` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `colors`
--

DROP TABLE IF EXISTS `colors`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `colors` (
                          `id` int NOT NULL AUTO_INCREMENT,
                          `product_id` int DEFAULT NULL,
                          `name` varchar(255) NOT NULL,
                          `code` varchar(10) NOT NULL,
                          PRIMARY KEY (`id`),
                          KEY `product_id` (`product_id`),
                          CONSTRAINT `colors_ibfk_1` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `colors`
--

LOCK TABLES `colors` WRITE;
/*!40000 ALTER TABLE `colors` DISABLE KEYS */;
INSERT INTO `colors` VALUES
                         (1,1,'粉','#f2abd1'),
                         (2,2,'白','#fafafa'),
                         (3,3,'黑','#080808'),
                         (4,4,'黃','#eac22e'),
                         (5,5,'白','#fafafa'),
                         (6,6,'綠','#198a1d'),
                         (7,7,'黃','#eed111'),
                         (8,8,'白','#fafafa'),
                         (9,9,'粉','#e48b8b'),
                         (10,10,'藍','#12349b'),
                         (11,11,'綠','#1cceb1'),
                         (12,12,'黃','#cec31c'),
                         (13,13,'橘','#e59306'),
                         (14,14,'綠','#06e5ad'),
                         (15,15,'黑','#000000');
/*!40000 ALTER TABLE `colors` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `hotproducts`
--

DROP TABLE IF EXISTS `hotproducts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `hotproducts` (
                               `id` int NOT NULL AUTO_INCREMENT,
                               `hot_id` int DEFAULT NULL,
                               `product_id` int DEFAULT NULL,
                               PRIMARY KEY (`id`),
                               KEY `hot_id` (`hot_id`),
                               KEY `product_id` (`product_id`),
                               CONSTRAINT `hotproducts_ibfk_1` FOREIGN KEY (`hot_id`) REFERENCES `hots` (`id`),
                               CONSTRAINT `hotproducts_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hotproducts`
--

LOCK TABLES `hotproducts` WRITE;
/*!40000 ALTER TABLE `hotproducts` DISABLE KEYS */;
/*!40000 ALTER TABLE `hotproducts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `hots`
--

DROP TABLE IF EXISTS `hots`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `hots` (
                        `id` int NOT NULL AUTO_INCREMENT,
                        `title` varchar(255) DEFAULT NULL,
                        PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hots`
--

LOCK TABLES `hots` WRITE;
/*!40000 ALTER TABLE `hots` DISABLE KEYS */;
/*!40000 ALTER TABLE `hots` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `images`
--

DROP TABLE IF EXISTS `images`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `images` (
                          `id` int NOT NULL AUTO_INCREMENT,
                          `product_id` int DEFAULT NULL,
                          `url` varchar(255) DEFAULT NULL,
                          PRIMARY KEY (`id`),
                          KEY `product_id` (`product_id`),
                          CONSTRAINT `images_ibfk_1` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=43 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `images`
--

LOCK TABLES `images` WRITE;
/*!40000 ALTER TABLE `images` DISABLE KEYS */;
INSERT INTO `images` VALUES
                         (1,1,'2.jpg'),
                         (2,1,'2-1.jpg'),
                         (3,2,'1-1.jpg'),
                         (4,2,'1.jpg'),
                         (5,3,'3-1.jpg'),
                         (6,3,'3.jpg'),
                         (7,4,'4.jpg'),
                         (8,4,'4-1.jpg'),
                         (9,5,'5-1.jpg'),
                         (10,5,'5.jpg'),
                         (11,6,'6.jpg'),
                         (12,6,'6-1.jpg'),
                         (13,7,'7.jpg'),
                         (14,7,'7-1.jpg'),
                         (15,8,'8-1.jpg'),
                         (16,8,'8.jpg'),
                         (17,9,'9.jpg'),
                         (18,9,'9-1.jpg'),
                         (19,10,'10.jpg'),
                         (20,10,'10-1.jpg'),
                         (21,11,'11-1.jpg'),
                         (22,11,'11.jpg'),
                         (23,12,'12.jpg'),
                         (24,12,'12-1.jpg'),
                         (25,13,'13-1.jpg'),
                         (26,13,'13.jpg'),
                         (27,14,'14.jpg'),
                         (28,14,'14-1.jpg'),
                         (29,15,'15.jpg'),
                         (30,15,'15-1.jpg');
/*!40000 ALTER TABLE `images` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `orderproduct`
--

DROP TABLE IF EXISTS `orderproduct`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orderproduct` (
                                `order_id` int NOT NULL,
                                `product_id` int NOT NULL,
                                `size` varchar(10) NOT NULL,
                                `qty` int DEFAULT NULL,
                                PRIMARY KEY (`order_id`,`product_id`,`size`),
                                KEY `product_id` (`product_id`),
                                CONSTRAINT `orderproduct_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`),
                                CONSTRAINT `orderproduct_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orderproduct`
--

LOCK TABLES `orderproduct` WRITE;
/*!40000 ALTER TABLE `orderproduct` DISABLE KEYS */;
/*!40000 ALTER TABLE `orderproduct` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `orders`
--

DROP TABLE IF EXISTS `orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orders` (
                          `id` int NOT NULL AUTO_INCREMENT,
                          `variant_id` int NOT NULL,
                          `qty` int NOT NULL,
                          `shipping` varchar(50) DEFAULT NULL,
                          `payment` varchar(50) NOT NULL,
                          `subtotal` decimal(10,2) NOT NULL,
                          `freight` decimal(10,2) NOT NULL,
                          `total` decimal(10,2) NOT NULL,
                          `recipient_name` varchar(255) NOT NULL,
                          `recipient_phone` varchar(50) NOT NULL,
                          `recipient_email` varchar(255) NOT NULL,
                          `recipient_address` varchar(255) NOT NULL,
                          `recipient_time` enum('morning','afternoon','anytime') NOT NULL,
                          `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                          `payment_status` tinyint(1) NOT NULL,
                          PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orders`
--

LOCK TABLES `orders` WRITE;
/*!40000 ALTER TABLE `orders` DISABLE KEYS */;
/*!40000 ALTER TABLE `orders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `products`
--

DROP TABLE IF EXISTS `products`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `products` (
                            `id` int NOT NULL AUTO_INCREMENT,
                            `category_id` int DEFAULT NULL,
                            `title` varchar(255) NOT NULL,
                            `description` text,
                            `price` int NOT NULL,
                            `texture` varchar(255) DEFAULT NULL,
                            `wash` varchar(255) DEFAULT NULL,
                            `place` varchar(255) DEFAULT NULL,
                            `note` text,
                            `story` text,
                            PRIMARY KEY (`id`),
                            KEY `category_id` (`category_id`),
                            CONSTRAINT `products_ibfk_1` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `products`
--

LOCK TABLES `products` WRITE;
/*!40000 ALTER TABLE `products` DISABLE KEYS */;
INSERT INTO `products` VALUES
                           (1,1,'粉色碎花洋裝','讓你搖身一變，成為時尚俏皮的文青',2200,'棉、聚脂纖維','手洗','日本','本產品使用天然染料，手洗可維持最佳色調','飄逸的裙襬，粉嫩的色調與碎花點綴，輕鬆營造慵懶氛圍。'),
                           (2,1,'渡假風白色細肩帶洋裝','炎炎夏日，是時候為衣櫥添加一件簡約可愛的細肩帶洋裝!',750,'棉、聚脂纖維','手洗','日本','實品顏色以單品照為主','輕盈涼爽的材質，讓你就算在炎炎夏日下還是可以輕鬆擁有美美的造型。'),
                           (3,2,'大帽沿低調遮陽帽','帶給你女王氣場的無死角遮陽帽',1200,'棉、聚脂纖維','水洗','越南','實品顏色以單品照為主','越南手編工藝，簡約有形。'),
                           (4,1,'亮麗一字領長洋裝','可愛又迷人的你值得擁有',1600,'棉麻','水洗','越南','實品顏色以單品照為主','聯手越南新銳設計師打造，是你絕不能錯過的超值商品。'),
                           (5,1,'北歐風印花開岔白洋裝','簡約俐落的造型',1750,'棉、聚脂纖維','水洗','韓國','實品顏色以單品照為主','萬年不敗的經典造型，你值得入手一件。'),
                           (6,1,'簍空深綠長洋裝','展現你的率性美',1800,'other','手洗','日本','實品顏色以單品照為主','專為歐美系的你量身打造，環保合成材質，愛美之餘也愛護地球。'),
                           (7,1,'挖肩橘黃色細肩帶洋裝','簡約俐落又可愛的基本款造型',1600,'other','手洗','韓國','簡約美型就是你','穿上直接視覺減五公斤，環保合成材質，愛美之餘也愛護地球。'),
                           (8,1,'學院風襯衫洋裝','青春洋溢簡約可愛',650,'棉、聚脂纖維','水洗','日本','穿上它化身最young的美眉','寬鬆的設計，自在的活動，跑跳不拘束!'),
                           (9,1,'裸色繡花長洋裝','出自大師之手，錯過不再',2800,'棉 100%','手洗','日本','穿上它化身最有風格的美眉','十年磨一件的經典，細節讓人驚嘆。'),
                           (10,1,'花花深色九分袖洋裝','清涼一夏的好選擇',2480,'棉 100%','手洗','韓國','建議手洗保存天然印花染料','炎炎夏日又想藏掰掰袖的你不可以錯過。'),
                           (11,1,'薄荷綠一字領洋裝','出自大師之手，清涼一夏的好選擇',2300,'棉 100%','手洗','日本','獨特剪裁讓你獨樹一格','十年磨一件，給最特別的你。'),
                           (12,1,'加州陽光系長洋裝','出自大師之手，清涼一夏的好選擇',1580,'other','手洗','日本','獨特剪裁讓你獨樹一格','炎炎夏日釋放雙腳，來件輕飄飄的洋裝'),
                           (13,1,'桔黃深v洋裝','獻給最亮眼的你',3880,'棉 100%','手洗','日本','衣服皺摺為正常現象','本季主打設計商品，穿上它讓你成為這個夏天最亮眼的目光焦點。'),
                           (14,1,'墨綠漸層洋裝','都會女子的最佳穿搭',1880,'棉麻','乾洗','日本','本商品適合乾洗','簡約大方的設計，獻給需要機能性又想要美美的你。'),
                           (15,3,'黑色縮口長褲','簡約俐落有型',1880,'棉 100%','水洗','韓國','實品顏色以單品照為主','獻給愛好運動又想帥氣有型的你。');
/*!40000 ALTER TABLE `products` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sizes`
--

DROP TABLE IF EXISTS `sizes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sizes` (
                         `id` int NOT NULL AUTO_INCREMENT,
                         `product_id` int DEFAULT NULL,
                         `size` varchar(10) NOT NULL,
                         PRIMARY KEY (`id`),
                         KEY `product_id` (`product_id`),
                         CONSTRAINT `sizes_ibfk_1` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sizes`
--

LOCK TABLES `sizes` WRITE;
/*!40000 ALTER TABLE `sizes` DISABLE KEYS */;
INSERT INTO `sizes` VALUES
                        (1,1,'S'),(2,2,'F-size'),(3,3,'F-size'),(4,4,'M'),(5,5,'S'),(6,6,'L'),(7,7,'M'),
                        (8,8,'F-size'),(9,9,'F-size'),(10,10,'F-size'),(11,11,'F-size'),(12,12,'M'),(13,13,'M'),(14,14,'S'),
                        (15,15,'S'),(16,15,'M'),(17,15,'L');
/*!40000 ALTER TABLE `sizes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
                         `id` int NOT NULL AUTO_INCREMENT,
                         `provider` varchar(255) NOT NULL,
                         `name` varchar(255) NOT NULL,
                         `email` varchar(255) NOT NULL,
                         `password` varchar(255) DEFAULT NULL,
                         `picture` varchar(255) NOT NULL,
                         PRIMARY KEY (`id`),
                         UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'native','eve','cyc.evelynchang@gmail.com','$2a$10$fedCCZ9CKsPXM0vnFleE5OsZRb2ZZeE7qqO0lTy0tXcSh2duLdVlm',''),(2,'facebook','張育菁','eve870703@gmail.com',NULL,'https://platform-lookaside.fbsbx.com/platform/profilepic/?asid=8523474794363892&height=50&width=50&ext=1724744737&hash=Abb-6HG3gfZaKLTK8sjKd0hN');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `variants`
--

DROP TABLE IF EXISTS `variants`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `variants` (
                            `id` int NOT NULL AUTO_INCREMENT,
                            `product_id` int DEFAULT NULL,
                            `color_id` int DEFAULT NULL,
                            `size_id` int DEFAULT NULL,
                            `stock` int NOT NULL,
                            PRIMARY KEY (`id`),
                            KEY `product_id` (`product_id`),
                            KEY `color_id` (`color_id`),
                            KEY `size_id` (`size_id`),
                            CONSTRAINT `variants_ibfk_1` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`),
                            CONSTRAINT `variants_ibfk_2` FOREIGN KEY (`color_id`) REFERENCES `colors` (`id`),
                            CONSTRAINT `variants_ibfk_3` FOREIGN KEY (`size_id`) REFERENCES `sizes` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `variants`
--

LOCK TABLES `variants` WRITE;
/*!40000 ALTER TABLE `variants` DISABLE KEYS */;
INSERT INTO `variants` VALUES
                           (1,1,1,1,5),
                           (2,2,2,2,5),
                           (3,3,3,3,3),
                           (4,4,4,4,1),
                           (5,5,5,5,1),
                           (6,6,6,6,1),
                           (7,7,7,7,1),
                           (8,8,8,8,3),
                           (9,9,9,9,2),
                           (10,10,10,10,3),
                           (11,11,11,11,2),
                           (12,12,12,12,4),
                           (13,13,13,13,3),
                           (14,14,14,14,2),
                           (15,15,15,15,2),
                           (16,15,15,16,2),
                           (17,15,15,17,2);
/*!40000 ALTER TABLE `variants` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-08-01 15:17:57

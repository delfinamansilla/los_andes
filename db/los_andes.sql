CREATE DATABASE  IF NOT EXISTS `los_andes` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `los_andes`;
-- MySQL dump 10.13  Distrib 8.0.38, for Win64 (x86_64)
--
-- Host: localhost    Database: los_andes
-- ------------------------------------------------------
-- Server version	9.0.0

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `actividad`
--

DROP TABLE IF EXISTS `actividad`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `actividad` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(255) NOT NULL,
  `cupo` int NOT NULL,
  `descripcion` varchar(255) NOT NULL,
  `inscripcion_desde` date NOT NULL,
  `inscripcion_hasta` date NOT NULL,
  `id_profesor` int NOT NULL,
  `id_cancha` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `actividad_profesor_id_foreign_idx` (`id_profesor`),
  KEY `actividad_cancha_id_foreign_idx` (`id_cancha`),
  CONSTRAINT `actividad_cancha_id_foreign` FOREIGN KEY (`id_cancha`) REFERENCES `cancha` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `actividad_profesor_id_foreign` FOREIGN KEY (`id_profesor`) REFERENCES `profesor` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `actividad`
--

LOCK TABLES `actividad` WRITE;
/*!40000 ALTER TABLE `actividad` DISABLE KEYS */;
/*!40000 ALTER TABLE `actividad` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `alquiler_cancha`
--

DROP TABLE IF EXISTS `alquiler_cancha`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `alquiler_cancha` (
  `id` int NOT NULL AUTO_INCREMENT,
  `fecha_alquiler` date NOT NULL,
  `hora_desde` varchar(255) NOT NULL,
  `hora_hasta` varchar(255) NOT NULL,
  `id_usuario` int NOT NULL,
  `id_cancha` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `alquiler_cancha_cancha_id_foreign_idx` (`id_cancha`),
  KEY `alquiler_cancha_usuario_id_foreign_idx` (`id_usuario`),
  CONSTRAINT `alquiler_cancha_cancha_id_foreign` FOREIGN KEY (`id_cancha`) REFERENCES `cancha` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `alquiler_cancha_usuario_id_foreign` FOREIGN KEY (`id_usuario`) REFERENCES `usuario` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `alquiler_cancha`
--

LOCK TABLES `alquiler_cancha` WRITE;
/*!40000 ALTER TABLE `alquiler_cancha` DISABLE KEYS */;
/*!40000 ALTER TABLE `alquiler_cancha` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `alquiler_salon`
--

DROP TABLE IF EXISTS `alquiler_salon`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `alquiler_salon` (
  `id` int NOT NULL AUTO_INCREMENT,
  `fecha` date NOT NULL,
  `hora_desde` varchar(255) NOT NULL,
  `hora_antes` varchar(255) NOT NULL,
  `id_salon` int NOT NULL,
  `id_usuario` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `alquiler_salon_salon_id_foreign_idx` (`id_salon`),
  KEY `alquiler_salon_usuario_id_foreign_idx` (`id_usuario`),
  CONSTRAINT `alquiler_salon_salon_id_foreign` FOREIGN KEY (`id_salon`) REFERENCES `salon` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `alquiler_salon_usuario_id_foreign` FOREIGN KEY (`id_usuario`) REFERENCES `usuario` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `alquiler_salon`
--

LOCK TABLES `alquiler_salon` WRITE;
/*!40000 ALTER TABLE `alquiler_salon` DISABLE KEYS */;
/*!40000 ALTER TABLE `alquiler_salon` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cancha`
--

DROP TABLE IF EXISTS `cancha`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cancha` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nro_cancha` int NOT NULL,
  `ubicacion` varchar(255) NOT NULL,
  `descripcion` varchar(255) NOT NULL,
  `tamanio` float NOT NULL,
  `estado` tinyint NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cancha`
--

LOCK TABLES `cancha` WRITE;
/*!40000 ALTER TABLE `cancha` DISABLE KEYS */;
/*!40000 ALTER TABLE `cancha` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cuota`
--

DROP TABLE IF EXISTS `cuota`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cuota` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nro_cuota` int NOT NULL,
  `fecha_cuota` date NOT NULL,
  `fecha_vencimiento` date NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cuota`
--

LOCK TABLES `cuota` WRITE;
/*!40000 ALTER TABLE `cuota` DISABLE KEYS */;
/*!40000 ALTER TABLE `cuota` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `horario`
--

DROP TABLE IF EXISTS `horario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `horario` (
  `id` int NOT NULL AUTO_INCREMENT,
  `dia` varchar(255) NOT NULL,
  `hora_desde` varchar(255) NOT NULL,
  `hora_hasta` varchar(255) NOT NULL,
  `id_activdad` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `horario_actividad_id_foreign_idx` (`id_activdad`),
  CONSTRAINT `horario_actividad_id_foreign` FOREIGN KEY (`id_activdad`) REFERENCES `actividad` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `horario`
--

LOCK TABLES `horario` WRITE;
/*!40000 ALTER TABLE `horario` DISABLE KEYS */;
/*!40000 ALTER TABLE `horario` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `inscripcion`
--

DROP TABLE IF EXISTS `inscripcion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `inscripcion` (
  `id` int NOT NULL AUTO_INCREMENT,
  `fecha_inscripcion` date NOT NULL,
  `id_usuario` int NOT NULL,
  `id_actividad` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `inscripcion_actividad_id_foreign_idx` (`id_actividad`),
  KEY `inscripcion_usuario_id_foreign_idx` (`id_usuario`),
  CONSTRAINT `inscripcion_actividad_id_foreign` FOREIGN KEY (`id_actividad`) REFERENCES `actividad` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `inscripcion_usuario_id_foreign` FOREIGN KEY (`id_usuario`) REFERENCES `usuario` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `inscripcion`
--

LOCK TABLES `inscripcion` WRITE;
/*!40000 ALTER TABLE `inscripcion` DISABLE KEYS */;
/*!40000 ALTER TABLE `inscripcion` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `monto_cuota`
--

DROP TABLE IF EXISTS `monto_cuota`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `monto_cuota` (
  `fecha` date NOT NULL,
  `monto` float NOT NULL,
  `id_cuota` int NOT NULL,
  PRIMARY KEY (`fecha`),
  KEY `monto_cuota_cuota_id_foreign_idx` (`id_cuota`),
  CONSTRAINT `monto_cuota_cuota_id_foreign` FOREIGN KEY (`id_cuota`) REFERENCES `cuota` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `monto_cuota`
--

LOCK TABLES `monto_cuota` WRITE;
/*!40000 ALTER TABLE `monto_cuota` DISABLE KEYS */;
/*!40000 ALTER TABLE `monto_cuota` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pago_cuota`
--

DROP TABLE IF EXISTS `pago_cuota`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pago_cuota` (
  `id` int NOT NULL AUTO_INCREMENT,
  `fecha_pago` date NOT NULL,
  `id_usuario` int NOT NULL,
  `id_cuota` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `pago_cuota_cuota_id_foreign_idx` (`id_cuota`),
  KEY `pago_cuota_usuario_id_foreign_idx` (`id_usuario`),
  CONSTRAINT `pago_cuota_cuota_id_foreign` FOREIGN KEY (`id_cuota`) REFERENCES `cuota` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `pago_cuota_usuario_id_foreign` FOREIGN KEY (`id_usuario`) REFERENCES `usuario` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pago_cuota`
--

LOCK TABLES `pago_cuota` WRITE;
/*!40000 ALTER TABLE `pago_cuota` DISABLE KEYS */;
/*!40000 ALTER TABLE `pago_cuota` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `partido`
--

DROP TABLE IF EXISTS `partido`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `partido` (
  `id` int NOT NULL AUTO_INCREMENT,
  `fecha` date NOT NULL,
  `oponente` varchar(255) NOT NULL,
  `hora_desde` varchar(255) NOT NULL,
  `hora_hasta` varchar(255) NOT NULL,
  `categoria` varchar(255) NOT NULL,
  `precio_entrada` float NOT NULL,
  `id_cancha` int DEFAULT NULL,
  `id_actividad` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `partido_cancha_id_foreign_idx` (`id_cancha`),
  KEY `partido_actividad_id_foreign_idx` (`id_actividad`),
  CONSTRAINT `partido_actividad_id_foreign` FOREIGN KEY (`id_actividad`) REFERENCES `actividad` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `partido_cancha_id_foreign` FOREIGN KEY (`id_cancha`) REFERENCES `cancha` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `partido`
--

LOCK TABLES `partido` WRITE;
/*!40000 ALTER TABLE `partido` DISABLE KEYS */;
/*!40000 ALTER TABLE `partido` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `profesor`
--

DROP TABLE IF EXISTS `profesor`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `profesor` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nombre_completo` varchar(255) NOT NULL,
  `telefono` varchar(255) NOT NULL,
  `mail` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `profesor`
--

LOCK TABLES `profesor` WRITE;
/*!40000 ALTER TABLE `profesor` DISABLE KEYS */;
/*!40000 ALTER TABLE `profesor` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `salon`
--

DROP TABLE IF EXISTS `salon`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `salon` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(255) NOT NULL,
  `capacidad` int NOT NULL,
  `descripcion` varchar(255) NOT NULL,
  `imagen` blob,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `salon`
--

LOCK TABLES `salon` WRITE;
/*!40000 ALTER TABLE `salon` DISABLE KEYS */;
/*!40000 ALTER TABLE `salon` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usuario`
--

DROP TABLE IF EXISTS `usuario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuario` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nombre_completo` varchar(255) NOT NULL,
  `telefono` varchar(255) NOT NULL,
  `mail` varchar(255) NOT NULL,
  `contrasenia` varchar(255) NOT NULL,
  `rol` varchar(255) NOT NULL,
  `nro_socio` int DEFAULT NULL,
  `estado` tinyint DEFAULT NULL,
  `dni` int NOT NULL,
  `fecha_nacimiento` date NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuario`
--

LOCK TABLES `usuario` WRITE;
/*!40000 ALTER TABLE `usuario` DISABLE KEYS */;
INSERT INTO `usuario` VALUES (2,'Juan Pérez','1199887766','juan@mail.com','1234','socio',1,1,12345678,'1990-05-15');
/*!40000 ALTER TABLE `usuario` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-10-21 20:00:03

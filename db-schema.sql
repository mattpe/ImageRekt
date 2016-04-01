-- MySQL dump 10.16  Distrib 10.1.9-MariaDB, for Linux (x86_64)
--
-- Host: localhost    Database: ImageRekt
-- ------------------------------------------------------
-- Server version	5.1.73

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `COMMENT`
--

DROP TABLE IF EXISTS `COMMENT`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `COMMENT` (
  `CID` int(11) NOT NULL AUTO_INCREMENT,
  `IID` int(11) DEFAULT NULL,
  `UID` int(11) DEFAULT NULL,
  `CONTENTS` char(140) DEFAULT NULL,
  `COMMENTTIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`CID`),
  KEY `UID` (`UID`),
  KEY `IID` (`IID`),
  CONSTRAINT `COMMENT_ibfk_1` FOREIGN KEY (`UID`) REFERENCES `USER` (`UID`),
  CONSTRAINT `COMMENT_ibfk_2` FOREIGN KEY (`IID`) REFERENCES `IMAGE` (`IID`)
) ENGINE=InnoDB AUTO_INCREMENT=903 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `IMAGE`
--

DROP TABLE IF EXISTS `IMAGE`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `IMAGE` (
  `IID` int(11) NOT NULL AUTO_INCREMENT,
  `TITLE` char(30) NOT NULL,
  `DESCRIPTION` char(140) NOT NULL,
  `UPLOADTIME` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `PATH` char(200) NOT NULL,
  `UID` int(11) DEFAULT NULL,
  `TYPE` char(5) NOT NULL,
  `MIMETYPE` char(50) NOT NULL,
  PRIMARY KEY (`IID`),
  KEY `UID` (`UID`),
  CONSTRAINT `IMAGE_ibfk_1` FOREIGN KEY (`UID`) REFERENCES `USER` (`UID`)
) ENGINE=InnoDB AUTO_INCREMENT=1559 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `J_IMAGE_TAG`
--

DROP TABLE IF EXISTS `J_IMAGE_TAG`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `J_IMAGE_TAG` (
  `TID` int(11) DEFAULT NULL,
  `IID` int(11) DEFAULT NULL,
  KEY `TID` (`TID`),
  KEY `IID` (`IID`),
  CONSTRAINT `J_IMAGE_TAG_ibfk_1` FOREIGN KEY (`TID`) REFERENCES `TAG` (`TID`),
  CONSTRAINT `J_IMAGE_TAG_ibfk_2` FOREIGN KEY (`IID`) REFERENCES `IMAGE` (`IID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `J_USER_FAVOURITE`
--

DROP TABLE IF EXISTS `J_USER_FAVOURITE`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `J_USER_FAVOURITE` (
  `UID` int(11) DEFAULT NULL,
  `IID` int(11) DEFAULT NULL,
  KEY `UID` (`UID`),
  KEY `IID` (`IID`),
  CONSTRAINT `J_USER_FAVOURITE_ibfk_1` FOREIGN KEY (`UID`) REFERENCES `USER` (`UID`),
  CONSTRAINT `J_USER_FAVOURITE_ibfk_2` FOREIGN KEY (`IID`) REFERENCES `IMAGE` (`IID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `RATE`
--

DROP TABLE IF EXISTS `RATE`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `RATE` (
  `UID` int(11) NOT NULL DEFAULT '0',
  `IID` int(11) NOT NULL DEFAULT '0',
  `RATING` int(11) DEFAULT NULL,
  PRIMARY KEY (`UID`,`IID`),
  KEY `IID` (`IID`),
  CONSTRAINT `RATE_ibfk_1` FOREIGN KEY (`UID`) REFERENCES `USER` (`UID`),
  CONSTRAINT `RATE_ibfk_2` FOREIGN KEY (`IID`) REFERENCES `IMAGE` (`IID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `TAG`
--

DROP TABLE IF EXISTS `TAG`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `TAG` (
  `TID` int(11) NOT NULL AUTO_INCREMENT,
  `TCONTENT` char(20) NOT NULL,
  PRIMARY KEY (`TID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `USER`
--

DROP TABLE IF EXISTS `USER`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `USER` (
  `UID` int(11) NOT NULL AUTO_INCREMENT,
  `UNAME` char(15) NOT NULL,
  `UPASS` char(200) NOT NULL,
  `UEMAIL` char(30) NOT NULL,
  `IID` int(11) DEFAULT NULL,
  PRIMARY KEY (`UID`),
  KEY `IID` (`IID`),
  CONSTRAINT `USER_ibfk_1` FOREIGN KEY (`IID`) REFERENCES `IMAGE` (`IID`)
) ENGINE=InnoDB AUTO_INCREMENT=337 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- MySQL dump 10.13  Distrib 5.7.21, for macOS (x86_64)
--
-- Host: localhost    Database: phonebook
-- ------------------------------------------------------
--
-- Table structure for table `phone_entry`
--

CREATE TABLE IF NOT EXISTS `phone_entry` (
  `phonenumber` int(10) PRIMARY KEY,
  `phone_book_id` int(10) NOT NULL REFERENCES phone_books(`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  `first_name` text,
  `last_name` text
);

--
-- Dumping data for table `phone_entry`
--

INSERT IGNORE INTO `phone_entry` VALUES (480110987, null, 'Jimmy','V'), (4805551212, 1, 'Joe','Schmoe'), (4801111111,2, 'K','G'), (4801234567, 1, 'Bill','Bob'), (4809658300, 3, 'Susie','Q');

--
-- Table structure for table `phone_books`
--

CREATE TABLE IF NOT EXISTS `phone_books` (
  `id` int(7) PRIMARY KEY,
  `name` text
);

--
-- Dumping data for table `books`
--
INSERT IGNORE INTO `phone_books` VALUES (1, 'funbook'),(2, 'sadbook'),(3, 'epicbook');
TABLE CREATION SCRIPTS:


CREATE TABLE `student` (
  `id` int(8) NOT NULL,
  `name` varchar(40) NOT NULL,
  `surname` varchar(25) NOT NULL,
  `email` varchar(35) NOT NULL,
  PRIMARY KEY (`id`)
)

CREATE TABLE `trainer` (
  `id` int(8) NOT NULL AUTO_INCREMENT,
  `name` varchar(40) NOT NULL,
  `surname` varchar(25) NOT NULL,
  `email` varchar(35) NOT NULL,
  `branch` varchar(25) NOT NULL,
  PRIMARY KEY (`id`)
)

CREATE TABLE `course` (
  `id` int(8) NOT NULL AUTO_INCREMENT,
  `title` varchar(30) NOT NULL,
  `description` varchar(125) DEFAULT NULL,
  `quota` int(4) NOT NULL,
  PRIMARY KEY (`id`)
)

CREATE TABLE `place` (
  `id` int(6) NOT NULL AUTO_INCREMENT,
  `name` varchar(20) NOT NULL,
  `description` varchar(120) DEFAULT NULL,
  PRIMARY KEY (`id`)
)

CREATE TABLE `section` (
  `id` int(8) NOT NULL AUTO_INCREMENT,
  `trainerId` int(8) NOT NULL,
  `courseId` int(8) NOT NULL,
  `placeId` int(6) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `sections_trainer` (`trainerId`),
  KEY `sections_course` (`courseId`),
  KEY `sections_place` (`placeId`),
  CONSTRAINT `sections_course` FOREIGN KEY (`courseId`) REFERENCES `course` (`id`),
  CONSTRAINT `sections_place` FOREIGN KEY (`placeId`) REFERENCES `place` (`id`),
  CONSTRAINT `sections_trainer` FOREIGN KEY (`trainerId`) REFERENCES `trainer` (`id`)
)

CREATE TABLE `enrollment` (
  `studentId` int(8) NOT NULL,
  `sectionId` int(8) NOT NULL,
  UNIQUE KEY `enrollment_unique` (`studentId`,`sectionId`),
  KEY `enrollments_section` (`sectionId`),
  CONSTRAINT `enrollments_section` FOREIGN KEY (`sectionId`) REFERENCES `section` (`id`),
  CONSTRAINT `enrollments_student` FOREIGN KEY (`studentId`) REFERENCES `student` (`id`)
)

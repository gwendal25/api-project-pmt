This project is the back-end of the Project Management Tool application made for the SpringBoot/Angular formation of Iscod.
In order to use this project for development, you will need to have MySQL8, Java 21 and Maven installed on your computer.
Download the project then right click on the pom.xml and use Maven -> Sync Project, then Maven -> Generate sources and update folders

### Use the production image

In order to use the image of this project, you will need to have Docker installed.
Follow the below steps in order to run the project with Docker

Create a network named `spring-cloud-network`
```bash
docker network create spring-cloud-network
```

Pull the mysql image version 8.4.7
```bash
docker pull mysql:8.4.7
```

Run the mysql image
```bash
docker run -it --rm --name mysql-db --network spring-cloud-network `
		-e MYSQL_ROOT_PASSWORD=3de7Qt?bMtHXGJpo `
		-e MYSQL_DATABASE=pmt `
		-e MYSQL_USER=user `
		-e MYSQL_PASSWORD=password `
		-v mysql-db:/var/lib/mysql `
		-p 3305:3306 `
		mysql:8.4.7
```

Pull the latest project image
```bash
docker pull gwendal25/api-project-pmt:latest
```

Run the project image
```bash
docker container run -it --rm --name api-project-pmt-hub --network spring-cloud-network `
		-e MYSQL_DATABASE=pmt `
		-e MYSQL_USER=user `
		-e MYSQL_PASSWORD=password `
		-p 8081:8081 `
		gwendal25/api-project-pmt:latest 
```

You can generate the database trougth the following sql script if you want to have the database created before launching the project

```sql
-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
-- -----------------------------------------------------
-- Schema pmt
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `pmt` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci ;
USE `pmt` ;

-- -----------------------------------------------------
-- Table `pmt`.`projects`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `pmt`.`projects` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `start_date` DATETIME(6) NOT NULL,
  `description` TEXT NOT NULL,
  `name` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
AUTO_INCREMENT = 4
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `pmt`.`users`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `pmt`.`users` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `email` VARCHAR(255) NULL DEFAULT NULL,
  `name` VARCHAR(255) NOT NULL,
  `password` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
AUTO_INCREMENT = 11
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `pmt`.`project_user`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `pmt`.`project_user` (
  `role` TINYINT NOT NULL,
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `project_id` BIGINT NULL DEFAULT NULL,
  `user_id` BIGINT NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `FK1c6bgkykjvnomkpds2xapx719` (`project_id` ASC) VISIBLE,
  INDEX `FKfscw5rga2yu389e705x9wg6kb` (`user_id` ASC) VISIBLE,
  CONSTRAINT `FK1c6bgkykjvnomkpds2xapx719`
    FOREIGN KEY (`project_id`)
    REFERENCES `pmt`.`projects` (`id`),
  CONSTRAINT `FKfscw5rga2yu389e705x9wg6kb`
    FOREIGN KEY (`user_id`)
    REFERENCES `pmt`.`users` (`id`))
ENGINE = InnoDB
AUTO_INCREMENT = 21
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `pmt`.`tasks`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `pmt`.`tasks` (
  `task_priority` TINYINT NOT NULL,
  `task_status` TINYINT NOT NULL,
  `end_date` DATETIME(6) NOT NULL,
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `project_id` BIGINT NOT NULL,
  `user_id` BIGINT NULL DEFAULT NULL,
  `description` TEXT NOT NULL,
  `name` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `FKsfhn82y57i3k9uxww1s007acc` (`project_id` ASC) VISIBLE,
  INDEX `FK6s1ob9k4ihi75xbxe2w0ylsdh` (`user_id` ASC) VISIBLE,
  CONSTRAINT `FK6s1ob9k4ihi75xbxe2w0ylsdh`
    FOREIGN KEY (`user_id`)
    REFERENCES `pmt`.`users` (`id`),
  CONSTRAINT `FKsfhn82y57i3k9uxww1s007acc`
    FOREIGN KEY (`project_id`)
    REFERENCES `pmt`.`projects` (`id`))
ENGINE = InnoDB
AUTO_INCREMENT = 20
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `pmt`.`tasks_history`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `pmt`.`tasks_history` (
  `task_priority` TINYINT NULL DEFAULT NULL,
  `task_status` TINYINT NULL DEFAULT NULL,
  `edit_date` DATETIME(6) NULL DEFAULT NULL,
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `start_date` DATETIME(6) NULL DEFAULT NULL,
  `task_id` BIGINT NOT NULL,
  `description` TEXT NULL DEFAULT NULL,
  `name` VARCHAR(255) NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `FKqt2p0rj898ugrok0de220c4kq` (`task_id` ASC) VISIBLE,
  CONSTRAINT `FKqt2p0rj898ugrok0de220c4kq`
    FOREIGN KEY (`task_id`)
    REFERENCES `pmt`.`tasks` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `pmt`.`tasks_notifications`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `pmt`.`tasks_notifications` (
  `task_id` BIGINT NOT NULL,
  `user_id` BIGINT NOT NULL,
  PRIMARY KEY (`task_id`, `user_id`),
  INDEX `FKpcwjvj3stih85ut1f9bkgn8yv` (`user_id` ASC) VISIBLE,
  CONSTRAINT `FKpcwjvj3stih85ut1f9bkgn8yv`
    FOREIGN KEY (`user_id`)
    REFERENCES `pmt`.`users` (`id`),
  CONSTRAINT `FKphw6lhylxkhblaei8d3xxhoyy`
    FOREIGN KEY (`task_id`)
    REFERENCES `pmt`.`tasks` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
```

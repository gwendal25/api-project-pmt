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

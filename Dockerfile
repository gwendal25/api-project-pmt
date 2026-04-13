# Use a base image with OpenJDK Java 17 installed
FROM eclipse-temurin:21

# Set the working directory in the container
WORKDIR /app

# Copy the packaged Spring Boot application JAR file into the container
COPY target/api-project-pmt-1.0.0.jar /app/api-project-pmt-1.0.0.jar

# Expose port 8081
EXPOSE 8081

# Specify the command to run the Spring Boot application when the container starts
CMD ["java", "-jar", "api-project-pmt-1.0.0.jar"]

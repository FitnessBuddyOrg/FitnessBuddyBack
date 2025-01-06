FROM maven:3.9.6-amazoncorretto-21 AS builder
COPY . .
RUN mvn package -DskipTests

FROM amazoncorretto:21

ENV TZ="France/Paris"

# Set the working directory
WORKDIR /app

# Copy the packaged JAR file to the container
COPY --from=builder target/*.jar /app/app.jar

# Expose the application port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]

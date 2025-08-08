# Build stage
FROM maven:3.8.6-openjdk-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn package -DskipTests

# Run stage
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/target/MovieManagementSystem-0.0.1-SNAPSHOT.jar app.jar

# Install wait-for-it.sh for DB connection checks (optional)
RUN apt-get update && apt-get install -y wget && \
    wget -O /wait-for-it.sh https://github.com/vishnubob/wait-for-it/raw/master/wait-for-it.sh && \
    chmod +x /wait-for-it.sh && \
    apt-get remove -y wget && apt-get autoremove -y && rm -rf /var/lib/apt/lists/*

EXPOSE 8080

# Entrypoint with environment variables support
ENTRYPOINT ["sh", "-c", "java -jar app.jar"]

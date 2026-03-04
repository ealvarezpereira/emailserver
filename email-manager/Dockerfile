FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy parent pom
COPY pom.xml .

# Copy modules
COPY email-manager/pom.xml ./email-manager/
COPY email-sender/pom.xml ./email-sender/

# Download dependencies
RUN mvn dependency:go-offline -q

# Copy base code
COPY email-manager/src ./email-manager/src

# Build email-manager module
RUN mvn clean package -DskipTests -pl email-manager -am

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /app/email-manager/target/email-manager.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
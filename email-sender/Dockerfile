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
COPY email-sender/src ./email-sender/src

# Build email-sender module
RUN mvn clean package -DskipTests -pl email-sender -am

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /app/email-sender/target/email-sender.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
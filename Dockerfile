FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
RUN ./mvnw -v || mvn -v

# Copy source and build
COPY src src
RUN ./mvnw -q -DskipTests clean package || mvn -q -DskipTests clean package

FROM eclipse-temurin:21-jre

# Install Tesseract runtime (OCR)
RUN apt-get update && apt-get install -y --no-install-recommends tesseract-ocr && rm -rf /var/lib/apt/lists/*

WORKDIR /app
COPY --from=build /app/target/elastic-0.0.1-SNAPSHOT.jar /app/app.jar

EXPOSE 8080

ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]

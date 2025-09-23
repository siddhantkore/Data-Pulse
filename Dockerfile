FROM eclipse-temurin:21-jdk

# Install Tesseract
RUN apt-get update && apt-get install -y tesseract-ocr && rm -rf /var/lib/apt/lists/*

WORKDIR /app
COPY mvnw pom.xml ./
COPY .mvn .mvn

# Download dependencies (cache layer)
RUN ./mvnw dependency:go-offline

# Copy source code
COPY src src
# Copy the project (pom.xml + src + mvnw etc.)
# Build using Maven Wrapper if present
RUN ./mvnw clean package -DskipTests

EXPOSE 8080

CMD ["java", "-jar", "target/elastic-0.0.1-SNAPSHOT.jar"]

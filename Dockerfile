# Используем официальный OpenJDK образ
FROM eclipse-temurin:21-jdk AS builder

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем Maven wrapper и pom.xml
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Загружаем зависимости (чтобы кэшировалось)
RUN ./mvnw dependency:go-offline -B

# Копируем исходники и собираем jar
COPY src src
RUN ./mvnw clean package -DskipTests

# --- Runtime stage ---
FROM eclipse-temurin:21-jre

WORKDIR /app

# Копируем jar из builder stage
COPY --from=builder /app/target/*.jar app.jar

# Экспонируем порт
EXPOSE 8080

# Запуск
ENTRYPOINT ["java","-jar","app.jar"]

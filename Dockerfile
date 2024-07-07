# Используем официальный образ OpenJDK в качестве базового образа
FROM openjdk:17-jdk-slim

# Устанавливаем рабочую директорию в контейнере
WORKDIR /app

# Копируем файл с зависимостями приложения
COPY target/*.jar app.jar

# Определяем переменные окружения для подключения к базе данных
ENV SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5434/salfa_msg
ENV SPRING_DATASOURCE_USERNAME=sandbox
ENV SPRING_DATASOURCE_PASSWORD=sandbox


# Команда для запуска приложения
ENTRYPOINT ["sh", "-c", "java -jar /app/app.jar"]

# Указываем порт, который будет слушать приложение
EXPOSE 8080

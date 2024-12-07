# Usa uma imagem base do OpenJDK para Java 11
FROM openjdk:11-jdk-slim

# Define o diretório de trabalho dentro do container
WORKDIR /app

# Copia o arquivo .jar gerado para o container
COPY target/*.jar app.jar

# Expõe a porta 8080 (usada pelo Spring Boot por padrão)
EXPOSE 8080

# Comando para rodar o servidor
ENTRYPOINT ["java", "-jar", "app.jar"]

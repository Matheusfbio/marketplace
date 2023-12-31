# FROM ubuntu:latest AS build

# FROM maven:3.9.5-jdk-17 as build

# WORKDIR /app

# COPY . .

# RUN mvn clean install

# RUN apt-get install maven -y

# FROM openjdk:17-jdk-slim 

# WORKDIR /app

# COPY --from=build /target/marketplace-1.0.0.jar app.jar

# EXPOSE 8080

# ENTRYPOINT [ "java", "-jar", "app.jar" ]

# Estágio de construção
FROM ubuntu:latest AS build

# Atualizar e instalar o OpenJDK
RUN apt-get update
RUN apt-get install openjdk-17-jdk -y

# Copiar o conteúdo para o diretório de trabalho
COPY . .

# Instalar o Maven
RUN apt-get install maven -y 

# Construir o projeto Maven
RUN mvn clean install

# Estágio de produção
FROM openjdk:17-jdk-slim

# Configurar o diretório de trabalho no segundo estágio

# Expor a porta 8080
EXPOSE 8080

# Copiar o arquivo JAR construído a partir do estágio anterior
COPY --from=build /target/marketplace-1.0.0.jar app.jar

# Comando de entrada
ENTRYPOINT ["java", "-jar", "app.jar"]

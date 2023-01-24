# Construir y empaquetar
FROM maven:3.6.0-jdk-11-slim AS build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package

# Ejecutar
FROM openjdk:11-jre-slim
COPY --from=build /home/app/build/DevOpsUsach2020-0.0.1.jar /usr/local/lib/DevOpsUsach2020.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","/usr/local/lib/DevOpsUsach2020.jar"]
# Dockerfile

# Build stage
FROM maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /app

COPY . .

RUN mvn clean package

# Run stage - deploy the WAR to Tomcat
FROM tomcat:10.1-jdk17

COPY --from=build /app/target/student-management.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080

CMD ["catalina.sh", "run"]

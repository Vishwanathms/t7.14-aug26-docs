FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY . .

RUN javac -cp .:mysql-connector-j-9.3.0.jar *.java

CMD ["java","-cp",".:mysql-connector-j-9.3.0.jar","Main"]
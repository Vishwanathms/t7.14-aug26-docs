
* Create the network 
```bash
docker network create student-network
```



* Run the DB

```bash

docker run -d --name mysql-db --network student-network -e MYSQL_ROOT_PASSWORD=root123 -e MYSQL_DATABASE=studentdb -p 4306:3306 mysql:8.4
```

* Create the build 
```bash
docker build -t student-management:java-db .
```

* Run the java container 
```bash

docker run -it --name student-app --network student-network student-management:java-db
```
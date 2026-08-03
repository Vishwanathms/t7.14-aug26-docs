# Lab 1 – Running MySQL Using Docker

## Objective

In this lab, you will:

- Run a MySQL 8 container using Docker.
- Verify that the container is running.
- Connect to the MySQL server.
- Confirm that the default database has been created.

---

# Prerequisites

Before starting this lab, ensure you have:

- Docker Desktop installed and running
- Command Prompt, PowerShell, or Git Bash
- Internet connection (to download the MySQL image if it is not available locally)

Verify Docker installation:

```bash
docker --version
```

Expected Output

```text
Docker version xx.xx.x
```

---

# Lab Architecture

```
+-------------------------+
|      Docker Desktop     |
+-----------+-------------+
            |
            |
     MySQL Container
     mysql:8
            |
    Port 3306
            |
      MySQL Database
```

---

# Step 1 – Pull the MySQL Image (Optional)

If the MySQL image is not already available, download it.

```bash
docker pull mysql:8
```

Verify the image:

```bash
docker images
```

Expected Output

```
REPOSITORY   TAG
mysql        8
```

---

# Step 2 – Start the MySQL Container

Run the following command:

```bash
docker run -d \
--name mysql-db \
-p 3306:3306 \
-e MYSQL_ROOT_PASSWORD=root123 \
-e MYSQL_DATABASE=company \
mysql:8
```

### Explanation

| Option | Description |
|---------|-------------|
| `docker run` | Creates and starts a new container |
| `-d` | Runs the container in detached (background) mode |
| `--name mysql-db` | Assigns the container name `mysql-db` |
| `-p 3306:3306` | Maps host port 3306 to the container's MySQL port |
| `MYSQL_ROOT_PASSWORD` | Sets the MySQL root user password |
| `MYSQL_DATABASE` | Creates a database named `company` automatically |
| `mysql:8` | Uses the official MySQL 8 Docker image |

---

# Step 3 – Verify the Container

List all running containers.

```bash
docker ps
```

Expected Output

```
CONTAINER ID   IMAGE      STATUS
xxxxxxxxxxxx   mysql:8    Up
```

Ensure:

- Container name is **mysql-db**
- Status is **Up**
- Port **3306** is exposed

---

# Step 4 – View Container Logs (Optional)

Check the startup logs.

```bash
docker logs mysql-db
```

Wait until you see messages indicating that MySQL is ready for connections.

Example:

```
MySQL init process done.
MySQL is ready for connections.
```

---

# Step 5 – Connect to MySQL

Open a MySQL shell inside the running container.

```bash
docker exec -it mysql-db mysql -uroot -p
```

When prompted, enter:

```
root123
```

If successful, you should see the MySQL prompt:

```sql
mysql>
```

---

# Step 6 – Verify the Default Database

List all databases.

```sql
SHOW DATABASES;
```

Expected Output

```text
+--------------------+
| Database           |
+--------------------+
| company            |
| information_schema |
| mysql              |
| performance_schema |
| sys                |
+--------------------+
```

Verify that the **company** database exists.

---

# Step 7 – Select the Company Database

```sql
USE company;
```

Expected Output

```text
Database changed
```

Verify the selected database.

```sql
SELECT DATABASE();
```

Expected Output

```text
+------------+
| DATABASE() |
+------------+
| company    |
+------------+
```

---

# Step 8 – Exit MySQL

```sql
EXIT;
```

or

```sql
QUIT;
```

---

# Step 9 – Stop the Container (Optional)

```bash
docker stop mysql-db
```

Verify:

```bash
docker ps
```

The container should no longer appear in the running container list.

---

# Step 10 – Start the Container Again

```bash
docker start mysql-db
```

Verify:

```bash
docker ps
```

---

# Learning Outcome

After completing this lab, you should be able to:

- Deploy MySQL using Docker.
- Understand Docker port mapping.
- Configure environment variables during container creation.
- Connect to a MySQL server running inside a container.
- Verify databases using SQL commands.
- Start and stop Docker containers.

---

# Troubleshooting

### Container is not running

Check:

```bash
docker ps -a
```

View logs:

```bash
docker logs mysql-db
```

---

### Port 3306 already in use

Check which process is using the port.

Windows:

```powershell
netstat -ano | findstr 3306
```

Stop the conflicting application or run MySQL on a different host port.

---

### Access Denied

Ensure you entered the correct password:

```
root123
```

---

# Lab Summary

In this lab you learned how to:

- Pull the MySQL Docker image
- Run a MySQL container
- Verify the container status
- Connect to MySQL
- Verify the automatically created database
- Start and stop the MySQL container
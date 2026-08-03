Since this is the **first Docker lab** in your DevOps/DevSecOps training, the objective should be to help students understand the complete Docker image build lifecycle rather than just executing commands.

---

# Lab Manual – Building a Docker Image for a Python Flask Application

## Lab Objective

By the end of this lab, students will be able to:

* Understand the application structure
* Understand the purpose of `requirements.txt`
* Understand the purpose of a `Dockerfile`
* Build a Docker image
* Verify the image
* Run the container
* Access the application
* Stop and remove the container

---

# Lab Environment

Ubuntu 24.04

Verify Docker installation.

```bash
docker --version
```

Expected Output

```
Docker version xx.xx.x
```

Check Docker service.

```bash
sudo systemctl status docker
```

If Docker is not running

```bash
sudo systemctl start docker
```

---

# Step 1 – Create a Working Directory

Create a project directory.

```bash
mkdir python-app
```

Move into the directory.

```bash
cd python-app
```

---

# Step 2 – Verify the Project Files

The project already contains three files.

```bash
ls
```

Expected Output

```
app.py
requirements.txt
Dockerfile
```

---

# Step 3 – Understand the Files

Display the application.

```bash
cat app.py
```

Observe:

* Flask application
* HTTP server
* Listening port

---

View Python dependencies.

```bash
cat requirements.txt
```

Example

```
Flask==3.0.0
```

This file tells pip which packages to install.

---

View the Dockerfile.

```bash
cat Dockerfile
```

Students should identify:

* Base Image
* Working Directory
* Copy files
* Install dependencies
* Expose port
* Start application

---

# Step 4 – Build the Docker Image

Run the build command.

```bash
docker build -t python-flask-app:v1 .
```

Explanation

```
docker build      → Build image
-t                → Tag image
python-flask-app  → Image name
v1                → Version
.                 → Current directory
```

---

# Step 5 – Observe the Build Process

Students should observe each build step.

Typical output

```
FROM python:3.12
WORKDIR /app
COPY .
RUN pip install
EXPOSE 5000
CMD
```

Each Dockerfile instruction creates a new image layer.

---

# Step 6 – Verify Image Creation

List images.

```bash
docker images
```

Expected Output

```
REPOSITORY          TAG
python-flask-app    v1
```

---

# Step 7 – Inspect the Image

Display image details.

```bash
docker image inspect python-flask-app:v1
```

Observe

* Image ID
* Size
* Layers
* Creation Time
* Entrypoint

---

# Step 8 – Run the Container

Start the container.

```bash
docker run -d --name flask-app -p 5000:5000 python-flask-app:v1
```

Explanation

```
-d          Detached mode
--name      Container name
-p          Host:Container port
```

---

# Step 9 – Verify Running Containers

```bash
docker ps
```

Expected

```
CONTAINER ID
IMAGE
STATUS
PORTS
NAMES
```

---

# Step 10 – Access the Application

Using the terminal.

```bash
curl http://localhost:5000
```

Or

Open a browser.

```
http://<VM-IP>:5000
```

Expected output

```
Hello World
```

(or whatever your Flask application returns)

---

# Step 11 – View Container Logs

Display application logs.

```bash
docker logs flask-app
```

Students should observe:

* Flask startup
* Listening port
* HTTP requests

---

# Step 12 – Enter the Running Container

Open an interactive shell.

```bash
docker exec -it flask-app bash
```

If bash is unavailable

```bash
docker exec -it flask-app sh
```

Verify current directory.

```bash
pwd
```

List files.

```bash
ls
```

Exit

```bash
exit
```

---

# Step 13 – Monitor Resource Usage

View CPU and Memory usage.

```bash
docker stats
```

Stop monitoring

```
CTRL+C
```

---

# Step 14 – Stop the Container

```bash
docker stop flask-app
```

Verify.

```bash
docker ps
```

The container should no longer appear in the running container list.

---

# Step 15 – View All Containers

```bash
docker ps -a
```

Notice the container status is now **Exited**.

---

# Step 16 – Start the Container Again

```bash
docker start flask-app
```

Verify.

```bash
docker ps
```

---

# Step 17 – Remove the Container

Stop if running.

```bash
docker stop flask-app
```

Delete it.

```bash
docker rm flask-app
```

Verify.

```bash
docker ps -a
```

---

# Step 18 – Remove the Image

Delete the Docker image.

```bash
docker rmi python-flask-app:v1
```

Verify.

```bash
docker images
```

The image should no longer be listed.

---

# Lab Verification Checklist

Students should be able to demonstrate:

* ✅ Docker daemon is running
* ✅ Project files are present
* ✅ Docker image built successfully
* ✅ Docker image listed
* ✅ Container started
* ✅ Application accessible on port 5000
* ✅ Logs viewed
* ✅ Interactive shell accessed
* ✅ Container stopped and restarted
* ✅ Container removed
* ✅ Image removed

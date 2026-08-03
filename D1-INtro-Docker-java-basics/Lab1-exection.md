That is even closer to what most software companies use. I would actually **avoid PowerShell** and conduct the entire lab using **VS Code + Git Bash + Docker Desktop**.

This setup mirrors a typical developer workstation:

* **VS Code** → Code editor
* **Git Bash** → Terminal
* **Docker Desktop** → Container runtime
* **Browser** → Application testing

This is the workflow used by many development teams.

---

# Lab 1 – Containerizing a Python Flask Application using Docker Desktop

## Lab Objective

By the end of this lab, you will be able to:

* Open an existing Python project in VS Code
* Understand the project structure
* Build a Docker image
* Run a Docker container
* Verify the application
* View logs
* Access the running container
* Stop and remove containers
* Remove Docker images

---

# Lab Environment

| Software       | Version       |
| -------------- | ------------- |
| Windows 10/11  | Latest        |
| VS Code        | Installed     |
| Git Bash       | Installed     |
| Docker Desktop | Running       |
| Web Browser    | Chrome / Edge |

---

# Project Structure

Open the project folder.

```
python-app/
│
├── app.py
├── requirements.txt
└── Dockerfile
```

---

# Step 1 – Start Docker Desktop

Launch **Docker Desktop**.

Verify the status is:

```
Engine Running
```

Wait until Docker finishes initializing before continuing.

---

# Step 2 – Open the Project in VS Code

Open **VS Code**.

Select

```
File

↓

Open Folder

↓

python-app
```

Verify the Explorer shows:

```
app.py
requirements.txt
Dockerfile
```

---

# Step 3 – Open the Integrated Git Bash Terminal

Inside VS Code:

```
Terminal

↓

New Terminal
```

If the default terminal is not Git Bash:

```
▼

↓

Select Default Profile

↓

Git Bash
```

Verify the current directory.

```bash
pwd
```

Example Output

```bash
/c/DockerLab/python-app
```

---

# Step 4 – Verify Project Files

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

# Step 5 – Review the Application Files

## View app.py

```bash
cat app.py
```

Observe:

* Flask application
* Web server initialization
* Listening port

---

## View requirements.txt

```bash
cat requirements.txt
```

Observe the Python dependencies required by the application.

Example

```
Flask==3.0.0
```

---

## View Dockerfile

```bash
cat Dockerfile
```

Students should identify:

* Base image
* Working directory
* Copy instruction
* Dependency installation
* Port exposure
* Startup command

---

# Step 6 – Build the Docker Image

Execute:

```bash
docker build -t python-flask-app:v1 .
```

Explanation

| Command          | Description            |
| ---------------- | ---------------------- |
| docker build     | Builds a Docker image  |
| -t               | Assigns a name and tag |
| python-flask-app | Image name             |
| v1               | Image version          |
| .                | Current directory      |

Wait for the build to complete successfully.

---

# Step 7 – Verify the Image

Using the CLI:

```bash
docker images
```

Expected Output

```
REPOSITORY          TAG

python-flask-app    v1
```

---

# Step 8 – Verify the Image in Docker Desktop

Open Docker Desktop.

Navigate to:

```
Images
```

Confirm that the image **python-flask-app:v1** is listed.

Observe:

* Repository name
* Tag
* Image size
* Image ID

---

# Step 9 – Run the Container

```bash
docker run -d \
--name flask-app \
-p 5000:5000 \
python-flask-app:v1
```

Verify the container starts successfully.

---

# Step 10 – Verify Running Containers

```bash
docker ps
```

Expected Output

```
CONTAINER ID

IMAGE

STATUS

PORTS

NAMES
```

---

# Step 11 – Verify in Docker Desktop

Open:

```
Containers
```

Locate:

```
flask-app
```

Observe:

* Container status
* Port mapping
* Container ID
* Runtime duration

---

# Step 12 – Access the Application

Open a web browser.

```
http://localhost:5000
```

Expected Output

```
Hello World
```

(or the response defined in your `app.py`)

---

# Step 13 – View Application Logs

Using the CLI:

```bash
docker logs flask-app
```

Observe:

* Flask server startup
* Listening port
* Incoming requests

---

# Step 14 – View Logs in Docker Desktop

Navigate to:

```
Containers

↓

flask-app

↓

Logs
```

Observe the same output through the graphical interface.

---

# Step 15 – Access the Running Container

```bash
docker exec -it flask-app bash
```

If Bash is unavailable:

```bash
docker exec -it flask-app sh
```

Verify the working directory.

```bash
pwd
```

List the application files.

```bash
ls
```

Exit the container.

```bash
exit
```

---

# Step 16 – Monitor Container Resource Usage

```bash
docker stats
```

Observe:

* CPU usage
* Memory usage
* Network I/O
* Block I/O

Press:

```
Ctrl + C
```

to stop monitoring.

---

# Step 17 – Stop the Container

```bash
docker stop flask-app
```

Verify:

```bash
docker ps
```

The container should no longer appear in the running container list.

---

# Step 18 – Restart the Container

```bash
docker start flask-app
```

Verify:

```bash
docker ps
```

---

# Step 19 – Remove the Container

Stop the container if it is running.

```bash
docker stop flask-app
```

Remove the container.

```bash
docker rm flask-app
```

Verify.

```bash
docker ps -a
```

The container should no longer be listed.

---

# Step 20 – Remove the Docker Image

```bash
docker rmi python-flask-app:v1
```

Verify.

```bash
docker images
```

The image should no longer be listed.

---

# Challenge Exercise (Optional)

Modify the response in `app.py` from:

```python
Hello World
```

to:

```python
Hello Docker Students!
```

Then:

1. Save the file.
2. Rebuild the image with a new tag:

```bash
docker build -t python-flask-app:v2 .
```

3. Remove the old container:

```bash
docker rm -f flask-app
```

4. Run the new version:

```bash
docker run -d --name flask-app -p 5000:5000 python-flask-app:v2
```

5. Refresh `http://localhost:5000` and verify the updated response.

---

## Lab Completion Checklist

Students should successfully demonstrate:

* ✅ Docker Desktop Engine is running
* ✅ Open the project in VS Code
* ✅ Use the integrated Git Bash terminal
* ✅ Build a Docker image
* ✅ Verify the image using both CLI and Docker Desktop
* ✅ Run a container
* ✅ Access the application in a browser
* ✅ View logs using CLI and Docker Desktop
* ✅ Access the container shell
* ✅ Monitor container resources
* ✅ Stop, restart, and remove the container
* ✅ Remove the Docker image
* ✅ Rebuild the application after making a code change (challenge exercise)


# Node.js Lab Manual

## Student File Processing System

**Module:** Node.js – File System, Streams & Events
**Lab Duration:** 30–40 minutes
**Level:** Beginner / Freshers
**Scenario:** Training Institute Student Management Utility

---

## 1. Lab Objective

In this lab, you will build a simple Node.js application that manages student information stored in a text file.

By completing the lab, you will learn how to:

* Create a Node.js project
* Initialize a project using `npm`
* Use the built-in `fs` module
* Read a file asynchronously
* Write data to a file
* Append data to an existing file
* Handle file errors
* Verify file contents from the terminal

### Final workflow

```text
             Student File
                  |
                  ↓
          students.txt
                  |
        ┌─────────┴─────────┐
        ↓                   ↓
   Read Students        Add Student
        ↓                   ↓
   readFile()          appendFile()
        │                   │
        └─────────┬─────────┘
                  ↓
             Updated File
```

---

# 2. Lab Scenario

You are working as a junior Node.js developer for a training institute.

The institute currently maintains student information in a simple text file:

```text
students.txt
```

Each student record contains:

```text
Student ID, Student Name, Technology
```

For example:

```text
101,John,Java
102,Alice,Spring Boot
103,David,Node.js
```

The application must initially support:

1. Read all students
2. Add a new student
3. Verify the updated student list

Later, this application will be extended to process large files using **Streams** and notify other parts of the application using **Events**.

---

# 3. Prerequisites

Before starting, make sure the following are installed.

### Node.js

Verify:

```bash
node --version
```

Example:

```text
v22.x.x
```

Also verify npm:

```bash
npm --version
```

Example:

```text
10.x.x
```

If both commands return a version number, your environment is ready.

---

# 4. Create the Lab Directory

Open your terminal.

Create the project directory:

```bash
mkdir node-file-lab
```

Move into the directory:

```bash
cd node-file-lab
```

Verify your location:

```bash
pwd
```

On Windows PowerShell:

```powershell
Get-Location
```

---

# 5. Initialize the Node.js Project

Run:

```bash
npm init -y
```

You should see a `package.json` file created.

Verify:

```bash
ls
```

On Windows:

```powershell
dir
```

You should have:

```text
package.json
```

---

# 6. Understand `package.json`

Open:

```text
package.json
```

You should see something similar to:

```json
{
  "name": "node-file-lab",
  "version": "1.0.0",
  "description": "",
  "main": "index.js",
  "scripts": {
    "test": "echo \"Error: no test specified\" && exit 1"
  },
  "keywords": [],
  "author": "",
  "license": "ISC"
}
```

### Important

For this lab, **you do not need to install any npm package**.

Why?

Because `fs` is a **built-in Node.js module**.

---

# 7. Create the Lab Files

Create the following files:

```text
node-file-lab/
│
├── package.json
├── app.js
└── students.txt
```

You can create `app.js` from the terminal:

```bash
touch app.js
```

On Windows PowerShell:

```powershell
New-Item app.js
```

Create the student file:

```bash
touch students.txt
```

---

# 8. Create the Student Data

Open:

```text
students.txt
```

Add the following:

```text
101,John,Java
102,Alice,Spring Boot
103,David,Node.js
```

Save the file.

Your directory should now look like:

```text
node-file-lab/
│
├── package.json
├── app.js
└── students.txt
```

---

# 9. Understand the `fs` Module

Node.js provides a built-in module called:

```text
fs
```

`fs` stands for:

> **File System**

It provides APIs for working with files and directories.

Examples:

```text
fs.readFile()
fs.writeFile()
fs.appendFile()
fs.unlink()
fs.rename()
fs.mkdir()
```

For this lab, we will primarily use:

```text
readFile()
appendFile()
```

---

# 10. Import the `fs` Module

Open:

```text
app.js
```

Add:

```javascript
const fs = require('fs');
```

Your file should contain:

```javascript
const fs = require('fs');
```

### What does this do?

It loads Node.js's built-in File System module.

```text
app.js
   |
   ↓
require('fs')
   |
   ↓
Node.js File System APIs
```

---

# 11. Read the Student File

Now add the following code to `app.js`:

```javascript
const fs = require('fs');

fs.readFile(
  'students.txt',
  'utf8',
  (err, data) => {

    if (err) {
      console.error(err);
      return;
    }

    console.log(data);
  }
);
```

Save the file.

---

# 12. Understand `readFile()`

The important part is:

```javascript
fs.readFile(
  'students.txt',
  'utf8',
  (err, data) => {
```

There are three important parameters.

### Parameter 1 — File name

```text
students.txt
```

This tells Node.js which file to read.

### Parameter 2 — Encoding

```text
utf8
```

This tells Node.js to return the file contents as text.

### Parameter 3 — Callback

```javascript
(err, data) => {
```

The callback executes after Node.js completes the file operation.

---

# 13. Understand `err` and `data`

The callback receives two important values:

```javascript
(err, data)
```

### `err`

Contains information if something goes wrong.

Example:

```text
File does not exist
Permission denied
Invalid path
```

### `data`

Contains the contents of the file when the operation succeeds.

Conceptually:

```text
readFile()
     |
     ↓
File System
     |
     ├── Error → err
     |
     └── Success → data
```

---

# 14. Run the Application

Execute:

```bash
node app.js
```

Expected output:

```text
101,John,Java
102,Alice,Spring Boot
103,David,Node.js
```

Congratulations!

You have successfully read a file using Node.js.

---

# 15. Student Checkpoint #1

### Question

Is this operation synchronous or asynchronous?

```javascript
fs.readFile(...)
```

### Expected answer

**Asynchronous.**

### Why?

Node.js starts the file operation and does not block the application while waiting for the file system operation to complete.

The callback executes after the operation finishes.

---

# 16. Demonstrate Asynchronous Execution

Add the following line **before** `fs.readFile()`:

```javascript
console.log('Starting file read...');
```

Add this line **after** `fs.readFile()`:

```javascript
console.log('File read request submitted...');
```

Your code should now be:

```javascript
const fs = require('fs');

console.log('Starting file read...');

fs.readFile(
  'students.txt',
  'utf8',
  (err, data) => {

    if (err) {
      console.error(err);
      return;
    }

    console.log(data);
  }
);

console.log('File read request submitted...');
```

Run:

```bash
node app.js
```

You should see something similar to:

```text
Starting file read...
File read request submitted...
101,John,Java
102,Alice,Spring Boot
103,David,Node.js
```

### Important observation

The last `console.log()` outside the callback executes before the file contents are printed.

This demonstrates asynchronous execution.

---

# 17. Handle a File Error

Temporarily change:

```javascript
'students.txt'
```

to:

```javascript
'student.txt'
```

Run:

```bash
node app.js
```

Because `student.txt` does not exist, the error block executes.

You should see an error similar to:

```text
Error: ENOENT: no such file or directory
```

Change the filename back:

```javascript
'students.txt'
```

### Student checkpoint

Why is this code important?

```javascript
if (err) {
  console.error(err);
  return;
}
```

### Expected answer

It prevents the application from trying to process invalid data when the file operation fails.

---

# 18. Add a New Student

Now we will add a new student.

Add the following code after your file-reading code:

```javascript
const student =
  '\n104,Michael,DevOps';

fs.appendFile(
  'students.txt',
  student,
  (err) => {

    if (err) {
      console.error(err);
      return;
    }

    console.log(
      'Student added successfully'
    );
  }
);
```

Your `app.js` can now be:

```javascript
const fs = require('fs');

fs.readFile(
  'students.txt',
  'utf8',
  (err, data) => {

    if (err) {
      console.error(err);
      return;
    }

    console.log(data);
  }
);

const student =
  '\n104,Michael,DevOps';

fs.appendFile(
  'students.txt',
  student,
  (err) => {

    if (err) {
      console.error(err);
      return;
    }

    console.log(
      'Student added successfully'
    );
  }
);
```

---

# 19. Understand `appendFile()`

We are using:

```javascript
fs.appendFile()
```

The purpose is to add new content to the **end of an existing file**.

Existing file:

```text
101,John,Java
102,Alice,Spring Boot
103,David,Node.js
```

After:

```javascript
fs.appendFile(...)
```

the file becomes:

```text
101,John,Java
102,Alice,Spring Boot
103,David,Node.js
104,Michael,DevOps
```

---

# 20. Why Do We Use `\n`?

Our student value is:

```javascript
const student =
  '\n104,Michael,DevOps';
```

The:

```text
\n
```

means:

> New line

Without it, we could get:

```text
103,David,Node.js104,Michael,DevOps
```

With it:

```text
103,David,Node.js
104,Michael,DevOps
```

---

# 21. Run the Application

Run:

```bash
node app.js
```

You should see:

```text
101,John,Java
102,Alice,Spring Boot
103,David,Node.js

Student added successfully
```

---

# 22. Verify the File

Now inspect the file.

Linux/macOS:

```bash
cat students.txt
```

Windows PowerShell:

```powershell
Get-Content students.txt
```

Expected:

```text
101,John,Java
102,Alice,Spring Boot
103,David,Node.js
104,Michael,DevOps
```

---

# 23. Important Observation

Run:

```bash
node app.js
```

**one more time.**

Then:

```bash
cat students.txt
```

You may now see:

```text
101,John,Java
102,Alice,Spring Boot
103,David,Node.js
104,Michael,DevOps
104,Michael,DevOps
```

Why?

Because:

```javascript
fs.appendFile()
```

**adds** data every time the program runs.

It does not replace existing data.

---

# 24. `writeFile()` vs `appendFile()`

| Method         | Purpose                        |
| -------------- | ------------------------------ |
| `writeFile()`  | Creates/replaces file contents |
| `appendFile()` | Adds data to the end           |
| `readFile()`   | Reads file contents            |

Conceptually:

```text
writeFile()
     ↓
[Replace existing content]


appendFile()
     ↓
[Keep existing content]
     +
[Add new content]
```

---

# 25. Clean Up Duplicate Data

If you ran the program multiple times, reset `students.txt`.

Change it back to:

```text
101,John,Java
102,Alice,Spring Boot
103,David,Node.js
```

Save the file.

Now you have a clean starting point.

---

# 26. Final Version — Basic Student File Manager

At the end of this section, students should understand the following:

```javascript
const fs = require('fs');

fs.readFile(
  'students.txt',
  'utf8',
  (err, data) => {

    if (err) {
      console.error(
        'Error reading file:',
        err.message
      );
      return;
    }

    console.log('Current Students:');
    console.log(data);
  }
);

const student =
  '\n104,Michael,DevOps';

fs.appendFile(
  'students.txt',
  student,
  (err) => {

    if (err) {
      console.error(
        'Error adding student:',
        err.message
      );
      return;
    }

    console.log(
      'Student added successfully'
    );
  }
);
```

---

# 27. Lab Checkpoint

Before moving to Streams, students should be able to answer these questions.

### Q1. What module is used for file operations?

**Answer:**

```text
fs
```

---

### Q2. Is `fs` an npm package?

**Answer:**

No. It is a **built-in Node.js module**.

---

### Q3. Which method reads a file asynchronously?

**Answer:**

```javascript
fs.readFile()
```

---

### Q4. Which method adds content to an existing file?

**Answer:**

```javascript
fs.appendFile()
```

---

### Q5. What does `utf8` represent?

**Answer:**

The character encoding used to interpret the file contents as text.

---

### Q6. What is the purpose of the callback?

**Answer:**

It executes after the asynchronous file operation completes.

---

### Q7. What is the difference between `writeFile()` and `appendFile()`?

**Answer:**

`writeFile()` replaces/creates file content, while `appendFile()` adds content to the end.

---

# 28. Student Challenge

Now modify the application.

Instead of:

```javascript
const student =
  '\n104,Michael,DevOps';
```

create:

```javascript
const student =
  '\n105,Sarah,Cloud';
```

Append the student.

Then verify:

```bash
cat students.txt
```

Expected:

```text
101,John,Java
102,Alice,Spring Boot
103,David,Node.js
104,Michael,DevOps
105,Sarah,Cloud
```

---

# 29. Challenge 2 — Add Multiple Students

Try adding three students:

```text
106,Robert,Docker
107,Emma,Kubernetes
108,James,AWS
```

**Requirement:**

Use `appendFile()`.

Do not manually edit `students.txt`.

Verify the result using:

```bash
cat students.txt
```

---

# 30. Challenge 3 — Error Handling

Change the filename temporarily:

```javascript
fs.readFile(
  'students-data.txt',
```

Run the application.

Observe the error.

Then restore:

```javascript
fs.readFile(
  'students.txt',
```

### Objective

Understand why error handling is necessary for file operations.

---

# 31. Lab Completion Checklist

Before moving to the next section, make sure you can check all of these:

* [ ] Node.js is installed
* [ ] Created `node-file-lab`
* [ ] Initialized project using `npm init -y`
* [ ] Created `app.js`
* [ ] Created `students.txt`
* [ ] Added initial student data
* [ ] Imported the `fs` module
* [ ] Read the file using `fs.readFile()`
* [ ] Used `utf8` encoding
* [ ] Added error handling
* [ ] Added a student using `fs.appendFile()`
* [ ] Verified the updated file
* [ ] Understand synchronous vs asynchronous file operations
* [ ] Understand `writeFile()` vs `appendFile()`

---

# 32. What Comes Next?

The current application works well for a small file.

But imagine:

```text
students.txt
      ↓
       500 MB
```

Would we want to load the entire file into memory?

Instead, we will use:

```javascript
fs.createReadStream()
```

The next stage of the lab will change:

```text
Read Entire File
      ↓
fs.readFile()
```

into:

```text
Read File in Chunks
      ↓
fs.createReadStream()
      ↓
data event
      ↓
Process each chunk
```

Then we will introduce:

```javascript
EventEmitter
```

and build the complete flow:

```text
students.txt
     ↓
Readable Stream
     ↓
Process Chunks
     ↓
Count Bytes
     ↓
Emit "studentsProcessed"
     ↓
Event Listener
     ↓
Display Result
```

**This gives students a very natural progression from basic Node.js file handling → asynchronous programming → streams → events, rather than teaching these concepts as isolated APIs.**

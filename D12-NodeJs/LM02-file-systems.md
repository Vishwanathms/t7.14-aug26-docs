# Progressive Lab Manual — Node.js File System, Streams & Events

**Level:** Beginner → Lower-Medium
**Duration:** 45–60 minutes
**Scenario:** Build a small **Student File Processing System** using Node.js `fs`, Streams, and `EventEmitter`.

This lab follows the uploaded material: `fs/promises` for file operations, streams for chunk-based processing, and `EventEmitter` for event-driven actions. 

---

## 1. Lab Objectives

By the end of this lab, you will be able to:

* Create and write files using `fs/promises`
* Append student records
* Read files asynchronously
* Create directories
* Check file information using `stat()`
* Copy a file using streams
* Use `pipe()`
* Create custom events using `EventEmitter`
* Register multiple event listeners

---

# Part 1 — Create the Project

### Step 1: Create project directory

```bash
mkdir node-file-lab
cd node-file-lab
```

### Step 2: Initialize Node.js

```bash
npm init -y
```

### Step 3: Create files

```bash
touch app.js logger.js processor.js events.js
```

Your structure:

```text
node-file-lab/
│
├── app.js
├── logger.js
├── processor.js
├── events.js
└── package.json
```

---

# Part 2 — Create Student File

### Step 4: Open `app.js`

Add:

```javascript
const fs = require('fs/promises');

async function createStudentFile() {
    await fs.writeFile(
        'students.txt',
        '101,John\n102,Smith\n'
    );

    console.log('Student file created');
}

createStudentFile();
```

### Step 5: Run

```bash
node app.js
```

Expected:

```text
Student file created
```

Check the file:

```bash
cat students.txt
```

Expected:

```text
101,John
102,Smith
```

`writeFile()` creates the file or replaces its existing content. 

---

# Part 3 — Append a Student

### Step 6: Modify `app.js`

Replace the code with:

```javascript
const fs = require('fs/promises');

async function manageStudents() {

    await fs.writeFile(
        'students.txt',
        '101,John\n102,Smith\n'
    );

    await fs.appendFile(
        'students.txt',
        '103,David\n'
    );

    console.log('Student added');
}

manageStudents();
```

### Step 7: Run

```bash
node app.js
```

Check:

```bash
cat students.txt
```

Expected:

```text
101,John
102,Smith
103,David
```

`appendFile()` adds data without removing existing content. 

---

# Part 4 — Read the File

### Step 8: Add reading functionality

Update `app.js`:

```javascript
const fs = require('fs/promises');

async function manageStudents() {

    await fs.writeFile(
        'students.txt',
        '101,John\n102,Smith\n'
    );

    await fs.appendFile(
        'students.txt',
        '103,David\n'
    );

    const data = await fs.readFile(
        'students.txt',
        'utf-8'
    );

    console.log('Student Records:');
    console.log(data);
}

manageStudents();
```

### Step 9: Run

```bash
node app.js
```

Expected:

```text
Student Records:
101,John
102,Smith
103,David
```

The lab uses the recommended `fs/promises` + `async/await` approach from the material. 

---

# Part 5 — Create a Directory

### Step 10: Create `reports` directory

Add this before reading the file:

```javascript
await fs.mkdir('reports', {
    recursive: true
});
```

Complete section:

```javascript
await fs.mkdir('reports', {
    recursive: true
});

console.log('Reports directory created');
```

Run:

```bash
node app.js
```

Check:

```bash
ls
```

Expected:

```text
app.js
events.js
logger.js
processor.js
reports
students.txt
package.json
```

The `recursive: true` option allows Node.js to create missing parent directories as required. 

---

# Part 6 — Check File Metadata

### Step 11: Use `stat()`

Add:

```javascript
const info = await fs.stat('students.txt');

console.log('File size:', info.size);
console.log('Is file:', info.isFile());
console.log('Modified:', info.mtime);
```

Run:

```bash
node app.js
```

Example output:

```text
File size: 36
Is file: true
Modified: 2026-08-14T...
```

`stat()` provides information such as file size, type, and modification time. 

---

# Part 7 — Stream the Student File

Now we move from `fs` to **Streams**.

Streams process data in chunks instead of loading the complete file into memory. 

### Step 12: Open `processor.js`

Add:

```javascript
const fs = require('fs');

const readStream =
    fs.createReadStream('students.txt');

readStream.on('data', (chunk) => {
    console.log('Received chunk:');
    console.log(chunk.toString());
});

readStream.on('end', () => {
    console.log('Finished reading file');
});

readStream.on('error', (err) => {
    console.error('Error:', err.message);
});
```

### Step 13: Run

```bash
node processor.js
```

You should see something similar to:

```text
Received chunk:
101,John
102,Smith
103,David

Finished reading file
```

The important stream events are `data`, `end`, and `error`. 

---

# Part 8 — Copy File Using `pipe()`

Instead of manually handling every chunk, use `pipe()`.

### Step 14: Replace `processor.js`

```javascript
const fs = require('fs');

const readStream =
    fs.createReadStream('students.txt');

const writeStream =
    fs.createWriteStream('students-copy.txt');

readStream.pipe(writeStream);

writeStream.on('finish', () => {
    console.log('File copied successfully');
});
```

### Step 15: Run

```bash
node processor.js
```

Expected:

```text
File copied successfully
```

Check:

```bash
cat students-copy.txt
```

You should get the same student records.

`pipe()` connects a readable stream to a writable stream and handles data flow automatically. 

---

# Part 9 — Create an EventEmitter

Now create a simple event-driven student system.

### Step 16: Open `events.js`

Add:

```javascript
const EventEmitter = require('events');

const studentEvents = new EventEmitter();

studentEvents.on('studentAdded', (student) => {
    console.log('Student added:', student);
});

studentEvents.emit(
    'studentAdded',
    '104,Alice'
);
```

### Step 17: Run

```bash
node events.js
```

Expected:

```text
Student added: 104,Alice
```

The EventEmitter pattern follows:

```text
Create
   ↓
Subscribe
   ↓
Emit
   ↓
Listener executes
```

This is the basic publisher/subscriber pattern described in the material. 

---

# Part 10 — Multiple Listeners

### Step 18: Add another listener

Update `events.js`:

```javascript
const EventEmitter = require('events');

const studentEvents = new EventEmitter();

studentEvents.on('studentAdded', (student) => {
    console.log('Student Logger:', student);
});

studentEvents.on('studentAdded', (student) => {
    console.log('Notification:', `${student} was added`);
});

studentEvents.emit(
    'studentAdded',
    '104,Alice'
);
```

### Step 19: Run

```bash
node events.js
```

Expected:

```text
Student Logger: 104,Alice
Notification: 104,Alice was added
```

One event can trigger multiple independent listeners. 

---

# Part 11 — Mini Integration

Now combine **File System + EventEmitter**.

### Step 20: Update `events.js`

```javascript
const fs = require('fs/promises');
const EventEmitter = require('events');

const studentEvents = new EventEmitter();

studentEvents.on('studentAdded', (student) => {
    console.log('Student Logger:', student);
});

studentEvents.on('studentAdded', (student) => {
    console.log('Notification sent for:', student);
});

async function addStudent(student) {

    await fs.appendFile(
        'students.txt',
        student + '\n'
    );

    studentEvents.emit(
        'studentAdded',
        student
    );
}

addStudent('104,Alice');
```

### Step 21: Run

```bash
node events.js
```

Expected:

```text
Student Logger: 104,Alice
Notification sent for: 104,Alice
```

Check:

```bash
cat students.txt
```

Expected:

```text
101,John
102,Smith
103,David
104,Alice
```

---

# Final Lab Architecture

```text
              Student Application
                     |
                     v
              addStudent()
                     |
          +----------+----------+
          |                     |
          v                     v
     fs.appendFile()      EventEmitter
          |                     |
          v              studentAdded
   students.txt           /          \
                         v            v
                      Logger      Notification
```

This combines the three core concepts from the session: `fs/promises`, Streams, and `EventEmitter`. 

---

# Student Challenge

Complete these **3 small tasks** without looking at the solution:

### Challenge 1 — File Logger

Create:

```text
application.log
```

Append:

```text
Application started
Student added
Application stopped
```

Use `appendFile()`.

This follows the file-logger exercise suggested in the source material. 

### Challenge 2 — File Copy

Create:

```text
students-backup.txt
```

Copy `students.txt` using:

```javascript
createReadStream()
createWriteStream()
pipe()
```

### Challenge 3 — Student Event

Create:

```text
studentRemoved
```

Add two listeners:

```text
Student removed from database
Notification sent
```

Then emit:

```javascript
studentEvents.emit('studentRemoved', '104,Alice');
```

---

## Lab Completion Checklist

* [ ] Created Node.js project
* [ ] Used `fs/promises`
* [ ] Created a file using `writeFile()`
* [ ] Added records using `appendFile()`
* [ ] Read file using `readFile()`
* [ ] Created directory using `mkdir()`
* [ ] Checked metadata using `stat()`
* [ ] Created readable stream
* [ ] Used `data`, `end`, and `error` events
* [ ] Copied file using `pipe()`
* [ ] Created `EventEmitter`
* [ ] Added multiple listeners
* [ ] Integrated file operation with an event

**Outcome:** Students finish with a small working Node.js application that demonstrates the practical relationship between **file handling → streams → events**, without going beyond the lower-medium difficulty level.

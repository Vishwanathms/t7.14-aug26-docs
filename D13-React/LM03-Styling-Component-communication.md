# Progressive Hands-on Lab Manual

## Lab: Student Management UI — Styling & Component Communication

### Duration

**40–50 minutes**

### Difficulty

**Beginner → Lower-Intermediate**

### Scenario

You are developing a small **Student Management UI**.

The application should allow users to:

1. Enter a student name
2. Add the student
3. Display the student list
4. Display the total number of students
5. Style different components
6. Demonstrate parent-child and child-parent communication
7. Share state between sibling components

---

# Part 1 — Create the React Project

## Step 1 — Create Project

If you do not already have a React project:

```bash
npm create vite@latest student-management-ui
```

Select:

```text
React
JavaScript
```

Move into the project:

```bash
cd student-management-ui
```

Install dependencies:

```bash
npm install
```

Start the application:

```bash
npm run dev
```

Open the URL shown by Vite.

---

# Part 2 — Create Component Structure

## Step 2 — Create Folders

Inside `src`, create:

```text
src/
├── components/
│   ├── StudentForm.jsx
│   ├── StudentList.jsx
│   ├── StudentCard.jsx
│   └── Badge.jsx
├── App.jsx
├── App.css
└── main.jsx
```

---

# Part 3 — Create StudentCard

## Step 3 — Create StudentCard.jsx

Create:

```text
src/components/StudentCard.jsx
```

Add:

```jsx
function StudentCard({ name }) {
  return (
    <div className="student-card">
      <h3>{name}</h3>
      <p>React Student</p>
    </div>
  );
}

export default StudentCard;
```

### What is happening?

The component receives:

```jsx
{name}
```

through props.

The parent can use:

```jsx
<StudentCard name="Rahul" />
```

---

# Part 4 — Style Using CSS

## Step 4 — Create StudentCard.css

Create:

```text
src/components/StudentCard.css
```

Add:

```css
.student-card {
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 15px;
  margin: 10px 0;
  background: #f8f8f8;
}

.student-card h3 {
  margin: 0;
}
```

Import it into `StudentCard.jsx`:

```jsx
import './StudentCard.css';

function StudentCard({ name }) {
  return (
    <div className="student-card">
      <h3>{name}</h3>
      <p>React Student</p>
    </div>
  );
}

export default StudentCard;
```

### Checkpoint

Your component should now have visible styling.

---

# Part 5 — Create Badge Using Inline Styling

## Step 5 — Create Badge.jsx

Create:

```text
src/components/Badge.jsx
```

Add:

```jsx
function Badge() {
  const style = {
    backgroundColor: '#0F6E56',
    color: 'white',
    padding: '4px 8px',
    borderRadius: '4px'
  };

  return (
    <span style={style}>
      Active
    </span>
  );
}

export default Badge;
```

---

# Part 6 — Use Badge in StudentCard

## Step 6 — Import Badge

Modify `StudentCard.jsx`:

```jsx
import './StudentCard.css';
import Badge from './Badge';

function StudentCard({ name }) {
  return (
    <div className="student-card">
      <h3>{name}</h3>
      <p>React Student</p>
      <Badge />
    </div>
  );
}

export default StudentCard;
```

### Observe

You have now used:

```text
StudentCard
   |
   └── Badge
```

This demonstrates **component composition**.

---

# Part 7 — Create StudentList

## Step 7 — Create StudentList.jsx

Create:

```text
src/components/StudentList.jsx
```

Add:

```jsx
import StudentCard from './StudentCard';

function StudentList({ students }) {
  return (
    <div>
      <h2>Students</h2>

      {students.map((student) => (
        <StudentCard
          key={student.id}
          name={student.name}
        />
      ))}
    </div>
  );
}

export default StudentList;
```

The component receives:

```jsx
students
```

from its parent.

---

# Part 8 — Create StudentForm

## Step 8 — Create StudentForm.jsx

Create:

```text
src/components/StudentForm.jsx
```

Add:

```jsx
import { useState } from 'react';

function StudentForm({ onAddStudent }) {
  const [name, setName] = useState('');

  function handleSubmit(e) {
    e.preventDefault();

    if (!name.trim()) {
      return;
    }

    onAddStudent(name);

    setName('');
  }

  return (
    <form onSubmit={handleSubmit}>
      <input
        value={name}
        onChange={(e) => setName(e.target.value)}
        placeholder="Enter student name"
      />

      <button type="submit">
        Add Student
      </button>
    </form>
  );
}

export default StudentForm;
```

### Important Concept

The child component does this:

```jsx
onAddStudent(name);
```

It is calling a function provided by the parent.

This is **child → parent communication**.

---

# Part 9 — Implement State in App

## Step 9 — Modify App.jsx

Replace the contents with:

```jsx
import { useState } from 'react';
import StudentForm from './components/StudentForm';
import StudentList from './components/StudentList';
import './App.css';

function App() {
  const [students, setStudents] = useState([]);

  function addStudent(name) {
    const newStudent = {
      id: Date.now(),
      name: name
    };

    setStudents([...students, newStudent]);
  }

  return (
    <div className="app">
      <h1>Student Management</h1>

      <StudentForm
        onAddStudent={addStudent}
      />

      <p>
        Total Students: {students.length}
      </p>

      <StudentList
        students={students}
      />
    </div>
  );
}

export default App;
```

---

# Part 10 — Understand the Data Flow

At this point, your application looks like:

```text
                     App
                      |
              students state
                      |
          ┌───────────┴───────────┐
          ↓                       ↓
    StudentForm              StudentList
          |                       |
          | callback              | students
          ↓                       ↓
       addStudent             StudentCard
                                  |
                                  ↓
                                Badge
```

### Direction 1 — Parent → Child

```jsx
<StudentList students={students} />
```

Data moves down.

### Direction 2 — Child → Parent

```jsx
<StudentForm onAddStudent={addStudent} />
```

The child calls:

```jsx
onAddStudent(name);
```

### Direction 3 — Parent → Sibling

`App` sends the updated `students` to `StudentList`.

This is **lifting state up**.

---

# Part 11 — Style the Application

## Step 11 — Update App.css

```css
.app {
  width: 600px;
  margin: 40px auto;
  font-family: Arial, sans-serif;
}

.app h1 {
  text-align: center;
}

form {
  display: flex;
  gap: 10px;
}

input {
  flex: 1;
  padding: 10px;
}

button {
  padding: 10px 15px;
  cursor: pointer;
}
```

Save the file.

Refresh the browser.

---

# Part 12 — Test the Application

## Step 12 — Add Students

Enter:

```text
Rahul
```

Click:

```text
Add Student
```

Then add:

```text
Priya
```

Then:

```text
Arun
```

Expected:

```text
Student Management

[Enter student name] [Add Student]

Total Students: 3

Students

Rahul
React Student     Active

Priya
React Student     Active

Arun
React Student     Active
```

---

# Part 13 — Demonstrate CSS Modules

Now modify `StudentCard` to understand the third styling approach.

## Step 13 — Create CSS Module

Create:

```text
src/components/StudentCard.module.css
```

Add:

```css
.card {
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 15px;
  margin: 10px 0;
}

.title {
  margin: 0;
}
```

---

## Step 14 — Modify StudentCard.jsx

Replace the CSS import:

```jsx
import './StudentCard.css';
```

with:

```jsx
import styles from './StudentCard.module.css';
```

Then change:

```jsx
<div className="student-card">
```

to:

```jsx
<div className={styles.card}>
```

And:

```jsx
<h3>{name}</h3>
```

to:

```jsx
<h3 className={styles.title}>
  {name}
</h3>
```

Complete component:

```jsx
import styles from './StudentCard.module.css';
import Badge from './Badge';

function StudentCard({ name }) {
  return (
    <div className={styles.card}>
      <h3 className={styles.title}>
        {name}
      </h3>

      <p>React Student</p>

      <Badge />
    </div>
  );
}

export default StudentCard;
```

---

# Part 15 — Progressive Exercise

Now ask students to implement the following without copying the solution.

## Exercise 1 — Add Course

Modify each student object to contain:

```text
name
course
```

Example:

```jsx
{
  id: 1,
  name: 'Rahul',
  course: 'React'
}
```

Display:

```text
Rahul
Course: React
```

---

## Exercise 2 — Pass Course Through Props

Modify:

```jsx
<StudentCard
  name={student.name}
/>
```

to also pass:

```jsx
course={student.course}
```

Then receive it:

```jsx
function StudentCard({ name, course }) {
```

Display:

```jsx
<p>Course: {course}</p>
```

---

## Exercise 3 — Add Student Count Component

Create:

```text
StudentCount.jsx
```

The component should receive:

```jsx
students
```

and display:

```text
Total Students: 5
```

Use it from `App`.

This reinforces:

**Parent → Child via props**

---

# Part 16 — Challenge: Child → Parent

Modify `StudentForm` to collect:

```text
Student Name
Course
```

Example:

```text
Name: Rahul
Course: React
```

When the user clicks Add:

```jsx
onAddStudent({
  name,
  course
});
```

The parent should receive the object.

Then create:

```jsx
{
  id: Date.now(),
  name: student.name,
  course: student.course
}
```

and add it to state.

---

# Part 17 — Final Application Architecture

Students should finish with approximately:

```text
src/
│
├── components/
│   ├── Badge.jsx
│   ├── StudentCard.jsx
│   ├── StudentCard.module.css
│   ├── StudentForm.jsx
│   ├── StudentList.jsx
│   └── StudentCount.jsx
│
├── App.jsx
├── App.css
└── main.jsx
```

### Component Relationship

```text
                         App
                          |
                   students state
                          |
             ┌────────────┼────────────┐
             ↓            ↓            ↓
       StudentForm   StudentCount  StudentList
             |                         |
             | callback                |
             ↓                         ↓
        addStudent                StudentCard
                                       |
                                       ↓
                                     Badge
```

---

# Part 18 — Lab Verification Checklist

* [ ] React application starts successfully
* [ ] StudentCard uses CSS styling
* [ ] Badge uses inline styles
* [ ] StudentCard uses CSS Modules
* [ ] App maintains `students` state
* [ ] StudentForm maintains input state
* [ ] Parent passes callback to StudentForm
* [ ] StudentForm calls the callback
* [ ] App updates the student list
* [ ] StudentList receives students through props
* [ ] StudentCard receives student data through props
* [ ] Total student count updates automatically
* [ ] Multiple students can be added
* [ ] No page refresh is required when adding a student

## Final Learning Outcome

By completing this lab, students should be able to explain this flow:

```text
             PROPS
Parent ─────────────────→ Child
   ↑                         |
   |                         |
   | CALLBACK                |
   └─────────────────────────┘

Shared state
     ↓
Nearest common parent
     ↓
Sibling components
```

**Core rule for the class:**

> **Data normally flows down through props. Events flow up through callback props. Shared state is lifted to the nearest common parent.**

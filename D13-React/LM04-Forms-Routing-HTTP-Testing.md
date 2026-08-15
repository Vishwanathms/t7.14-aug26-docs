

# Part B — Progressive Hands-on Lab Manual

# Lab: React Student Management Portal

### Duration

**60–75 minutes**

### Difficulty

**Below Medium / Beginner-Intermediate**

### Scenario

You are building the frontend for a **Student Management System**.

The existing backend is a Spring Boot REST API:

```text
http://localhost:8080/api/students
```

The React application must provide:

```text
Home
   ↓
Student List
   ↓
Register Student
   ↓
Submit Student
   ↓
Spring Boot API
```

You will progressively implement:

1. Controlled form
2. Multiple fields
3. Form validation
4. React Router
5. HTTP GET
6. HTTP POST
7. Jest + RTL test

---

# Step 1 — Create the React Project

Open terminal:

```bash
npm create vite@latest student-portal -- --template react
```

Move into the project:

```bash
cd student-portal
```

Install dependencies:

```bash
npm install
```

Start the application:

```bash
npm run dev
```

Open:

```text
http://localhost:5173
```

Expected:

```text
Vite + React
```

---

# Step 2 — Install React Router

Stop the server if required and install:

```bash
npm install react-router-dom
```

---

# Step 3 — Create Components

Create:

```text
src/
├── components/
│   ├── Home.jsx
│   ├── StudentList.jsx
│   └── RegisterStudent.jsx
│
├── App.jsx
├── main.jsx
└── index.css
```

---

# Step 4 — Create Home Component

Create:

```text
src/components/Home.jsx
```

Add:

```jsx
function Home() {
  return (
    <div>
      <h1>Student Portal</h1>
      <p>Welcome to the Student Management System</p>
    </div>
  );
}

export default Home;
```

---

# Step 5 — Configure React Router

Open:

```text
src/App.jsx
```

Replace the content:

```jsx
import {
  BrowserRouter,
  Routes,
  Route,
  Link
} from 'react-router-dom';

import Home from './components/Home';
import StudentList from './components/StudentList';
import RegisterStudent from './components/RegisterStudent';

function App() {
  return (
    <BrowserRouter>

      <nav>
        <Link to="/">Home</Link> |{' '}
        <Link to="/students">Students</Link> |{' '}
        <Link to="/register">Register</Link>
      </nav>

      <Routes>

        <Route
          path="/"
          element={<Home />}
        />

        <Route
          path="/students"
          element={<StudentList />}
        />

        <Route
          path="/register"
          element={<RegisterStudent />}
        />

      </Routes>

    </BrowserRouter>
  );
}

export default App;
```

### Test

Click:

```text
Home
Students
Register
```

The URL should change without a complete browser reload.

---

# Step 6 — Create the Student Registration Form

Open:

```text
src/components/RegisterStudent.jsx
```

Add:

```jsx
import { useState } from 'react';

function RegisterStudent() {

  const [form, setForm] = useState({
    name: '',
    email: '',
    course: ''
  });

  function handleChange(e) {

    setForm({
      ...form,
      [e.target.name]: e.target.value
    });

  }

  function handleSubmit(e) {

    e.preventDefault();

    console.log('Student:', form);
  }

  return (
    <div>

      <h2>Register Student</h2>

      <form onSubmit={handleSubmit}>

        <div>
          <label>Name</label>

          <input
            name="name"
            value={form.name}
            onChange={handleChange}
          />
        </div>

        <div>
          <label>Email</label>

          <input
            name="email"
            value={form.email}
            onChange={handleChange}
          />
        </div>

        <div>
          <label>Course</label>

          <input
            name="course"
            value={form.course}
            onChange={handleChange}
          />
        </div>

        <button type="submit">
          Register
        </button>

      </form>

    </div>
  );
}

export default RegisterStudent;
```

---

# Step 7 — Test Controlled Form Behavior

Open:

```text
/register
```

Enter:

```text
Name: Rahul
Email: rahul@gmail.com
Course: Java
```

Click:

```text
Register
```

Open browser developer tools:

```text
F12
→ Console
```

Expected:

```text
Student:
{
  name: "Rahul",
  email: "rahul@gmail.com",
  course: "Java"
}
```

### What happened?

```text
Input
 ↓
onChange()
 ↓
handleChange()
 ↓
setForm()
 ↓
React State
```

---

# Step 8 — Add Basic Validation

Modify `handleSubmit()`:

```jsx
function handleSubmit(e) {

  e.preventDefault();

  if (!form.name) {
    alert('Name is required');
    return;
  }

  if (!form.email) {
    alert('Email is required');
    return;
  }

  if (!form.course) {
    alert('Course is required');
    return;
  }

  console.log('Student:', form);
}
```

Test by submitting an empty form.

The appropriate validation message should appear.

---

# Step 9 — Create Student List

Open:

```text
src/components/StudentList.jsx
```

Add:

```jsx
import { useEffect, useState } from 'react';

function StudentList() {

  const [students, setStudents] = useState([]);

  useEffect(() => {

    fetch('http://localhost:8080/api/students')
      .then(response => response.json())
      .then(data => {
        setStudents(data);
      })
      .catch(error => {
        console.error(error);
      });

  }, []);

  return (
    <div>

      <h2>Students</h2>

      <ul>

        {students.map(student => (
          <li key={student.id}>
            {student.name}
          </li>
        ))}

      </ul>

    </div>
  );
}

export default StudentList;
```

---

# Step 10 — Understand the API Flow

When `/students` opens:

```text
StudentList renders
       ↓
useEffect()
       ↓
fetch()
       ↓
GET /api/students
       ↓
Spring Boot
       ↓
Database
       ↓
JSON response
       ↓
setStudents()
       ↓
React re-renders
```

Example response:

```json
[
  {
    "id": 1,
    "name": "Rahul",
    "email": "rahul@gmail.com"
  },
  {
    "id": 2,
    "name": "Priya",
    "email": "priya@gmail.com"
  }
]
```

---

# Step 11 — Display More Student Information

Change:

```jsx
<li key={student.id}>
  {student.name}
</li>
```

to:

```jsx
<li key={student.id}>
  <strong>{student.name}</strong>
  <br />
  Email: {student.email}
  <br />
  Course: {student.course}
</li>
```

Now students should see more information from the API.

---

# Step 12 — Add Loading State

Improve the component.

Add:

```jsx
const [loading, setLoading] = useState(true);
```

Update the API call:

```jsx
useEffect(() => {

  fetch('http://localhost:8080/api/students')
    .then(response => response.json())
    .then(data => {
      setStudents(data);
      setLoading(false);
    })
    .catch(error => {
      console.error(error);
      setLoading(false);
    });

}, []);
```

Before the list:

```jsx
{loading && <p>Loading students...</p>}
```

This gives the user feedback while the API request is running.

---

# Step 13 — Connect Registration Form to Spring Boot

Now replace:

```jsx
console.log('Student:', form);
```

with an HTTP POST.

```jsx
fetch('http://localhost:8080/api/students', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json'
  },
  body: JSON.stringify(form)
})
  .then(response => response.json())
  .then(data => {
    console.log('Created:', data);
    alert('Student registered successfully');
  })
  .catch(error => {
    console.error(error);
    alert('Registration failed');
  });
```

The complete submission flow becomes:

```text
React Form
    ↓
Validation
    ↓
JSON.stringify()
    ↓
HTTP POST
    ↓
Spring Boot
    ↓
Database
    ↓
Response
    ↓
React
```

---

# Step 14 — Handle HTTP Errors

Improve the POST request:

```jsx
fetch('http://localhost:8080/api/students', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json'
  },
  body: JSON.stringify(form)
})
  .then(response => {

    if (!response.ok) {
      throw new Error('Failed to register student');
    }

    return response.json();
  })
  .then(data => {

    console.log(data);

    alert('Student registered successfully');

  })
  .catch(error => {

    console.error(error);

    alert('Registration failed');

  });
```

### Important

HTTP status codes should be checked.

```text
2xx → Success
4xx → Client error
5xx → Server error
```

---

# Step 15 — Install Testing Libraries

Install:

```bash
npm install -D vitest jsdom @testing-library/react @testing-library/jest-dom
```

For a Vite-based project, **Vitest** is commonly used as the Jest-compatible test runner.

The testing concepts remain very similar:

```text
Test Runner
    +
React Testing Library
    +
jest-dom assertions
```

---

# Step 16 — Configure Vitest

Create:

```text
vitest.config.js
```

Add:

```jsx
import { defineConfig } from 'vitest/config';
import react from '@vitejs/plugin-react';

export default defineConfig({
  plugins: [react()],
  test: {
    environment: 'jsdom',
    setupFiles: './src/setupTests.js'
  }
});
```

Create:

```text
src/setupTests.js
```

Add:

```jsx
import '@testing-library/jest-dom';
```

---

# Step 17 — Add Test Script

Open:

```text
package.json
```

Add/update:

```json
{
  "scripts": {
    "dev": "vite",
    "build": "vite build",
    "test": "vitest",
    "test:ui": "vitest --ui"
  }
}
```

---

# Step 18 — Create a Test for Home

Create:

```text
src/components/Home.test.jsx
```

Add:

```jsx
import { render, screen } from '@testing-library/react';
import { describe, test, expect } from 'vitest';
import Home from './Home';

describe('Home component', () => {

  test('renders student portal heading', () => {

    render(<Home />);

    expect(
      screen.getByText('Student Portal')
    ).toBeInTheDocument();

  });

});
```

---

# Step 19 — Run the Test

Execute:

```bash
npm test
```

Expected result:

```text
✓ Home component
  ✓ renders student portal heading
```

The test verifies that the user can see:

```text
Student Portal
```

---

# Step 20 — Test Using Role

Change the test to:

```jsx
test('renders student portal heading', () => {

  render(<Home />);

  const heading =
    screen.getByRole('heading', {
      name: 'Student Portal'
    });

  expect(heading).toBeInTheDocument();

});
```

This is preferred because it tests the UI using a semantic role.

---

# Step 21 — Test the Registration Form

Create:

```text
src/components/RegisterStudent.test.jsx
```

Add:

```jsx
import {
  render,
  screen
} from '@testing-library/react';

import {
  describe,
  test,
  expect
} from 'vitest';

import RegisterStudent
  from './RegisterStudent';

describe('Register Student', () => {

  test('renders registration form', () => {

    render(<RegisterStudent />);

    expect(
      screen.getByRole('heading', {
        name: 'Register Student'
      })
    ).toBeInTheDocument();

    expect(
      screen.getByRole('button', {
        name: 'Register'
      })
    ).toBeInTheDocument();

  });

});
```

Run:

```bash
npm test
```

---

# Step 22 — Final Application Structure

Your project should now look approximately like:

```text
student-portal/
│
├── src/
│   │
│   ├── components/
│   │   ├── Home.jsx
│   │   ├── Home.test.jsx
│   │   ├── StudentList.jsx
│   │   ├── RegisterStudent.jsx
│   │   └── RegisterStudent.test.jsx
│   │
│   ├── setupTests.js
│   ├── App.jsx
│   ├── main.jsx
│   └── index.css
│
├── package.json
└── vite.config.js
```

---

# Step 23 — Final Lab Verification

Students should verify the following:

### Forms

* Register page opens
* Name field works
* Email field works
* Course field works
* Validation works
* Submit handler works

### Routing

```text
/ 
/students
/register
```

All routes should work without full-page navigation.

### HTTP

Verify:

```text
GET /api/students
POST /api/students
```

using the browser Network tab.

### Testing

Run:

```bash
npm test
```

All tests should pass.

---

# Step 24 — Mini Challenge

Ask students to implement these **without providing the solution**:

### Challenge 1 — Add Phone

Add:

```text
phone
```

to the registration form.

---

### Challenge 2 — Add Student Count

Display:

```text
Total Students: 5
```

using:

```jsx
students.length
```

---

### Challenge 3 — Add Delete Button

Display:

```text
Rahul    [Delete]
Priya    [Delete]
```

The button should eventually call:

```text
DELETE /api/students/{id}
```

---

### Challenge 4 — Add API Error Message

Instead of:

```jsx
console.error(error);
```

display:

```text
Unable to load students
```

on the page.

---

### Challenge 5 — Write One More Test

Write a test that verifies:

```text
Register button exists
```

using:

```jsx
screen.getByRole()
```

---

## Instructor Checkpoint

At the end of Hour 4, students should be able to explain this complete flow:

```text
                 React
                   │
        ┌──────────┼──────────┐
        │          │          │
      Forms      Router     Components
        │          │          │
        └──────────┼──────────┘
                   │
               useEffect
                   │
                   ▼
             HTTP Request
                   │
                   ▼
          Spring Boot REST API
                   │
                   ▼
                Database


Testing:

Component
    ↓
RTL render()
    ↓
screen.getByRole()
    ↓
Jest/Vitest expect()
    ↓
PASS / FAIL
```

**Teaching emphasis:** Since your learners already have Spring Boot REST API experience, spend less time explaining REST itself and focus on the **React side of the integration**—form state → JSON → HTTP request → API response → React state → UI.

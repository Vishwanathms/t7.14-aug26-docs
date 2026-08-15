# Part B — Progressive Lab Manual

# Lab: Student Portal — Search & Enrollment Dashboard

**Duration:** 35–45 minutes
**Level:** Fresher
**Starting point:** Continue from the **Hour 1 Student Profile Portal**

---

## Lab Scenario

You already built:

```text
Student Profile Portal
```

In Hour 1, student information was static.

Now the portal should become interactive.

We need to support:

```text
Student Portal
│
├── Student count
│
├── Add Student button
│
├── Search students
│
└── Search button
```

Students will progressively learn:

```text
useState
   ↓
Events
   ↓
Input handling
   ↓
Controlled input
   ↓
Combining state + events
```

---

# Step 1 — Start the Existing Project

Open the project created in Hour 1:

```bash
cd student-profile-portal
```

Start the development server:

```bash
npm run dev
```

Open:

```text
http://localhost:5173
```

### Checkpoint

You should see your Student Profile Portal from Hour 1.

---

# Step 2 — Create a Counter Component

Create:

```text
src/components/StudentCounter.jsx
```

Add:

```jsx
import { useState } from "react";

function StudentCounter() {
  const [count, setCount] = useState(0);

  return (
    <div>
      <h2>Enrolled Students: {count}</h2>

      <button onClick={() => setCount(count + 1)}>
        Add Student
      </button>
    </div>
  );
}

export default StudentCounter;
```

---

# Step 3 — Add Counter to App

Open:

```text
src/App.jsx
```

Import:

```jsx
import StudentCounter from "./components/StudentCounter";
```

Add it inside `<main>`:

```jsx
<StudentCounter />
```

For example:

```jsx
<main>
  <StudentCounter />

  <StudentCard
    name="Aditi"
    department="Computer Science"
    year={2}
    email="aditi@example.com"
    status="Active"
  />
</main>
```

---

# Step 4 — Test the State

Open the browser.

Initially:

```text
Enrolled Students: 0
```

Click:

```text
Add Student
```

It should become:

```text
Enrolled Students: 1
```

Click again:

```text
Enrolled Students: 2
```

### Stop and Ask Students

**Question:**

What changed?

Answer:

```text
count
```

What changed `count`?

```text
setCount()
```

What caused the UI to update?

```text
State update → React re-render
```

---

# Step 5 — Add a Remove Button

Now introduce another event.

Modify:

```jsx
<StudentCounter />
```

component:

```jsx
import { useState } from "react";

function StudentCounter() {
  const [count, setCount] = useState(0);

  function addStudent() {
    setCount(count + 1);
  }

  function removeStudent() {
    if (count > 0) {
      setCount(count - 1);
    }
  }

  return (
    <div>
      <h2>Enrolled Students: {count}</h2>

      <button onClick={addStudent}>
        Add Student
      </button>

      <button onClick={removeStudent}>
        Remove Student
      </button>
    </div>
  );
}

export default StudentCounter;
```

### Why use functions?

Instead of:

```jsx
onClick={() => setCount(count + 1)}
```

we now have:

```jsx
onClick={addStudent}
```

This makes the code easier to read as applications grow.

---

# Step 6 — Introduce Search State

Create:

```text
src/components/StudentSearch.jsx
```

Add:

```jsx
import { useState } from "react";

function StudentSearch() {
  const [query, setQuery] = useState("");

  return (
    <div>
      <h2>Search Students</h2>

      <input
        value={query}
        onChange={(e) => setQuery(e.target.value)}
        placeholder="Search student"
      />

      <p>Search text: {query}</p>
    </div>
  );
}

export default StudentSearch;
```

---

# Step 7 — Add Search to App

Import:

```jsx
import StudentSearch from "./components/StudentSearch";
```

Add:

```jsx
<StudentSearch />
```

Your page should now contain:

```text
Student Profile Portal

Enrolled Students: 0
[Add Student] [Remove Student]

Search Students
[ Search student ]

Search text:
```

---

# Step 8 — Test `onChange`

Click inside the input.

Type:

```text
Aditi
```

You should see:

```text
Search text: Aditi
```

Type:

```text
React
```

You should see:

```text
Search text: React
```

### Explain the flow

```text
User types
    ↓
onChange
    ↓
event object
    ↓
e.target.value
    ↓
setQuery()
    ↓
query changes
    ↓
React re-renders
```

---

# Step 9 — Understand the Event Object

Temporarily modify:

```jsx
function handleChange(e) {
  console.log(e);
}
```

Then:

```jsx
<input
  value={query}
  onChange={handleChange}
/>
```

Open browser developer tools.

Type something.

Students will see the event object.

The important part for this lab is:

```jsx
e.target.value
```

It represents the current value typed into the input.

---

# Step 10 — Move Event Logic into a Function

Instead of:

```jsx
onChange={(e) => setQuery(e.target.value)}
```

write:

```jsx
function handleChange(e) {
  setQuery(e.target.value);
}
```

Then:

```jsx
<input
  value={query}
  onChange={handleChange}
/>
```

This is easier to extend later.

---

# Step 11 — Add Search Button

Update `StudentSearch.jsx`:

```jsx
import { useState } from "react";

function StudentSearch() {
  const [query, setQuery] = useState("");

  function handleChange(e) {
    setQuery(e.target.value);
  }

  function handleSearch() {
    console.log("Searching for:", query);
  }

  return (
    <div>
      <h2>Search Students</h2>

      <input
        value={query}
        onChange={handleChange}
        placeholder="Search student"
      />

      <button onClick={handleSearch}>
        Search
      </button>
    </div>
  );
}

export default StudentSearch;
```

---

# Step 12 — Test Search

Enter:

```text
Aditi
```

Click:

```text
Search
```

Open the browser console.

You should see:

```text
Searching for: Aditi
```

Try:

```text
Rahul
```

Then:

```text
Search
```

You should see:

```text
Searching for: Rahul
```

---

# Step 13 — Demonstrate the Common Mistake

Ask students to temporarily change:

```jsx
<button onClick={handleSearch}>
```

to:

```jsx
<button onClick={handleSearch()}>
```

Save the file.

Explain what happens.

The function is executed while React is rendering instead of waiting for the click.

### Correct

```jsx
onClick={handleSearch}
```

### Incorrect

```jsx
onClick={handleSearch()}
```

This is one of the most important fresher mistakes to understand.

---

# Step 14 — Build the Final Search Component

Students should end up with:

```jsx
import { useState } from "react";

function StudentSearch() {
  const [query, setQuery] = useState("");

  function handleChange(e) {
    setQuery(e.target.value);
  }

  function handleSearch() {
    console.log("Searching for:", query);
  }

  return (
    <div>
      <h2>Search Students</h2>

      <input
        value={query}
        onChange={handleChange}
        placeholder="Search student"
      />

      <button onClick={handleSearch}>
        Search
      </button>

      <p>
        Current search: {query}
      </p>
    </div>
  );
}

export default StudentSearch;
```

---

# Step 15 — Add a Clear Button

Now give students a small challenge.

Create:

```jsx
function handleClear() {
  setQuery("");
}
```

Add:

```jsx
<button onClick={handleClear}>
  Clear
</button>
```

Expected behavior:

```text
Input:
[Aditi]

Click Clear

Input:
[       ]
```

This reinforces:

```text
Event
 ↓
setState
 ↓
UI update
```

---

# Step 16 — Final Application Structure

At the end of the lab:

```text
src/
│
├── components/
│   ├── Header.jsx
│   ├── Footer.jsx
│   ├── Card.jsx
│   ├── StudentCard.jsx
│   ├── StudentBadge.jsx
│   ├── StudentDetails.jsx
│   ├── StudentCounter.jsx
│   └── StudentSearch.jsx
│
├── App.jsx
├── App.css
└── main.jsx
```

Application:

```text
Student Profile Portal
│
├── Header
│
├── StudentCounter
│     ├── Add Student
│     └── Remove Student
│
├── StudentSearch
│     ├── Input
│     ├── Search
│     └── Clear
│
├── StudentCard
├── StudentCard
├── StudentCard
│
└── Footer
```

---

# Step 17 — Fresher Exercise

Give students **5–10 minutes** for this challenge.

### Challenge 1 — Add Reset

Add:

```text
Reset Count
```

The button should set:

```text
count = 0
```

---

### Challenge 2 — Search Message

If the query is empty, display:

```text
Enter a student name
```

Otherwise display:

```text
Searching for: Aditi
```

Hint:

```jsx
{query ? ... : ...}
```

Tell students that this is only an introduction to conditional rendering; it will be covered properly in the next session.

---

### Challenge 3 — Character Counter

Below the search box, display:

```text
Characters: 5
```

Hint:

```jsx
query.length
```

---

# Final Checkpoint

Ask every student to explain this diagram:

```text
             User
              │
              ▼
           Event
              │
       ┌──────┴──────┐
       │             │
    onClick       onChange
       │             │
       ▼             ▼
  Event Handler   Event Handler
       │             │
       ▼             ▼
   setCount()     setQuery()
       │             │
       └──────┬──────┘
              ▼
         State Update
              │
              ▼
        React Re-render
              │
              ▼
          Updated UI
```

## Lab Completion Criteria

Students should be able to demonstrate all six:

* [ ] Create state using `useState()`
* [ ] Update state using the setter function
* [ ] Handle `onClick`
* [ ] Handle `onChange`
* [ ] Read `e.target.value`
* [ ] Build a controlled input

### The main mental model for freshers

```text
Props = Data coming IN
State = Data owned INSIDE
Events = User actions
setState = Tell React something changed
Re-render = React updates the UI
```

This lab intentionally stays focused on **state and events**. Avoid introducing `useEffect`, API calls, lists, or complex form validation at this stage.

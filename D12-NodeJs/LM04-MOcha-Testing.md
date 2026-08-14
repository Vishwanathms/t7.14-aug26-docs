# Progressive Lab Manual — Mocha + Chai

**Duration:** 30–35 minutes
**Level:** Freshers
**Goal:** Build a small User Service and progressively test synchronous, callback-based, Promise-based, and Chai assertions.

---

## Lab Scenario

You are developing a simple **Student/User Service** for a Node.js application.

The service should:

```text
Fetch User
   ↓
Test synchronous logic
   ↓
Test callback-based async logic
   ↓
Test Promise-based async logic
   ↓
Improve assertions using Chai
```

---

# Step 1 — Create the Project

```bash
mkdir mocha-chai-lab
cd mocha-chai-lab

npm init -y
npm install --save-dev mocha chai
```

Create folders:

```bash
mkdir test
```

Final structure:

```text
mocha-chai-lab/
├── package.json
├── userService.js
└── test/
    └── userService.test.js
```

---

# Step 2 — Configure Mocha

Open `package.json`.

Change the scripts section:

```json
"scripts": {
  "test": "mocha"
}
```

Run:

```bash
npm test
```

Expected:

```text
0 passing
```

This confirms Mocha is configured.

---

# Step 3 — Create the User Service

Create `userService.js`:

```javascript
function getUser(id) {
    return {
        id: id,
        name: 'Aditi',
        role: 'student'
    };
}

module.exports = {
    getUser
};
```

---

# Step 4 — Write the First Mocha Test

Create:

```text
test/userService.test.js
```

Add:

```javascript
const assert = require('assert');

const { getUser } = require('../userService');

describe('User Service', () => {

    it('returns a user', () => {

        const user = getUser(1);

        assert.strictEqual(user.name, 'Aditi');

    });

});
```

Run:

```bash
npm test
```

Expected:

```text
1 passing
```

### Checkpoint

Students should understand:

```text
describe() → groups tests
it()       → defines a test
assert     → verifies expected result
```

---

# Step 5 — Add More Test Cases

Add:

```javascript
it('returns the correct user id', () => {

    const user = getUser(10);

    assert.strictEqual(user.id, 10);

});

it('returns student role', () => {

    const user = getUser(1);

    assert.strictEqual(user.role, 'student');

});
```

Run:

```bash
npm test
```

Expected:

```text
3 passing
```

---

# Step 6 — Add Callback-Based Async Function

Modify `userService.js`:

```javascript
function getUserAsync(id, callback) {

    setTimeout(() => {

        callback(null, {
            id: id,
            name: 'Aditi',
            role: 'student'
        });

    }, 100);
}

module.exports = {
    getUser,
    getUserAsync
};
```

---

# Step 7 — Test Async Code with `done()`

Update the test:

```javascript
const {
    getUser,
    getUserAsync
} = require('../userService');
```

Add:

```javascript
it('fetches user asynchronously', (done) => {

    getUserAsync(5, (err, user) => {

        assert.strictEqual(user.id, 5);
        assert.strictEqual(user.name, 'Aditi');

        done();
    });

});
```

Run:

```bash
npm test
```

Expected:

```text
4 passing
```

### Important

Explain:

```javascript
done();
```

tells Mocha:

> "The asynchronous test has finished."

---

# Step 8 — Demonstrate the Common Mistake

Temporarily remove:

```javascript
done();
```

Run:

```bash
npm test
```

Students should eventually see:

```text
Error: Timeout of 2000ms exceeded
```

Restore:

```javascript
done();
```

---

# Step 9 — Add Promise-Based Function

Modify `userService.js`:

```javascript
function getUserPromise(id) {

    return new Promise((resolve) => {

        setTimeout(() => {

            resolve({
                id: id,
                name: 'Aditi',
                role: 'student'
            });

        }, 100);

    });
}
```

Update exports:

```javascript
module.exports = {
    getUser,
    getUserAsync,
    getUserPromise
};
```

---

# Step 10 — Test with async/await

Update the import:

```javascript
const {
    getUser,
    getUserAsync,
    getUserPromise
} = require('../userService');
```

Add:

```javascript
it('fetches user using Promise', async () => {

    const user = await getUserPromise(20);

    assert.strictEqual(user.id, 20);
    assert.strictEqual(user.name, 'Aditi');

});
```

Run:

```bash
npm test
```

Expected:

```text
5 passing
```

---

# Step 11 — Introduce Chai

Install already completed in Step 1.

Add:

```javascript
const { expect } = require('chai');
```

Create another test:

```javascript
it('validates user using Chai', () => {

    const user = getUser(1);

    expect(user.name).to.equal('Aditi');
    expect(user.role).to.equal('student');
    expect(user.id).to.be.a('number');

});
```

Run:

```bash
npm test
```

---

# Step 12 — Progressive Challenge

Ask students to add these tests using Chai.

### Challenge 1

Verify that the user has an `id` property.

```javascript
expect(user).to.have.property('id');
```

### Challenge 2

Verify the user is an object.

```javascript
expect(user).to.be.an('object');
```

### Challenge 3

Verify the name.

```javascript
expect(user.name).to.equal('Aditi');
```

### Challenge 4

Verify multiple properties.

```javascript
expect(user).to.include({
    name: 'Aditi',
    role: 'student'
});
```

---

# Final Validation

Run:

```bash
npm test
```

Students should see approximately:

```text
User Service
  ✓ returns a user
  ✓ returns the correct user id
  ✓ returns student role
  ✓ fetches user asynchronously
  ✓ fetches user using Promise
  ✓ validates user using Chai

6 passing
```

---

## Final Discussion

Ask students:

1. What does `describe()` do?
2. What does `it()` represent?
3. Why is `done()` required?
4. What happens when `done()` is forgotten?
5. Why is `async/await` easier to read?
6. What is the difference between Mocha and Chai?

### Key takeaway

```text
Mocha → Runs and organizes tests
Chai  → Provides readable assertions
done  → Signals callback completion
async/await → Preferred modern async testing style
```

# GitLab SSH Lab — Create Repository and Push from Laptop

## Lab Objective

By the end of this lab, you will be able to:

* Create a project in GitLab SaaS
* Create a Git repository
* Add a sample file
* Configure Git on your laptop
* Generate an SSH key
* Add the SSH key to GitLab
* Clone the repository using SSH
* Make a change locally
* Push the change from your laptop to GitLab

---

## Prerequisites

You need:

* GitLab SaaS account
* Laptop with Git installed
* Internet access
* Terminal / Command Prompt / PowerShell

Check Git:

```bash
git --version
```

Expected:

```text
git version 2.x.x
```

---

# PART 1 — Create a GitLab Project

### Step 1 — Login to GitLab

Open GitLab SaaS:

**[https://gitlab.com](https://gitlab.com)**

Login using your GitLab account.

---

### Step 2 — Create a New Project

From the GitLab dashboard:

1. Click **New project**
2. Select **Create blank project**

Enter:

| Field        | Value          |
| ------------ | -------------- |
| Project name | `student-demo` |
| Project slug | `student-demo` |
| Visibility   | Private        |

For this lab, select:

**Initialize repository with a README**

Click:

**Create project**

---

# PART 2 — Verify the Repository

After the project is created, you should see:

```text
student-demo
│
└── README.md
```

The repository URL will look similar to:

```text
https://gitlab.com/<username>/student-demo
```

---

# PART 3 — Create a Sample File

Inside GitLab:

1. Open the project.
2. Click **+** or **New file**
3. Select **New file**

File name:

```text
hello.txt
```

Add:

```text
Hello GitLab!
This file was created from GitLab.
```

Commit the file.

Use commit message:

```text
Add hello file
```

Click:

**Commit changes**

You should now have:

```text
student-demo
├── README.md
└── hello.txt
```

---

# PART 4 — Configure Git on the Laptop

Open Terminal / PowerShell.

Configure your Git identity:

```bash
git config --global user.name "Your Name"
```

Example:

```bash
git config --global user.name "Rahul Kumar"
```

Configure your email:

```bash
git config --global user.email "your-email@example.com"
```

Verify:

```bash
git config --global --list
```

You should see:

```text
user.name=Rahul Kumar
user.email=your-email@example.com
```

---

# PART 5 — Generate an SSH Key

Check whether an SSH key already exists.

### Linux / macOS / Git Bash

```bash
ls ~/.ssh
```

Look for:

```text
id_ed25519
id_ed25519.pub
```

If you don't have one, create it:

```bash
ssh-keygen -t ed25519 -C "your-email@example.com"
```

When prompted:

```text
Enter file in which to save the key:
```

Press **Enter** to accept the default.

For the passphrase, you can enter one or press **Enter** for no passphrase.

You should get:

```text
Your identification has been saved in ~/.ssh/id_ed25519
Your public key has been saved in ~/.ssh/id_ed25519.pub
```

---

# PART 6 — Start SSH Agent

Run:

```bash
eval "$(ssh-agent -s)"
```

Add your SSH private key:

```bash
ssh-add ~/.ssh/id_ed25519
```

Verify:

```bash
ssh-add -l
```

You should see your SSH key listed.

> **Important:** Never share `id_ed25519`.
> The private key must remain on your laptop.

---

# PART 7 — Copy the Public SSH Key

Display the public key:

```bash
cat ~/.ssh/id_ed25519.pub
```

You will see something similar to:

```text
ssh-ed25519 AAAAC3NzaC1lZDI1NTE5AAAAI... your-email@example.com
```

Copy the **entire line**.

### Windows PowerShell

You can also use:

```powershell
Get-Content $env:USERPROFILE\.ssh\id_ed25519.pub
```

Copy the complete output.

---

# PART 8 — Add SSH Key to GitLab

In GitLab:

1. Click your profile picture.
2. Select **Preferences** / **Edit profile** depending on the current GitLab UI.
3. Open **SSH Keys**.
4. Click **Add new key**.

Paste your public key into:

**Key**

Add a title such as:

```text
Student Laptop
```

Click:

**Add key**

You should now see the key listed in your GitLab account.

---

# PART 9 — Test SSH Connection

From your laptop run:

```bash
ssh -T git@gitlab.com
```

The first time, you may see:

```text
Are you sure you want to continue connecting (yes/no/[fingerprint])?
```

Enter:

```text
yes
```

A successful connection should return a GitLab welcome/authentication message.

This proves:

```text
Laptop
   │
   │ SSH
   ▼
GitLab
```

---

# PART 10 — Get the SSH Repository URL

Go back to your GitLab project.

Click:

**Code**

Select:

**Clone with SSH**

You should get something similar to:

```text
git@gitlab.com:<username>/student-demo.git
```

Example:

```text
git@gitlab.com:rahul/student-demo.git
```

---

# PART 11 — Clone the Repository

Create a working directory:

```bash
mkdir gitlab-lab
cd gitlab-lab
```

Clone the repository:

```bash
git clone git@gitlab.com:<username>/student-demo.git
```

Example:

```bash
git clone git@gitlab.com:rahul/student-demo.git
```

You should see:

```text
Cloning into 'student-demo'...
```

Enter the repository:

```bash
cd student-demo
```

Check the files:

```bash
ls
```

Expected:

```text
README.md
hello.txt
```

---

# PART 12 — Check the Git Remote

Run:

```bash
git remote -v
```

Expected:

```text
origin  git@gitlab.com:<username>/student-demo.git (fetch)
origin  git@gitlab.com:<username>/student-demo.git (push)
```

This is important.

It confirms that your local repository is connected to GitLab using **SSH**.

---

# PART 13 — Make a Change on the Laptop

Edit `hello.txt`.

Change it to:

```text
Hello GitLab!

This file was modified from my laptop.

GitLab SSH push test successful.
```

Save the file.

---

# PART 14 — Check the Change

Run:

```bash
git status
```

You should see something similar to:

```text
modified:   hello.txt
```

Now view the change:

```bash
git diff
```

You should see the changes you made.

---

# PART 15 — Stage the Change

Run:

```bash
git add hello.txt
```

Check:

```bash
git status
```

The file should now appear under:

```text
Changes to be committed
```

---

# PART 16 — Commit the Change

Run:

```bash
git commit -m "Update hello file from laptop"
```

Expected:

```text
1 file changed
```

---

# PART 17 — Push to GitLab

Now perform the important step:

```bash
git push origin main
```

If your repository uses `master`, use:

```bash
git push origin master
```

Expected output will look similar to:

```text
Enumerating objects...
Counting objects...
Writing objects...
To gitlab.com:<username>/student-demo.git
   xxxxxxx..xxxxxxx  main -> main
```

---

# PART 18 — Verify in GitLab

Go back to your GitLab project.

Refresh the page.

Open:

```text
hello.txt
```

You should see:

```text
Hello GitLab!

This file was modified from my laptop.

GitLab SSH push test successful.
```

You have successfully completed:

```text
Laptop
   │
   │ git push
   │
   │ SSH authentication
   ▼
GitLab SaaS
   │
   ▼
Repository
```

---

# PART 19 — Verify the Complete Git Workflow

Students should understand the complete flow:

```text
                 GitLab SaaS
                     │
              SSH Authentication
                     │
                     ▼
Laptop ── clone ──► Local Repository
                     │
                     │
                 Edit File
                     │
                     ▼
                git status
                     │
                     ▼
                 git add
                     │
                     ▼
                git commit
                     │
                     ▼
                  git push
                     │
                     ▼
                 GitLab Repo
```

---

# Final Verification Checklist

* [ ] GitLab account created
* [ ] GitLab project created
* [ ] Repository initialized
* [ ] `README.md` created
* [ ] `hello.txt` created
* [ ] Git installed on laptop
* [ ] Git username configured
* [ ] Git email configured
* [ ] SSH key generated
* [ ] SSH public key added to GitLab
* [ ] `ssh -T git@gitlab.com` successful
* [ ] Repository cloned using SSH
* [ ] `git remote -v` shows SSH URL
* [ ] File modified locally
* [ ] `git add` completed
* [ ] `git commit` completed
* [ ] `git push` completed
* [ ] Change verified in GitLab

---

## Common Errors

### Error 1 — `Permission denied (publickey)`

Check:

```bash
ssh-add -l
```

If no key is listed:

```bash
ssh-add ~/.ssh/id_ed25519
```

Then test again:

```bash
ssh -T git@gitlab.com
```

---

### Error 2 — `Repository not found`

Check the remote:

```bash
git remote -v
```

Make sure it points to the correct GitLab project.

---

### Error 3 — `Author identity unknown`

Configure:

```bash
git config --global user.name "Your Name"
git config --global user.email "your-email@example.com"
```

---

### Error 4 — `src refspec main does not match any`

Check the current branch:

```bash
git branch
```

If the branch is `master`:

```bash
git push origin master
```

If it is `main`:

```bash
git push origin main
```

---

## Lab Success Criteria

The lab is successfully completed when the student can demonstrate:

```text
1. GitLab project
       ↓
2. SSH key configured
       ↓
3. SSH authentication successful
       ↓
4. Repository cloned
       ↓
5. File modified locally
       ↓
6. git add
       ↓
7. git commit
       ↓
8. git push
       ↓
9. Change visible in GitLab
```

**Student takeaway:** GitLab stores the remote repository, while Git on the laptop manages the local repository. SSH provides secure authentication between the laptop and GitLab during operations such as `clone`, `pull`, and `push`.

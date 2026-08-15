# Terraform Basics — Step-by-Step Hands-on Lab Manual

## 1. Lab Overview

### Objective

In this lab, you will learn the basic Terraform workflow by creating and managing a simple resource.

You will practice:

* Installing and verifying Terraform
* Creating a Terraform project
* Understanding providers
* Writing resources
* Using variables
* Using outputs
* Running `terraform init`
* Running `terraform plan`
* Running `terraform apply`
* Checking Terraform state
* Modifying infrastructure
* Destroying infrastructure

### Difficulty

**Beginner / Below Medium**

### Duration

**45–60 minutes**

### Scenario

You are a DevOps engineer working on a small application environment.

Before creating cloud infrastructure, your team wants you to understand the basic Terraform workflow.

You will create a simple file using Terraform.

The workflow will be:

```text
Terraform Configuration
        ↓
terraform init
        ↓
terraform plan
        ↓
terraform apply
        ↓
Resource Created
        ↓
Modify Configuration
        ↓
terraform apply
        ↓
terraform destroy
```

---

# 2. Prerequisites

You need:

* Ubuntu/Linux/Windows/macOS
* Terminal
* Basic Linux commands
* Basic understanding of infrastructure

No AWS/Azure/GCP account is required for this lab.

---

# 3. Verify Terraform Installation

Open a terminal.

Run:

```bash
terraform version
```

Expected output:

```text
Terraform v1.x.x
```

If Terraform is not installed, install it before continuing.

Verify the command:

```bash
which terraform
```

Example:

```text
/usr/bin/terraform
```

---

# 4. Create the Lab Directory

Create a working directory:

```bash
mkdir terraform-basic-lab
```

Move into it:

```bash
cd terraform-basic-lab
```

Check the directory:

```bash
pwd
```

You should see something similar to:

```text
/home/student/terraform-basic-lab
```

---

# 5. Create Your First Terraform File

Create:

```bash
touch main.tf
```

Open the file:

```bash
nano main.tf
```

Add:

```hcl
terraform {
  required_providers {
    local = {
      source  = "hashicorp/local"
      version = "~> 2.5"
    }
  }
}

provider "local" {}

resource "local_file" "student_file" {
  filename = "${path.module}/student.txt"

  content = <<EOT
Student Name: Terraform Student
Course: DevOps
Technology: Terraform
EOT
}
```

Save the file.

---

# 6. Understand the Terraform Configuration

The configuration contains three important sections.

## Terraform Block

```hcl
terraform {
  required_providers {
    local = {
      source  = "hashicorp/local"
      version = "~> 2.5"
    }
  }
}
```

This tells Terraform which provider is required.

A **provider** allows Terraform to communicate with a platform or service.

Examples:

```text
AWS        → AWS Provider
Azure      → AzureRM Provider
Kubernetes → Kubernetes Provider
Docker     → Docker Provider
Local      → Local Provider
```

---

## Provider

```hcl
provider "local" {}
```

This activates the Local provider.

---

## Resource

```hcl
resource "local_file" "student_file" {
```

The general syntax is:

```hcl
resource "<TYPE>" "<NAME>" {
}
```

In our example:

```text
Type = local_file
Name = student_file
```

Terraform identifies the resource as:

```text
local_file.student_file
```

---

# 7. Initialize Terraform

This is the first Terraform command you normally execute in a new project.

Run:

```bash
terraform init
```

You should see messages indicating that Terraform initialized successfully and downloaded the required provider.

Terraform will create a directory:

```text
.terraform/
```

Check:

```bash
ls -la
```

You should see:

```text
main.tf
.terraform
.terraform.lock.hcl
```

### Why do we run `terraform init`?

`terraform init`:

* Initializes the Terraform working directory
* Downloads required providers
* Prepares Terraform for execution

---

# 8. Format the Terraform Code

Run:

```bash
terraform fmt
```

Terraform will format the configuration according to standard Terraform formatting.

You can also check formatting with:

```bash
terraform fmt -check
```

---

# 9. Validate the Configuration

Run:

```bash
terraform validate
```

Expected result:

```text
Success! The configuration is valid.
```

### Difference

```text
terraform fmt
        ↓
Formats code

terraform validate
        ↓
Checks configuration syntax and structure
```

---

# 10. Create a Terraform Plan

Run:

```bash
terraform plan
```

Terraform analyzes:

```text
Configuration
      +
Current State
      ↓
Required Changes
```

You should see something similar to:

```text
Plan: 1 to add, 0 to change, 0 to destroy.
```

Terraform is telling you:

> One resource will be created.

At this stage, **nothing has been created yet**.

---

# 11. Apply the Configuration

Run:

```bash
terraform apply
```

Terraform will display the planned changes.

You will see:

```text
Do you want to perform these actions?
  Enter a value:
```

Enter:

```text
yes
```

Terraform should finish with:

```text
Apply complete! Resources: 1 added, 0 changed, 0 destroyed.
```

---

# 12. Verify the Created Resource

Check the directory:

```bash
ls -l
```

You should now see:

```text
main.tf
student.txt
.terraform
.terraform.lock.hcl
terraform.tfstate
```

Read the generated file:

```bash
cat student.txt
```

Expected:

```text
Student Name: Terraform Student
Course: DevOps
Technology: Terraform
```

Congratulations!

You have created your first resource using Terraform.

---

# 13. Understand Terraform State

Terraform created:

```text
terraform.tfstate
```

Check it:

```bash
ls -l terraform.tfstate
```

You can inspect the resources managed by Terraform:

```bash
terraform state list
```

Expected:

```text
local_file.student_file
```

You can also run:

```bash
terraform show
```

This displays information about the current Terraform-managed infrastructure.

### Important Concept

Terraform uses the state file to remember:

```text
What Terraform created
        +
Current resource information
        ↓
terraform.tfstate
```

In real environments, state management becomes very important.

---

# 14. Modify the Infrastructure

Now modify the file.

Open:

```bash
nano main.tf
```

Change:

```hcl
Technology: Terraform
```

to:

```text
Technology: Terraform and DevOps
```

Your resource should now contain:

```hcl
resource "local_file" "student_file" {
  filename = "${path.module}/student.txt"

  content = <<EOT
Student Name: Terraform Student
Course: DevOps
Technology: Terraform and DevOps
EOT
}
```

Save the file.

---

# 15. Run Terraform Plan Again

Run:

```bash
terraform plan
```

This time Terraform should show:

```text
Plan: 0 to add, 1 to change, 0 to destroy.
```

Terraform has detected that the desired configuration changed.

---

# 16. Apply the Change

Run:

```bash
terraform apply
```

Enter:

```text
yes
```

Now check:

```bash
cat student.txt
```

You should see:

```text
Student Name: Terraform Student
Course: DevOps
Technology: Terraform and DevOps
```

This demonstrates Terraform's **desired state model**.

---

# 17. Introduce Variables

Now we will remove hard-coded values.

Create:

```bash
touch variables.tf
```

Open it:

```bash
nano variables.tf
```

Add:

```hcl
variable "student_name" {
  description = "Name of the student"
  type        = string
  default     = "Terraform Student"
}
```

---

# 18. Use the Variable

Modify `main.tf`.

Change:

```text
Student Name: Terraform Student
```

to:

```text
Student Name: ${var.student_name}
```

The resource becomes:

```hcl
resource "local_file" "student_file" {
  filename = "${path.module}/student.txt"

  content = <<EOT
Student Name: ${var.student_name}
Course: DevOps
Technology: Terraform and DevOps
EOT
}
```

---

# 19. Run Terraform Plan

Run:

```bash
terraform plan
```

Terraform will use:

```hcl
default = "Terraform Student"
```

as the variable value.

---

# 20. Pass a Variable Value

You can override the default value.

Run:

```bash
terraform apply -var="student_name=Rahul"
```

Enter:

```text
yes
```

Check:

```bash
cat student.txt
```

You should see:

```text
Student Name: Rahul
Course: DevOps
Technology: Terraform and DevOps
```

---

# 21. Create an Output

Create:

```bash
touch outputs.tf
```

Add:

```hcl
output "student_file" {
  description = "Path of the generated student file"
  value       = local_file.student_file.filename
}
```

Run:

```bash
terraform apply
```

Terraform will display the output.

You can also run:

```bash
terraform output
```

Example:

```text
student_file = "./student.txt"
```

---

# 22. Terraform Workflow

At this point, you have learned the basic Terraform workflow:

```text
              main.tf
                 |
                 v
        terraform init
                 |
                 v
       terraform validate
                 |
                 v
          terraform plan
                 |
                 v
         terraform apply
                 |
                 v
       Infrastructure
                 |
                 v
        terraform state
                 |
                 v
         terraform destroy
```

Remember these commands:

| Command                | Purpose                 |
| ---------------------- | ----------------------- |
| `terraform init`       | Initialize project      |
| `terraform fmt`        | Format code             |
| `terraform validate`   | Validate configuration  |
| `terraform plan`       | Preview changes         |
| `terraform apply`      | Create/update resources |
| `terraform show`       | Display state           |
| `terraform state list` | List managed resources  |
| `terraform output`     | Display outputs         |
| `terraform destroy`    | Remove resources        |

---

# 23. View Terraform Resources

Run:

```bash
terraform state list
```

Expected:

```text
local_file.student_file
```

Get detailed information:

```bash
terraform state show local_file.student_file
```

Observe:

* Resource type
* Resource name
* Filename
* File content
* Resource attributes

---

# 24. Destroy the Infrastructure

Finally, remove the resource.

Run:

```bash
terraform destroy
```

Terraform will show:

```text
Plan: 0 to add, 0 to change, 1 to destroy.
```

Enter:

```text
yes
```

Verify:

```bash
ls
```

The `student.txt` file should no longer exist.

---

# 25. Final Lab Structure

Your project should contain:

```text
terraform-basic-lab/
│
├── main.tf
├── variables.tf
├── outputs.tf
├── .terraform/
├── .terraform.lock.hcl
└── terraform.tfstate
```

After `terraform destroy`, the Terraform state may remain but the managed resource is removed.

---

# 26. Hands-on Tasks

Complete these tasks without looking at the solution.

### Task 1 — Change Student Information

Change the generated file to:

```text
Student Name: <your name>
Course: Cloud and DevOps
Technology: Terraform
Level: Beginner
```

---

### Task 2 — Add Another Variable

Create:

```hcl
variable "course_name" {
  description = "Course name"
  type        = string
  default     = "Cloud and DevOps"
}
```

Use it inside `main.tf`.

---

### Task 3 — Add Another Output

Create an output that displays the student name.

Example:

```hcl
output "student_name" {
  value = var.student_name
}
```

Run:

```bash
terraform apply
```

Then:

```bash
terraform output
```

---

### Task 4 — Test Change Detection

1. Run `terraform apply`.
2. Modify the course name.
3. Run `terraform plan`.
4. Observe what Terraform wants to change.
5. Run `terraform apply`.
6. Verify the generated file.

---

# 27. Troubleshooting

## Error: Terraform command not found

Check:

```bash
terraform version
```

If the command does not exist, Terraform is not installed or is not in the PATH.

---

## Error: Provider initialization required

Run:

```bash
terraform init
```

---

## Error: Configuration is invalid

Run:

```bash
terraform validate
```

Then inspect the `.tf` files for syntax errors.

---

## Terraform is not detecting my change

Run:

```bash
terraform plan
```

Check that you modified a Terraform-managed attribute.

---

# 28. Key Concepts Learned

By completing this lab, you should understand:

### 1. Infrastructure as Code

Infrastructure can be described using configuration files instead of manually creating resources.

### 2. Provider

A provider allows Terraform to interact with an external platform or service.

```text
Terraform
   |
   +-- AWS
   +-- Azure
   +-- Kubernetes
   +-- Docker
   +-- VMware
```

### 3. Resource

A resource represents something Terraform manages.

```hcl
resource "local_file" "student_file" {
}
```

### 4. Variable

Variables make Terraform configurations reusable.

```hcl
var.student_name
```

### 5. Output

Outputs expose useful information after Terraform runs.

```hcl
output "student_file" {
}
```

### 6. State

Terraform maintains information about managed infrastructure in its state.

```text
terraform.tfstate
```

### 7. Desired State

You describe what you want:

```text
Desired Configuration
        ↓
     Terraform
        ↓
Current Infrastructure
```

Terraform determines the changes required to reach the desired state.

---

# 29. Mini Challenge

Create a new Terraform project called:

```text
employee-lab
```

The project must:

* Create a file called `employee.txt`
* Accept employee name using a variable
* Accept department using a variable
* Display both values in the file
* Create outputs for employee name and department
* Successfully run:

```bash
terraform init
terraform fmt
terraform validate
terraform plan
terraform apply
terraform output
terraform destroy
```

### Expected File

```text
Employee Name: <name>
Department: <department>
Role: DevOps Engineer
```

---
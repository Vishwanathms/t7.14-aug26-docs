# Terraform Variables — Step-by-Step Hands-on Lab Manual

## Lab Title

**Terraform Variables: Input, Output and Local Values**

## Duration

**60–75 minutes**

## Difficulty

**Beginner to Lower-Intermediate**

## Prerequisites

Before starting, students should know:

* Basic Terraform commands
* Terraform configuration files
* Basic HCL syntax
* `terraform init`
* `terraform plan`
* `terraform apply`
* Basic understanding of Terraform resources

This lab is based on the attached **Terraform Variables** training material. The presentation covers input variables, variable types, variable value precedence, output values, and local values.

---

# 1. Lab Scenario

You are working as a DevOps engineer for a training company.

The company wants to create reusable Terraform configurations for different students and environments.

Instead of hard-coding values such as:

```text
Student Name
Environment
Number of Servers
Server Names
Region
```

you will use Terraform variables.

The final configuration will demonstrate:

```text
                    Terraform Variables
                           |
          +----------------+----------------+
          |                |                |
       Input            Output            Local
      Variables         Values            Values
          |                |                |
     User provides     Terraform        Internal
       values           returns          reusable
                         values            values
```

The attached presentation also categorizes Terraform variables into **Input Variables, Output Values and Local Values**.

---

# 2. Lab Objectives

By the end of this lab, you will be able to:

* Create input variables
* Use string variables
* Use number variables
* Use boolean variables
* Use list variables
* Use map variables
* Pass variable values through the command line
* Use `terraform.tfvars`
* Understand variable precedence
* Create output values
* Create local values
* Reference variables using `var.<name>`
* Reference locals using `local.<name>`
* Use variables to make Terraform configurations reusable

---

# Part 1 — Create the Terraform Project

## Step 1. Create the Project Directory

Run:

```bash
mkdir terraform-variables-lab
cd terraform-variables-lab
```

Verify:

```bash
pwd
```

---

## Step 2. Create the Terraform Files

Create:

```bash
touch main.tf
touch variables.tf
touch outputs.tf
touch locals.tf
```

Your directory should look like:

```text
terraform-variables-lab/
│
├── main.tf
├── variables.tf
├── outputs.tf
└── locals.tf
```

---

# Part 2 — Create the Basic Terraform Configuration

For this lab, we will use the Terraform **local provider** so that students can practice variables without needing AWS or Azure credentials.

This allows us to focus on the variable concepts presented in the training material.

---

## Step 3. Configure the Provider

Open `main.tf`:

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
```

Save the file.

---

## Step 4. Initialize Terraform

Run:

```bash
terraform init
```

Verify:

```bash
terraform validate
```

Expected:

```text
Success! The configuration is valid.
```

---

# Part 3 — String Input Variable

The presentation explains that input variables act as parameters for Terraform configurations and allow configurations to be customized without changing their source code.

## Step 5. Create a String Variable

Open:

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

The basic syntax is:

```hcl
variable "variable_name" {
  description = "Description"
  type        = string
  default     = "value"
}
```

The presentation identifies `default`, `type`, `description`, `validation`, and `sensitive` as arguments available in a variable block.

---

# Part 4 — Use the Variable

## Step 6. Create a Resource

Add the following to `main.tf`:

```hcl
resource "local_file" "student" {
  filename = "${path.module}/student.txt"

  content = <<EOT
Student Name: ${var.student_name}
Course: Terraform
EOT
}
```

Notice:

```hcl
${var.student_name}
```

Terraform retrieves the value of the input variable using:

```text
var.<variable_name>
```

---

## Step 7. Run Terraform Plan

Run:

```bash
terraform plan
```

You should see:

```text
Plan: 1 to add, 0 to change, 0 to destroy.
```

---

## Step 8. Apply

Run:

```bash
terraform apply
```

Enter:

```text
yes
```

Check the file:

```bash
cat student.txt
```

Expected:

```text
Student Name: Terraform Student
Course: Terraform
```

---

# Part 5 — Override a Variable from Command Line

The presentation shows command-line assignment as one of the variable value sources:

```text
-var="my_var=my_value"
```

and places command-line values at the highest precedence in the illustrated precedence order.

## Step 9. Pass a Different Student Name

Run:

```bash
terraform apply -var="student_name=Rahul"
```

Enter:

```text
yes
```

Then:

```bash
cat student.txt
```

Expected:

```text
Student Name: Rahul
Course: Terraform
```

### Observation

You did **not** modify `variables.tf`.

The value was supplied at runtime.

---

# Part 6 — Number Variable

The attached presentation includes **number** as one of the Terraform input variable types.

## Step 10. Add a Number Variable

Edit `variables.tf`:

```hcl
variable "server_count" {
  description = "Number of servers"
  type        = number
  default     = 3
}
```

---

## Step 11. Use the Number Variable

Modify `main.tf`:

```hcl
resource "local_file" "student" {
  filename = "${path.module}/student.txt"

  content = <<EOT
Student Name: ${var.student_name}
Course: Terraform
Number of Servers: ${var.server_count}
EOT
}
```

Run:

```bash
terraform apply
```

Then:

```bash
cat student.txt
```

Expected:

```text
Student Name: Terraform Student
Course: Terraform
Number of Servers: 3
```

---

## Step 12. Override the Number

Run:

```bash
terraform apply -var="server_count=5"
```

Verify:

```bash
cat student.txt
```

Expected:

```text
Number of Servers: 5
```

---

# Part 7 — Boolean Variable

The presentation also demonstrates boolean variables, including a `create_vm` example and using a boolean to determine whether a resource should be created.

## Step 13. Create a Boolean Variable

Add to `variables.tf`:

```hcl
variable "training_enabled" {
  description = "Whether training is enabled"
  type        = bool
  default     = true
}
```

---

## Step 14. Display the Boolean Value

Update `main.tf`:

```hcl
resource "local_file" "student" {
  filename = "${path.module}/student.txt"

  content = <<EOT
Student Name: ${var.student_name}
Course: Terraform
Number of Servers: ${var.server_count}
Training Enabled: ${var.training_enabled}
EOT
}
```

Run:

```bash
terraform apply
```

Verify:

```bash
cat student.txt
```

---

## Step 15. Pass `false`

Run:

```bash
terraform apply -var="training_enabled=false"
```

Verify:

```bash
cat student.txt
```

You should see:

```text
Training Enabled: false
```

---

# Part 8 — List Variable

The presentation demonstrates list variables and accessing list elements using an index such as:

```text
var.users[0]
```

## Step 16. Create a List Variable

Add to `variables.tf`:

```hcl
variable "students" {
  description = "List of students"
  type        = list(string)

  default = [
    "Rahul",
    "Priya",
    "Amit"
  ]
}
```

---

## Step 17. Access a List Element

Modify `main.tf`:

```hcl
resource "local_file" "student" {
  filename = "${path.module}/student.txt"

  content = <<EOT
Student Name: ${var.student_name}
Course: Terraform
Number of Servers: ${var.server_count}
Training Enabled: ${var.training_enabled}

First Student: ${var.students[0]}
Second Student: ${var.students[1]}
EOT
}
```

Run:

```bash
terraform apply
```

Then:

```bash
cat student.txt
```

Expected:

```text
First Student: Rahul
Second Student: Priya
```

---

# Part 9 — Map Variable

The presentation demonstrates map variables where values are accessed using a key, for example:

```text
var.plans["5USD"]
```

## Step 18. Create a Map Variable

Add to `variables.tf`:

```hcl
variable "environment_sizes" {
  description = "Server size for each environment"
  type        = map(string)

  default = {
    dev  = "small"
    test = "medium"
    prod = "large"
  }
}
```

---

## Step 19. Access a Map Value

Update `main.tf`:

```hcl
resource "local_file" "student" {
  filename = "${path.module}/student.txt"

  content = <<EOT
Student Name: ${var.student_name}
Course: Terraform
Number of Servers: ${var.server_count}
Training Enabled: ${var.training_enabled}

First Student: ${var.students[0]}
Second Student: ${var.students[1]}

Development Size: ${var.environment_sizes["dev"]}
Production Size: ${var.environment_sizes["prod"]}
EOT
}
```

Run:

```bash
terraform apply
```

Verify:

```bash
cat student.txt
```

Expected:

```text
Development Size: small
Production Size: large
```

---

# Part 10 — Variable Files

Terraform supports several ways to provide variable values. The presentation shows:

```text
Command line
*.auto.tfvars.*
terraform.tfvars.json
terraform.tfvars
Environment variables
variables.tf
```

with precedence increasing toward the command line.

Now we will practice `terraform.tfvars`.

---

## Step 20. Create `terraform.tfvars`

Create:

```bash
touch terraform.tfvars
```

Add:

```hcl
student_name = "Anil"

server_count = 4

training_enabled = true

students = [
  "Anil",
  "Meena",
  "Kiran"
]

environment_sizes = {
  dev  = "small"
  test = "medium"
  prod = "large"
}
```

---

## Step 21. Run Terraform

Run:

```bash
terraform plan
```

Terraform automatically reads:

```text
terraform.tfvars
```

Run:

```bash
terraform apply
```

Verify:

```bash
cat student.txt
```

---

# Part 11 — Test Variable Precedence

This is an important exercise from the presentation.

Your `terraform.tfvars` contains:

```hcl
student_name = "Anil"
```

Now run:

```bash
terraform plan -var="student_name=Vishal"
```

Observe which value Terraform uses.

The command-line value should override the value supplied through the lower-precedence source shown in the presentation.

---

# Part 12 — Local Values

The presentation explains that local values are defined in a `locals` block and are local to a module. They can be referenced using:

```text
local.Variable_Name
```

## Step 22. Create Local Values

Open:

```bash
nano locals.tf
```

Add:

```hcl
locals {
  project_name = "terraform-training"

  environment = "dev"

  project_environment = "${local.project_name}-${local.environment}"
}
```

Here:

```text
local.project_name
local.environment
local.project_environment
```

are local values.

---

# Part 13 — Use Local Values

## Step 23. Update the Resource

Modify `main.tf`:

```hcl
resource "local_file" "student" {
  filename = "${path.module}/student.txt"

  content = <<EOT
Student Name: ${var.student_name}
Course: Terraform
Number of Servers: ${var.server_count}
Training Enabled: ${var.training_enabled}

First Student: ${var.students[0]}
Second Student: ${var.students[1]}

Development Size: ${var.environment_sizes["dev"]}
Production Size: ${var.environment_sizes["prod"]}

Project Name: ${local.project_name}
Environment: ${local.environment}
Project Environment: ${local.project_environment}
EOT
}
```

Run:

```bash
terraform apply
```

Check:

```bash
cat student.txt
```

---

# Part 14 — Output Values

The presentation explains that output values can expose useful resource attributes, display important information to the Terraform user, and act as return values from modules.

## Step 24. Create an Output

Open:

```bash
nano outputs.tf
```

Add:

```hcl
output "student_name" {
  description = "Student name"
  value       = var.student_name
}
```

Add another:

```hcl
output "project_environment" {
  description = "Project and environment"
  value       = local.project_environment
}
```

---

# Part 15 — Apply and View Outputs

## Step 25. Apply

Run:

```bash
terraform apply
```

At the end, Terraform should display something similar to:

```text
student_name = "Anil"

project_environment = "terraform-training-dev"
```

---

## Step 26. Use `terraform output`

Run:

```bash
terraform output
```

You can retrieve one specific output:

```bash
terraform output student_name
```

And:

```bash
terraform output project_environment
```

---

# Part 16 — Complete Project

At this stage your project should look like:

```text
terraform-variables-lab/
│
├── main.tf
├── variables.tf
├── terraform.tfvars
├── outputs.tf
├── locals.tf
│
├── .terraform/
├── .terraform.lock.hcl
└── terraform.tfstate
```

---

# Part 17 — Understand the Three Types

You have now implemented all three major concepts from the presentation.

## Input Variable

User/configuration provides the value.

```hcl
variable "student_name" {
  type    = string
  default = "Terraform Student"
}
```

Reference:

```hcl
var.student_name
```

Think:

```text
Input → Terraform
```

---

## Local Value

Terraform configuration creates an internal reusable value.

```hcl
locals {
  environment = "dev"
}
```

Reference:

```hcl
local.environment
```

Think:

```text
Internal calculation/reusable value
```

---

## Output Value

Terraform exposes a useful value.

```hcl
output "student_name" {
  value = var.student_name
}
```

Think:

```text
Terraform → User / Other Configuration
```

The presentation compares these concepts to programming functions: input variables are similar to function arguments, outputs to return values, and locals to temporary local symbols.

---

# Part 18 — Practical Exercise

## Exercise 1 — Add a Region Variable

Create:

```hcl
variable "region" {
  description = "Deployment region"
  type        = string
  default     = "ap-south-1"
}
```

Display it in `student.txt`.

---

## Exercise 2 — Add a Project Variable

Create:

```hcl
variable "project_name" {
  description = "Project name"
  type        = string
  default     = "student-management"
}
```

Use it in a local:

```hcl
locals {
  project_environment = "${var.project_name}-${var.environment}"
}
```

---

## Exercise 3 — Add an Environment Variable

Create:

```hcl
variable "environment" {
  description = "Environment name"
  type        = string
  default     = "dev"
}
```

Then use:

```hcl
locals {
  project_environment = "${var.project_name}-${var.environment}"
}
```

---

# Part 19 — Mini Challenge

Create a Terraform configuration for a small application environment.

The configuration must accept:

### Input Variables

```text
project_name
environment
region
server_count
enabled
server_names
environment_sizes
```

Use these types:

```text
project_name       → string
environment        → string
region             → string
server_count       → number
enabled            → bool
server_names      → list(string)
environment_sizes → map(string)
```

Create locals:

```text
project_environment
```

Create outputs:

```text
project_environment
server_count
region
```

Generate a file containing all these values.

---

# Part 20 — Validation Exercise

Add validation to the `server_count` variable.

Example requirement:

```text
Server count must be between 1 and 10.
```

Use:

```hcl
variable "server_count" {
  description = "Number of servers"
  type        = number
  default     = 3

  validation {
    condition     = var.server_count >= 1 && var.server_count <= 10
    error_message = "Server count must be between 1 and 10."
  }
}
```

Now test:

```bash
terraform plan -var="server_count=5"
```

This should work.

Test:

```bash
terraform plan -var="server_count=20"
```

Terraform should reject the value.

---

# Part 21 — Final Verification

Run the following commands in order:

```bash
terraform fmt
```

```bash
terraform validate
```

```bash
terraform plan
```

```bash
terraform apply
```

```bash
terraform output
```

Then verify:

```bash
cat student.txt
```

---

# Part 22 — Cleanup

Because this lab creates a local file, cleanup is simple.

Run:

```bash
terraform destroy
```

Enter:

```text
yes
```

Verify:

```bash
ls
```

---

# 23. Knowledge Check

Before completing the lab, students should be able to answer:

1. What is an input variable?
2. Why are variables useful in Terraform?
3. How do you reference an input variable?
4. What is the difference between `var.x` and `local.x`?
5. What is a `terraform.tfvars` file?
6. How can a variable be supplied through the command line?
7. What is variable precedence?
8. What is a list variable?
9. What is a map variable?
10. What is a boolean variable?
11. What is an output value?
12. How do you display outputs?
13. When should local values be used?
14. Why should locals not be overused?

The presentation specifically recommends using locals moderately, particularly where a value or expression is reused in multiple places and may need to be changed centrally.

---

# 24. Final Terraform Variable Workflow

Students should remember this workflow:

```text
                 INPUT
                   |
          +--------+---------+
          |                  |
     CLI / tfvars       Environment
          |                  |
          +--------+---------+
                   |
                   v
             Terraform
                   |
          +--------+---------+
          |                  |
        Locals            Resources
          |                  |
          +--------+---------+
                   |
                   v
                Output
                   |
                   v
             User / Module
```

## Key Commands

```bash
terraform init
terraform fmt
terraform validate
terraform plan
terraform apply
terraform output
terraform destroy
```

## Key References

```hcl
var.student_name
```

```hcl
var.students[0]
```

```hcl
var.environment_sizes["dev"]
```

```hcl
local.project_environment
```

---

# Lab Completion Criteria

The lab is considered complete when the student can independently:

* [ ] Create an input variable
* [ ] Use a string variable
* [ ] Use a number variable
* [ ] Use a boolean variable
* [ ] Use a list variable
* [ ] Use a map variable
* [ ] Pass a variable using `-var`
* [ ] Use `terraform.tfvars`
* [ ] Explain variable precedence
* [ ] Create a local value
* [ ] Reference a local value
* [ ] Create an output
* [ ] Retrieve an output using `terraform output`
* [ ] Add variable validation
* [ ] Run `terraform plan`
* [ ] Run `terraform apply`
* [ ] Run `terraform destroy`

**Next recommended lab:** Build a small **AWS/Azure Terraform environment using variables**, where the same configuration can deploy `dev`, `test`, and `prod` environments by changing only variable values.

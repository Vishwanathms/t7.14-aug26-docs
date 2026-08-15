# Terraform Conditions & Operations

## Simple Step-by-Step Hands-on Lab Manual

### Duration

**60–75 minutes**

### Level

**Beginner / Below Medium**

### Lab Scenario

You are creating a small Terraform-based training environment.

The configuration should be able to:

* Create resources conditionally
* Create multiple resources using `count`
* Create resources using `for_each`
* Control resource dependencies
* Use Terraform operators
* Use interpolation
* Read information using a data source
* Experiment with Terraform expressions using `terraform console`

The attached presentation covers these areas: **conditions, iterations, resource metadata, operators, interpolation and data sources**.

To keep the lab simple, we will use the **Local provider** instead of AWS/Azure. No cloud account is required.

---

# 1. Prerequisites

Make sure Terraform is installed.

Check:

```bash
terraform version
```

Create the lab directory:

```bash
mkdir terraform-conditions-lab
cd terraform-conditions-lab
```

---

# 2. Create the Terraform Project

Create the following files:

```bash
touch main.tf
touch variables.tf
touch outputs.tf
```

Directory:

```text
terraform-conditions-lab/
│
├── main.tf
├── variables.tf
└── outputs.tf
```

---

# 3. Configure the Local Provider

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

Initialize Terraform:

```bash
terraform init
```

Validate:

```bash
terraform validate
```

Expected:

```text
Success! The configuration is valid.
```

---

# 4. Lab 1 — Terraform Conditional Expression

The presentation introduces conditional expressions using:

```text
condition ? true-value : false-value
```

and demonstrates using a condition to decide a resource count.

We will use a variable to decide whether a file should be created.

---

## Step 1 — Create a Variable

Open `variables.tf`:

```bash
nano variables.tf
```

Add:

```hcl
variable "create_file" {
  description = "Whether to create the environment file"
  type        = bool
  default     = true
}
```

---

## Step 2 — Create the Conditional Resource

Add to `main.tf`:

```hcl
resource "local_file" "environment" {
  count = var.create_file ? 1 : 0

  filename = "${path.module}/environment.txt"

  content = "Environment file created by Terraform."
}
```

The important line is:

```hcl
count = var.create_file ? 1 : 0
```

Meaning:

```text
true  → count = 1 → create file

false → count = 0 → do not create file
```

---

## Step 3 — Apply

Run:

```bash
terraform plan
```

You should see:

```text
Plan: 1 to add, 0 to change, 0 to destroy.
```

Apply:

```bash
terraform apply
```

Enter:

```text
yes
```

Check:

```bash
ls
```

You should see:

```text
environment.txt
```

---

## Step 4 — Disable the Resource

Run:

```bash
terraform apply -var="create_file=false"
```

Enter:

```text
yes
```

Check:

```bash
ls
```

The file should be removed.

### Key Learning

Conditional expressions are useful when a resource should exist only when a condition is satisfied.

---

# 5. Lab 2 — Arithmetic and Logical Operators

The presentation covers:

* Arithmetic operators
* Equality operators
* Comparison operators
* Logical operators.

We can practice these without creating any infrastructure.

---

## Step 1 — Open Terraform Console

Run:

```bash
terraform console
```

---

## Step 2 — Test Arithmetic

Run:

```text
2 + 3
```

Expected:

```text
5
```

Try:

```text
10 - 4
```

Expected:

```text
6
```

Try:

```text
5 * 4
```

Expected:

```text
20
```

Try:

```text
20 / 5
```

Expected:

```text
4
```

Try:

```text
10 % 3
```

Expected:

```text
1
```

The presentation also demonstrates an expression such as `${2+3*4}`, producing `14`.

Test:

```text
2 + 3 * 4
```

Expected:

```text
14
```

---

# 6. Test Comparison Operators

The presentation covers:

```text
<
<=
>
>=
```

In Terraform console:

```text
5 > 3
```

Result:

```text
true
```

Try:

```text
5 < 3
```

Result:

```text
false
```

Try:

```text
10 >= 10
```

Result:

```text
true
```

---

# 7. Test Equality Operators

The presentation covers:

```text
==
!=
```

Run:

```text
"dev" == "dev"
```

Result:

```text
true
```

Run:

```text
"dev" == "prod"
```

Result:

```text
false
```

Run:

```text
"dev" != "prod"
```

Result:

```text
true
```

Exit:

```text
exit
```

---

# 8. Test Logical Operators

The presentation covers:

```text
||
&&
!
```

Run:

```bash
terraform console
```

Test:

```text
true && true
```

Result:

```text
true
```

Test:

```text
true && false
```

Result:

```text
false
```

Test:

```text
true || false
```

Result:

```text
true
```

Test:

```text
!true
```

Result:

```text
false
```

Exit:

```text
exit
```

---

# 9. Lab 3 — Interpolation

The presentation explains that Terraform can use interpolation to reference variables, resources and data sources. Examples include:

```text
${var.VARIABLE-NAME}
${aws_instance.name.id}
${data.template_file.name.rendered}
```

We will use interpolation with variables.

---

## Step 1 — Create Variables

Add to `variables.tf`:

```hcl
variable "project_name" {
  type    = string
  default = "student-project"
}

variable "environment" {
  type    = string
  default = "dev"
}
```

---

## Step 2 — Create a File

Add to `main.tf`:

```hcl
resource "local_file" "project" {
  filename = "${path.module}/project.txt"

  content = <<EOT
Project: ${var.project_name}
Environment: ${var.environment}
EOT
}
```

Here:

```hcl
${var.project_name}
```

and:

```hcl
${var.environment}
```

are interpolations.

---

## Step 3 — Apply

Run:

```bash
terraform fmt
terraform validate
terraform plan
terraform apply
```

Enter:

```text
yes
```

Check:

```bash
cat project.txt
```

Expected:

```text
Project: student-project
Environment: dev
```

---

# 10. Lab 4 — `count` Meta-Argument

The presentation explains that `count` creates a specified number of resource or module instances. Each instance is separately managed.

---

## Step 1 — Create a Count Variable

Add to `variables.tf`:

```hcl
variable "student_count" {
  description = "Number of student files"
  type        = number
  default     = 3
}
```

---

## Step 2 — Create Multiple Files

Add to `main.tf`:

```hcl
resource "local_file" "student" {
  count = var.student_count

  filename = "${path.module}/student-${count.index + 1}.txt"

  content = "Student ${count.index + 1}"
}
```

Notice:

```hcl
count.index
```

starts from:

```text
0
```

Therefore:

```hcl
count.index + 1
```

produces:

```text
1
2
3
```

---

## Step 3 — Apply

Run:

```bash
terraform apply
```

Enter:

```text
yes
```

Check:

```bash
ls student-*.txt
```

Expected:

```text
student-1.txt
student-2.txt
student-3.txt
```

---

## Step 4 — Increase the Count

Run:

```bash
terraform apply -var="student_count=5"
```

Check:

```bash
ls student-*.txt
```

You should now have:

```text
student-1.txt
student-2.txt
student-3.txt
student-4.txt
student-5.txt
```

---

# 11. Lab 5 — `for_each`

The presentation explains that `for_each` accepts a map or set of strings and creates one instance for each item.

---

## Step 1 — Create a Map Variable

Add to `variables.tf`:

```hcl
variable "environments" {
  type = map(string)

  default = {
    dev  = "Development"
    test = "Testing"
    prod = "Production"
  }
}
```

---

## Step 2 — Create Files Using `for_each`

Add to `main.tf`:

```hcl
resource "local_file" "environment" {
  for_each = var.environments

  filename = "${path.module}/${each.key}.txt"

  content = "Environment: ${each.value}"
}
```

Important values:

```text
each.key
each.value
```

For example:

```text
dev  → Development
test → Testing
prod → Production
```

---

## Step 3 — Apply

Run:

```bash
terraform apply
```

Enter:

```text
yes
```

Check:

```bash
ls *.txt
```

You should see:

```text
dev.txt
test.txt
prod.txt
```

Check one:

```bash
cat dev.txt
```

Expected:

```text
Environment: Development
```

---

# 12. `count` vs `for_each`

Remember:

### `count`

Use when you need a specific number of similar instances.

```hcl
count = 3
```

Example:

```text
server-1
server-2
server-3
```

### `for_each`

Use when each instance has a meaningful key or value.

```hcl
for_each = var.environments
```

Example:

```text
dev
test
prod
```

---

# 13. Lab 6 — `depends_on`

The presentation describes `depends_on` as a way to handle dependencies Terraform cannot automatically infer.

We will create two files.

The second file should only be created after the first resource.

---

## Step 1 — Create the First Resource

Add:

```hcl
resource "local_file" "first" {
  filename = "${path.module}/first.txt"

  content = "First resource"
}
```

---

## Step 2 — Create the Dependent Resource

Add:

```hcl
resource "local_file" "second" {
  filename = "${path.module}/second.txt"

  content = "Second resource depends on first"

  depends_on = [
    local_file.first
  ]
}
```

The important part is:

```hcl
depends_on = [
  local_file.first
]
```

This tells Terraform:

```text
Create first.txt
       ↓
Create second.txt
```

---

## Step 3 — Check the Plan

Run:

```bash
terraform plan
```

Apply:

```bash
terraform apply
```

Enter:

```text
yes
```

Verify:

```bash
cat first.txt
cat second.txt
```

---

# 14. Lab 7 — Data Source

The presentation explains that data sources provide dynamic information from providers. Examples mentioned include AWS AMIs and availability zones.

For this simple lab, we will use the Local provider's file data source.

---

## Step 1 — Create a Data Source

Add to `main.tf`:

```hcl
data "local_file" "project_info" {
  filename = "${path.module}/project.txt"
}
```

This tells Terraform to read the existing:

```text
project.txt
```

---

## Step 2 — Display the Data

Add to `outputs.tf`:

```hcl
output "project_content" {
  value = data.local_file.project_info.content
}
```

---

## Step 3 — Run Terraform

Run:

```bash
terraform apply
```

Then:

```bash
terraform output project_content
```

Expected:

```text
Project: student-project
Environment: dev
```

### Key Difference

```text
resource
   ↓
Terraform creates/manages something

data
   ↓
Terraform reads existing information
```

---

# 15. Lab 8 — Lifecycle

The presentation introduces the `lifecycle` block and these arguments:

```text
create_before_destroy
prevent_destroy
ignore_changes
```

For a simple demonstration, we will use `ignore_changes`.

---

## Step 1 — Add Lifecycle

Modify the project resource:

```hcl
resource "local_file" "project" {
  filename = "${path.module}/project.txt"

  content = <<EOT
Project: ${var.project_name}
Environment: ${var.environment}
EOT

  lifecycle {
    ignore_changes = [
      content
    ]
  }
}
```

---

## Step 2 — Apply

Run:

```bash
terraform apply
```

Now manually change the file:

```bash
echo "Manual change" >> project.txt
```

Run:

```bash
terraform plan
```

Terraform ignores the content change because we specified:

```hcl
ignore_changes = [
  content
]
```

### Important

`ignore_changes` should be used carefully. It tells Terraform to ignore changes to selected attributes.

---

# 16. Lab 9 — Terraform Console with Variables

Run:

```bash
terraform console
```

Try:

```text
var.project_name
```

Expected:

```text
"student-project"
```

Try:

```text
var.environment
```

Expected:

```text
"dev"
```

Try:

```text
var.student_count
```

Expected:

```text
3
```

Try:

```text
var.student_count > 2
```

Expected:

```text
true
```

Try:

```text
var.environment == "dev"
```

Expected:

```text
true
```

Exit:

```text
exit
```

---

# 17. Mini Challenge

Now create a small environment using what you learned.

## Requirement

Create:

```text
dev
test
prod
```

using `for_each`.

Each environment should generate a file.

Expected:

```text
dev.txt
test.txt
prod.txt
```

Each file should contain:

```text
Environment: dev
Project: terraform-training
```

Use:

* Variable
* `for_each`
* Interpolation
* Output

---

# 18. Final Verification

Run:

```bash
terraform fmt
```

Then:

```bash
terraform validate
```

Then:

```bash
terraform plan
```

Then:

```bash
terraform apply
```

Check the resources:

```bash
terraform state list
```

Check outputs:

```bash
terraform output
```

---

# 19. Cleanup

Remove all resources created by Terraform:

```bash
terraform destroy
```

Enter:

```text
yes
```

Verify:

```bash
terraform state list
```

There should be no managed resources remaining.

---

# 20. Quick Reference

| Concept        | Example                    |   |    |
| -------------- | -------------------------- | - | -- |
| Conditional    | `condition ? true : false` |   |    |
| Variable       | `var.name`                 |   |    |
| Interpolation  | `"Hello ${var.name}"`      |   |    |
| `count`        | `count = 3`                |   |    |
| Count index    | `count.index`              |   |    |
| `for_each`     | `for_each = var.items`     |   |    |
| For-each key   | `each.key`                 |   |    |
| For-each value | `each.value`               |   |    |
| Dependency     | `depends_on`               |   |    |
| Data source    | `data.local_file.example`  |   |    |
| Arithmetic     | `+ - * / %`                |   |    |
| Equality       | `== !=`                    |   |    |
| Comparison     | `< <= > >=`                |   |    |
| Logical        | `&&                        |   | !` |
| Lifecycle      | `lifecycle { ... }`        |   |    |

---

# 21. Lab Completion Checklist

* [ ] Create a conditional resource
* [ ] Use a conditional expression
* [ ] Test arithmetic operators
* [ ] Test equality operators
* [ ] Test comparison operators
* [ ] Test logical operators
* [ ] Use interpolation
* [ ] Use `count`
* [ ] Use `count.index`
* [ ] Use `for_each`
* [ ] Use `each.key`
* [ ] Use `each.value`
* [ ] Create an explicit dependency using `depends_on`
* [ ] Read a file using a data source
* [ ] Create an output from a data source
* [ ] Use a lifecycle rule
* [ ] Use `terraform console`
* [ ] Run `terraform plan`
* [ ] Run `terraform apply`
* [ ] Run `terraform destroy`

---

# 22. Expected Learning Outcome

After completing this lab, the learner should understand the basic Terraform operations covered in the presentation:

```text
Conditions
    ↓
Operators
    ↓
Interpolation
    ↓
count
    ↓
for_each
    ↓
depends_on
    ↓
Data Sources
    ↓
Lifecycle
    ↓
terraform console
```

The focus of this lab is **hands-on understanding rather than cloud infrastructure**, so students can complete it without AWS or Azure credentials.

**Next lab:** Apply these same concepts to an AWS/Azure environment—for example, conditionally creating VMs, creating multiple resources with `count`/`for_each`, using data sources to discover images, and controlling dependencies.

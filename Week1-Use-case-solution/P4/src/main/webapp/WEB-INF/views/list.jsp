<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Student Management System</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 40px; background: #f4f6f8; }
        h1 { color: #2c3e50; }
        table { border-collapse: collapse; width: 100%; background: #fff; }
        th, td { border: 1px solid #ddd; padding: 10px; text-align: left; }
        th { background: #2c3e50; color: #fff; }
        tr:nth-child(even) { background: #f2f2f2; }
        a.button { display: inline-block; padding: 6px 12px; margin: 2px; text-decoration: none; border-radius: 4px; color: #fff; }
        a.add { background: #27ae60; }
        a.edit { background: #2980b9; }
        a.delete { background: #c0392b; }
        a.stats { background: #8e44ad; }
        .toolbar { margin-bottom: 15px; }
    </style>
</head>
<body>

<h1>Student Management System</h1>

<div class="toolbar">
    <a class="button add" href="${pageContext.request.contextPath}/students/add">Add Student</a>
    <a class="button stats" href="${pageContext.request.contextPath}/students/stats">Highest / Average</a>
</div>

<table>
    <tr>
        <th>ID</th>
        <th>Name</th>
        <th>Age</th>
        <th>Marks</th>
        <th>Actions</th>
    </tr>
    <c:forEach var="student" items="${students}">
        <tr>
            <td>${student.id}</td>
            <td>${student.name}</td>
            <td>${student.age}</td>
            <td>${student.marks}</td>
            <td>
                <a class="button edit" href="${pageContext.request.contextPath}/students/edit/${student.id}">Edit</a>
                <a class="button delete" href="${pageContext.request.contextPath}/students/delete/${student.id}"
                   onclick="return confirm('Delete this student?');">Delete</a>
            </td>
        </tr>
    </c:forEach>
    <c:if test="${empty students}">
        <tr>
            <td colspan="5">No Students Found.</td>
        </tr>
    </c:if>
</table>

</body>
</html>

<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <title><c:choose><c:when test="${editMode}">Edit Student</c:when><c:otherwise>Add Student</c:otherwise></c:choose></title>
    <style>
        body { font-family: Arial, sans-serif; margin: 40px; background: #f4f6f8; }
        form { background: #fff; padding: 20px; border-radius: 6px; max-width: 400px; }
        label { display: block; margin-top: 10px; font-weight: bold; }
        input { width: 100%; padding: 8px; margin-top: 4px; box-sizing: border-box; }
        button { margin-top: 15px; padding: 8px 16px; background: #2980b9; color: #fff; border: none; border-radius: 4px; cursor: pointer; }
        a { display: inline-block; margin-top: 15px; }
    </style>
</head>
<body>

<h1><c:choose><c:when test="${editMode}">Edit Student</c:when><c:otherwise>Add Student</c:otherwise></c:choose></h1>

<c:set var="formAction" value="${pageContext.request.contextPath}/students/add" />
<c:if test="${editMode}">
    <c:set var="formAction" value="${pageContext.request.contextPath}/students/edit/${student.id}" />
</c:if>

<form method="post" action="${formAction}">

    <c:if test="${!editMode}">
        <label>Student ID</label>
        <input type="number" name="id" value="${student.id}" required />
    </c:if>

    <label>Name</label>
    <input type="text" name="name" value="${student.name}" required />

    <label>Age</label>
    <input type="number" name="age" value="${student.age}" required />

    <label>Marks</label>
    <input type="number" step="0.01" name="marks" value="${student.marks}" required />

    <button type="submit">Save</button>

</form>

<a href="${pageContext.request.contextPath}/students">Back to list</a>

</body>
</html>

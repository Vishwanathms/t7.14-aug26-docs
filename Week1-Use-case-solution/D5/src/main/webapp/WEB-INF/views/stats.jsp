<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Student Stats</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 40px; background: #f4f6f8; }
        .card { background: #fff; padding: 20px; border-radius: 6px; max-width: 400px; margin-bottom: 15px; }
        a { display: inline-block; margin-top: 15px; }
    </style>
</head>
<body>

<h1>Class Statistics</h1>

<div class="card">
    <h2>Topper</h2>
    <c:choose>
        <c:when test="${topper != null}">
            <p>ID: ${topper.id}</p>
            <p>Name: ${topper.name}</p>
            <p>Age: ${topper.age}</p>
            <p>Marks: ${topper.marks}</p>
        </c:when>
        <c:otherwise>
            <p>No Students Found.</p>
        </c:otherwise>
    </c:choose>
</div>

<div class="card">
    <h2>Class Average</h2>
    <p>${average}</p>
</div>

<a href="${pageContext.request.contextPath}/students">Back to list</a>

</body>
</html>

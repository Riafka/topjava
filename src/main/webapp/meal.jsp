<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<html lang="ru">
<head>
    <title>Edit Meal</title>
</head>
<body>
<style>
    .wrapper {
        background-color: whitesmoke;
        list-style-type: none;
        padding: 0;
        border-radius: 3px;
    }
    .form-row {
        display: flex;
        justify-content: flex-start;
        padding: .5em;
    }
    .form-row > label {
        padding: .5em 1em .5em 0;
        flex: 1;
    }
    .form-row > input {
        flex: 2;
    }
    .form-row > input,
    .form-row > button {
        padding: .5em;
    }
    .form-row > button {
        background: gray;
        color: white;
        border: 0;
    }
</style>
<h2>${meal == null ? 'Create Meal': 'Edit Meal'}</h2>
<form method="POST" action='meals' name="frmEditMeal">
<input type="text" readonly="readonly" name="Id" hidden value="<c:out value="${meal.id}" />" />
    <ul class="wrapper">
    <li class="form-row">
        <label for="DateTime">DateTime:</label>
        <input required type="datetime-local" name="DateTime" id="DateTime" value="<c:out value="${meal.dateTime}" />" />
    </li>
    <li class="form-row">
        <label for="Description">Description:</label>
        <input required type="text" name="Description" id="Description" value="<c:out value="${meal.description}" />" />
    </li>
    <li class="form-row">
        <label for="Calories">Calories:</label>
        <input required type="number" name="Calories" id="Calories" value="<c:out value="${meal.calories}" />" />
    </li>
    </ul>
    <input class ="button" type="submit" value="Save" />
    <input class ="button" type="button" value="Cancel"  onclick="window.location='meals';" />
</form>
</body>
</html>

package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.MealTo;
import ru.javawebinar.topjava.repository.MealCrud;
import ru.javawebinar.topjava.repository.InMemoryMealCrud;
import ru.javawebinar.topjava.util.MealsUtil;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Predicate;

import static org.slf4j.LoggerFactory.getLogger;

public class MealServlet extends HttpServlet {
    private static final String INSERT_OR_EDIT = "meal.jsp";
    private static final String MEALS_LIST = "meals.jsp";

    private static final Logger log = getLogger(MealServlet.class);
    private MealCrud mealCrud;
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Override
    public void init()  {
        Predicate<Meal> getAllMealsPredicate = (meal -> true);
        MealsUtil.setFilterMeal(getAllMealsPredicate);
        mealCrud = new InMemoryMealCrud();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String forward;
        String action = request.getParameter("action");
        if (action == null) {
            action = "Meals";
        }

        if (action.equalsIgnoreCase("delete")) {
            int id = Integer.parseInt(request.getParameter("id"));
            mealCrud.delete(id);
            log.debug("Doing GET/delete. redirect to meals.jsp");
            response.sendRedirect("meals");
            return;
        } else if (action.equalsIgnoreCase("edit")) {
            forward = INSERT_OR_EDIT;
            int id = Integer.parseInt(request.getParameter("id"));
            Meal mealTo = mealCrud.findById(id);
            request.setAttribute("meal", mealTo);
            log.debug("Doing GET/edit. forward to meal.jsp");
        } else if (action.equalsIgnoreCase("Meals")) {
            forward = MEALS_LIST;
            List<Meal> meals = mealCrud.findAll();
            List<MealTo> mealsTo = MealsUtil.filteredByStreams(meals,  MealsUtil.MAX_CALORIES);
            request.setAttribute("meals", mealsTo);
            log.debug("Doing GET. forward to meals.jsp");
        } else {
            forward = INSERT_OR_EDIT;
            log.debug("Doing GET/create. forward to meal.jsp");
        }
        RequestDispatcher view = request.getRequestDispatcher(forward);
        view.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        LocalDateTime dateTime = LocalDateTime.parse(request.getParameter("DateTime"));
        String description = request.getParameter("Description");
        int calories = Integer.parseInt(request.getParameter("Calories"));
        String id = request.getParameter("Id");
        Meal meal;
        if (id == null || id.isEmpty()) {
            meal = new Meal(0, dateTime, description, calories);
            mealCrud.create(meal);
        } else {
            meal = new Meal(Integer.parseInt(id), dateTime, description, calories);
            mealCrud.update(meal);
        }
        log.debug("Doing POST. redirect to " + MEALS_LIST);
        response.sendRedirect("meals");
    }
}

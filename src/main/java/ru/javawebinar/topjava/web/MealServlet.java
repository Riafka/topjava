package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import ru.javawebinar.topjava.model.MealTo;
import ru.javawebinar.topjava.repository.MealToCrud;
import ru.javawebinar.topjava.repository.MealToCrudImpl;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.slf4j.LoggerFactory.getLogger;

public class MealServlet extends HttpServlet {
    private static final String INSERT_OR_EDIT = "meal.jsp";
    private static final String MEALS_LIST = "meals.jsp";

    private static final Logger log = getLogger(MealServlet.class);
    private static final MealToCrud mealToCrud = MealToCrudImpl.getInstance();
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String forward;
        String action = request.getParameter("action");
        if (action == null) {
            action = "Meals";
        }

        if (action.equalsIgnoreCase("delete")) {
            int id = Integer.parseInt(request.getParameter("id"));
            MealTo mealTo = mealToCrud.findById(id);
            mealToCrud.deleteMealTo(mealTo);
            // при удалении прямой редирект
            log.debug("Doing GET. redirect to meals");
            response.sendRedirect("meals");
            return;
        } else if (action.equalsIgnoreCase("edit")) {
            forward = INSERT_OR_EDIT;
            int id = Integer.parseInt(request.getParameter("id"));
            MealTo mealTo = mealToCrud.findById(id);
            request.setAttribute("meal", mealTo);
        } else if (action.equalsIgnoreCase("Meals")) {
            forward = MEALS_LIST;
            request.setAttribute("meals", mealToCrud.findAllMealsTo());
        } else {
            forward = INSERT_OR_EDIT;
        }

        log.debug("Doing GET. Forward to " + forward);
        RequestDispatcher view = request.getRequestDispatcher(forward);
        view.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        LocalDateTime dateTime = LocalDateTime.from(dateTimeFormatter.parse(request.getParameter("DateTime")));
        String description = request.getParameter("Description");
        int calories = Integer.parseInt(request.getParameter("Calories"));
        String id = request.getParameter("Id");

        MealTo mealTo = new MealTo(dateTime, description, calories, false);
        if (id == null || id.isEmpty()) {
            mealToCrud.createMealTo(mealTo);
        } else {
            MealTo mealToFound = mealToCrud.findById(Integer.parseInt(id));
            if (mealToFound != null) {
                mealTo.setId(mealToFound.getId());
                mealToCrud.updateMealTo(mealTo);
            }
        }

        log.debug("Doing POST. Forward to " + MEALS_LIST);
        RequestDispatcher view = request.getRequestDispatcher(MEALS_LIST);
        request.setAttribute("meals", mealToCrud.findAllMealsTo());
        view.forward(request, response);
    }
}

package ru.javawebinar.topjava.repository;

import ru.javawebinar.topjava.model.MealTo;
import java.util.List;

public interface MealToCrud {
    void createMealTo(MealTo mealTo);
    void deleteMealTo(MealTo mealTo);
    void updateMealTo(MealTo mealTo);
    MealTo findById(int id);
    List<MealTo> findAllMealsTo();
}

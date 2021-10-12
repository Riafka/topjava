package ru.javawebinar.topjava.repository;

import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.MealTo;
import java.util.List;

public interface MealCrud {
    void create(Meal mealTo);
    void delete(int id);
    void update(Meal mealTo);
    Meal findById(int id);
    List<MealTo> findAll();
}

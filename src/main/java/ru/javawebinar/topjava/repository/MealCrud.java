package ru.javawebinar.topjava.repository;

import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.MealTo;
import java.util.List;

public interface MealCrud {
    Meal create(Meal meal);
    void delete(int id);
    Meal update(Meal meal);
    Meal findById(int id);
    List<Meal> findAll();
}

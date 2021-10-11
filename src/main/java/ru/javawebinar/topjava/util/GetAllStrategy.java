package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.MealTo;

import java.time.LocalTime;
import java.util.List;

import static ru.javawebinar.topjava.util.MealsUtil.getAllByStreams;

public class GetAllStrategy implements FilterStrategy{
    @Override
    public List<MealTo> filter(List<Meal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        return getAllByStreams(meals,caloriesPerDay);
    }
}

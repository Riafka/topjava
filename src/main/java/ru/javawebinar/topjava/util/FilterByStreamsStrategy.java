package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.MealTo;

import java.time.LocalTime;
import java.util.List;

public class FilterByStreamsStrategy implements FilterStrategy {
    @Override
    public List<MealTo> filter(List<Meal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        return MealsUtil.filteredByStreams(meals,startTime,endTime,caloriesPerDay);
    }
}

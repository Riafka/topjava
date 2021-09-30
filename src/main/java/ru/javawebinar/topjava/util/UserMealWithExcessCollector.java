package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class UserMealWithExcessCollector implements Collector<UserMeal, HashMap<LocalDate, List<UserMeal>>, List<UserMealWithExcess>> {
    private final int caloriesPerday;
    private final LocalTime startTime;
    private final LocalTime endTime;

    public UserMealWithExcessCollector(int caloriesPerday, LocalTime startTime, LocalTime endTime) {
        this.caloriesPerday = caloriesPerday;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    @Override
    public Supplier<HashMap<LocalDate, List<UserMeal>>> supplier() {
        return HashMap::new;
    }

    @Override
    public BiConsumer<HashMap<LocalDate, List<UserMeal>>, UserMeal> accumulator() {
        return  (map, UserMeal) -> {
            LocalDate thisDate = UserMeal.getDateTime().toLocalDate();
            List<UserMeal> dateMeals = map.getOrDefault(thisDate, new ArrayList<>());
            dateMeals.add(UserMeal);
            map.put(thisDate, dateMeals);
        };
    }

    @Override
    public BinaryOperator<HashMap<LocalDate, List<UserMeal>>> combiner() {
        return (l, r) -> {
            l.putAll(r);
            return l;
        };
    }

    @Override
    public Function<HashMap<LocalDate, List<UserMeal>>, List<UserMealWithExcess>> finisher() {
        return o -> {
            List<UserMealWithExcess> result = new ArrayList<>();
            o.forEach((key, partUserMeal) -> {
                int caloriesPerDayPart = partUserMeal.stream().mapToInt(UserMeal::getCalories).sum();

                for (UserMeal UserMeal : partUserMeal) {
                    LocalTime thisTime = UserMeal.getDateTime().toLocalTime();
                    if (TimeUtil.isBetweenHalfOpen(thisTime, startTime, endTime)) {
                        boolean excess = caloriesPerDayPart > caloriesPerday;
                        result.add(new UserMealWithExcess(UserMeal.getDateTime(), UserMeal.getDescription(), UserMeal.getCalories(), excess));
                    }
                }
            });
            return result;
        };
    }

    @Override
    public Set<Characteristics> characteristics() {
        return new HashSet<>();
    }

}

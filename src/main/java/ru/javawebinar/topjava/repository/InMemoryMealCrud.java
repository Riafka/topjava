package ru.javawebinar.topjava.repository;

import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.MealsUtil;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAdder;

import static org.slf4j.LoggerFactory.getLogger;

public class InMemoryMealCrud implements MealCrud {
    private static final Logger log = getLogger(InMemoryMealCrud.class);
    private final Map<Integer, Meal> mealCollection = new ConcurrentHashMap<>();
    private final AtomicInteger counter = new AtomicInteger(0);

    public InMemoryMealCrud() {
        List<Meal> meals = MealsUtil.getMeals();
        meals.forEach(this::create);
    }

    @Override
    public Meal create(Meal meal) {
        synchronized (counter) {
            int i = counter.incrementAndGet();
            Meal mealWithCounter = new Meal(i, meal.getDateTime(), meal.getDescription(), meal.getCalories());
            mealCollection.put(i, mealWithCounter);
            log.debug("Create " + mealWithCounter);
            return mealWithCounter;
        }
    }

    @Override
    public void delete(int id) {
        mealCollection.remove(id);
        log.debug("Delete meal with " + id);
    }

    @Override
    public Meal update(Meal meal) {
        Meal oldMeal = mealCollection.replace(meal.getId(), meal);
        if (oldMeal != null) {
            log.debug("Update " + meal);
            return meal;
        } else {
            log.debug("Not found Meal with id " + meal.getId());
            return null;
        }
    }

    @Override
    public List<Meal> findAll() {
        return new ArrayList<>(mealCollection.values());
    }

    @Override
    public Meal findById(int id) {
        return mealCollection.get(id);
    }
}

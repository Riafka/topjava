package ru.javawebinar.topjava.repository;

import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.MealsUtil;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static org.slf4j.LoggerFactory.getLogger;

public class MealCrudInMemory implements MealCrud {
    private static final Logger log = getLogger(MealCrudInMemory.class);
    private final Map<Integer, Meal> mealCollection = new ConcurrentHashMap<>();
    private final AtomicInteger counter = new AtomicInteger(0);

    public MealCrudInMemory() {
        List<Meal> meals = MealsUtil.getMeals();
        meals.forEach(this::create);
    }

    @Override
    public Meal create(Meal meal) {
        Meal mealWithCounter = new Meal(counter.incrementAndGet(), meal.getDateTime(), meal.getDescription(), meal.getCalories());
        mealCollection.put(mealWithCounter.getId(), mealWithCounter);
        log.debug("Create " + mealWithCounter);
        return mealWithCounter;
    }

    @Override
    public void delete(int id) {
        mealCollection.remove(id);
        log.debug("Delete meal with " + id);
    }

    @Override
    public Meal update(Meal meal) {
       Meal oldMeal = mealCollection.replace(meal.getId(),meal);
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

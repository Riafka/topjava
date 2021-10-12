package ru.javawebinar.topjava.repository;

import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.MealTo;
import ru.javawebinar.topjava.util.MealsUtil;
import org.slf4j.Logger;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.slf4j.LoggerFactory.getLogger;

public class MealCrudImpl implements MealCrud {
    private static final Logger log = getLogger(MealCrudImpl.class);
    private final ConcurrentHashMap<Integer, Meal> mealToMap = new ConcurrentHashMap<>();
    private final AtomicInteger counter = new AtomicInteger(0);

    public MealCrudImpl() {
        List<Meal> meals = MealsUtil.getMeals();
        meals.forEach(meal -> {
            Meal mealWithCounter = new Meal(counter.incrementAndGet(), meal.getDateTime(), meal.getDescription(), meal.getCalories());
            mealToMap.put(mealWithCounter.getId(), mealWithCounter);
        });
        Predicate <Meal> getAllMealsPredicate = (meal -> true);
        MealsUtil.setFilterMeal(getAllMealsPredicate);
    }

    @Override
    public void create(Meal meal) {
        Meal mealWithCounter = new Meal(counter.incrementAndGet(), meal.getDateTime(), meal.getDescription(), meal.getCalories());
        mealToMap.put(mealWithCounter.getId(), mealWithCounter);
        log.debug("Create " + mealWithCounter);
    }

    @Override
    public synchronized void delete(int id) {
        Meal meal = findById(id);
        mealToMap.remove(meal.getId());
        log.debug("Delete " + meal);
    }

    @Override
    public synchronized void update(Meal meal) {
        Meal mealFound = findById(meal.getId());
        if (mealFound != null) {
            Meal mealToUpdate = new Meal(mealFound.getId(), meal.getDateTime(), meal.getDescription(), meal.getCalories());
            mealToMap.put(mealToUpdate.getId(), mealToUpdate);
            log.debug("Update " + mealToUpdate);
        } else {
            log.debug("Not found Meal with id " + meal.getId());
        }
    }

    @Override
    public List<MealTo> findAll() {
        List<Meal> meals = mealToMap.values().stream().sorted(Comparator.comparing(Meal::getDateTime))
                .map(mealTo -> new Meal(mealTo.getId(), mealTo.getDateTime(), mealTo.getDescription(), mealTo.getCalories()))
                .collect(Collectors.toList());
        return MealsUtil.filteredByStreams(meals,  MealsUtil.MAX_CALORIES);
    }

    @Override
    public Meal findById(int id) {
        return mealToMap.get(id);
    }
}

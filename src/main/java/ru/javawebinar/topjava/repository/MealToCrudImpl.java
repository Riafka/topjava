package ru.javawebinar.topjava.repository;

import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.MealTo;
import ru.javawebinar.topjava.util.FilterStrategy;
import ru.javawebinar.topjava.util.MealsUtil;
import org.slf4j.Logger;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.slf4j.LoggerFactory.getLogger;

public class MealToCrudImpl implements MealToCrud {
    private static final Logger log = getLogger(MealToCrudImpl.class);
    private static final ConcurrentHashMap<Integer, MealTo> mealToMap = new ConcurrentHashMap<>();
    private static final AtomicInteger counter = new AtomicInteger(0);
    private static volatile MealToCrudImpl instance;
    private static final FilterStrategy filterStrategy = (meals,startTime,endTime,calories)-> MealsUtil.getAllByStreams(meals,calories);

    private MealToCrudImpl() {
        List<Meal> meals = MealsUtil.getMeals();

        List<MealTo> mealsTo= filterStrategy.filter(meals, null, null, MealsUtil.MAX_CALORIES);

        mealsTo.forEach(mealTo -> {
            mealTo.setId(counter.incrementAndGet());
            mealToMap.put(mealTo.getId(), mealTo);
        });
    }

    public static MealToCrudImpl getInstance() {
        MealToCrudImpl result = instance;
        if (result != null) {
            return result;
        }
        synchronized (MealToCrudImpl.class) {
            if (instance == null) {
                instance = new MealToCrudImpl();
            }
            return instance;
        }
    }

    @Override
    public void createMealTo(MealTo mealTo) {
        mealTo.setId(counter.incrementAndGet());
        mealToMap.put(mealTo.getId(), mealTo);
        log.debug("Create " + mealTo);
    }

    @Override
    public void deleteMealTo(MealTo mealTo) {
        if (mealTo != null) {
            mealToMap.remove(mealTo.getId());
        }
        log.debug("Delete " + mealTo);
    }

    @Override
    public void updateMealTo(MealTo mealTo) {
        mealToMap.put(mealTo.getId(), mealTo);
        log.debug("Update " + mealTo);
    }

    @Override
    public List<MealTo> findAllMealsTo() {
        List<Meal> meals = mealToMap.values().stream().sorted(Comparator.comparing(MealTo::getDateTime))
                .map(mealTo -> new Meal(mealTo.getIdAtomic(), mealTo.getDateTime(), mealTo.getDescription(), mealTo.getCalories()))
                .collect(Collectors.toList());
        return filterStrategy.filter(meals, null, null, MealsUtil.MAX_CALORIES);
    }

    @Override
    public MealTo findById(int id) {
        return mealToMap.get(id);
    }
}

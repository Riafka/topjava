package ru.javawebinar.topjava.repository.inmemory;

import org.slf4j.Logger;
import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.DateTimeUtil;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.web.SecurityUtil;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.slf4j.LoggerFactory.getLogger;

@Repository
public class InMemoryMealRepository implements MealRepository {
    private static final Logger log = getLogger(InMemoryMealRepository.class);
    private final Map<Integer, Map<Integer, Meal>> repository = new ConcurrentHashMap<>();
    private final AtomicInteger counter = new AtomicInteger(0);

    {
        MealsUtil.userMeals.forEach(meal -> save(meal, SecurityUtil.USER_ID));
        MealsUtil.adminMeals.forEach(meal -> save(meal, SecurityUtil.ADMIN_ID));
    }

    @Override
    public Meal save(Meal meal, Integer userId) {
        log.info("save {}", meal);
        Map<Integer, Meal> meals = repository.get(userId);
        if (meals == null) {
            meals = new ConcurrentHashMap<>();
            repository.put(userId, meals);
        }
        if (meal.isNew()) {
            meal.setId(counter.incrementAndGet());
            meals.put(meal.getId(), meal);
            return meal;
        }
        return isMealBelongsToUser(meal, userId) ? meals.computeIfPresent(meal.getId(), (id, oldMeal) -> meal) : null;
    }

    @Override
    public boolean delete(int id, Integer userId) {
        log.info("delete {}", id);
        Map<Integer, Meal> meals = repository.get(userId);
        if (meals != null) {
            Meal meal = meals.get(id);
            return isMealBelongsToUser(meal, userId) && meals.remove(id) != null;
        }
        return false;
    }

    @Override
    public Meal get(int id, Integer userId) {
        log.info("get {}", id);
        Map<Integer, Meal> meals = repository.get(userId);
        Meal result = meals != null ? meals.get(id) : null;
        return isMealBelongsToUser(result, userId) ? result : null;
    }

    public List<Meal> getAllWithPredicate(Integer userId, Predicate<Meal> predicate) {
        Map<Integer, Meal> meals = repository.get(userId);
        if (meals == null) return Collections.emptyList();
        return meals.values().stream()
                .filter(predicate)
                .sorted(Comparator.comparing(Meal::getDateTime).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public List<Meal> getAll(Integer userId) {
        log.info("getAll");
        return getAllWithPredicate(userId, meal -> true);
    }

    public List<Meal> getAllWithFilter(LocalDate startDate, LocalDate endDate, Integer userId) {
        log.info("getAllWithFilter {} {}", startDate, endDate);
        return getAllWithPredicate(userId, meal -> DateTimeUtil.isBetweenClose(meal.getDate(),
                                   startDate != null ? startDate : LocalDate.MIN,
                                   endDate != null ? endDate : LocalDate.MAX));
    }

    private boolean isMealBelongsToUser(Meal meal, Integer UserId) {
        return meal != null && meal.getUserId() == UserId;
    }
}


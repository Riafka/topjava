package ru.javawebinar.topjava.repository.inmemory;

import org.slf4j.Logger;
import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.DateTimeUtil;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.web.SecurityUtil;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.slf4j.LoggerFactory.getLogger;

@Repository
public class InMemoryMealRepository implements MealRepository {
    private final Map<Integer, Meal> repository = new ConcurrentHashMap<>();
    private final AtomicInteger counter = new AtomicInteger(0);
    private static final Logger log = getLogger(InMemoryMealRepository.class);

    {
        MealsUtil.userMeals.forEach(meal -> save(meal, SecurityUtil.authUserId()));
        MealsUtil.adminMeals.forEach(meal -> save(meal, SecurityUtil.adminId));
    }

    @Override
    public Meal save(Meal meal, Integer userId) {
        log.info("save {}", meal);
        if (meal.isNew()) {
            meal.setId(counter.incrementAndGet());
            repository.put(meal.getId(), meal);
            return meal;
        }
        return IsMealBelongsToUser(meal.getId(), userId) ? repository.computeIfPresent(meal.getId(), (id, oldMeal) -> meal) : null;
    }

    @Override
    public boolean delete(int id, Integer userId) {
        log.info("delete {}", id);
        return IsMealBelongsToUser(id, userId) && repository.remove(id) != null;
    }

    @Override
    public Meal get(int id, Integer userId) {
        log.info("get {}", id);
        return IsMealBelongsToUser(id, userId) ? repository.get(id) : null;
    }

    @Override
    public List<Meal> getAll(Integer userId) {
        log.info("getAll");
        return repository.values().stream()
                .filter(meal -> meal.getUserId() == userId)
                .sorted(Comparator.comparing(Meal::getDateTime).reversed())
                .collect(Collectors.toList());
    }

    public List<Meal> getAllTosWithFilter(LocalDate startDate, LocalDate endDate, Integer userId) {
        List<Meal> meals = getAll(userId);
        return meals.stream().filter(meal -> DateTimeUtil.isBetweenClose(meal.getDate(),
                        startDate != null ? startDate : LocalDate.MIN,
                        endDate != null ? endDate : LocalDate.MAX))
                .collect(Collectors.toList());
    }

    private boolean IsMealBelongsToUser(int mealId, Integer UserId) {
        Meal meal = repository.get(mealId);
        return meal != null && meal.getUserId() == UserId;
    }
}


package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class UserMealsUtil {
    public static void main(String[] args) {
        List<UserMeal> meals = Arrays.asList(
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
        );

        List<UserMealWithExcess> mealsTo = filteredByCycles(meals, LocalTime.of(10, 0), LocalTime.of(13, 0), 2000);
        mealsTo.forEach(System.out::println);
        mealsTo = filteredByCyclesOptional(meals, LocalTime.of(10, 0), LocalTime.of(13, 0), 2000);
        mealsTo.forEach(System.out::println);

        System.out.println(filteredByStreams(meals, LocalTime.of(10, 0), LocalTime.of(13, 0), 2000));
        System.out.println(filteredByStreamsOptional(meals, LocalTime.of(10, 0), LocalTime.of(13, 0), 2000));
    }

    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> caloriesPerDayMap = new HashMap<>();
        for (UserMeal userMeal : meals) {
            LocalDate thisDate = userMeal.getDateTime().toLocalDate();
            caloriesPerDayMap.merge(thisDate, userMeal.getCalories(), Integer::sum);
        }

        List<UserMealWithExcess> result = new ArrayList<>();
        for (UserMeal userMeal : meals) {
            LocalTime thisTime = userMeal.getDateTime().toLocalTime();
            if (TimeUtil.isBetweenHalfOpen(thisTime, startTime, endTime)) {
                boolean excess = caloriesPerDayMap.get(userMeal.getDateTime().toLocalDate()) > caloriesPerDay;
                UserMealWithExcess userMealWithExcess = new UserMealWithExcess(userMeal.getDateTime(), userMeal.getDescription(), userMeal.getCalories(), excess);
                result.add(userMealWithExcess);
            }
        }
        return result;
    }

    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> caloriesPerDayMap = meals
                .stream()
                .collect(Collectors.groupingBy(userMeal -> userMeal.getDateTime().toLocalDate(), Collectors.summingInt(UserMeal::getCalories)));
        return meals
                .stream()
                .filter(userMeal -> TimeUtil.isBetweenHalfOpen(userMeal.getDateTime().toLocalTime(), startTime, endTime))
                .map(userMeal -> {
                    boolean excess = caloriesPerDayMap.get(userMeal.getDateTime().toLocalDate()) > caloriesPerDay;
                    return new UserMealWithExcess(userMeal.getDateTime(), userMeal.getDescription(), userMeal.getCalories(), excess);
                }).collect(Collectors.toList());
    }

    private static List<UserMealWithExcess> filteredByCyclesOptional(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> caloriesPerDayMap = new HashMap<>();
        List<UserMealWithExcess> result = new ArrayList<>();
        meals = new ArrayList<>(meals);

        int countAdd = meals.size();
        for (int j = 0; j < meals.size(); j++) {
            UserMeal userMeal = meals.get(j);
            if (j < countAdd) {
                LocalDate thisDate = userMeal.getDateTime().toLocalDate();
                caloriesPerDayMap.merge(thisDate, userMeal.getCalories(), Integer::sum);
                meals.add(userMeal);
            } else {
                LocalTime thisTime = userMeal.getDateTime().toLocalTime();
                if (TimeUtil.isBetweenHalfOpen(thisTime, startTime, endTime)) {
                    boolean excess = caloriesPerDayMap.get(userMeal.getDateTime().toLocalDate()) > caloriesPerDay;
                    UserMealWithExcess userMealWithExcess = new UserMealWithExcess(userMeal.getDateTime(), userMeal.getDescription(), userMeal.getCalories(), excess);
                    result.add(userMealWithExcess);
                }
            }

        }
        return result;
    }

    public static List<UserMealWithExcess> filteredByStreamsOptional(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {

        class UserMealWithExcessCollector implements Collector<UserMeal, HashMap<LocalDate, List<UserMeal>>, List<UserMealWithExcess>> {
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
                return (map, UserMeal) -> {
                    LocalDate thisDate = UserMeal.getDateTime().toLocalDate();
                    List<UserMeal> dateMeals = map.getOrDefault(thisDate, new ArrayList<>());
                    dateMeals.add(UserMeal);
                    map.put(thisDate, dateMeals);
                };
            }

            @Override
            public BinaryOperator<HashMap<LocalDate, List<UserMeal>>> combiner() {
                return (l, r) -> {
                    l.forEach((key, partUserMeal) -> {
                        if (r.containsKey(key)) {
                            List<UserMeal> rightUserMealArrayList = r.get(key);
                            partUserMeal.stream()
                                    .filter(UserMeal -> !rightUserMealArrayList.contains(partUserMeal))
                                    .forEach(rightUserMealArrayList::add);
                        } else {
                            r.put(key, partUserMeal);
                        }
                    });
                    return r;
                };
            }

            @Override
            public Function<HashMap<LocalDate, List<UserMeal>>, List<UserMealWithExcess>> finisher() {
                return o -> {
                    List<UserMealWithExcess> result = new ArrayList<>();
                    o.forEach((key, partUserMeal) -> {
                        int caloriesPerDayPart = partUserMeal.stream().mapToInt(UserMeal::getCalories).sum();

                        partUserMeal.forEach(UserMeal -> {
                            LocalTime thisTime = UserMeal.getDateTime().toLocalTime();
                            if (TimeUtil.isBetweenHalfOpen(thisTime, startTime, endTime)) {
                                boolean excess = caloriesPerDayPart > caloriesPerday;
                                result.add(new UserMealWithExcess(UserMeal.getDateTime(), UserMeal.getDescription(), UserMeal.getCalories(), excess));
                            }
                        });
                    });
                    return result;
                };
            }

            @Override
            public Set<Characteristics> characteristics() {
                return new HashSet<>();
            }

        }
        return meals.stream().parallel()
                .collect(new UserMealWithExcessCollector(caloriesPerDay, startTime, endTime));
    }
}

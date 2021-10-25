package ru.javawebinar.topjava;

import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.javawebinar.topjava.model.AbstractBaseEntity.START_SEQ;

public class MealTestData {
    public static final int MEAL_ID_USER_BREAKFAST = START_SEQ + 2;
    public static final int MEAL_ID_USER_LUNCH = START_SEQ + 3;
    public static final int MEAL_ID_USER_DINNER = START_SEQ + 4;
    public static final int MEAL_ID_ADMIN_BREAKFAST = START_SEQ + 5;
    public static final int MEAL_ID_ADMIN_LUNCH = START_SEQ + 6;
    public static final int MEAL_ID_ADMIN_DINNER = START_SEQ + 7;

    public static final Meal userBreakfast = new Meal(MEAL_ID_USER_BREAKFAST, LocalDateTime.of(2021, Month.OCTOBER, 22, 9, 0), "Завтрак", 500);
    public static final Meal userLunch = new Meal(MEAL_ID_USER_LUNCH, LocalDateTime.of(2021, Month.OCTOBER, 22, 12, 0), "Обед", 1000);
    public static final Meal userDinner = new Meal(MEAL_ID_USER_DINNER, LocalDateTime.of(2021, Month.OCTOBER, 23, 19, 0), "Ужин на завтра", 2500);

    public static final Meal adminBreakfast = new Meal(MEAL_ID_ADMIN_BREAKFAST, LocalDateTime.of(2021, Month.OCTOBER, 22, 9, 0), "Завтрак админа", 1500);
    public static final Meal adminLunch = new Meal(MEAL_ID_ADMIN_LUNCH, LocalDateTime.of(2021, Month.OCTOBER, 22, 12, 0), "Плотный обед админа", 1500);
    public static final Meal adminDinner = new Meal(MEAL_ID_ADMIN_DINNER, LocalDateTime.of(2021, Month.OCTOBER, 23, 19, 0), "Ужин админа на завтра", 500);

    public static Meal getNew() {
        return new Meal(null,LocalDateTime.of(2021, Month.OCTOBER, 25, 12, 0), "description", 100);
    }

    public static Meal getUpdated() {
        Meal updated = new Meal(userBreakfast);
        updated.setCalories(300);
        updated.setDescription("UpdatedMeal");
        updated.setDateTime(LocalDateTime.of(2022, Month.JANUARY, 1, 0, 0));
        return updated;
    }

    public static void assertMatch(Meal actual, Meal expected) {
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    public static void assertMatch(Iterable<Meal> actual, Meal... expected) {
        assertMatch(actual, Arrays.asList(expected));
    }

    public static void assertMatch(Iterable<Meal> actual, Iterable<Meal> expected) {
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }
}

package ru.javawebinar.topjava.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringRunner;
import ru.javawebinar.topjava.UserTestData;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static org.junit.Assert.assertThrows;
import static ru.javawebinar.topjava.MealTestData.*;
import static ru.javawebinar.topjava.UserTestData.ADMIN_ID;
import static ru.javawebinar.topjava.UserTestData.USER_ID;


@ContextConfiguration({
        "classpath:spring/spring-app.xml",
        "classpath:spring/spring-db.xml"
})
@RunWith(SpringRunner.class)
@Sql(scripts = "classpath:db/populateDB.sql", config = @SqlConfig(encoding = "UTF-8"))
public class MealServiceTest {

    static {
        SLF4JBridgeHandler.install();
    }

    @Autowired
    private MealService service;

    @Test
    public void create() {
        Meal created = service.create(getNew(), UserTestData.USER_ID);
        Integer newId = created.getId();
        Meal newMeal = getNew();
        newMeal.setId(newId);
        assertMatch(created, newMeal);
        assertMatch(service.get(newId, UserTestData.USER_ID), newMeal);
    }

    @Test
    public void duplicateDateTimeCreate() {
        assertThrows(DataAccessException.class,
                () -> service.create(new Meal(null, userBreakfast.getDateTime(), "Дубль завтрака юзера", 1500), USER_ID));
    }

    @Test
    public void get() {
        Meal meal = service.get(MEAL_ID_USER_DINNER, USER_ID);
        assertMatch(meal, userDinner);
    }

    @Test
    public void getNotFound() {
        assertThrows(NotFoundException.class, () -> service.get(MEAL_ID_USER_BREAKFAST, ADMIN_ID));
    }

    @Test
    public void delete() {
        service.delete(MEAL_ID_USER_BREAKFAST, USER_ID);
        assertThrows(NotFoundException.class, () -> service.get(MEAL_ID_USER_BREAKFAST, USER_ID));
    }

    @Test
    public void deleteNotFound() {
        assertThrows(NotFoundException.class, () -> service.delete(MEAL_ID_USER_BREAKFAST, ADMIN_ID));
    }

    @Test
    public void getBetweenInclusive() {
        List<Meal> tomorrowUserMeals = service.getBetweenInclusive(LocalDate.of(2021, Month.OCTOBER, 23), null, USER_ID);
        List<Meal> todayAdminMeals = service.getBetweenInclusive(null, LocalDate.of(2021, Month.OCTOBER, 22), ADMIN_ID);
        assertMatch(tomorrowUserMeals, userDinner);
        assertMatch(todayAdminMeals, adminLunch, adminBreakfast);
    }

    @Test
    public void getAllForUser() {
        List<Meal> allUserMeals = service.getAll(USER_ID);
        assertMatch(allUserMeals, userDinner, userLunch, userBreakfast);
    }

    @Test
    public void getAllForAdmin() {
        List<Meal> allAdminMeals = service.getAll(ADMIN_ID);
        assertMatch(allAdminMeals, adminDinner, adminLunch, adminBreakfast);
    }

    @Test
    public void update() {
        Meal updated = getUpdated();
        service.update(updated, USER_ID);
        assertMatch(service.get(MEAL_ID_USER_BREAKFAST, USER_ID), getUpdated());
    }

    @Test
    public void updateNotFound() {
        Meal meal = service.get(MEAL_ID_USER_DINNER, USER_ID);
        assertThrows(NotFoundException.class, () -> service.update(meal, ADMIN_ID));
    }
}
package ru.javawebinar.topjava.web;

import static ru.javawebinar.topjava.util.MealsUtil.DEFAULT_CALORIES_PER_DAY;

public class SecurityUtil {
    public static int userId = 1;
    public static int adminId = 2;

    public static int authUserId() {
        return userId;
    }

    public static void setUserId(int userId) {
        SecurityUtil.userId = userId;
    }

    public static int authUserCaloriesPerDay() {
        return DEFAULT_CALORIES_PER_DAY;
    }
}
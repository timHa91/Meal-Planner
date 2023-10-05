package mealplanner.service;

import mealplanner.dao.MealDAO;
import mealplanner.model.Meal;
import mealplanner.model.MealCategory;

import java.util.List;

public class MealService {
    private final MealDAO mealDAO;
    public MealService(MealDAO mealDAO) {
        this.mealDAO = mealDAO;
    }

    public void addMeal(Meal meal) {
        mealDAO.createAMeal(meal);
    }

    public List<Meal> getAllMeals() {
        return mealDAO.getAllMeals();
    }

    public List<Meal> getMealsByCategory(MealCategory category){
        return mealDAO.getMealsByCategory(category);
    }
}

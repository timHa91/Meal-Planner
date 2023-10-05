package mealplanner;


import mealplanner.dao.IngredientDAO;
import mealplanner.dao.MealDAO;
import mealplanner.db.DatabaseService;
import mealplanner.service.MealService;
import mealplanner.ui.MealAppUI;

import java.sql.Connection;
import java.sql.SQLException;

public class Main {

  public static void main(String[] args) throws SQLException {
    try (Connection connection = DatabaseService.connect()) {
      IngredientDAO ingredientDAO = new IngredientDAO(connection);
      MealDAO mealDAO = new MealDAO(ingredientDAO, connection);
      MealService mealService = new MealService(mealDAO);
      MealAppUI mealAppUI = new MealAppUI(mealService);
      mealAppUI.startApp();
    }
  }
}

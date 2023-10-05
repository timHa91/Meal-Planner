package mealplanner.dao;

import mealplanner.model.Ingredient;
import mealplanner.model.Meal;
import mealplanner.model.MealCategory;
import mealplanner.ui.MealAppUI;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MealDAO {
    private final IngredientDAO ingredientDAO;
    private final Connection connection;

    public MealDAO(IngredientDAO ingredientDAO, Connection connection) {
        this.ingredientDAO = ingredientDAO;
        this.connection = connection;
    }
    public List<Meal> getAllMeals() {
        List<Meal> allMeals = new ArrayList<>();
        try (Statement statement = connection.createStatement()) {
            try (ResultSet meals = statement.executeQuery("SELECT * FROM meals")) {
                while (meals.next()) {
                    int mealId = meals.getInt("meal_id");
                    String mealName = meals.getString("meal");
                    String category = meals.getString("category");
                    List<Ingredient> ingredients = ingredientDAO.getIngredientByMealId(mealId);
                    allMeals.add(new Meal(mealName, MealAppUI.validateCategory(category), ingredients));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return allMeals;
    }
    public void createAMeal(Meal meal) {
        String insertSql = "INSERT INTO meals (meal_id, category, meal) VALUES (?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(insertSql, new String[]{"meal_id"})) {
            int mealId = getLastMealId() + 1;
            preparedStatement.setInt(1, mealId);
            preparedStatement.setString(2, meal.getCategory().toString());
            preparedStatement.setString(3, meal.getName());
            preparedStatement.executeUpdate();
            ingredientDAO.addIngredients(meal.getIngredients(), mealId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private int getLastMealId() throws SQLException {
        String query = "SELECT MAX(meal_id) FROM meals";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);

        int lastPrimaryKeyValue = 0;
        if (resultSet.next()) {
            lastPrimaryKeyValue = resultSet.getInt(1);
        }
        return lastPrimaryKeyValue;
    }

    public List<Meal> getMealsByCategory(MealCategory mealCategory) {
        List<Meal> allFoundMeals = new ArrayList<>();
        String query = "SELECT * FROM meals WHERE category = ? ORDER BY category";
        try(PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, mealCategory.toString());
            try (ResultSet meals = preparedStatement.executeQuery()) {
                while (meals.next()) {
                    int mealId = meals.getInt("meal_id");
                    String mealName = meals.getString("meal");
                    String category = meals.getString("category");
                    List<Ingredient> ingredients = ingredientDAO.getIngredientByMealId(mealId);
                    allFoundMeals.add(new Meal(mealName, MealAppUI.validateCategory(category), ingredients));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return allFoundMeals;
    }
}

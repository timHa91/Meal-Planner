package mealplanner.dao;

import mealplanner.model.Ingredient;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class IngredientDAO {
    private final Connection connection;

    public IngredientDAO(Connection connection) {
        this.connection = connection;
    }
    public List<Ingredient> getIngredientByMealId(int mealId) {
        List<Ingredient> allIngredients = new ArrayList<>();
        String insertSql = "SELECT * FROM ingredients WHERE meal_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertSql)) {
            preparedStatement.setInt(1, mealId);
            ResultSet ingredients = preparedStatement.executeQuery();

            while (ingredients.next()) {
                String ingredientName = ingredients.getString("ingredient");
                allIngredients.add(new Ingredient(ingredientName));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return allIngredients;
    }
    public void addIngredients(List<Ingredient> ingredients, int mealId) {
        String insertSql = "INSERT INTO ingredients (ingredient_id, meal_id, ingredient) VALUES (?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertSql)) {
            int nextIngredientId = getLastIngredientId() + 1; // Holt den nächsten verfügbaren Wert
            for (Ingredient ingredient : ingredients) {
                int ingredientsId = nextIngredientId++;
                preparedStatement.setInt(1, ingredientsId);
                preparedStatement.setInt(2, mealId);
                preparedStatement.setString(3, ingredient.getIngredientName());
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int getLastIngredientId() throws SQLException {
        String query = "SELECT MAX(ingredient_id) FROM ingredients";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);

        int lastPrimaryKeyValue = 0;
        if (resultSet.next()) {
            lastPrimaryKeyValue = resultSet.getInt(1);
        }
        return lastPrimaryKeyValue;
    }
}

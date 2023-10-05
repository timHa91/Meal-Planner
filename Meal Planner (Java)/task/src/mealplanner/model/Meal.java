package mealplanner.model;

import java.util.List;

public class Meal {
    private String name;
    private MealCategory category;
    private List<Ingredient> ingredients;


    public Meal(String name, MealCategory category, List<Ingredient> ingredients) {
        this.name = name;
        this.category = category;
        this.ingredients = ingredients;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MealCategory getCategory() {
        return category;
    }

    public void setCategory(MealCategory category) {
        this.category = category;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }
}

package mealplanner.model;

public enum MealCategory {
    BREAKFAST,
    LUNCH,
    DINNER;

    @Override
    public String toString() {
        // Return the category name in lowercase
        return name().toLowerCase();
    }
}

package mealplanner.ui;
import mealplanner.model.Ingredient;
import mealplanner.model.Meal;
import mealplanner.model.MealCategory;
import mealplanner.model.MealOption;
import mealplanner.service.MealService;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MealAppUI {

    private final MealService mealService;
    public MealAppUI(MealService mealService) {
        this.mealService = mealService;
    }

    public void startApp() throws SQLException {
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;
        while (!exit) {
            MealOption mealOption = getMealOption(scanner);

            switch (mealOption) {
                case ADD -> addMealUI(scanner);
                case SHOW -> showMealUI(scanner);
                case EXIT -> {
                    System.out.println("Bye!");
                    exit = true;
                }
            }
        }
        scanner.close();
    }

    public void addMealUI(Scanner scanner) {
        System.out.println("Which meal do you want to add (breakfast, lunch, dinner)?");
        MealCategory mealCategory = getValidMealCategory(scanner);
        System.out.println("Input the meal's name:");
        String mealName = checkFormat("^[A-Za-z]+( [A-Za-z]+)*$", scanner);
        System.out.println("Input the ingredients:");
        String ingredients = checkFormat("^([A-Za-z]+(?:[ ]?[A-Za-z]+)*)(?:,[ ]*([A-Za-z]+(?:[ ]?[A-Za-z]+)*))*$", scanner);
        List<Ingredient> ingredientList = Arrays.stream(ingredients.split(","))
                .map(Ingredient::new)
                .collect(Collectors.toList());
        System.out.println("The meal has been added!");
        Meal meal = new Meal (mealName, mealCategory, ingredientList);
        mealService.addMeal(meal);
    }

    // Prints all saved Meals
    public void showMealUI(Scanner scanner) {
        System.out.println("Which category do you want to print (breakfast, lunch, dinner)?");
        MealCategory mealCategory = getValidMealCategory(scanner);
        List<Meal> mealList = mealService.getMealsByCategory(mealCategory);
        if (mealList.isEmpty()) {
            System.out.println("No meals found.");
        } else {
            System.out.printf("Category: %s%n%n", mealCategory.toString());
            IntStream.range(0, mealList.size()).forEach(index -> {
                Meal meal = mealList.get(index);
                System.out.printf("Name: %s%n", meal.getName());
                System.out.println("Ingredients:");
                meal.getIngredients().forEach(ingredient -> {
                    System.out.println(ingredient.getIngredientName());
                });

                if (index < mealList.size() - 1) {
                    System.out.println();
                }
            });
        }
    }

    // Read-in the selected Option (add, show, exit)
    private MealOption getMealOption(Scanner scanner) {
        MealOption mealOption = null;

        while (mealOption == null) {
            System.out.println("What would you like to do (add, show, exit)?");
            String option = scanner.nextLine().toLowerCase();
            mealOption = validateOption(option);
        }

        return mealOption;
    }

    // Checks if the Option Input is valid
    private MealOption validateOption(String input) {
        return switch (input.toLowerCase()) {
            case "add" -> MealOption.ADD;
            case "show" -> MealOption.SHOW;
            case "exit" -> MealOption.EXIT;
            default -> null;
        };
    }
    // Checks the Inputs of the Meal properties
    public String checkFormat(String regEx, Scanner scanner) {
        String input;
        do {
            input = scanner.nextLine();
            if (input.isEmpty() || !input.matches(regEx)) {
                System.out.println("Wrong format. Use letters only!");
            }
        } while (input.isEmpty() || !input.matches(regEx));
        return input;
    }

    // Read-in the Meal Category and checks if it is valid
    public MealCategory getValidMealCategory(Scanner scanner) {
        MealCategory mealCategory = null;

        while (mealCategory == null) {
            String mealTypeInput = scanner.nextLine().trim();
            mealCategory = validateCategory(mealTypeInput);
        }

        return mealCategory;
    }

    public static MealCategory validateCategory(String input) {
        return switch (input.toLowerCase()) {
            case "breakfast" -> MealCategory.BREAKFAST;
            case "lunch" -> MealCategory.LUNCH;
            case "dinner" -> MealCategory.DINNER;
            default -> {
                System.out.println("Wrong meal category! Choose from: breakfast, lunch, dinner.");
                yield null;
            }
        };
    }
}

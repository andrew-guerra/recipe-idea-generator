package libs.selection;

import java.util.ArrayList;
import java.util.Random;

import libs.recipe.Recipe;

/**
 * The RecipeSelector class contains utility functions to select a recipe from a list of recipes
 * based on a defined criteria.
 */
public class RecipeSelector {
    /**
     * Returns a random recipe from the list of recipes
     * 
     * @param recipes The list of recipes 
     * @return a random recipe from the list of recipes
     */
    public static Recipe randomSelect(ArrayList<Recipe> recipes) {
        int randomIndex = new Random().nextInt(recipes.size());
        return recipes.get(randomIndex);
    }

    /**
     * Returns a recipe that contains the highest percentage of ingridents from the ingrident list
     * 
     * @param recipes The list of recipes
     * @param ingridents The list of recipes to check for
     * 
     * @return a recipe that contains the highest percentage of ingridents from the ingrident list
     */
    public static Recipe ingridentSelect(ArrayList<Recipe> recipes, ArrayList<String> ingridents) {
        return null;
    }
}

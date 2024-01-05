package libs.recipe;

import java.util.ArrayList;

/**
 * The Recipe class represents a digitally accessible recipe.
 */
public class Recipe {
    public final String url;
    public final String name;
    public final String time;
    public final String yield;
    public final ArrayList<String> ingredients;

    /**
     * Constructs a Recipe object with a url, name, time, yield, and ingrident list.
     * 
     * @param url The url link to the recipe
     * @param name The name of the recipe
     * @param time The time needed to complete the recipe
     * @param yield The yield from the recipe
     * @param ingredients The ingrident list needed for the recipe
     */
    public Recipe(String url, String name, String time, String yield, ArrayList<String> ingredients) {
        this.url = url;
        this.name = name;
        this.time = time;
        this.yield = yield;
        this.ingredients = ingredients;
    }

    /**
     * Returns whether the recipe contains the targetIngrident, quantity not considered.
     * 
     * @param targetIngrident The ingrident to search for
     * @return whether the recipe contains the targetIngrident
     */
    public boolean containsIngrident(String targetIngrident) {
        for(String ingrident : ingredients) {
            if(ingrident.contains(targetIngrident)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(this.url);
        stringBuilder.append("\n");
        stringBuilder.append(this.name);
        stringBuilder.append("\n");
        stringBuilder.append(this.time);
        stringBuilder.append("\n");
        stringBuilder.append(this.yield);

        for(String ingredient : this.ingredients) {
            stringBuilder.append("\n");
            stringBuilder.append(ingredient);
        }

        return stringBuilder.toString();
    }
}

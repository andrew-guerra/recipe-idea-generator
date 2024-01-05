package libs.crawler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import libs.recipe.Recipe;

/**
 * The RecipeCrawler class contains utility functions to extract recipes from webpages.
 */
public class RecipeCrawler {
    /**
     * Returns a list of recipes from the list of recipe links
     * 
     * @param recipeLinks The list of recipe links
     * @return a list of recipes from the list of recipe links
     */
    public static ArrayList<Recipe> getRecipes(Set<String> recipeLinks) {
        ArrayList<Recipe> recipes =  new ArrayList<>();

        for(String link : recipeLinks) {
            recipes.add(getRecipe(link));
        }

        return recipes;
    }

    /**
     *  Returns a list of recipes from a list of recipe links from the specifed file.
     *  Each recipe link should be line seperated in the file.
     * 
     * @param filename The file where the recipe links are located
     * @return a list of recipes from a list of recipe links from the specifed file
     */
    public static ArrayList<Recipe> getRecipesFromFile(String filename) {
        ArrayList<Recipe> recipes =  new ArrayList<>();

        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(filename));
            String line;

            while((line = bufferedReader.readLine()) != null) {
                Set<String> recipeLinks = getRecipeLinksRecursive(line.strip(), 1);
                recipes.addAll(getRecipes(recipeLinks));
            }
           
            bufferedReader.close();
        } catch(FileNotFoundException e) {
            e.printStackTrace();
        } catch(IOException e) {
            e.printStackTrace();
        }

        return recipes;
    }
    
    /**
     * Returns a set of recipe links from the specified url link. The search will begin at the url and
     * search for any recipes in that webpage. The search will then recursively search those recipes for
     * more recipes until the maximum depth is reached.
     * 
     * @param url The link to start at
     * @param maxDepth The maximum depth of the search
     * @return Returns a set of recipe links from the specified url link recursively to the maxDepth
     */
    public static Set<String> getRecipeLinksRecursive(String url, int maxDepth) {
        return getRecipeLinksRecursiveHelper(url, 0, maxDepth);
    }

    /**
     * Returns a set of recipe links from the specified url link. The search will begin at the url and
     * search for any recipes in that webpage. The search will then recursively search those recipes for
     * more recipes until depth is equal to maximum depth.
     * 
     * @param url The link to start at
     * @param depth The current search depth
     * @param maxDepth The maximum depth of the search
     * @return Returns a set of recipe links from the specified url link recursively until depth equals maxDepth
     */
    private static Set<String> getRecipeLinksRecursiveHelper(String url, int depth, int maxDepth) {
        if(depth == maxDepth) {
            return new HashSet<>();
        }

        Set<String> recipeLinks = getRecipeLinks(url);
        Set<String> recipeSublinks = new HashSet<>();

        for(String link : recipeLinks) {
            recipeSublinks.addAll(getRecipeLinksRecursiveHelper(link, depth + 1, maxDepth));
        }

        recipeLinks.addAll(recipeSublinks);

        return recipeLinks;
    }

    /**
     * Returns a list links in the webpage at the url where the links are recipes.
     * 
     * @param url The url of the webpage to search
     * @return a list links in the webpage at the url where the links are recipes
     */
    public static Set<String> getRecipeLinks(String url) {
        Set<String> recipeLinks = new HashSet<>();

        try {
            Document doc = Jsoup.connect(url).get();
            Elements links = doc.select("a[href]");
            
            for(Element link : links) {
                String linkUrl = link.absUrl("href");

                if(linkUrl.contains("recipe") && isRecipeLink(linkUrl)) {
                    recipeLinks.add(link.absUrl("href"));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return recipeLinks;
    }

    /**
     * Returns whether the url links to a recipe wepage.
     * 
     * @param url The url to check
     * @return whether the url links to a recipe wepage
     */
    public static boolean isRecipeLink(String url) {
        try {
            Document doc = Jsoup.connect(url).get();
            String text = doc.text().toLowerCase();

            return text.contains("ingredients") && text.contains("recipe") && (text.contains("instructions") || text.contains("directions"));

        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Return a recipe extracted from the wepage linked by the url.
     * 
     * @param url The url to extract a recipe from
     * @return a recipe extracted from the wepage linked by the url
     */
    public static Recipe getRecipe(String url) {
        String name = "";
        String time = "";
        String yield = "";
        ArrayList<String> ingridents = new ArrayList<>();

        try {
            Document doc = Jsoup.connect(url).get();

            name = getRecipeName(doc);
            time = getRecipeTime(doc);
            yield = getRecipeYield(doc);
            ingridents = getIngredientList(doc);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return new Recipe(url, name, time, yield, ingridents);
    }

    /**
     * Return the recipe name from the document.
     * 
     * @param doc The document to extract the recipe name from
     * @return the recipe name from the document
     */
    private static String getRecipeName(Document doc) {
        return doc.title();
    }

    /**
     * Returns the recipe total time to prepare from the document.
     * 
     * @param doc The document to extract the time to prepare from
     * @return the recipe total time to prepare from the document
     */
    private static String getRecipeTime(Document doc) {
        Element timeElement = doc.select("li:contains(Total)").first();

        return timeElement.text();
    }

    /**
     * Returns the recipe yield from the document.
     * 
     * @param doc The document to extract the recipe yield from
     * @return the recipe yield from the document
     */
    private static String getRecipeYield(Document doc) {
        Element yieldElement = doc.select("li:contains(Yield)").first();

        return yieldElement.text();
    }

    /**
     * Returns the recipe ingrident list from the document.
     * 
     * @param doc The document to extract the recipe ingrident list from
     * @return Returns the recipe ingrident list from the document
     */
    private static ArrayList<String> getIngredientList(Document doc) {
        Elements paragraphs = doc.select("p");
        ArrayList<String> ingredientList = new ArrayList<>();
        
        for (Element paragraph : paragraphs) {
            if(paragraph.className().toLowerCase().contains("ingredient") && !paragraph.text().equals("Deselect All")) {
                ingredientList.add(paragraph.text());
            }
        }

        return ingredientList;
    }

    /**
     * Writes the recipes to a file
     * 
     * @param filename THe name of the file to write to
     * @param recipes The list of recipes to write
     */
    public static void writeRecipesToFile(String filename, ArrayList<Recipe> recipes) {
        File file = new File(filename);

        try {
            PrintWriter writer = new PrintWriter(file);

            for(Recipe recipe : recipes) {
                writer.println(recipe);
                writer.println();
            }

            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
} 
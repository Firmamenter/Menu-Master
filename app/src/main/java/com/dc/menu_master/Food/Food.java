package com.dc.menu_master.Food;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * This class is used to store dish information.
 */

public class Food implements Serializable {
    private String name;
    private String imageUrl;
    private String recipeId;
    private ArrayList<String> recipes = new ArrayList<>();

    public void setName(String name) {
        this.name = name;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setRecipeId(String recipeId) {
        this.recipeId = recipeId;
    }

    public String getRecipeId() {
        return recipeId;
    }

    public void appendRecipes(String newRecipe) {
        recipes.add(newRecipe);
    }

    public ArrayList<String> getRecipes() {
        return recipes;
    }
}

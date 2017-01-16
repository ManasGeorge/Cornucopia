package com.cornucopia.cornucopia_app.model;

import android.support.annotation.NonNull;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * The pantry tracks all of the data about the ingredients in the pantry.
 * TODO: Replace with persistent approach
 */
public class Pantry {
    @NonNull
    private List<PantryIngredient> ingredientList;

    public Pantry() {
        this.ingredientList = Arrays.asList(
                new PantryIngredient("Eggs", new Date()),
                new PantryIngredient("Bread", new Date())
        );
    }

    @NonNull
    public List<PantryIngredient> getIngredientList() {
        return ingredientList;
    }
}

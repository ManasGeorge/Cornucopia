package com.cornucopia.cornucopia_app.model;

import io.realm.annotations.Required;

public class Recipe {
    @Required
    private String recipeName;
    private boolean isFavorited;
    @Required
    private String prepTime;

    public Recipe(String recipeName, boolean isFavorited, String prepTime) {
        this.recipeName = recipeName;
        this.isFavorited = isFavorited;
        this.prepTime = prepTime;
    }

    public String getRecipeName() {
        return recipeName;
    }

    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }

    public boolean isFavorited() {
        return isFavorited;
    }

    public void setFavorited(boolean favorited) {
        isFavorited = favorited;
    }

    public String getPrepTime() {
        return prepTime;
    }

    public void setPrepTime(String prepTime) {
        this.prepTime = prepTime;
    }
}


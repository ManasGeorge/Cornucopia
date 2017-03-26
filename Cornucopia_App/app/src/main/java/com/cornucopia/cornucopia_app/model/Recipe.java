package com.cornucopia.cornucopia_app.model;

public class Recipe {
    private String recipeName;
    private boolean isFavorited;
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


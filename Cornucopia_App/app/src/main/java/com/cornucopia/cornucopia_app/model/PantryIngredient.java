package com.cornucopia.cornucopia_app.model;

import java.util.Date;

public class PantryIngredient {

    private String ingredientName;
    private Date expirationDate;

    public PantryIngredient(String ingredientName, Date expirationDate) {
        this.ingredientName = ingredientName;
        this.expirationDate = expirationDate;
    }

    public String getIngredientName() {
        return ingredientName;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }
}

package com.cornucopia.cornucopia_app.model;

import android.support.annotation.NonNull;

import java.util.Date;

public class PantryIngredient {

    @NonNull
    private String ingredientName;
    @NonNull
    private Date expirationDate;
    @NonNull
    private String quantity;


    public PantryIngredient(@NonNull String ingredientName, @NonNull Date expirationDate, @NonNull String quantity) {
        this.ingredientName = ingredientName;
        this.expirationDate = expirationDate;
        this.quantity = quantity;
    }

    @NonNull
    public String getIngredientName() {
        return ingredientName;
    }

    @NonNull
    public Date getExpirationDate() {
        return expirationDate;
    }

    @NonNull
    public String getQuantity() {
        return quantity;
    }
}

package com.cornucopia.cornucopia_app.model;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.Required;

public class PantryIngredient extends RealmObject {

    @Required
    private String ingredientName;
    @Required
    private Date expirationDate;
    @Required
    private String quantity;

    public String getIngredientName() {
        return ingredientName;
    }

    public void setIngredientName(String ingredientName) {
        this.ingredientName = ingredientName;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
}

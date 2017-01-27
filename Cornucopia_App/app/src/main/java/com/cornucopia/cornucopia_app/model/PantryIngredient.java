package com.cornucopia.cornucopia_app.model;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.Required;

public class PantryIngredient extends RealmObject {

    @Required
    private String ingredientName;
    @Required
    private Date expirationDate;
    @Required
    private boolean isExpirationEstimated;
    @Required
    private String quantity;

    public static PantryIngredient newPantryIngredient(Realm realm, String ingredientName, Date expirationDate, boolean isExpirationEstimated, String quantity) {
        PantryIngredient newIngredient = realm.createObject(PantryIngredient.class);
        newIngredient.setIngredientName(ingredientName);
        newIngredient.setExpirationDate(expirationDate);
        newIngredient.setExpirationEstimated(isExpirationEstimated);
        newIngredient.setQuantity(quantity);
        return newIngredient;
    }

    public String getIngredientName() {
        return ingredientName;
    }

    public void setIngredientName(String ingredientName) {
        this.ingredientName = ingredientName;
    }

    public boolean isExpirationEstimated() {
        return isExpirationEstimated;
    }

    public void setExpirationEstimated(boolean expirationEstimated) {
        isExpirationEstimated = expirationEstimated;
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

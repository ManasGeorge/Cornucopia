package com.cornucopia.cornucopia_app.businessLogic;

import android.support.annotation.NonNull;

import com.cornucopia.cornucopia_app.model.GroceryIngredient;
import com.cornucopia.cornucopia_app.model.PantryIngredient;

import java.util.Date;

import io.realm.Realm;

/**
 * Handles moving items to and from grocery list and pantry
 */
public class IngredientTransformer {
    /**
     * Removes the ingredient from the pantry and then
     * adds the ingredient to the grocery list with the same quantity and with estimated expiration date.
     */
    public static void moveToGroceryList(@NonNull final PantryIngredient pantryIngredient) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                String ingredientName = pantryIngredient.getIngredientName();
                Date estimatedExpirationDate = new Date();
                GroceryIngredient groceryIngredient = GroceryIngredient.newGroceryIngredient(realm, ingredientName, estimatedExpirationDate, true, pantryIngredient.getQuantity());
                realm.copyToRealm(groceryIngredient);
                pantryIngredient.deleteFromRealm();
            }
        });
    }

    public static void moveToPantry(@NonNull GroceryIngredient groceryIngredient) {

    }
}

package com.cornucopia.cornucopia_app;

import android.app.Application;

import com.cornucopia.cornucopia_app.businessLogic.CustomIconsModule;
import com.cornucopia.cornucopia_app.model.GroceryIngredient;
import com.cornucopia.cornucopia_app.model.PantryIngredient;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * MyApplication is acting as an initialization point where a {@link android.content.Context} is required
 */
public class MyApplication extends Application {
    @Override

    public void onCreate() {
        super.onCreate();

        Realm.init(this);
        RealmConfiguration realmConfig = new RealmConfiguration.Builder()
                .initialData(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        Date date = new Date();

                        // Milk is expired
                        PantryIngredient milk = PantryIngredient.newPantryIngredient(realm, "Milk", date, false, "3 gallons");
                        realm.copyToRealm(milk);

                        date.setTime(date.getTime() + 1000 * 60 * 60 * 24 * 2); // 2 days from now

                        PantryIngredient eggs = PantryIngredient.newPantryIngredient(realm, "Eggs", date, true, "2 dozen");
                        realm.copyToRealm(eggs);

                        PantryIngredient bread = PantryIngredient.newPantryIngredient(realm, "Bread", date, false, "1 loaf");
                        realm.copyToRealm(bread);


                        GroceryIngredient milk2 = GroceryIngredient.newGroceryIngredient(realm, "Milk", date, false, "2 quarts");
                        realm.copyToRealm(milk2);

                        GroceryIngredient cheese = GroceryIngredient.newGroceryIngredient(realm, "Cheese", date, true, "1 wheel");
                        realm.copyToRealm(cheese);
                    }
                })
                .build();
        Realm.deleteRealm(realmConfig); // Delete Realm between app restarts (testing only)
        Realm.setDefaultConfiguration(realmConfig);

        Iconify.with(new FontAwesomeModule()).with(new CustomIconsModule());
    }
}

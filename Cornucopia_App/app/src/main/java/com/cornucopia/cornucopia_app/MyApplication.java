package com.cornucopia.cornucopia_app;

import android.app.Application;

import com.cornucopia.cornucopia_app.model.PantryIngredient;

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
                        // Seed data
                        Date now = new Date();
                        PantryIngredient eggs = realm.createObject(PantryIngredient.class);
                        eggs.setIngredientName("Eggs");
                        eggs.setExpirationDate(now);
                        eggs.setQuantity("2 dozen");
                        realm.copyToRealm(eggs);

                        PantryIngredient bread = realm.createObject(PantryIngredient.class);
                        bread.setIngredientName("Bread");
                        bread.setExpirationDate(now);
                        bread.setQuantity("1 loaf");
                        realm.copyToRealm(bread);

                        PantryIngredient milk = realm.createObject(PantryIngredient.class);
                        milk.setIngredientName("Milk");
                        milk.setExpirationDate(now);
                        milk.setQuantity("3 gallons");
                        realm.copyToRealm(milk);
                    }
                })
                .build();
        Realm.deleteRealm(realmConfig); // Delete Realm between app restarts (testing only)
        Realm.setDefaultConfiguration(realmConfig);
    }
}

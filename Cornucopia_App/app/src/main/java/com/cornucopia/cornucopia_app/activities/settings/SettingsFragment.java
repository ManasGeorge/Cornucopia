package com.cornucopia.cornucopia_app.activities.settings;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.cornucopia.cornucopia_app.R;
import com.cornucopia.cornucopia_app.model.PantryIngredient;

import io.realm.Realm;

/**
 * Created by Kevin on 2/1/17.
 */
public class SettingsFragment extends Fragment {
    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_settings, container, false);

        LinearLayout clearFavorites = (LinearLayout) view.findViewById(R.id.settings_clear_all_favorites);
        clearFavorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("SETTINGS", "Clear favorites clicked");
                Toast.makeText(getContext(), "All Favorites Cleared!", Toast.LENGTH_LONG).show();
            }
        });

        LinearLayout deleteInventory = (LinearLayout) view.findViewById(R.id.settings_delete_entire_inventory);
        deleteInventory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("SETTINGS", "Delete inventory clicked");
                Realm.getDefaultInstance().executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        realm.where(PantryIngredient.class).findAll().deleteAllFromRealm();
                    }
                });

                Toast.makeText(getContext(), "Entire Inventory Deleted!", Toast.LENGTH_LONG).show();

            }
        });

        LinearLayout contactDeveloper = (LinearLayout) view.findViewById(R.id.settings_contact_developer);
        contactDeveloper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("SETTINGS", "Contact developer clicked");

                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "pbale@gatech.edu", null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Developer Contact");

                try {
                    startActivity(emailIntent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(getContext(), "Email unavailable on this device", Toast.LENGTH_LONG).show();
                    //TODO: Handle case where no email app is available
                }
            }
        });

        return view;
    }
}

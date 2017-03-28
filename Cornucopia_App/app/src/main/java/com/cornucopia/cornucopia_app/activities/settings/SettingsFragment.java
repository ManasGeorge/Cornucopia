package com.cornucopia.cornucopia_app.activities.settings;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.cornucopia.cornucopia_app.R;

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
            }
        });

        LinearLayout deleteInventory = (LinearLayout) view.findViewById(R.id.settings_delete_entire_inventory);
        deleteInventory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("SETTINGS", "Delete inventory clicked");
            }
        });

        LinearLayout contactDeveloper = (LinearLayout) view.findViewById(R.id.settings_contact_developer);
        contactDeveloper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("SETTINGS", "Contact developer clicked");
            }
        });

        return view;
    }
}

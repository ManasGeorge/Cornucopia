package com.cornucopia.cornucopia_app.activities.recipes;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.cornucopia.cornucopia_app.R;

/**
 * Created by Kevin on 2/1/17.
 */
public class RecipeFragment extends Fragment {
    public static RecipeFragment newInstance() {
        return new RecipeFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_recipes_home, container, false);

        LinearLayout layout = (LinearLayout) view.findViewById(R.id.recipe_home_make_now_linear_layout);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//        layoutParams.setMargins(1, 1, 1, 1);
        layoutParams.gravity = Gravity.LEFT;

        // Add 4 images
        for (int i = 0; i < 4; i++) {
            ImageView imageView = new ImageView(getActivity());
            imageView.setImageResource(R.drawable.whale);
            imageView.setLayoutParams(layoutParams);
            layout.addView(imageView);
        }

        return view;
    }
}

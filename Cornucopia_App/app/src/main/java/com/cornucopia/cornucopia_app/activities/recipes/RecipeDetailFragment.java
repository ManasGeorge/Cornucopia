package com.cornucopia.cornucopia_app.activities.recipes;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cornucopia.cornucopia_app.R;
import com.cornucopia.cornucopia_app.model.Recipe;
import com.joanzapata.iconify.widget.IconButton;

/**
 * Displays the ingredients, instructions and comments for a recipe.
 */
public class RecipeDetailFragment extends Fragment {

    public static RecipeDetailFragment newInstance(@NonNull Recipe recipe) {
        RecipeDetailFragment fragment = new RecipeDetailFragment();
        fragment.recipe = recipe;
        return fragment;
    }

    private Recipe recipe;

    private IconButton favorite;
    private IconButton prepTime;
    private IconButton multiplierButton;
    private IconButton pantryStatus;

    private int multiplier = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe_detail, container, false);

        // Title bar with name of recipe and back button
        TextView title = (TextView) view.findViewById(R.id.recipe_detail_title);
        title.setText(recipe.getRecipeName());
        view.findViewById(R.id.recipe_detail_back_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });

        favorite = (IconButton) view.findViewById(R.id.recipe_detail_favorite);
        prepTime = (IconButton) view.findViewById(R.id.recipe_detail_prep_time);
        multiplierButton = (IconButton) view.findViewById(R.id.recipe_detail_multiplier);
        pantryStatus = (IconButton) view.findViewById(R.id.recipe_detail_pantry_status);

        favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Save?
                recipe.setFavorited(!recipe.isFavorited());
                layoutView();
            }
        });
        prepTime.setEnabled(false);
        multiplierButton.setEnabled(false); // TODO: Multiplier
        pantryStatus.setEnabled(false);

        layoutView();

        return view;
    }

    private void layoutView() {
        favorite.setText(recipe.isFavorited() ? "{fa-heart}": "{fa-heart-o}");
        String prepTimeTextWithIcon = "" + recipe.getPrepTime();
        prepTime.setText(prepTimeTextWithIcon);
        String multiplierText = multiplier + "x";
        multiplierButton.setText(multiplierText);

        pantryStatus.setText("{pantry-check}");
    }
}

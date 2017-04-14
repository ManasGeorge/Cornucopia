package com.cornucopia.cornucopia_app.activities.recipes;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cornucopia.cornucopia_app.R;
import com.cornucopia.cornucopia_app.businessLogic.ServerConnector;
import com.cornucopia.cornucopia_app.model.Recipe;
import com.joanzapata.iconify.widget.IconButton;

import java.util.List;

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
    private List<Recipe.Ingredient> recipeIngredients;
    private List<Recipe.Instruction> recipeInstructions;
    private List<Recipe.Comment> recipeComments;

    private IconButton favorite;
    private IconButton prepTime;
    private IconButton multiplierButton;
    private IconButton pantryStatus;

    private int multiplier = 1;

    private ViewPager viewPager;

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

        viewPager = (ViewPager) view.findViewById(R.id.recipe_detail_view_pager);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.e("RecipeDetail", "On start");
        new ServerConnector(getContext()).getFullRecipe(recipe.getRecipeName(), new ServerConnector.FullRecipeServerResult() {
            @Override
            public void onCompletion(List<Recipe.Ingredient> ingredients, List<Recipe.Instruction> instructions, List<Recipe.Comment> comments) {
                recipeIngredients = ingredients;
                recipeInstructions = instructions;
                recipeComments = comments;
                // Connect the adapter once we have loaded the ingredients, instructions, and comments
                viewPager.setAdapter(new RecipeDetailFragmentPagerAdapter(getFragmentManager()));

                Log.e("RecipeDetail", "Server completion");
            }
        });
    }

    private void layoutView() {
        favorite.setText(recipe.isFavorited() ? "{fa-heart}": "{fa-heart-o}");
        String prepTimeTextWithIcon = "" + recipe.getPrepTime();
        prepTime.setText(prepTimeTextWithIcon);
        String multiplierText = multiplier + "x";
        multiplierButton.setText(multiplierText);

        pantryStatus.setText("{pantry-check}");
    }

    private static int INGREDIENTS_IDX = 0;
    private static int INSTRUCTIONS_IDX = 1;
    private static int COMMENTS_IDX = 2;

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    private class RecipeDetailFragmentPagerAdapter extends FragmentPagerAdapter {

        RecipeDetailFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            if (position == INGREDIENTS_IDX) {
                RecipeDetailIngredientAdapter adapter = new RecipeDetailIngredientAdapter(recipeIngredients);
                return RecipeDetailFragmentPage.newInstance(adapter, "All items in pantry");
            } else if (position == INSTRUCTIONS_IDX) {
                RecipeDetailInstructionAdapter adapter = new RecipeDetailInstructionAdapter(recipeInstructions);
                return RecipeDetailFragmentPage.newInstance(adapter, null);
            } else if (position == COMMENTS_IDX) {
                RecipeDetailCommentAdapter adapter = new RecipeDetailCommentAdapter(recipeComments);
                return RecipeDetailFragmentPage.newInstance(adapter, "ALLRECIPES.COM");
            }
            return new Fragment();
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Ingredients";
                case 1:
                    return "Instructions";
                case 2:
                    return "Comments";
            }
            return null;
        }
    }
}

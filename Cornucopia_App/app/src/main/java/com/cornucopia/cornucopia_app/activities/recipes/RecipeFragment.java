package com.cornucopia.cornucopia_app.activities.recipes;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ViewSwitcher;

import com.cornucopia.cornucopia_app.R;
import com.cornucopia.cornucopia_app.model.Recipe;

/**
 * Displays the recipe lists for "WHAT YOU CAN MAKE NOW", "WHAT YOU COULD MAKE", and "BROWSE RECIPES"
 */
public class RecipeFragment extends Fragment implements RecipeCardRecyclerViewAdapter.OnRecipeClickListener {
    public static final String BROWSE_RECIPES_FRAGMENT_TAG = "BrowseRecipes";

    RecipeCardRecyclerViewAdapter canRecipes;
    RecipeCardRecyclerViewAdapter couldRecipes;
    RecipeCardRecyclerViewAdapter browse;

    public static RecipeFragment newInstance() {
        return new RecipeFragment();
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if(menuVisible) {
            canRecipes.updateRecipes();
            couldRecipes.updateRecipes();
            browse.updateRecipes();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_recipes_home, container, false);

        this.canRecipes = new RecipeCardRecyclerViewAdapter(getContext(), "can_make", this);
        this.couldRecipes = new RecipeCardRecyclerViewAdapter(getContext(), "could_make", this);
        this.browse = new RecipeCardRecyclerViewAdapter(getContext(), "browse", this);

        RecyclerView canView = (RecyclerView) view.findViewById(R.id.recipe_home_make_now_recycler_view);
        canView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        canView.setAdapter(canRecipes);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recipe_home_could_make_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(couldRecipes);

        RecyclerView browseView = (RecyclerView) view.findViewById(R.id.recipe_home_browse_recipes);
        browseView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        browseView.setAdapter(browse);

        final ViewSwitcher canMakeViewSwitcher = (ViewSwitcher) view.findViewById(R.id.recipe_home_make_now_view_switcher);
        final ViewSwitcher couldMakeViewSwitcher = (ViewSwitcher) view.findViewById(R.id.recipe_home_could_make_view_switcher);
        canRecipes.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                Log.d("CAN VIEW HOLDER", "Data changed");
                // If no PantryIngredients then animate to empty view (only if needed)
                if (canRecipes.getItemCount() == 0 && canMakeViewSwitcher.getCurrentView() ==
                        view.findViewById(R.id.recipe_home_make_now_recycler_view)) {
                    canMakeViewSwitcher.showPrevious();
                }
                if (canRecipes.getItemCount() != 0 && canMakeViewSwitcher.getCurrentView() !=
                        view.findViewById(R.id.recipe_home_make_now_recycler_view)) {
                    canMakeViewSwitcher.showNext();
                }
            }
        });

        couldRecipes.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                // If no PantryIngredients then animate to empty view (only if needed)
                if (couldRecipes.getItemCount() == 0 && couldMakeViewSwitcher.getCurrentView() ==
                        view.findViewById(R.id.recipe_home_could_make_recycler_view)) {
                    couldMakeViewSwitcher.showPrevious();
                }
                if (couldRecipes.getItemCount() != 0 && couldMakeViewSwitcher.getCurrentView() !=
                        view.findViewById(R.id.recipe_home_could_make_recycler_view)) {
                    couldMakeViewSwitcher.showNext();
                }
            }
        });

        Button couldMake = (Button) view.findViewById(R.id.recipe_home_action_button_could_make);
        Button canMake = (Button) view.findViewById(R.id.recipe_home_action_button_make_now);
        Button browseRecipes = (Button) view.findViewById(R.id.recipe_home_action_button_browse);

        browseRecipes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RecipeBrowseFragment recipeFragment =
                        RecipeBrowseFragment.newInstance("browse");
                Log.d("NUM FRAGS", String.valueOf(getFragmentManager().getFragments().size()));
                getFragmentManager().beginTransaction()
                        .addToBackStack(BROWSE_RECIPES_FRAGMENT_TAG)
                        .add(R.id.recipe_home_container, recipeFragment,
                                BROWSE_RECIPES_FRAGMENT_TAG)
                        .commit();
            }
        });

        couldMake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RecipeBrowseFragment recipeFragment =
                        RecipeBrowseFragment.newInstance("could_make");
                Log.d("NUM FRAGS", String.valueOf(getFragmentManager().getFragments().size()));
                getFragmentManager().beginTransaction()
                        .addToBackStack(BROWSE_RECIPES_FRAGMENT_TAG)
                        .add(R.id.recipe_home_container, recipeFragment,
                                BROWSE_RECIPES_FRAGMENT_TAG)
                        .commit();
            }
        });

        canMake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RecipeBrowseFragment recipeFragment =
                        RecipeBrowseFragment.newInstance("can_make");
                Log.d("NUM FRAGS", String.valueOf(getFragmentManager().getFragments().size()));
                getFragmentManager().beginTransaction()
                        .addToBackStack(BROWSE_RECIPES_FRAGMENT_TAG)
                        .add(R.id.recipe_home_container, recipeFragment,
                                BROWSE_RECIPES_FRAGMENT_TAG)
                        .commit();
            }
        });
        return view;
    }

    // RecipeCardRecyclerViewAdapter.OnRecipeClickListener

    @Override
    public void onClick(Recipe recipe) {
        // Show the recipe detail page
        RecipeDetailFragment fragment = RecipeDetailFragment.newInstance(recipe);
        getFragmentManager().beginTransaction()
                .addToBackStack(BROWSE_RECIPES_FRAGMENT_TAG)
                .add(R.id.recipe_home_container, fragment, BROWSE_RECIPES_FRAGMENT_TAG)
                .commit();
    }
}

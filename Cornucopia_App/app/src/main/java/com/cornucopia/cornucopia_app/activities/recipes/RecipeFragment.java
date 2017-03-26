package com.cornucopia.cornucopia_app.activities.recipes;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ViewSwitcher;

import com.cornucopia.cornucopia_app.R;

public class RecipeFragment extends Fragment {

    RecipeCardRecyclerViewAdaptor canRecipes;
    RecipeCardRecyclerViewAdaptor couldRecipes;
    RecipeCardRecyclerViewAdaptor browse;

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
        this.canRecipes = new RecipeCardRecyclerViewAdaptor(getContext(), "can_make");
        this.couldRecipes = new RecipeCardRecyclerViewAdaptor(getContext(), "could_make");
        this.browse = new RecipeCardRecyclerViewAdaptor(getContext(), "browse");

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

        return view;
    }
}

package com.cornucopia.cornucopia_app.activities.recipes;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Context;

import com.cornucopia.cornucopia_app.R;
import com.cornucopia.cornucopia_app.businessLogic.ServerConnector;
import com.cornucopia.cornucopia_app.model.Recipe;
import com.joanzapata.iconify.widget.IconTextView;

import java.util.ArrayList;
import java.util.List;

public class RecipeCardRecyclerViewAdapter extends RecyclerView.Adapter<RecipeCardRecyclerViewAdapter.RecipeViewHolder> {

    public interface OnRecipeClickListener {
        void onClick(Recipe recipe);
    }

    private List<Recipe> recipes = new ArrayList<>();

    private Context context;
    private String source;
    private boolean isExpanded;

    @NonNull
    private OnRecipeClickListener listener;

    public RecipeCardRecyclerViewAdapter(Context context, String source, @NonNull OnRecipeClickListener listener) {
        this(context, source, listener, false);
    }

    public RecipeCardRecyclerViewAdapter(Context context, String source, @NonNull OnRecipeClickListener listener, boolean isExpanded) {
        (new ServerConnector(context)).getRecipes(source, this.recipes, this);
        this.context = context;
        this.source = source;
        this.listener = listener;
        this.isExpanded = isExpanded;
    }

    public void updateRecipes() {
        (new ServerConnector(context)).getRecipes(source, this.recipes, this);
    }

    @Override
    public RecipeCardRecyclerViewAdapter.RecipeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if(isExpanded)
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recipe_ingredient_card_large, parent, false);
        else
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recipe_ingredient_card_small, parent, false);
        return new RecipeCardRecyclerViewAdapter.RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecipeViewHolder holder, int position) {
        final Recipe recipe = recipes.get(position);
        assert recipe != null;
        holder.layoutWithRecipe(recipe);

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(recipe);
            }
        });
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    public class RecipeViewHolder extends RecyclerView.ViewHolder {
        final View view;
        final ImageView recipeImage;
        final TextView recipeName;
        final IconTextView recipeFavorited;
        final IconTextView recipeTime;
        final IconTextView recipePantryStatus;

        public RecipeViewHolder(View itemView) {
            super(itemView);
            this.view =itemView;
            recipeImage = (ImageView) itemView.findViewById(
                    R.id.recipe_ingredient_card_small_recipe_image);
            recipeName = (TextView) itemView.findViewById(
                    R.id.recipe_ingredient_card_small_recipe_name);
            recipeFavorited = (IconTextView) itemView.findViewById(
                    R.id.recipe_ingredient_card_small_recipe_favorited);
            recipeTime = (IconTextView) itemView.findViewById(
                    R.id.recipe_ingredient_card_small_recipe_time);
            recipePantryStatus = (IconTextView) itemView.findViewById(
                    R.id.recipe_ingredient_card_small_recipe_pantry_status);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + recipeName.getText() + "'";
        }

        public void layoutWithRecipe(Recipe recipe) {
            recipeName.setText(recipe.getRecipeName());
            recipeFavorited.setText((recipe.isFavorited())?
                    "{fa-heart}": "{fa-heart-o}");
            recipeTime.setText(recipe.getPrepTime());
        }
    }
}


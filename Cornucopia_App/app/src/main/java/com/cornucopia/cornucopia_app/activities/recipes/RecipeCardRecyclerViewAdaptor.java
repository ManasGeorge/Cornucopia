package com.cornucopia.cornucopia_app.activities.recipes;

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

public class RecipeCardRecyclerViewAdaptor extends RecyclerView.Adapter<RecipeCardRecyclerViewAdaptor.RecipeViewHolder> {
    List<Recipe> recipes = new ArrayList<>();
    Context context;

    public RecipeCardRecyclerViewAdaptor(Context context, String source) {
        (new ServerConnector(context)).getRecipes(source, this.recipes, this);
        this.context = context;
    }

    @Override
    public RecipeCardRecyclerViewAdaptor.RecipeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recipe_ingredient_card_small, parent, false);
        return new RecipeCardRecyclerViewAdaptor.RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecipeViewHolder holder, int position) {
        final Recipe recipe = recipes.get(position);
        assert recipe != null;
        holder.layoutWithRecipe(recipe);

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ;
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


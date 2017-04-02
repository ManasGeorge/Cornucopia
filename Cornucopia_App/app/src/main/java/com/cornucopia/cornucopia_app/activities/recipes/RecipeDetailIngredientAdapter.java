package com.cornucopia.cornucopia_app.activities.recipes;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cornucopia.cornucopia_app.R;
import com.cornucopia.cornucopia_app.model.Recipe;

import java.util.List;

/**
 * Displays the ingredient name along with the quantity
 */
class RecipeDetailIngredientAdapter extends RecyclerView.Adapter<RecipeDetailIngredientAdapter.RecipeDetailIngredientViewHolder> {
    @NonNull
    private List<Recipe.Ingredient> ingredients;

    RecipeDetailIngredientAdapter(@NonNull List<Recipe.Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    @Override
    public RecipeDetailIngredientViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recipe_detail_ingredient, parent, false);
        return new RecipeDetailIngredientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecipeDetailIngredientViewHolder holder, int position) {
        Recipe.Ingredient ingredient = ingredients.get(position);
        holder.layoutWithRecipeIngredient(ingredient);
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }

    class RecipeDetailIngredientViewHolder extends RecyclerView.ViewHolder {

        private TextView name;
        private TextView quantity;

        RecipeDetailIngredientViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.recipe_detail_ingredient_name);
            quantity = (TextView) itemView.findViewById(R.id.recipe_detail_ingredient_quantity);
        }

        private void layoutWithRecipeIngredient(@NonNull final Recipe.Ingredient ingredient) {
            name.setText(ingredient.getIngredientName());
            quantity.setText(ingredient.getQuantity());
        }
    }
}

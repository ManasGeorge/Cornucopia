package com.cornucopia.cornucopia_app.activities.pantry;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cornucopia.cornucopia_app.R;
import com.cornucopia.cornucopia_app.model.PantryIngredient;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link PantryIngredient}
 */
public class PantryIngredientRecyclerViewAdapter extends RecyclerView.Adapter<PantryIngredientRecyclerViewAdapter.PantryIngredientViewHolder> {

    private final List<PantryIngredient> mValues;

    public PantryIngredientRecyclerViewAdapter(List<PantryIngredient> items) {
        mValues = items;
    }

    @Override
    public PantryIngredientViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_pantry_ingredient, parent, false);
        return new PantryIngredientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final PantryIngredientViewHolder holder, int position) {
        final PantryIngredient pantryIngredient = mValues.get(position);
        holder.ingredientNameView.setText(pantryIngredient.getIngredientName());
        String expirationDate = pantryIngredient.getExpirationDate().toString();
        holder.expirationDateView.setText(expirationDate);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    /**
     *  This ViewHolder is used to cache the reference to the ingredient name and expiration date UI
     */
    class PantryIngredientViewHolder extends RecyclerView.ViewHolder {
        final TextView ingredientNameView;
        final TextView expirationDateView;

        PantryIngredientViewHolder(View view) {
            super(view);
            ingredientNameView = (TextView) view.findViewById(R.id.pantry_ingredient_name);
            expirationDateView = (TextView) view.findViewById(R.id.pantry_ingredient_expiration_date);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + ingredientNameView.getText() + "'";
        }
    }
}

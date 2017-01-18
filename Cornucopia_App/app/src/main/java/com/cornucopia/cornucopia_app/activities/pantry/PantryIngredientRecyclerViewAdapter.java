package com.cornucopia.cornucopia_app.activities.pantry;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
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

    /**
     * Tracks which card is expanded to show extra details
     * -1 represents nothing expanded - only 1 card can be expanded at a time
     */
    private int expandedPosition = -1;

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
    // Suppress as Google engineers > Lint
    public void onBindViewHolder(final PantryIngredientViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final PantryIngredient pantryIngredient = mValues.get(position);
        holder.layoutWithPantryIngredient(pantryIngredient);

        final boolean isExpanded = position == this.expandedPosition;
        if (isExpanded) {
            holder.revealDetail();
        } else {
            holder.hideDetail();
        }
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // If expanded then make nothing expanded, otherwise expand this item
                expandedPosition = isExpanded ? -1 : position;

                // TODO: Transition
                // https://www.youtube.com/watch?v=EjTJIDKT72M&feature=youtu.be&t=6m48s
                // TransitionManager.beginDelayedTransition(???);
                notifyDataSetChanged();;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    /**
     *  This ViewHolder is used to cache the reference to the ingredient name and expiration date UI
     */
    class PantryIngredientViewHolder extends RecyclerView.ViewHolder {
        final View view;
        final TextView ingredientNameView;
        final View expirationDateHeaderView;
        final TextView expirationDateView;

        final View details;
        final TextView detailQuantity;
        final TextView detailExpirationDate;

        final View actions;

        PantryIngredientViewHolder(View view) {
            super(view);
            this.view = view;
            ingredientNameView = (TextView) view.findViewById(R.id.pantry_ingredient_name);
            expirationDateHeaderView = view.findViewById(R.id.pantry_ingredient_expiration_date_header);
            expirationDateView = (TextView) view.findViewById(R.id.pantry_ingredient_expiration_date);

            details = view.findViewById(R.id.pantry_ingredient_details);
            detailQuantity = (TextView) view.findViewById(R.id.pantry_ingredient_detail_quantity);
            detailExpirationDate = (TextView) view.findViewById(R.id.pantry_ingredient_detail_expiration_date);

            actions = view.findViewById(R.id.pantry_ingredient_actions);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + ingredientNameView.getText() + "'";
        }

        private void layoutWithPantryIngredient(@NonNull PantryIngredient pantryIngredient) {
            ingredientNameView.setText(pantryIngredient.getIngredientName());
            String expirationDate = pantryIngredient.getExpirationDate().toString();
            expirationDateView.setText(expirationDate);

            detailQuantity.setText(pantryIngredient.getQuantity());
            detailExpirationDate.setText(pantryIngredient.getExpirationDate().toString());
        }

        private void revealDetail() {
            expirationDateHeaderView.setVisibility(View.INVISIBLE);
            expirationDateView.setVisibility(View.INVISIBLE);
            details.setVisibility(View.VISIBLE);
            actions.setVisibility(View.VISIBLE);
        }

        private void hideDetail() {
            expirationDateHeaderView.setVisibility(View.VISIBLE);
            expirationDateView.setVisibility(View.VISIBLE);
            details.setVisibility(View.GONE);
            actions.setVisibility(View.GONE);
        }
    }
}

package com.cornucopia.cornucopia_app.activities.grocery;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.cornucopia.cornucopia_app.R;
import com.cornucopia.cornucopia_app.model.GroceryIngredient;

import java.text.DateFormat;

import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;

/**
 * {@link RecyclerView.Adapter} that can display a {@link GroceryIngredient}
 */
public class GroceryIngredientRecyclerViewAdapter extends RealmRecyclerViewAdapter<GroceryIngredient, GroceryIngredientRecyclerViewAdapter.GroceryIngredientViewHolder> {

    private static final DateFormat dateFormat = DateFormat.getDateInstance();

    /**
     * Tracks which card is expanded to show extra details
     * -1 represents nothing expanded - only 1 card can be expanded at a time
     */
    private int expandedPosition = -1;

    public GroceryIngredientRecyclerViewAdapter(@NonNull Context context, @NonNull OrderedRealmCollection<GroceryIngredient> groceryIngredients) {
        super(context, groceryIngredients, true);
    }

    @Override
    public GroceryIngredientRecyclerViewAdapter.GroceryIngredientViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.grocery_ingredient_card, parent, false);
        return new GroceryIngredientRecyclerViewAdapter.GroceryIngredientViewHolder(view);
    }

    @Override
    // Suppress warning as Google engineers > Lint
    public void onBindViewHolder(final GroceryIngredientRecyclerViewAdapter.GroceryIngredientViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final GroceryIngredient groceryIngredient = getItem(position);
        assert groceryIngredient != null;
        holder.layoutWithGroceryIngredient(groceryIngredient);

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
                notifyDataSetChanged();
            }
        });
    }

    private void deleteItemAtPosition(final int adapterPosition) {
        // Retrieve the ingredient and then delete it
        final GroceryIngredient ingredient = getItem(adapterPosition);
        assert ingredient != null;
        Realm.getDefaultInstance().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                // TODO: Convert to executeTransactionAsync, retrieve GroceryIngredient using a id and then delete the newly retrieved object
                expandedPosition = -1;
                ingredient.deleteFromRealm();
            }
        });
    }

    /**
     *  This ViewHolder is used to cache the reference to the ingredient name and expiration date UI
     */
    class GroceryIngredientViewHolder extends RecyclerView.ViewHolder {
        final View view;
        final TextView ingredientNameView;
        final View quantityHeaderView;
        final TextView quantityView;

        final View details;
        final TextView detailQuantity;
        final TextView detailExpirationDate;
        final TextView detailExpirationDateHeader;

        final View actions;
        final TextView actionRemove;
        final TextView actionMove;

        GroceryIngredientViewHolder(View view) {
            super(view);
            this.view = view;
            ingredientNameView = (TextView) view.findViewById(R.id.grocery_ingredient_name);
            quantityHeaderView = view.findViewById(R.id.grocery_ingredient_quantity_header);
            quantityView = (TextView) view.findViewById(R.id.grocery_ingredient_quantity);

            details = view.findViewById(R.id.grocery_ingredient_details);
            detailQuantity = (TextView) view.findViewById(R.id.grocery_ingredient_detail_quantity);
            detailExpirationDate = (TextView) view.findViewById(R.id.grocery_ingredient_detail_expiration_date);
            detailExpirationDateHeader = (TextView) view.findViewById(R.id.grocery_ingredient_detail_expiration_date_header);

            actions = view.findViewById(R.id.grocery_ingredient_actions);
            actionRemove = (TextView) actions.findViewById(R.id.grocery_ingredient_action_remove);
            actionMove = (TextView) actions.findViewById(R.id.grocery_ingredient_action_move);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + ingredientNameView.getText() + "'";
        }

        private void layoutWithGroceryIngredient(@NonNull GroceryIngredient groceryIngredient) {

            ingredientNameView.setText(groceryIngredient.getIngredientName());
            String quantity = groceryIngredient.getQuantity();
            quantityView.setText(quantity);

            // Detail
            detailQuantity.setText(quantity);

            String expirationDateString = dateFormat.format(groceryIngredient.getExpirationDate());
            detailExpirationDate.setText(expirationDateString);

            if (groceryIngredient.isExpirationEstimated()) {
                detailExpirationDateHeader.setText(R.string.grocery_estimated_expiration_date_title);
            } else {
                detailExpirationDateHeader.setText(R.string.grocery_expiration_date_title);
            }

            actionRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //GroceryIngredientRecyclerViewAdapter.this.deleteItemAtPosition(getAdapterPosition());
                }
            });
            actionMove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(GroceryIngredientRecyclerViewAdapter.GroceryIngredientViewHolder.this.itemView.getContext(), "Coming soon to DVD", Toast.LENGTH_SHORT).show();
                }
            });
        }

        private void revealDetail() {
            quantityHeaderView.setVisibility(View.INVISIBLE);
            quantityView.setVisibility(View.INVISIBLE);
            details.setVisibility(View.VISIBLE);
            actions.setVisibility(View.VISIBLE);
        }

        private void hideDetail() {
            quantityHeaderView.setVisibility(View.VISIBLE);
            quantityView.setVisibility(View.VISIBLE);
            details.setVisibility(View.GONE);
            actions.setVisibility(View.GONE);
        }
    }
}

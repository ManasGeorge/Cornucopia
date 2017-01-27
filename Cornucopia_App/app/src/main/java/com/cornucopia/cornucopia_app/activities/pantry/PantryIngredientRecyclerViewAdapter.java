package com.cornucopia.cornucopia_app.activities.pantry;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.cornucopia.cornucopia_app.R;
import com.cornucopia.cornucopia_app.model.ExpirationStatus;
import com.cornucopia.cornucopia_app.model.PantryIngredient;

import java.text.DateFormat;
import java.util.Date;

import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;

/**
 * {@link RecyclerView.Adapter} that can display a {@link PantryIngredient}
 */
public class PantryIngredientRecyclerViewAdapter extends RealmRecyclerViewAdapter<PantryIngredient, PantryIngredientRecyclerViewAdapter.PantryIngredientViewHolder> {

    private static final DateFormat dateFormat = DateFormat.getDateInstance();

    /**
     * Tracks which card is expanded to show extra details
     * -1 represents nothing expanded - only 1 card can be expanded at a time
     */
    private int expandedPosition = -1;

    public PantryIngredientRecyclerViewAdapter(@NonNull Context context, @NonNull OrderedRealmCollection<PantryIngredient> pantryIngredients) {
        super(context, pantryIngredients, true);
    }

    @Override
    public PantryIngredientViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pantry_ingredient_card, parent, false);
        return new PantryIngredientViewHolder(view);
    }

    @Override
    // Suppress warning as Google engineers > Lint
    public void onBindViewHolder(final PantryIngredientViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final PantryIngredient pantryIngredient = getItem(position);
        assert pantryIngredient != null;
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
                notifyDataSetChanged();
            }
        });
    }

    private void deleteItemAtPosition(final int adapterPosition) {
        // Retrieve the ingredient and then delete it
        final PantryIngredient ingredient = getItem(adapterPosition);
        assert ingredient != null;
        Realm.getDefaultInstance().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                // TODO: Convert to executeTransactionAsync, retrieve PantryIngredient using a id and then delete the newly retrieved object
                expandedPosition = -1;
                ingredient.deleteFromRealm();
            }
        });
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
        final TextView actionRemove;
        final TextView actionMove;

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
            actionRemove = (TextView) actions.findViewById(R.id.pantry_ingredient_action_remove);
            actionMove = (TextView) actions.findViewById(R.id.pantry_ingredient_action_move);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + ingredientNameView.getText() + "'";
        }

        private void layoutWithPantryIngredient(@NonNull PantryIngredient pantryIngredient) {
            Date expirationDate = pantryIngredient.getExpirationDate();
            String expirationDateString = dateFormat.format(expirationDate);
            ExpirationStatus expirationStatus = ExpirationStatus.fromIngredientExpirationDate(expirationDate);
            int expirationColor = ContextCompat.getColor(view.getContext(), expirationStatus.getExpirationColor());

            ingredientNameView.setText(pantryIngredient.getIngredientName());
            expirationDateView.setText(expirationDateString);
            expirationDateView.setTextColor(expirationColor);

            detailQuantity.setText(pantryIngredient.getQuantity());
            detailExpirationDate.setText(expirationDateString);
            detailExpirationDate.setTextColor(expirationColor);

            actionRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PantryIngredientRecyclerViewAdapter.this.deleteItemAtPosition(getAdapterPosition());
                }
            });
            actionMove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(PantryIngredientViewHolder.this.itemView.getContext(), "Coming soon to DVD", Toast.LENGTH_SHORT).show();
                }
            });
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

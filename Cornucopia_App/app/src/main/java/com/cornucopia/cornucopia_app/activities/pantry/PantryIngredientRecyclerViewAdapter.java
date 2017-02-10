package com.cornucopia.cornucopia_app.activities.pantry;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cornucopia.cornucopia_app.R;
import com.cornucopia.cornucopia_app.businessLogic.ExpirationDateEstimator;
import com.cornucopia.cornucopia_app.businessLogic.IngredientTransformer;
import com.cornucopia.cornucopia_app.model.ExpirationStatus;
import com.cornucopia.cornucopia_app.model.PantryIngredient;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
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

    private void moveToGroceryList(final int adapterPosition) {
        PantryIngredient pantryIngredient = getItem(adapterPosition);
        assert pantryIngredient != null;
        IngredientTransformer.moveToGroceryList(pantryIngredient);
    }

    /**
     *  This ViewHolder is used to cache the reference to the ingredient name and expiration date UI
     */
    class PantryIngredientViewHolder extends RecyclerView.ViewHolder {
        final View view;
        final TextView ingredientNameView;
        final TextView expirationDateHeaderView;
        final TextView expirationDateView;

        final View details;
        final TextView detailQuantity;
        final TextView detailExpirationDate;
        final TextView detailExpirationDateHeader;

        final View actions;
        final View editButtons;
        final TextView actionRemove;
        final TextView actionMove;
        final TextView editDone;
        final TextView editCancel;

        PantryIngredientViewHolder(View view) {
            super(view);
            this.view = view;

            ingredientNameView = (TextView) view.findViewById(R.id.pantry_ingredient_name);
            expirationDateHeaderView = (TextView) view.findViewById(R.id.pantry_ingredient_expiration_date_header);
            expirationDateView = (TextView) view.findViewById(R.id.pantry_ingredient_expiration_date);

            details = view.findViewById(R.id.pantry_ingredient_details);
            detailQuantity = (TextView) view.findViewById(R.id.pantry_ingredient_detail_quantity);
            detailExpirationDate = (TextView) view.findViewById(R.id.pantry_ingredient_detail_expiration_date);
            detailExpirationDateHeader = (TextView) view.findViewById(R.id.pantry_ingredient_detail_expiration_date_header);

            actions = view.findViewById(R.id.pantry_ingredient_actions);
            actionRemove = (TextView) actions.findViewById(R.id.pantry_ingredient_action_remove);
            actionMove = (TextView) actions.findViewById(R.id.pantry_ingredient_action_move);

            editButtons = view.findViewById(R.id.pantry_ingredient_edit);
            editDone = (TextView) view.findViewById(R.id.pantry_ingredient_action_done);
            editCancel = (TextView) view.findViewById(R.id.pantry_ingredient_action_cancel);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + ingredientNameView.getText() + "'";
        }

        private void layoutWithPantryIngredient(@NonNull final PantryIngredient pantryIngredient) {
            view.requestFocus();
            Date expirationDate = pantryIngredient.getExpirationDate();
            final String expirationDateString = dateFormat.format(expirationDate);
            ExpirationStatus expirationStatus = ExpirationStatus.fromIngredientExpirationDate(expirationDate);
            int expirationColor = ContextCompat.getColor(view.getContext(), expirationStatus.getExpirationColor());

            if (pantryIngredient.isExpirationEstimated()) {
                expirationDateHeaderView.setText(R.string.grocery_estimated_expiration_date_title);
                detailExpirationDateHeader.setText(R.string.grocery_estimated_expiration_date_title);
            } else {
                expirationDateHeaderView.setText(R.string.grocery_expiration_date_title);
                detailExpirationDateHeader.setText(R.string.grocery_expiration_date_title);
            }

            ingredientNameView.setText(pantryIngredient.getIngredientName());
            expirationDateView.setText(expirationDateString);
            expirationDateView.setTextColor(expirationColor);

            detailQuantity.setText(pantryIngredient.getQuantity());
            detailQuantity.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if(b) {
                        actions.setVisibility(LinearLayout.GONE);
                        editButtons.setVisibility(LinearLayout.VISIBLE);
                    }
                }
            });

            detailExpirationDate.setText(expirationDateString);
            detailExpirationDate.setTextColor(expirationColor);

            detailExpirationDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    actions.setVisibility(LinearLayout.GONE);
                    editButtons.setVisibility(LinearLayout.VISIBLE);

                    Calendar calendar = Calendar.getInstance();
                    Dialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                            Calendar cal = Calendar.getInstance();
                            cal.set(Calendar.YEAR, year);
                            cal.set(Calendar.MONTH, month);
                            cal.set(Calendar.DAY_OF_MONTH, day);
                            final Date date = cal.getTime();
                            detailExpirationDate.setText(DateFormat.getDateInstance(
                                    DateFormat.MEDIUM).format(date));
                        }
                    }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH));
                    datePickerDialog.show();
                }
            });

            actionRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteItemAtPosition(getAdapterPosition());
                }
            });

            actionMove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    moveToGroceryList(getAdapterPosition());
                }
            });

            editCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    detailQuantity.setText(pantryIngredient.getQuantity());
                    detailExpirationDate.setText(expirationDateString);
                    hideDetail();
                }
            });

            editDone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    hideDetail();
                    
                    Date date;
                    boolean estimated;
                    try {
                        date = DateFormat.getDateInstance(DateFormat.MEDIUM)
                                .parse(String.valueOf(detailExpirationDate.getText()));
                        estimated = false;
                    } catch (ParseException e) {
                        date = ExpirationDateEstimator
                                .estimateExpirationDate(String.valueOf(ingredientNameView.getText()));
                        estimated = true;
                    }

                    if(String.valueOf(detailQuantity.getText()).equals("")) {
                        Toast.makeText(context, "Quantity can't be empty",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    final Date finalDate = date;
                    final boolean finalEstimated = estimated;
                    Realm.getDefaultInstance().executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            pantryIngredient.setExpirationDate(finalDate);
                            pantryIngredient.setQuantity(String.valueOf(detailQuantity.getText()));
                            pantryIngredient.setExpirationEstimated(finalEstimated);
                        }
                    });
                }
            });
        }

        private void revealDetail() {
            editButtons.setVisibility(LinearLayout.GONE);
            expirationDateHeaderView.setVisibility(View.INVISIBLE);
            expirationDateView.setVisibility(View.INVISIBLE);
            details.setVisibility(View.VISIBLE);
            actions.setVisibility(View.VISIBLE);
        }

        private void hideDetail() {
            editButtons.setVisibility(LinearLayout.GONE);
            expirationDateHeaderView.setVisibility(View.VISIBLE);
            expirationDateView.setVisibility(View.VISIBLE);
            details.setVisibility(View.GONE);
            actions.setVisibility(View.GONE);
        }
    }
}

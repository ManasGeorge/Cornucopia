package com.cornucopia.cornucopia_app.activities.pantry;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.percent.PercentLayoutHelper;
import android.support.percent.PercentRelativeLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.cornucopia.cornucopia_app.R;
import com.cornucopia.cornucopia_app.activities.MainActivity;
import com.cornucopia.cornucopia_app.activities.grocery.GroceryFragment;
import com.cornucopia.cornucopia_app.businessLogic.ExpirationDateEstimator;
import com.cornucopia.cornucopia_app.businessLogic.ServerConnector;
import com.cornucopia.cornucopia_app.model.PantryIngredient;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

import static android.R.id.empty;
import static com.cornucopia.cornucopia_app.model.PantryIngredient.newPantryIngredient;

/**
 * A fragment containing a list of ingredients in the user's Pantry.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnPantryFragmentInteractionListener}
 * interface.
 */
public class PantryFragment extends Fragment {

    private static String GROCERY_LIST_FRAGMENT_TAG = "GroceryList";

    /**
     * Interface must be implemented by activities that contain this fragment
     */
    public interface OnPantryFragmentInteractionListener {
        void showRecipeScreen();
    }

    private OnPantryFragmentInteractionListener interactionListener;
    private ViewSwitcher emptyListViewSwitcher;

    private View pantryContainer;
    private View groceryListContainer;
    private boolean isGroceryListExpanded = false;


    // The PercentRelativeLayout was initially used when the grocery list only covered a portion of the screen
    // but now the grocery list covers the entire screen.
    private static float DEFAULT_PANTRY_CONTAINER_HEIGHT_PERCENTAGE = 0.90f;
    private static float DEFAULT_GROCERY_LIST_CONTAINER_HEIGHT_PERCENTAGE = 0.10f;

    private static float EXPANDED_PANTRY_CONTAINER_HEIGHT_PERCENTAGE = 0.0f;
    private static float EXPANDED_GROCERY_LIST_CONTAINER_HEIGHT_PERCENTAGE = 1.0f;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PantryFragment() {
    }

    public static PantryFragment newInstance() {
        return new PantryFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_pantry_ingredient_list, container, false);
        final Context context = view.getContext();

        // Data source
        RealmResults<PantryIngredient> pantryIngredients = Realm.getDefaultInstance().where(PantryIngredient.class).findAllAsync();
        pantryIngredients.addChangeListener(new RealmChangeListener<RealmResults<PantryIngredient>>() {
            @Override
            public void onChange(RealmResults<PantryIngredient> element) {
                Log.d("REALM ELEMENT", element.toString());
                ((MainActivity) getActivity()).updateExpired(
                        Realm.getDefaultInstance().where(PantryIngredient.class)
                        .greaterThan("expirationDate", Calendar.getInstance().getTime())
                        .findAll()
                        .size()
                );
            }
        });

        // Respond to top action buttons
        Button addItem = (Button) view.findViewById(R.id.pantry_ingredient_list_action_button_add_items);
        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout newIngredient = (LinearLayout) view.findViewById(R.id.new_pantry_ingredient);
                newIngredient.setVisibility(LinearLayout.VISIBLE);
            }
        });
        Button recipeNav = (Button) view.findViewById(R.id.pantry_ingredient_list_action_button_make_now);
        recipeNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                interactionListener.showRecipeScreen();
            }
        });

        initializeAddPantryIngredientFlow(view, context);

        // Set the adapter
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.pantry_ingredient_list_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));

        // When we have no ingredients then we show an empty list message instead of the RecyclerView
        emptyListViewSwitcher = (ViewSwitcher) view.findViewById(R.id.pantry_ingredient_list_view_switcher);
        final View emptyView = view.findViewById(empty);
        pantryIngredients.addChangeListener(new RealmChangeListener<RealmResults<PantryIngredient>>() {
            @Override
            public void onChange(RealmResults<PantryIngredient> element) {
                // If no PantryIngredients then animate to empty view (only if needed)
                if (element.isEmpty() && emptyListViewSwitcher.getCurrentView() != emptyView) {
                    emptyListViewSwitcher.showPrevious();
                }
                // If PantryIngredients then animate to recycler view (only if needed)
                if (!element.isEmpty() && emptyListViewSwitcher.getCurrentView() == emptyView) {
                    emptyListViewSwitcher.showNext();
                }
            }
        });
        recyclerView.setAdapter(new PantryIngredientRecyclerViewAdapter(getContext(), pantryIngredients));

        // Grocery List button
        pantryContainer = view.findViewById(R.id.pantry_ingredient_list_container);
        groceryListContainer = view.findViewById(R.id.pantry_ingredient_list_grocery_list_container);

        view.findViewById(R.id.pantry_ingredient_list_grocery_list_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout newIngredient = (LinearLayout) view.findViewById(R.id.new_grocery_ingredient);
                newIngredient.setVisibility(LinearLayout.VISIBLE);
            }
        });


        // When use taps grocery list button show/hide the grocery list
        view.findViewById(R.id.pantry_ingredient_list_reveal_grocery_list)
                .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleGroceryList(view);
            }
        });

        return view;
    }

    private void toggleGroceryList(View view) {
        if (isGroceryListExpanded) {
            // Remove fragment then adjust UI
            getChildFragmentManager().popBackStack();
            ((FrameLayout) groceryListContainer.findViewById(R.id.pantry_ingredient_grocery_list_container)).removeAllViews();

            adjustPercentHeight(pantryContainer, DEFAULT_PANTRY_CONTAINER_HEIGHT_PERCENTAGE);
            adjustPercentHeight(groceryListContainer, DEFAULT_GROCERY_LIST_CONTAINER_HEIGHT_PERCENTAGE);

            CardView reveal = (CardView) view.findViewById(R.id.pantry_ingredient_list_reveal_grocery_list);
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) reveal.getLayoutParams();
            lp.setMarginEnd((int)getResources().getDimension(R.dimen.text_margin));
            lp.setMarginStart((int)(getResources().getDimension(R.dimen.text_margin)));
            lp.height = LinearLayout.LayoutParams.MATCH_PARENT;
            reveal.setLayoutParams(lp);
        } else {
            GroceryFragment groceryFragment = GroceryFragment.newInstance();
            getChildFragmentManager().beginTransaction()
                    .add(R.id.pantry_ingredient_grocery_list_container, groceryFragment, GROCERY_LIST_FRAGMENT_TAG)
                    .commit();

            adjustPercentHeight(pantryContainer, EXPANDED_PANTRY_CONTAINER_HEIGHT_PERCENTAGE);
            adjustPercentHeight(groceryListContainer, EXPANDED_GROCERY_LIST_CONTAINER_HEIGHT_PERCENTAGE);

            CardView reveal = (CardView) view.findViewById(R.id.pantry_ingredient_list_reveal_grocery_list);
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) reveal.getLayoutParams();
            lp.setMarginEnd(0);
            lp.setMarginStart(0);
            lp.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            reveal.setLayoutParams(lp);
        }
        isGroceryListExpanded = !isGroceryListExpanded;

        View groceryAdd = groceryListContainer.findViewById(R.id.pantry_ingredient_list_grocery_list_add);
        groceryAdd.setVisibility(isGroceryListExpanded ? View.VISIBLE : View.INVISIBLE);
    }

    private void adjustPercentHeight(View view, float heightPercent) {
        PercentRelativeLayout.LayoutParams params = (PercentRelativeLayout.LayoutParams) view.getLayoutParams();
        PercentLayoutHelper.PercentLayoutInfo info = params.getPercentLayoutInfo();
        info.heightPercent = heightPercent;
        view.requestLayout();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnPantryFragmentInteractionListener) {
            interactionListener = (OnPantryFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnPantryFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        interactionListener = null;
    }

    public void initializeAddPantryIngredientFlow(final View view, final Context context) {
        // Set up the add ingredient inputs
        final AutoCompleteTextView mName = (AutoCompleteTextView) view.findViewById(R.id.new_pantry_ingredient_name);
        final EditText mQuantity  = (EditText) view.findViewById(R.id.new_pantry_ingredient_quantity);
        final TextView mDate = (TextView) view.findViewById(R.id.new_pantry_ingredient_expiration_date);
        final Calendar calendar = Calendar.getInstance();
        final boolean[] isEstimated = {false};

        mName.setAdapter(new IngredientNameAdaptor(context));
        mName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Map.Entry<Integer, String> item = (Map.Entry<Integer, String>) adapterView.getItemAtPosition(i);
                mName.setText(item.getValue());
                new ServerConnector(context).setEstimatedDate(item.getKey(), mDate, isEstimated);
            }
        });

        mDate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    calendar.setTime(DateFormat.getDateInstance(DateFormat.MEDIUM)
                            .parse(String.valueOf(mDate.getText())));
                } catch (ParseException e) {
                    ;
                }

                Dialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        Calendar cal = Calendar.getInstance();
                        cal.set(Calendar.YEAR, year);
                        cal.set(Calendar.MONTH, month);
                        cal.set(Calendar.DAY_OF_MONTH, day);
                        Date date = cal.getTime();
                        mDate.setText(DateFormat.getDateInstance(DateFormat.MEDIUM).format(date));
                        isEstimated[0] = false;
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });

        Button mAddIngredient = (Button) view.findViewById(R.id.new_pantry_ingredient_add);
        mAddIngredient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Realm realm = Realm.getDefaultInstance();
                Date date;
                try {
                    date = DateFormat.getDateInstance(DateFormat.MEDIUM)
                            .parse(String.valueOf(mDate.getText()));
                    isEstimated[0] = false;
                } catch (ParseException e) {
                    date = ExpirationDateEstimator
                            .estimateExpirationDate(String.valueOf(mName.getText()));
                    isEstimated[0] = true;
                }

                if(String.valueOf(mName.getText()).equals("")
                        || String.valueOf(mQuantity.getText()).equals("")
                        || String.valueOf(mName.getText()).equals(
                        getResources().getString(R.string.enter_ingredient_name))) {
                    Toast.makeText(context, "Name and quantity can't be empty",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                final Date finalDate = date;
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        realm.copyToRealm(newPantryIngredient(realm,
                                mName.getText().toString(), finalDate,
                                isEstimated[0], mQuantity.getText().toString()));

                        LinearLayout newIngredient = (LinearLayout) view
                                .findViewById(R.id.new_pantry_ingredient);
                        mName.setText("");
                        mName.setHint(R.string.enter_ingredient_name);
                        mQuantity.setText("");
                        mDate.setText("");
                        newIngredient.setVisibility(LinearLayout.GONE);
                    }
                });

            }
        });

        Button mCancelIngredient = (Button) view.findViewById(R.id.new_pantry_ingredient_cancel);
        mCancelIngredient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout newIngredient = (LinearLayout) view.findViewById(R.id.new_pantry_ingredient);
                mName.setText("");
                mName.setHint(R.string.enter_ingredient_name);
                mQuantity.setText("");
                mDate.setText("");
                newIngredient.setVisibility(LinearLayout.GONE);
            }
        });
    }
}

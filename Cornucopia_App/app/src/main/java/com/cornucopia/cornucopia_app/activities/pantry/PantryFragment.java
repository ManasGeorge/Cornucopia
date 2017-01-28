package com.cornucopia.cornucopia_app.activities.pantry;

import android.content.Context;
import android.os.Bundle;
import android.support.percent.PercentLayoutHelper;
import android.support.percent.PercentRelativeLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ViewSwitcher;

import com.cornucopia.cornucopia_app.R;
import com.cornucopia.cornucopia_app.activities.grocery.GroceryFragment;
import com.cornucopia.cornucopia_app.model.GroceryIngredient;
import com.cornucopia.cornucopia_app.model.PantryIngredient;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

import static android.R.id.empty;

/**
 * A fragment containing a list of ingredients in the user's Pantry.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnPantryFragmentInteractionListener}
 * interface.
 */
public class PantryFragment extends Fragment {

    private static String GROCERY_LIST_FRAGMENT_TAG = "GroceryList";

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnPantryFragmentInteractionListener {
        // TODO: Replace with necessary communication (if any)
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
        View view = inflater.inflate(R.layout.fragment_pantry_ingredient_list, container, false);

        // Data source
        RealmResults<PantryIngredient> pantryIngredients = Realm.getDefaultInstance().where(PantryIngredient.class).findAllAsync();

        // Respond to top action buttons
        Button addItem = (Button) view.findViewById(R.id.pantry_ingredient_list_action_button_add_items);
        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Trigger ingredient creation flow
                Realm realm = Realm.getDefaultInstance();
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        PantryIngredient newItem = PantryIngredient.newPantryIngredient(realm, "New Item", new Date(), false, "???");
                        realm.copyToRealm(newItem);
                    }
                });
            }
        });

        // Set the adapter
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.pantry_ingredient_list_recycler_view);
        Context context = view.getContext();
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

        // When use taps grocery list button show/hide the grocery list
        view.findViewById(R.id.pantry_ingredient_list_reveal_grocery_list).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleGroceryList();
            }
        });
        view.findViewById(R.id.pantry_ingredient_list_grocery_list_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Trigger real grocery ingredient creation flow
                Realm realm = Realm.getDefaultInstance();
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        GroceryIngredient newItem = GroceryIngredient.newGroceryIngredient(realm, "New Item", new Date(), false, "???");
                        realm.copyToRealm(newItem);
                    }
                });
            }
        });

        return view;
    }

    private void toggleGroceryList() {
        if (isGroceryListExpanded) {
            // Remove fragment then adjust UI
            getChildFragmentManager().popBackStack();
            ((FrameLayout) groceryListContainer.findViewById(R.id.pantry_ingredient_grocery_list_container)).removeAllViews();

            adjustPercentHeight(pantryContainer, DEFAULT_PANTRY_CONTAINER_HEIGHT_PERCENTAGE);
            adjustPercentHeight(groceryListContainer, DEFAULT_GROCERY_LIST_CONTAINER_HEIGHT_PERCENTAGE);
        } else {
            GroceryFragment groceryFragment = GroceryFragment.newInstance();
            getChildFragmentManager().beginTransaction()
                    .add(R.id.pantry_ingredient_grocery_list_container, groceryFragment, GROCERY_LIST_FRAGMENT_TAG)
                    .commit();

            adjustPercentHeight(pantryContainer, EXPANDED_PANTRY_CONTAINER_HEIGHT_PERCENTAGE);
            adjustPercentHeight(groceryListContainer, EXPANDED_GROCERY_LIST_CONTAINER_HEIGHT_PERCENTAGE);
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
}

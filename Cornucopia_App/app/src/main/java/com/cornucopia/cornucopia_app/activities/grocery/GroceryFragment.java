package com.cornucopia.cornucopia_app.activities.grocery;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ViewSwitcher;

import com.cornucopia.cornucopia_app.R;
import com.cornucopia.cornucopia_app.model.PantryIngredient;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

import static android.R.id.empty;

/**
 * A fragment containing a list of ingredients in the user's Pantry.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnGroceryFragmentInteractionListener}
 * interface.
 */
public class GroceryFragment extends Fragment {
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
    public interface OnGroceryFragmentInteractionListener {
        // TODO: Replace with necessary communication (if any)
    }

    private GroceryFragment.OnGroceryFragmentInteractionListener interactionListener;
    private ViewSwitcher emptyListViewSwitcher;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public GroceryFragment() {
    }

    public static GroceryFragment newInstance() {
        return new GroceryFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_grocery_ingredient_list, container, false);

        // Data source
        RealmResults<PantryIngredient> groceryIngredients = Realm.getDefaultInstance().where(PantryIngredient.class).findAllAsync();

        // Respond to top action buttons
        Button addItem = (Button) view.findViewById(R.id.grocery_ingredient_list_action_button_add_item);
        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Trigger grocery ingredient creation flow
                Realm realm = Realm.getDefaultInstance();
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        // PantryIngredient newItem = PantryIngredient.newPantryIngredient(realm, "New Item", new Date(), true, "???");
                        // realm.copyToRealm(newItem);
                    }
                });
            }
        });


        // Set the adapter
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.grocery_ingredient_list_recycler_view);
        Context context = view.getContext();
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));

        // When we have no ingredients then we show an empty list message instead of the RecyclerView
        emptyListViewSwitcher = (ViewSwitcher) view.findViewById(R.id.grocery_ingredient_list_view_switcher);
        final View emptyView = view.findViewById(empty);
        groceryIngredients.addChangeListener(new RealmChangeListener<RealmResults<PantryIngredient>>() {
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
        recyclerView.setAdapter(new GroceryIngredientRecyclerViewAdapter(getContext(), groceryIngredients));

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof GroceryFragment.OnGroceryFragmentInteractionListener) {
            interactionListener = (GroceryFragment.OnGroceryFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnGroceryFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        interactionListener = null;
    }
}

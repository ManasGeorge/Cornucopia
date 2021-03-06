package com.cornucopia.cornucopia_app.activities.grocery;

import android.content.Context;
import android.content.Intent;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.print.PrintAttributes;
import android.print.pdf.PrintedPdfDocument;
import android.support.v4.app.Fragment;
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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.cornucopia.cornucopia_app.R;
import com.cornucopia.cornucopia_app.activities.pantry.IngredientNameAdaptor;
import com.cornucopia.cornucopia_app.businessLogic.ExpirationDateEstimator;
import com.cornucopia.cornucopia_app.businessLogic.ServerConnector;
import com.cornucopia.cornucopia_app.model.GroceryIngredient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Map; 

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

import static android.R.id.empty;
import static com.cornucopia.cornucopia_app.model.GroceryIngredient.newGroceryIngredient;

/**
 * A fragment containing a list of ingredients in the user's Pantry.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnGroceryFragmentInteractionListener}
 * interface.
 */
public class GroceryFragment extends Fragment {
    /**
     * Interface must be implemented by activities that contain this fragment
     */
    public interface OnGroceryFragmentInteractionListener {
        void showRecipeScreen();
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
        final View view = inflater.inflate(R.layout.fragment_grocery_ingredient_list, container, false);

        // Data source
        RealmResults<GroceryIngredient> groceryIngredients = Realm.getDefaultInstance().where(GroceryIngredient.class).findAllAsync();

        // Set the adapter
        final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.grocery_ingredient_list_recycler_view);
        Context context = view.getContext();
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));

        // Adding grocery view initialization
        initializeAddGroceryIngredientFlow(view, view.getContext());

        // When we have no ingredients then we show an empty list message instead of the RecyclerView
        emptyListViewSwitcher = (ViewSwitcher) view.findViewById(R.id.grocery_ingredient_list_view_switcher);
        final View emptyView = view.findViewById(empty);
        groceryIngredients.addChangeListener(new RealmChangeListener<RealmResults<GroceryIngredient>>() {
            @Override
            public void onChange(RealmResults<GroceryIngredient> element) {
                // If no GroceryIngredients then animate to empty view (only if needed)
                if (element.isEmpty() && emptyListViewSwitcher.getCurrentView() != emptyView) {
                    emptyListViewSwitcher.showPrevious();
                }
                // If GroceryIngredients then animate to recycler view (only if needed)
                if (!element.isEmpty() && emptyListViewSwitcher.getCurrentView() == emptyView) {
                    emptyListViewSwitcher.showNext();
                }
            }
        });
        recyclerView.setAdapter(new GroceryIngredientRecyclerViewAdapter(getContext(), groceryIngredients));

        // Actions
        view.findViewById(R.id.grocery_ingredient_list_action_button_share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PrintAttributes printAttributes = new PrintAttributes.Builder()
                        .setMediaSize(PrintAttributes.MediaSize.ISO_A0)
                        .setMinMargins(new PrintAttributes.Margins(1000, 1000, 1000, 1000))
                        .build();
                PrintedPdfDocument document = new PrintedPdfDocument(getContext(), printAttributes);
                PdfDocument.Page page = document.startPage(0);
                recyclerView.draw(page.getCanvas());

                document.finishPage(page);

                File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "grocery_pdfs");
                String myDir = f.getAbsolutePath();

                if (!f.exists()) Log.d("MAKE DIR", f.mkdir() + "");

                File outputFile = new File(myDir, "GroceryList.pdf");
                try {

                    if (outputFile.exists()) {
                        outputFile.delete();
                        outputFile.createNewFile();
                    }

                    try (OutputStream outputStream = new FileOutputStream(outputFile)) {
                        document.writeTo(outputStream);
                        outputStream.flush();
                        outputStream.close();
                    }

                    document.close();

                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.fromFile(outputFile), "application/pdf");
                    startActivity(Intent.createChooser(intent, "Export grocery list"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
      
        // Recipe navigation
        view.findViewById(R.id.grocery_ingredient_list_action_button_make_now).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                interactionListener.showRecipeScreen();
            }
        });

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

    public void initializeAddGroceryIngredientFlow(final View view, final Context context) {
        // Set up the add ingredient inputs
        final AutoCompleteTextView mName = (AutoCompleteTextView) view.findViewById(R.id.new_grocery_ingredient_name);
        final TextView mDate = new TextView(context);
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

        final EditText mQuantity  = (EditText) view.findViewById(R.id.new_grocery_ingredient_quantity);
        Button mAddIngredient = (Button) view.findViewById(R.id.new_grocery_ingredient_add);
        mAddIngredient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                try {
                    calendar.setTime(DateFormat.getDateInstance(DateFormat.MEDIUM)
                            .parse(String.valueOf(mDate.getText())));
                } catch (ParseException e) {
                    calendar.setTime(ExpirationDateEstimator.estimateExpirationDate(mName.getText().toString()));
                }

                Realm realm = Realm.getDefaultInstance();

                if(String.valueOf(mName.getText()).equals("")
                        || String.valueOf(mQuantity.getText()).equals("")
                        || String.valueOf(mName.getText()).equals(
                        getResources().getString(R.string.enter_ingredient_name))) {
                    Toast.makeText(context, "Name and quantity can't be empty",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        realm.copyToRealm(newGroceryIngredient(realm,
                                mName.getText().toString(), calendar.getTime(),
                                true, mQuantity.getText().toString()));

                        LinearLayout newIngredient = (LinearLayout) view
                                .findViewById(R.id.new_grocery_ingredient);
                        mName.setText("");
                        mName.setHint(R.string.enter_ingredient_name);
                        mQuantity.setText("");
                        newIngredient.setVisibility(LinearLayout.GONE);
                    }
                });

            }
        });

        Button mCancelIngredient = (Button) view.findViewById(R.id.new_grocery_ingredient_cancel);
        mCancelIngredient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout newIngredient = (LinearLayout) view.findViewById(R.id.new_grocery_ingredient);
                mName.setText("");
                mName.setHint(R.string.enter_ingredient_name);
                mQuantity.setText("");
                newIngredient.setVisibility(LinearLayout.GONE);
            }
        });
    }
}

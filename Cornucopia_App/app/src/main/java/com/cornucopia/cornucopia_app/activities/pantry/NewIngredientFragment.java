package com.cornucopia.cornucopia_app.activities.pantry;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;

import com.cornucopia.cornucopia_app.R;

import java.util.Calendar;
import java.util.Date;

import io.realm.Realm;

import static com.cornucopia.cornucopia_app.model.GroceryIngredient.newGroceryIngredient;
import static com.cornucopia.cornucopia_app.model.PantryIngredient.newPantryIngredient;

public class NewIngredientFragment extends DialogFragment {
    private EditText mName;
    private EditText mQuantity;
    private DatePicker mDate;

    private boolean isPantryIngredient;
    private static String arg1 = "isPantryIngredient";
//    private OnFragmentInteractionListener mListener;

    public NewIngredientFragment() {
        // Required empty public constructor
    }

    public static NewIngredientFragment newInstance(boolean isPantryIngredient) {
        NewIngredientFragment fragment = new NewIngredientFragment();
        Bundle args = new Bundle();
        args.putBoolean(arg1, isPantryIngredient);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_new_ingredient, container, false);
    }

    @Override @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_new_ingredient, null);
        mName = (EditText) view.findViewById(R.id.new_grocery_ingredient_name);
        mQuantity  = (EditText) view.findViewById(R.id.new_grocery_ingredient_quantity);
        mDate = (DatePicker) view.findViewById(R.id.new_grocery_ingredient_expiration_date);
        isPantryIngredient = getArguments().getBoolean(arg1);

        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Realm realm = Realm.getDefaultInstance();
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                Calendar cal = Calendar.getInstance();
                                cal.set(Calendar.YEAR, mDate.getYear());
                                cal.set(Calendar.MONTH, mDate.getMonth());
                                cal.set(Calendar.DAY_OF_MONTH, mDate.getDayOfMonth());
                                Date date = cal.getTime();

                                if(isPantryIngredient) {
                                    realm.copyToRealm(newPantryIngredient(realm,
                                                    mName.getText().toString(), date, false,
                                                    mQuantity.getText().toString()));
                                } else {
                                    realm.copyToRealm(newGroceryIngredient(realm,
                                            mName.getText().toString(), date, false,
                                            mQuantity.getText().toString()));
                                }
                            }
                        });
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        NewIngredientFragment.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }

//    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }
//
//    /**
//     * This interface must be implemented by activities that contain this
//     * fragment to allow an interaction in this fragment to be communicated
//     * to the activity and potentially other fragments contained in that
//     * activity.
//     * <p>
//     * See the Android Training lesson <a href=
//     * "http://developer.android.com/training/basics/fragments/communicating.html"
//     * >Communicating with Other Fragments</a> for more information.
//     */
//    public interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        void onFragmentInteraction(Uri uri);
//    }
}

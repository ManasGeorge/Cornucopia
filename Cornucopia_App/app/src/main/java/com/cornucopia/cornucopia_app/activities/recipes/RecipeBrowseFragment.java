package com.cornucopia.cornucopia_app.activities.recipes;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cornucopia.cornucopia_app.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RecipeBrowseFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecipeBrowseFragment extends Fragment {

    String source;

    public RecipeBrowseFragment() {
        // Required empty public constructor
    }

    public static RecipeBrowseFragment newInstance(String source) {
        RecipeBrowseFragment fragment = new RecipeBrowseFragment();
        Bundle args = new Bundle();
        args.putString("source", source);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.source = getArguments().getString("source");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        RecipeCardRecyclerViewAdapter recipes =
                new RecipeCardRecyclerViewAdapter(getContext(), source, true);
        View view =  inflater.inflate(R.layout.fragment_recipe_browse, container, false);

        TextView title = (TextView) view.findViewById(R.id.recipe_browse_detailed_title);
        TextView description = (TextView) view.findViewById(R.id.recipe_browse_detailed_description);
        TextView searchButton = (TextView) view.findViewById(R.id.recipe_browse_search_button);

        switch(source) {
            case "browse": {
                title.setText(R.string.recipe_fragment_browse_title);
                description.setText(R.string.recipe_fragment_browse_description);
                searchButton.setText("{fa-search}");
                break;
            }
            case "can_make": {
                title.setText(R.string.recipe_fragment_can_make_title);
                description.setText(R.string.recipe_fragment_can_make_description);
                searchButton.setText("");
                break;
            }
            case "could_make": {
                title.setText(R.string.recipe_fragment_could_make_title);
                description.setText(R.string.recipe_fragment_could_make_description);
                searchButton.setText("");
                break;
            }
        }

        TextView back = (TextView) view.findViewById(R.id.recipe_browse_back_button);
        RecyclerView browseView = (RecyclerView) view.findViewById(R.id.recipe_browse_list);
        browseView.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false));
        browseView.setAdapter(recipes);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("NUM FRAGS", String.valueOf(getFragmentManager().getFragments().size()));
                getFragmentManager().popBackStack();
                Log.d("NUM FRAGS", String.valueOf(getFragmentManager().getFragments().size()));
            }
        });

        return view;
    }
}

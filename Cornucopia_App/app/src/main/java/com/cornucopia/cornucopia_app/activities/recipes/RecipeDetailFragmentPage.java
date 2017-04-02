package com.cornucopia.cornucopia_app.activities.recipes;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cornucopia.cornucopia_app.R;

/**
 * Displays the contents of an adapter above an optional footer
 * Consistent style for all pages in the RecipeDetailFragment's ViewHolder
 */
public class RecipeDetailFragmentPage extends Fragment {

    public static RecipeDetailFragmentPage newInstance(RecyclerView.Adapter adapter, String footerText) {
        RecipeDetailFragmentPage fragment = new RecipeDetailFragmentPage();
        fragment.adapter = adapter;
        fragment.setFooterText(footerText);
        return fragment;
    }

    public void setFooterText(@Nullable String footerText) {
        this.footerText = footerText;
        if (footer != null) {
            footer.setText(footerText);
            footer.setVisibility(TextUtils.isEmpty(footerText) ? View.INVISIBLE : View.VISIBLE);
        }
    }

    @Nullable
    private String footerText;
    private TextView footer;

    private RecyclerView.Adapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_recipe_detail_page, container, false);

        // Configure recycler view
        final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recipe_detail_fragment_page_recycler_view);
        Context context = view.getContext();
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));

        recyclerView.setAdapter(adapter);

        // Footer
        footer = (TextView) view.findViewById(R.id.recipe_detail_fragment_page_footer);
        setFooterText(footerText);

        return view;
    }
}

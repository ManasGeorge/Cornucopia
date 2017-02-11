package com.cornucopia.cornucopia_app.activities.pantry;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.cornucopia.cornucopia_app.businessLogic.ServerConnector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static android.R.layout.simple_dropdown_item_1line;


public class IngredientNameAdaptor extends BaseAdapter implements Filterable {
    private static final int MAX_RESULTS = 10;
    private Context mContext;
    private List<Map.Entry<Integer, String>> resultList = new ArrayList<>();

    public IngredientNameAdaptor(Context context) {
        mContext = context;
    }

    @Override
    public int getCount() {
        return resultList.size();
    }

    @Override
    public Map.Entry<Integer, String> getItem(int i) {
        return resultList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return  i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            view = inflater.inflate(simple_dropdown_item_1line, viewGroup, false);
        }
        ((TextView) view.findViewById(android.R.id.text1)).setText(getItem(i).getValue());
        return view;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence needle) {
                FilterResults filterResults = new FilterResults();
                if (needle != null) {
                    List<Map.Entry<Integer, String>> results =
                            (new ServerConnector(mContext).findIngredients(needle.toString()));
                    filterResults.values = results;
                    filterResults.count = results.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                if(filterResults != null && filterResults.count > 0) {
                    resultList = (List<Map.Entry<Integer, String>>) filterResults.values;
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
        return filter;
    }
}

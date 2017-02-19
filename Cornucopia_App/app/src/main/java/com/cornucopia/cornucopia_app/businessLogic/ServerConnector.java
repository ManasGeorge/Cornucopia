package com.cornucopia.cornucopia_app.businessLogic;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;
import com.cornucopia.cornucopia_app.activities.recipes.RecipeCardRecyclerViewAdaptor;
import com.cornucopia.cornucopia_app.model.Recipe;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ServerConnector {
    private static final String URL = "https://cornucopia-gt.herokuapp.com/api/";
    private Context mContext;
    private RequestQueue queue;

    public ServerConnector(Context context) {
        mContext = context;
        queue = Volley.newRequestQueue(mContext);
    }

    public List<Map.Entry<Integer, String>> findIngredients(String needle) {
        List<Map.Entry<Integer, String>> results = new ArrayList<>();
        RequestFuture<JSONArray> future = RequestFuture.newFuture();
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET,
                URL + "ingredient/suggest/" + needle, new JSONArray(), future, future);
        queue.add(request);

        Log.d("Requesting", URL + "ingredient/suggest/" + needle);
        // Filtering happens on a worker thread anyways, so it's okay
        // to have Volley make a synchronous request and block.
        try {
            JSONArray response = future.get(10, TimeUnit.SECONDS);
            Log.d("VOLLEY", "Got response: " + response);
            for (int i = 0; i < response.length(); i++) {
                JSONArray ingredient;
                ingredient = response.getJSONArray(i);
                String ingredientName = ingredient.getString(0);
                Integer ingredientID = ingredient.getInt(1);
                results.add(new AbstractMap.SimpleEntry<>(ingredientID,
                        ingredientName));
            }
        } catch (InterruptedException e) {
            Log.d("VOLLEY", "Failed getting suggestions");
            e.printStackTrace();
        } catch (ExecutionException e) {
            Log.d("VOLLEY", "Failed getting suggestions");
            e.printStackTrace();
        } catch (TimeoutException e) {
            Log.d("VOLLEY", "Failed getting suggestions");
            e.printStackTrace();
        } catch (JSONException e) {
            Log.d("JSON", "Couldn't parse string " + e);
        }
        return results;
    }

    public void setEstimatedDate(final int id, final TextView mDate, final boolean[] isEstimated) {
        Log.d("Requesting", URL + "ingredient/by_id/" + id);
        JsonRequest request = new JsonObjectRequest(Request.Method.GET,
                URL + "ingredient/by_id/" + id, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("VOLLEY", "Got response: " + response);
                        try {
                            Calendar cal = Calendar.getInstance();
                            cal.add(Calendar.DATE, (Integer) response.getInt("estimated_shelf_life"));
                            mDate.setText(DateFormat.getDateInstance(DateFormat.MEDIUM).format(cal.getTime()));
                            isEstimated[0] = true;
                        } catch (JSONException e) {
                            Log.d("VOLLEY", "Failed getting ingredient for id " + id);
                            isEstimated[0] = false;
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("VOLLEY", "Failed getting ingredient for id " + id);
                    }
                });
        queue.add(request);
    }

    public void getRecipes(final String recipeEndpoint, final List<Recipe> results,
                           final RecipeCardRecyclerViewAdaptor adaptor) {
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET,
                URL + "recipe/suggest/" + recipeEndpoint, new JSONArray(), new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    Log.d("VOLLEY", "Got response for " + recipeEndpoint + ": " + response);
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject recipe;
                        recipe = response.getJSONObject(i);
                        String recipeName = recipe.getString("name");
                        String prepTime = recipe.getString("prep_time");
                        results.add(new Recipe(recipeName, false, prepTime));
                    }
                    adaptor.notifyDataSetChanged();
                } catch (JSONException e) {
                    Log.d("JSON", "Couldn't parse string " + e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("VOLLEY", "Failed getting recipes");
            }
        });

        queue.add(request);
        Log.d("Requesting", URL + "recipe/suggest/" + recipeEndpoint);
        // Filtering happens on a worker thread anyways, so it's okay
        // to have Volley make a synchronous request and block.
    }
}

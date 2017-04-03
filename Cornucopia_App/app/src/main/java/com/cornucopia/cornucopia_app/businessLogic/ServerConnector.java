package com.cornucopia.cornucopia_app.businessLogic;

import android.content.Context;
import android.provider.Settings.Secure;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;
import com.cornucopia.cornucopia_app.activities.recipes.RecipeCardRecyclerViewAdapter;
import com.cornucopia.cornucopia_app.model.Recipe;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ServerConnector {
    private static final String URL = "https://cornucopia-gt.herokuapp.com/api/";
    private static final String USER_TOKEN_KEY = "token";

    private Context mContext;
    private RequestQueue queue;

    private static String mUserToken;

    public ServerConnector(Context context) {
        mContext = context;
        queue = Volley.newRequestQueue(mContext);
        mUserToken = Secure.getString(mContext.getContentResolver(),
                Secure.ANDROID_ID);
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
                           final RecipeCardRecyclerViewAdapter adapter) {
        results.clear();
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
                        int time = recipe.getInt("prep_time");
                        String prepTime;
                        if(time >= 60)
                            prepTime = String.format("%02d:%02d hrs", time/60, time%60);
                        else
                            prepTime = time + " mins";
                        results.add(new Recipe(recipeName, false, prepTime));
                    }
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    Log.d("JSON", "Couldn't parse string " + e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String message = error.getMessage();
                if (message == null) {
                    message = "Unknown error";
                }
                // TODO: Remove after demo
                results.add(new Recipe("Brigaderiro", false, "15 mins"));
                adapter.notifyDataSetChanged();
                Log.d("VOLLEY", message);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap < String, String > headers = new HashMap<>();
                headers.put(USER_TOKEN_KEY, mUserToken);
                return headers;
            }

            @Override
            public String getBodyContentType() {
                return super.getBodyContentType();
            }
        };

        queue.add(request);
        Log.d("Requesting", URL + "recipe/suggest/" + recipeEndpoint);
        // Filtering happens on a worker thread anyways, so it's okay
        // to have Volley make a synchronous request and block.
    }

    public interface FullRecipeServerResult {
        void onCompletion(List<Recipe.Ingredient> ingredients, List<Recipe.Instruction> instructions, List<Recipe.Comment> comments);
    }

    public void getFullRecipe(final String recipeName, final FullRecipeServerResult recipeResult) {
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET,
                URL + "recipe/by_id/" + recipeName, new JSONArray(), new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    Log.d("VOLLEY", "Got response for recipe/by_id/" + recipeName  + ": " + response);

                    for (int i = 0; i < response.length(); i++) {
                        JSONObject recipe;
                        recipe = response.getJSONObject(i);
//                        String recipeName = recipe.getString("name");
//                        int time = recipe.getInt("prep_time");
//                        String prepTime;
//                        if(time >= 60)
//                            prepTime = String.format("%02d:%02d hrs", time/60, time%60);
//                        else
//                            prepTime = time + " mins";
//                        results.add(new Recipe(recipeName, false, prepTime));
                    }
//                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    Log.d("JSON", "Couldn't parse string " + e);

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //TODO: DEMO
                List<Recipe.Ingredient> ingredients = new ArrayList<>();
                ingredients.add(new Recipe.Ingredient("Cocoa powder", "2 tablespoons"));
                ingredients.add(new Recipe.Ingredient("Consended milk", "250 milliliters"));

                List<Recipe.Instruction> instructions = new ArrayList<>();
                instructions.add(new Recipe.Instruction("Pour all ingredients in a pot in medium-high heat"));
                instructions.add(new Recipe.Instruction("Mix well constantly until it stops sticking to the pan"));
                instructions.add(new Recipe.Instruction("Bring heat to low and mix for 2 more minutes"));
                instructions.add(new Recipe.Instruction("Pour in a bowl and serve after it cools off"));
                List<Recipe.Comment> comments = new ArrayList<>();
                comments.add(new Recipe.Comment("Amazing recipe!", "raphael"));

                recipeResult.onCompletion(ingredients, instructions, comments);
                Log.d("VOLLEY", error.toString());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap < String, String > headers = new HashMap<>();
                headers.put(USER_TOKEN_KEY, mUserToken);
                return headers;
            }

            @Override
            public String getBodyContentType() {
                return super.getBodyContentType();
            }
        };

        queue.add(request);
        Log.d("Requesting", URL + "recipe/by_id/" + recipeName);
    }

//    Include headers for favorite recipes endpoint
    public void getFavoriteRecipes(final String recipeEndpoint, final List<Recipe> results,
                           final RecipeCardRecyclerViewAdapter adaptor) {
        results.clear();
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
                        int time = recipe.getInt("prep_time");
                        String prepTime;
                        if(time >= 60)
                            prepTime = String.format("%02d:%02d hrs", time/60, time%60);
                        else
                            prepTime = time + " mins";
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
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap < String, String > headers = new HashMap<>();
                headers.put(USER_TOKEN_KEY, mUserToken);
                return headers;
            }

            @Override
            public String getBodyContentType() {
                return super.getBodyContentType();
            }
        };

        queue.add(request);
        Log.d("Requesting", URL + "recipe/suggest/" + recipeEndpoint);
        // Filtering happens on a worker thread anyways, so it's okay
        // to have Volley make a synchronous request and block.
    }
}

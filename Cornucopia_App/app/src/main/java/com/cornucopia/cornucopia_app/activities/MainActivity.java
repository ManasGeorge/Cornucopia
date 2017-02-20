package com.cornucopia.cornucopia_app.activities;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.cornucopia.cornucopia_app.R;
import com.cornucopia.cornucopia_app.activities.grocery.GroceryFragment;
import com.cornucopia.cornucopia_app.activities.pantry.PantryFragment;
import com.cornucopia.cornucopia_app.activities.recipes.RecipeFragment;
import com.cornucopia.cornucopia_app.activities.settings.SettingsFragment;

public class MainActivity extends AppCompatActivity implements PantryFragment.OnPantryFragmentInteractionListener, GroceryFragment.OnGroceryFragmentInteractionListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private int numExpired = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

    public void updateExpired(int newExpired) {
        numExpired = newExpired;
        mViewPager.getAdapter().notifyDataSetChanged();
    }

    // PantryFragment.OnPantryFragmentInteractionListener

    @Override
    public void showRecipeScreen() {
        mViewPager.setCurrentItem(RECIPE_IDX);
    }

    private static int PANTRY_IDX = 0;
    private static int RECIPE_IDX = 1;
    private static int SETTINGS_IDX = 2;

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            if (position == PANTRY_IDX) {
                return PantryFragment.newInstance();
            } else if (position == RECIPE_IDX) {
                return RecipeFragment.newInstance();
            } else if (position == SETTINGS_IDX) {
                return SettingsFragment.newInstance();
            }
            return new Fragment();
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    if(numExpired == 0)
                        return "Pantry";
                    else if(numExpired > 10)
                        return "Pantry (10+)";
                    else
                        return "Pantry ("+ numExpired +")";
                case 1:
                    return "Recipes";
                case 2:
                    return "Settings";
            }
            return null;
        }
    }
}

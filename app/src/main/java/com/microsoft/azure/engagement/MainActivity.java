// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package com.microsoft.azure.engagement;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.NavigationView.OnNavigationItemSelectedListener;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.microsoft.azure.engagement.fragment.AboutFragment;
import com.microsoft.azure.engagement.fragment.DataPushNotificationFragment;
import com.microsoft.azure.engagement.fragment.FeaturesFragment;
import com.microsoft.azure.engagement.fragment.WebAnnouncementFragment;
import com.microsoft.azure.engagement.fragment.FullScreenInterstitialFragment;
import com.microsoft.azure.engagement.fragment.GetDeviceIdFragment;
import com.microsoft.azure.engagement.fragment.HelpfulLinksFragment;
import com.microsoft.azure.engagement.fragment.HomeFragment;
import com.microsoft.azure.engagement.fragment.InAppPopupNotificationFragment;
import com.microsoft.azure.engagement.fragment.InAppNotificationsFragment;
import com.microsoft.azure.engagement.fragment.OutOfAppPushNotificationsFragment;
import com.microsoft.azure.engagement.fragment.PollFragment;
import com.microsoft.azure.engagement.fragment.RecentProductUpdatesFragment;
import com.microsoft.azure.engagement.fragment.VideosFragment;
import com.microsoft.azure.engagement.utils.CustomTabActivityHelper;

/**
 * The main screen of the application.
 */
public final class MainActivity
        extends CustomTabsActivity
        implements OnNavigationItemSelectedListener {

    public interface NavigationProvider {
        int getMenuIdentifier();
        int getTitleIdentifier();
    }

    public interface WebViewProvider {
        boolean canGoBack();
        void goBack();
    }

    private final static String TAG = MainActivity.class.getSimpleName();
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private MenuItem previousItem;

    @Override
    public int getLayoutResourceId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        replaceFragment(navigationView.getMenu().findItem(R.id.menu_home), HomeFragment.class);
    }


    @Override
    public void onCustomTabsConnected() {
        getCustomTabActivityHelper().mayLaunchUrl(Uri.parse(getString(R.string.drawer_twitter_url)), null, null);
        getCustomTabActivityHelper().mayLaunchUrl(Uri.parse(getString(R.string.drawer_demo_app_url)), null, null);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START) == true) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (inAppNotificationIsShown() == true) {
            hideNotificationOverlay();
        } else {
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragmentPlaceHolder);

            if ((currentFragment instanceof WebViewProvider == true) && (((WebViewProvider) currentFragment).canGoBack() == true)) {
                ((WebViewProvider) currentFragment).goBack();
            } else {
                if (getSupportFragmentManager().getBackStackEntryCount() <= 1) {
                    finish();
                } else {
                    getSupportFragmentManager().popBackStackImmediate();

                    currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragmentPlaceHolder);
                    refreshSelectedMenuItem(currentFragment);
                }
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(final MenuItem item) {
        if (previousItem == null || item != previousItem) {
            // Handle navigation view item clicks here.
            switch (item.getItemId()) {
                case R.id.menu_home:
                    replaceFragment(item, HomeFragment.class);
                    break;

                case R.id.menu_features:
                    replaceFragment(item, FeaturesFragment.class);
                    break;

                case R.id.menu_videos:
                    replaceFragment(item, VideosFragment.class);
                    break;

                case R.id.menu_helpful_links:
                    replaceFragment(item, HelpfulLinksFragment.class);
                    break;

                case R.id.menu_recent_product_updates:
                    replaceFragment(item, RecentProductUpdatesFragment.class);
                    break;

                case R.id.menu_twitter:
                    CustomTabActivityHelper.openCustomTab(this, Uri.parse(getString(R.string.drawer_twitter_url)), "menu_twitter", null, null);
                    break;

                case R.id.menu_demo_app_backend:
                    CustomTabActivityHelper.openCustomTab(this, Uri.parse(getString(R.string.drawer_demo_app_url)), "menu_backend_link", null, null);
                    break;

                case R.id.menu_get_device_id:
                    replaceFragment(item, GetDeviceIdFragment.class);
                    break;

                case R.id.menu_out_of_app:
                    replaceFragment(item, OutOfAppPushNotificationsFragment.class);
                    break;

                case R.id.menu_in_app:
                    replaceFragment(item, InAppNotificationsFragment.class);
                    break;

                case R.id.menu_in_app_popup:
                    replaceFragment(item, InAppPopupNotificationFragment.class);
                    break;

                case R.id.menu_web_announcement:
                    replaceFragment(item, WebAnnouncementFragment.class);
                    break;

                case R.id.menu_full_screen:
                    replaceFragment(item, FullScreenInterstitialFragment.class);
                    break;

                case R.id.menu_poll_survey:
                    replaceFragment(item, PollFragment.class);
                    break;

                case R.id.menu_data_push:
                    replaceFragment(item, DataPushNotificationFragment.class);
                    break;

                case R.id.menu_about:
                    replaceFragment(item, AboutFragment.class);
                    break;
            }
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Method that updates the current menu item selected
     *
     * @param item The menu item to select
     */
    private final void updateSelection(MenuItem item) {
        if (item.isCheckable() == true) {
            item.setChecked(true);
            if (previousItem != null) {
                previousItem.setChecked(false);
            }
            previousItem = item;
        }
    }

    /**
     * Method that replaces the current fragment
     *
     * @param item          The new menu item selected
     * @param fragmentClass The new fragment class
     */
    private final void replaceFragment(MenuItem item, @NonNull Class<? extends Fragment> fragmentClass) {
        // Hide the in-app layout if it's visible
        hideNotificationOverlay();

        updateSelection(item);

        try {
            final Fragment fragment = fragmentClass.newInstance();

            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(item.getTitle());
                getSupportActionBar().setDisplayShowTitleEnabled(fragment instanceof HomeFragment == false);
            }

            final String backStateName = fragment.getClass().getName();

            final FragmentManager fragmentManager = getSupportFragmentManager();
            final boolean fragmentPopped = fragmentManager.popBackStackImmediate(backStateName, 0);

            if (fragmentPopped == false) {
                //fragment not in back stack, create it.
                final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragmentPlaceHolder, fragment);
                fragmentTransaction.addToBackStack(backStateName);
                fragmentTransaction.commit();
            }
        } catch (Exception exception) {
            Log.e(MainActivity.TAG, "Unable to instantiate the fragment with class '" + fragmentClass.getSimpleName() + "'");
        }
    }

    /**
     * Method that refreshes the selected menu item on back
     *
     * @param currentFragment The currentFragment
     */
    private final void refreshSelectedMenuItem(Fragment currentFragment) {

        if (currentFragment != null) {
            if (currentFragment instanceof NavigationProvider) {
                final NavigationProvider navigationProvider = (NavigationProvider) currentFragment;
                final int menuIdentifier = navigationProvider.getMenuIdentifier();
                final int titleIdentifier = navigationProvider.getTitleIdentifier();
                updateSelection(navigationView.getMenu().findItem(menuIdentifier));

                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(titleIdentifier);
                    getSupportActionBar().setDisplayShowTitleEnabled(currentFragment instanceof HomeFragment == false);
                }
            }
        }
    }
}

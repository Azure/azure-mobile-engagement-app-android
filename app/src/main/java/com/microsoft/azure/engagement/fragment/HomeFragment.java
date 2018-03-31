// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package com.microsoft.azure.engagement.fragment;

import java.util.List;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.microsoft.azure.engagement.CustomTabsActivity;
import com.microsoft.azure.engagement.MainActivity.NavigationProvider;
import com.microsoft.azure.engagement.R;
import com.microsoft.azure.engagement.RecentProductUpdatesActivity;
import com.microsoft.azure.engagement.engagement.AzmeTracker;
import com.microsoft.azure.engagement.fragment.RecentProductUpdatesFragment.FeedItemClickListener;
import com.microsoft.azure.engagement.fragment.RecentProductUpdatesFragment.FeedItemViewHolder;
import com.microsoft.azure.engagement.utils.CustomTabActivityHelper;
import com.microsoft.azure.engagement.ws.AzmeServices;
import com.microsoft.azure.engagement.ws.AzmeServices.AsyncTaskResult;
import com.microsoft.azure.engagement.ws.FeedParser.FeedItem;

public final class HomeFragment
        extends Fragment
        implements OnClickListener, FeedItemClickListener, NavigationProvider {

    private static final String TAG = HomeFragment.class.getSimpleName();

    private ProgressBar progressBar;

    private View feedItemContainer;

    private View errorContainer;

    private View retryButton;

    private View viewAllButton;

    private FeedItemViewHolder feedItemViewHolder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        final View view = inflater.inflate(R.layout.fragment_home, container, false);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        feedItemContainer = view.findViewById(R.id.newContainer);
        errorContainer = view.findViewById(R.id.errorContainer);
        viewAllButton = view.findViewById(R.id.viewAllButton);
        feedItemViewHolder = new RecentProductUpdatesFragment.FeedItemViewHolder(view.findViewById(R.id.cardView));
        retryButton = errorContainer.findViewById(R.id.retryButton);
        retryButton.setOnClickListener(this);
        viewAllButton.setOnClickListener(this);
        asyncTaskCall();

        AzmeTracker.startActivity(getActivity(), "home");
        return view;
    }

    /**
     * Method that performed the webservice calling
     */
    private final void asyncTaskCall() {
        new AsyncTask<Void, Void, AsyncTaskResult<List<FeedItem>>>() {

            @Override
            protected void onPreExecute() {
                feedItemContainer.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                errorContainer.setVisibility(View.INVISIBLE);
            }

            @Override
            protected AsyncTaskResult<List<FeedItem>> doInBackground(Void... params) {
                try {
                    return new AsyncTaskResult<>(AzmeServices.getInstance().getLastUpdate(getString(R.string.recent_product_updates_url)));
                } catch (Exception exception) {
                    Log.w(HomeFragment.TAG, exception);
                    return new AsyncTaskResult<>(exception);
                }
            }

            @Override
            protected void onPostExecute(AsyncTaskResult<List<FeedItem>> result) {
                if (result.getError() != null || result.getResult().size() == 0) {
                    feedItemContainer.setVisibility(View.VISIBLE);
                    errorContainer.setVisibility(View.VISIBLE);
                } else {
                    final FeedItem feedItem = result.getResult().get(0);
                    if (getActivity() instanceof CustomTabsActivity) {
                        final CustomTabsActivity customTabsActivity = (CustomTabsActivity) getActivity();
                        customTabsActivity.getCustomTabActivityHelper().mayLaunchUrl(Uri.parse(feedItem.link), null, null);
                    }
                    feedItemViewHolder.update(feedItem, HomeFragment.this);
                    progressBar.setVisibility(View.GONE);
                    feedItemContainer.setVisibility(View.VISIBLE);
                }
                progressBar.setVisibility(View.INVISIBLE);
            }
        }.execute();
    }

    @Override
    public void onClick(View view) {
        if (view == retryButton) {
            asyncTaskCall();
        } else if (view == viewAllButton) {
            AzmeTracker.sendEvent(getActivity(), "view_all_updates");
            startActivity(new Intent(getActivity(), RecentProductUpdatesActivity.class));
        }
    }

    @Override
    public void onFeedItemClickListener(String url, String eventName, String eventTitle, String eventUrl) {
        CustomTabActivityHelper.openCustomTab(getActivity(), Uri.parse(url), eventName, eventTitle, eventUrl);
    }

    @Override
    public int getMenuIdentifier() {
        return R.id.menu_home;
    }

    @Override
    public int getTitleIdentifier() {
        return R.string.menu_home_title;
    }
}

// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package com.microsoft.azure.engagement.fragment;

import java.util.List;

import android.content.res.Resources;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.support.v7.widget.RecyclerView.State;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.microsoft.azure.engagement.CustomTabsActivity;
import com.microsoft.azure.engagement.MainActivity.NavigationProvider;
import com.microsoft.azure.engagement.R;
import com.microsoft.azure.engagement.engagement.AzmeTracker;
import com.microsoft.azure.engagement.utils.CustomTabActivityHelper;
import com.microsoft.azure.engagement.ws.AzmeServices;
import com.microsoft.azure.engagement.ws.AzmeServices.AsyncTaskResult;
import com.microsoft.azure.engagement.ws.FeedParser.FeedItem;

public final class RecentProductUpdatesFragment
        extends Fragment
        implements OnRefreshListener, OnClickListener, NavigationProvider {

    public static final String TAG = RecentProductUpdatesFragment.class.getSimpleName();

    public interface FeedItemClickListener {

        void onFeedItemClickListener(String uri, String eventName, String eventTitle, String eventUrl);

    }

    public static final class FeedItemAdapter
            extends Adapter<FeedItemViewHolder> {

        private final List<FeedItem> feedItems;

        private final FeedItemClickListener feedItemClickListener;

        public FeedItemAdapter(List<FeedItem> feedItems, FeedItemClickListener feedItemClickListener) {
            this.feedItems = feedItems;
            this.feedItemClickListener = feedItemClickListener;
        }

        @Override
        public FeedItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recent_update_item, parent, false);
            return new FeedItemViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(FeedItemViewHolder holder, int position) {
            holder.update(feedItems.get(position), feedItemClickListener);
        }

        @Override
        public int getItemCount() {
            return feedItems.size();
        }
    }

    public static final class FeedItemViewHolder
            extends ViewHolder {

        private final TextView newUpdateTitle;

        private final TextView newUpdateDate;

        private final TextView newUpdateText;

        public FeedItemViewHolder(View view) {
            super(view);
            newUpdateTitle = (TextView) view.findViewById(R.id.newUpdateTitle);
            newUpdateDate = (TextView) view.findViewById(R.id.newUpdateDate);
            newUpdateText = (TextView) view.findViewById(R.id.newUpdateText);
        }

        public final void update(final FeedItem feedItem, final FeedItemClickListener onFeedItemClickListener) {
            newUpdateTitle.setText(feedItem.title);
            newUpdateDate.setText(feedItem.publicationDate);
            newUpdateText.setText(Html.fromHtml(feedItem.description));
            itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onFeedItemClickListener != null) {
                        onFeedItemClickListener.onFeedItemClickListener(feedItem.link, "click_article", feedItem.title, feedItem.link);
                    }
                }
            });
        }
    }

    public static final class MarginItemDecoration
            extends RecyclerView.ItemDecoration {

        private final int firstSpaceElementsDimension;

        private final int spaceElementsDimension;

        private int gridSpan = 0;

        public MarginItemDecoration(int firstSpaceElementsDimension, int spaceElementsDimension) {
            this.firstSpaceElementsDimension = firstSpaceElementsDimension;
            this.spaceElementsDimension = spaceElementsDimension;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, State state) {
            if (gridSpan == 0) {
                final LayoutManager layoutManager = parent.getLayoutManager();
                if (layoutManager instanceof GridLayoutManager) {
                    gridSpan = ((GridLayoutManager) layoutManager).getSpanCount();
                } else {
                    gridSpan = 1;
                }
            }
            final int position = parent.getChildAdapterPosition(view);
            if (position < gridSpan) {
                outRect.top = position == 0 ? firstSpaceElementsDimension : spaceElementsDimension;
            }
            if (position % gridSpan == 0) {
                outRect.left = spaceElementsDimension;
            }
            outRect.right = spaceElementsDimension;
            outRect.bottom = spaceElementsDimension;
        }
    }

    private RecyclerView recyclerView;

    private View progressBar;

    private View errorContainer;

    private View retryButton;

    private SwipeRefreshLayout swipeRefreshLayout;

    private FeedItemAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_recent_product_updates, container, false);
        final Resources resources = getResources();

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(new MarginItemDecoration((int) resources.getDimension(R.dimen.dimen20dip), (int) resources.getDimension(R.dimen.dimen15dip)));
        progressBar = view.findViewById(R.id.progressBar);
        retryButton = view.findViewById(R.id.retryButton);
        retryButton.setOnClickListener(this);
        errorContainer = view.findViewById(R.id.errorContainer);
        errorContainer.setVisibility(View.VISIBLE);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(resources.getIntArray(R.array.refreshColors));

        asyncTaskCall(false);

        AzmeTracker.startActivity(getActivity(), "recent_updates");

        return view;
    }

    /**
     * Method that performed the webservice calling
     *
     * @param fromRefresh paramaters that indicates the calling source of the method
     **/
    private void asyncTaskCall(final boolean fromRefresh) {
        new AsyncTask<Void, Void, AsyncTaskResult<List<FeedItem>>>() {

            @Override
            protected void onPreExecute() {
                if (fromRefresh == true) {
                    swipeRefreshLayout.setRefreshing(true);
                    swipeRefreshLayout.setEnabled(false);
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    errorContainer.setVisibility(View.GONE);
                }
            }

            @Override
            protected AsyncTaskResult<List<FeedItem>> doInBackground(Void... params) {
                try {
                    return new AsyncTaskResult<>(AzmeServices.getInstance().getLastUpdate(getString(R.string.recent_product_updates_url)));
                } catch (Exception exception) {
                    Log.w(RecentProductUpdatesFragment.TAG, exception);
                    return new AsyncTaskResult<>(exception);
                }
            }

            @Override
            protected void onPostExecute(AsyncTaskResult<List<FeedItem>> result) {
                if (result.getError() != null) {
                    errorContainer.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    final List<FeedItem> feedItems = result.getResult();
                    if (getActivity() instanceof CustomTabsActivity) {
                        final CustomTabsActivity customTabsActivity = (CustomTabsActivity) getActivity();
                        for (FeedItem feedItem : feedItems) {
                            customTabsActivity.getCustomTabActivityHelper().mayLaunchUrl(Uri.parse(feedItem.link), null, null);
                        }
                    }
                    adapter = new FeedItemAdapter(feedItems, new FeedItemClickListener() {
                        @Override
                        public void onFeedItemClickListener(String url, String eventName, String eventTitle, String eventUrl) {
                            CustomTabActivityHelper.openCustomTab(getActivity(), Uri.parse(url), eventName, eventTitle, eventUrl);
                        }
                    });
                    recyclerView.setAdapter(adapter);
                    recyclerView.setVisibility(View.VISIBLE);
                    errorContainer.setVisibility(View.GONE);
                }

                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                swipeRefreshLayout.setEnabled(true);
            }
        }.execute();
    }

    @Override
    public void onClick(View view) {
        if (view == retryButton) {
            asyncTaskCall(false);
        }
    }

    @Override
    public void onRefresh() {
        asyncTaskCall(true);
    }

    @Override
    public int getMenuIdentifier() {
        return R.id.menu_recent_product_updates;
    }

    @Override
    public int getTitleIdentifier() {
        return R.string.menu_recent_product_updates_title;
    }
}

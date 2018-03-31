// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package com.microsoft.azure.engagement.fragment;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.microsoft.azure.engagement.MainActivity.NavigationProvider;
import com.microsoft.azure.engagement.R;
import com.microsoft.azure.engagement.engagement.AzmeTracker;
import com.microsoft.azure.engagement.fragment.RecentProductUpdatesFragment.FeedItemClickListener;
import com.microsoft.azure.engagement.utils.CustomTabActivityHelper;

public final class HelpfulLinksFragment
        extends Fragment
        implements FeedItemClickListener, NavigationProvider {

    private static final class SimpleDividerItemDecoration
            extends RecyclerView.ItemDecoration {

        private final Drawable divider;

        public SimpleDividerItemDecoration(Drawable drawable) {
            divider = drawable;
        }

        public final void onDrawOver(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
            final int left = parent.getPaddingLeft();
            final int right = parent.getWidth() - parent.getPaddingRight();

            final int childCount = parent.getChildCount();
            for (int index = 0; index < childCount; index++) {
                final View child = parent.getChildAt(index);
                final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

                final int top = child.getBottom() + params.bottomMargin;
                final int bottom = top + divider.getIntrinsicHeight();

                divider.setBounds(left, top, right, bottom);
                divider.draw(canvas);
            }
        }
    }

    private static final class HelpfulLink {

        public final String title;

        public final int iconResourceId;

        private final String url;

        private final String eventName;

        private final FeedItemClickListener feedItemClickListener;

        public HelpfulLink(String title, int iconResourceId, String url, String eventName, FeedItemClickListener feedItemClickListener) {
            this.title = title;
            this.iconResourceId = iconResourceId;
            this.url = url;
            this.eventName = eventName;
            this.feedItemClickListener = feedItemClickListener;
        }
    }

    private static final class HelpfulLinkItemAdapter
            extends Adapter<HelpfulLinkItemViewHolder> {

        private final List<HelpfulLink> feedItems;

        public HelpfulLinkItemAdapter(List<HelpfulLink> feedItems) {
            this.feedItems = feedItems;
        }

        @Override
        public HelpfulLinkItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            final View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.helpful_link_item, parent, false);
            return new HelpfulLinkItemViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(HelpfulLinkItemViewHolder holder, int position) {
            holder.update(feedItems.get(position));
        }

        @Override
        public int getItemCount() {
            return feedItems.size();
        }
    }

    public final static class HelpfulLinkItemViewHolder
            extends ViewHolder {

        private final TextView title;

        private final ImageView imageView;

        public HelpfulLinkItemViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            imageView = (ImageView) view.findViewById(R.id.imageView);
        }

        public final void update(final HelpfulLink helpfulLink) {
            title.setText(helpfulLink.title);
            imageView.setImageResource(helpfulLink.iconResourceId);
            itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    helpfulLink.feedItemClickListener.onFeedItemClickListener(helpfulLink.url, helpfulLink.eventName, null, null);
                }
            });
        }
    }

    private RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_helpful_link, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(ContextCompat.getDrawable(getActivity(), R.drawable.recyclerview_divider)));

        final List<HelpfulLink> helpfulLinksList = new ArrayList<>();
        helpfulLinksList.add(new HelpfulLink(getString(R.string.helpful_link_documentation), R.drawable.ic_documentation, getString(R.string.helpful_link_documentation_url), "click_documentation_link", this));
        helpfulLinksList.add(new HelpfulLink(getString(R.string.helpful_link_pricing), R.drawable.ic_pricing, getString(R.string.helpful_link_pricing_url), "click_pricing_link", this));
        helpfulLinksList.add(new HelpfulLink(getString(R.string.helpful_link_sla), R.drawable.ic_sla, getString(R.string.helpful_link_sla_url), "click_sla_link", this));
        helpfulLinksList.add(new HelpfulLink(getString(R.string.helpful_link_msdn_support_forum), R.drawable.ic_msdn_forum, getString(R.string.helpful_link_msdn_forum_url), "click_msdn_link", this));
        helpfulLinksList.add(new HelpfulLink(getString(R.string.helpful_link_suggest_product_features), R.drawable.ic_uservoice_forum, getString(R.string.helpful_link_user_voice_forum_url), "click_suggest_product_features_link", this));

        final HelpfulLinkItemAdapter adapter = new HelpfulLinkItemAdapter(helpfulLinksList);
        recyclerView.setAdapter(adapter);

        AzmeTracker.startActivity(getActivity(), "helpful_links");

        return view;
    }

    @Override
    public void onFeedItemClickListener(String uri, String eventName, String eventTitle, String eventUrl) {
        CustomTabActivityHelper.openCustomTab(getActivity(), Uri.parse(uri), eventName, eventTitle, eventUrl);
    }

    @Override
    public int getMenuIdentifier() {
        return R.id.menu_helpful_links;
    }

    @Override
    public int getTitleIdentifier() {
        return R.string.menu_helpful_links_title;
    }
}

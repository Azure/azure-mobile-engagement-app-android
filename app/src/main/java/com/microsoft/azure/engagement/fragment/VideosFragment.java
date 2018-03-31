// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package com.microsoft.azure.engagement.fragment;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.microsoft.azure.engagement.MainActivity.NavigationProvider;
import com.microsoft.azure.engagement.R;
import com.microsoft.azure.engagement.VideoActivity;
import com.microsoft.azure.engagement.engagement.AzmeTracker;
import com.microsoft.azure.engagement.ws.AzmeServices;
import com.microsoft.azure.engagement.ws.AzmeServices.AsyncTaskResult;
import com.microsoft.azure.engagement.ws.VideoParser.VideoItem;
import com.squareup.picasso.Picasso;

public final class VideosFragment
        extends Fragment
        implements NavigationProvider {

    private static final String TAG = VideosFragment.class.getSimpleName();

    private static final class VideoItemAdapter
            extends Adapter<VideoItemViewHolder> {

        private final List<VideoItem> videoItems;

        public VideoItemAdapter(List<VideoItem> videoItems) {
            this.videoItems = videoItems;
        }

        @Override
        public VideoItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            final View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_item, parent, false);
            return new VideoItemViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(VideoItemViewHolder holder, int position) {
            holder.update(videoItems.get(position));
        }

        @Override
        public int getItemCount() {
            return videoItems.size();
        }
    }

    private static final class VideoItemViewHolder
            extends ViewHolder {

        private final ImageView imageView;

        private final TextView title;

        private final TextView description;

        public VideoItemViewHolder(View view) {
            super(view);
            imageView = (ImageView) view.findViewById(R.id.imageView);
            title = (TextView) view.findViewById(R.id.title);
            description = (TextView) view.findViewById(R.id.description);
        }

        public final void update(final VideoItem videoItem) {
            final Context context = title.getContext();
            title.setText(videoItem.title);
            description.setText(Html.fromHtml(videoItem.description));
            if (videoItem.isExternalImage()) {
                // Load image using Picasso library
                Picasso.with(context).load(videoItem.pictureUrl).error(ContextCompat.getDrawable(context, R.drawable.video_place_holder)).centerCrop().fit().into(imageView);
            } else {
                // Load image from local resources
                final int imageId = context.getResources().getIdentifier(videoItem.pictureUrl, "drawable", context.getPackageName());
                imageView.setImageResource(imageId);
            }

            itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    context.startActivity(new Intent(context, VideoActivity.class).putExtra(VideoActivity.VIDEO_ITEM_EXTRA, videoItem));
                }
            });
        }
    }

    private static final class MarginItemDecoration
            extends RecyclerView.ItemDecoration {

        private final int spaceElementsDimension;

        private int gridSpan = 0;

        public MarginItemDecoration(int spaceElementsDimension) {
            this.spaceElementsDimension = spaceElementsDimension;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, State state) {
            if (gridSpan == 0) {
                final LayoutManager layoutManager = parent.getLayoutManager();
                if (layoutManager instanceof GridLayoutManager == true) {
                    gridSpan = ((GridLayoutManager) layoutManager).getSpanCount();
                } else {
                    gridSpan = 1;
                }
            }
            final int position = parent.getChildAdapterPosition(view);
            if (position < gridSpan) {
                outRect.top = spaceElementsDimension;
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

    private VideoItemAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_videos, container, false);
        final Resources resources = getResources();

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(new MarginItemDecoration((int) resources.getDimension(R.dimen.dimen15dip)));
        progressBar = view.findViewById(R.id.progressBar);

        asyncTaskCall();

        AzmeTracker.startActivity(getActivity(), "videos");

        return view;
    }

    private final void asyncTaskCall() {
        new AsyncTask<Void, Void, AsyncTaskResult<List<VideoItem>>>() {

            @Override
            protected void onPreExecute() {
                recyclerView.setVisibility(View.GONE);
            }

            @Override
            protected AsyncTaskResult<List<VideoItem>> doInBackground(Void... params) {
                try {
                    return new AsyncTaskResult<>(AzmeServices.getInstance().getVideos(getString(R.string.videos_url)));
                } catch (Exception exception) {
                    Log.e(VideosFragment.TAG, "An occurs while parsing the json" + exception);
                    return new AsyncTaskResult<>(exception);
                }
            }

            @Override
            protected void onPostExecute(AsyncTaskResult<List<VideoItem>> result) {
                if (result.getError() != null) {
                    recyclerView.setVisibility(View.GONE);
                } else {
                    final List<VideoItem> videoItems = result.getResult();
                    adapter = new VideoItemAdapter(videoItems);
                    recyclerView.setVisibility(View.VISIBLE);

                    recyclerView.setAdapter(adapter);
                }

                progressBar.setVisibility(View.GONE);
            }
        }.execute();
    }

    @Override
    public int getMenuIdentifier() {
        return R.id.menu_videos;
    }

    @Override
    public int getTitleIdentifier() {
        return R.string.menu_videos_title;
    }
}

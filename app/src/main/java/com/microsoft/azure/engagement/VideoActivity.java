// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package com.microsoft.azure.engagement;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.VideoView;

import com.microsoft.azure.engagement.engagement.AzmeTracker;
import com.microsoft.azure.engagement.ws.VideoParser.VideoItem;

public final class VideoActivity
        extends AppCompatActivity {

    public static final String VIDEO_ITEM_EXTRA = "videoItemExtra";

    private VideoView videoView;

    private boolean videoWasPaused;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        final VideoItem videoItem = (VideoItem) getIntent().getSerializableExtra(VIDEO_ITEM_EXTRA);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(videoItem.title);
        }

        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);

        videoView = (VideoView) findViewById(R.id.videoView);
        final MediaController controller = new MediaController(this);
        controller.setAnchorView(videoView);
        controller.setMediaPlayer(videoView);
        videoView.setMediaController(controller);

        videoView.setOnPreparedListener(new OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                progressBar.setVisibility(View.GONE);

                AzmeTracker.startJob(VideoActivity.this, "video", videoItem.title, videoItem.videoUrl);
            }
        });

        videoView.setOnCompletionListener(new OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                finish();
            }
        });

        if (videoItem.isExternalVideo() == true) {
            // We are playing an external video
            videoView.setVideoPath(videoItem.videoUrl);
        } else {
            // We are playing a local video
            final int videoId = getResources().getIdentifier(videoItem.videoUrl, "raw", getPackageName());
            final String path = "android.resource://" + getPackageName() + "/" + videoId;

            videoView.setVideoURI(Uri.parse(path));
        }

        videoView.start();
        hideSystemUI();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Test the latest videoView state on resume
        if (videoWasPaused == true) {
            if (videoView != null) {
                videoView.start();
            }
            videoWasPaused = false;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (videoView != null && (videoView.isPlaying() == true)) {
            videoView.pause();
            videoWasPaused = true;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        showSystemUI();

        AzmeTracker.endJob(this, "video");
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus == true) {
            hideSystemUI();
        }
    }

    private final void hideSystemUI() {
        // Set the IMMERSIVE flag.
        // Set the content to appear under the system bars so that the content
        // doesn't resize when the system bars hide and show.
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    private final void showSystemUI() {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

}

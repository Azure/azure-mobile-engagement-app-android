// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package com.microsoft.azure.engagement;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog.Builder;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;

import com.microsoft.azure.engagement.reach.EngagementAnnouncement;
import com.microsoft.azure.engagement.reach.EngagementReachAgent;

public final class ReboundFullScreenInterstitialActivity
        extends Activity {

    public static final String ACTION_URL_EXTRA = "actionUrlExtra";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent().hasExtra(ReboundFullScreenInterstitialActivity.ACTION_URL_EXTRA)) {
            // Local notification
            final String actionUrl = getIntent().getStringExtra(ReboundFullScreenInterstitialActivity.ACTION_URL_EXTRA);
            showFullScreenInterstitial(getString(R.string.full_screen_interstitial_dialog_announcement_title), getString(R.string.full_screen_interstitial_dialog_announcement_body), getString(R.string.home_recent_updates_view_all), getString(R.string.close_button), actionUrl);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        if (EngagementAgentUtils.isInDedicatedEngagementProcess(this)) {
            return;
        }
        super.onPostCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();

        final EngagementAnnouncement engagementAnnouncement = EngagementReachAgent.getInstance(this).getContent(getIntent());
        if (engagementAnnouncement != null) {
            showFullScreenInterstitial(engagementAnnouncement.getTitle(), engagementAnnouncement.getBody(), engagementAnnouncement.getActionLabel(), engagementAnnouncement.getExitLabel(), engagementAnnouncement.getActionURL());
            engagementAnnouncement.exitContent(this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        EngagementAgent.getInstance(this).endActivity();
    }

    /**
     * Method that shows the full screen interstitial dialog from local/external push notification
     *
     * @param announcementTitle The title of the announcement
     * @param announcementBody  The body of the announcement
     * @param actionButtonText  The action button text
     * @param exitButtonText    The exit button text
     * @param actionUrl         The action url of the announcement
     */
    private final void showFullScreenInterstitial(String announcementTitle, String announcementBody,
                                                  String actionButtonText, String exitButtonText, final String actionUrl) {
        final Builder builder = new Builder(this);
        builder.setTitle(announcementTitle);


        builder.setPositiveButton(actionButtonText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if ((actionUrl != null) && (actionUrl.isEmpty() == false)) {
                    final Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(actionUrl));
                    startActivity(intent);
                }
                finish();
            }
        });

        builder.setNegativeButton(exitButtonText, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        builder.setOnKeyListener(new Dialog.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface dialogInterface, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    finish();
                }
                return true;
            }
        });

        final View dialogContainerView = LayoutInflater.from(this).inflate(R.layout.dialog_full_screen_interstitial, null);
        final WebView webView = (WebView) dialogContainerView.findViewById(R.id.webView);

        webView.loadDataWithBaseURL("file:///android_asset/html/", announcementBody, "text/html", "UTF-8", "");

        builder.setView(dialogContainerView);
        builder.show();
    }

}

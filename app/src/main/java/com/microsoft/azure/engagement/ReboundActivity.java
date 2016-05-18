// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package com.microsoft.azure.engagement;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import com.microsoft.azure.engagement.engagement.AzmeTracker;
import com.microsoft.azure.engagement.reach.EngagementAnnouncement;
import com.microsoft.azure.engagement.reach.EngagementReachAgent;

public final class ReboundActivity
        extends AbstractAzmeActivity
        implements OnClickListener {

    public static final String ACTION_URL_EXTRA = "actionUrlExtra";
    private static final String TYPE_TEXT_PLAIN = "text/plain";
    private TextView contentTextView;
    private WebView webView;
    private Button closeButton;
    private Button viewAllButton;
    private String actionUrl;

    @Override
    public int getLayoutResourceId() {
        return R.layout.activity_rebound;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        closeButton = (Button) findViewById(R.id.closeButton);
        viewAllButton = (Button) findViewById(R.id.viewAllButton);
        contentTextView = (TextView) findViewById(R.id.contentTextView);
        webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);

        closeButton.setOnClickListener(this);
        viewAllButton.setOnClickListener(this);

        if (getIntent().hasExtra(ReboundActivity.ACTION_URL_EXTRA)) {
            // Local announcement notification + Text/html type
            actionUrl = getIntent().getStringExtra(ReboundActivity.ACTION_URL_EXTRA);
            showContent(getString(R.string.rebound_notification_title), getString(R.string.rebound_notification_body), getString(R.string.home_recent_updates_view_all), getString(R.string.close_button), false);
        }

        AzmeTracker.startActivity(this, "rebound");
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack() == true) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View view) {
        if (view == closeButton) {
            AzmeTracker.sendEvent(this, "close_rebound");
            finish();
        } else if (view == viewAllButton) {
            if ((actionUrl != null) && (actionUrl.isEmpty() == false)) {
                final Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(actionUrl));
                startActivity(intent);
            }

            AzmeTracker.sendEvent(this, "view_all_from_rebound");
        }
    }

    @Override
    protected void onResume() {
        final EngagementAnnouncement engagementAnnouncement = EngagementReachAgent.getInstance(this).getContent(getIntent());
        if (engagementAnnouncement != null) {
            setActionUrl(engagementAnnouncement.getActionURL());

            showContent(engagementAnnouncement.getNotificationTitle(), engagementAnnouncement.getBody(), engagementAnnouncement.getActionLabel(), engagementAnnouncement.getExitLabel(), isForTextPlainAnnouncement(engagementAnnouncement));
            engagementAnnouncement.exitContent(this);
        }

        super.onResume();
    }

    /**
     * Method that sets the actionUrl
     *
     * @param actionUrl The actionUrl
     */
    private final void setActionUrl(String actionUrl) {
        this.actionUrl = actionUrl;
    }

    /**
     * Methods that determines the kind of the specify announcement
     *
     * @param engagementAnnouncement The specify engagementAnnouncement
     */
    private final boolean isForTextPlainAnnouncement(EngagementAnnouncement engagementAnnouncement) {
        return engagementAnnouncement.getType().equals(ReboundActivity.TYPE_TEXT_PLAIN);
    }

    /**
     * Method that shows the content announcement
     *
     * @param title                      The title to show
     * @param body                       The content to show
     * @param actionButtonText           The action button text
     * @param exitButtonText             The exit button text
     * @param isForTextPlainAnnouncement The text type boolean value
     */
    private final void showContent(String title, String body, String actionButtonText, String exitButtonText,
                                   boolean isForTextPlainAnnouncement) {
        if (isForTextPlainAnnouncement == true) {
            contentTextView.setText(Html.fromHtml(body));
            contentTextView.setVisibility(View.VISIBLE);
            webView.setVisibility(View.GONE);
        } else {
            webView.loadDataWithBaseURL("file:///android_asset/html/", body, "text/html", "UTF-8", "");
            webView.setVisibility(View.VISIBLE);
            contentTextView.setVisibility(View.GONE);
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }

        viewAllButton.setText(actionButtonText);
        closeButton.setText(exitButtonText);
    }
}

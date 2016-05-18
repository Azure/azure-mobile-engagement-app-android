// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package com.microsoft.azure.engagement;

import android.os.Bundle;
import android.webkit.WebView;

import com.microsoft.azure.engagement.engagement.AzmeTracker;

public final class HowToSendNotificationActivity
        extends AbstractAzmeActivity {

    public static final String NOTIFICATION_TYPE_EXTRA = "notificationTypeExtra";

    // all notification types
    public enum NotificationType {
        outOfApp, inApp, inAppPopup, fullScreenInterstitial, webAnnouncement, poll, dataPushNotification
    }

    @Override
    public int getLayoutResourceId() {
        return R.layout.activity_how_to_send_notification;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final NotificationType notificationType = (NotificationType) getIntent().getSerializableExtra(NOTIFICATION_TYPE_EXTRA);

        final int titleId;
        final String pageName;
        final String trackingType;

        switch (notificationType) {
            case outOfApp:
                titleId = R.string.how_to_send_these_notification_out_of_app_title;
                pageName = "out_of_app.html";
                trackingType = "notification_out_of_app";
                break;
            case inApp:
                titleId = R.string.how_to_send_these_notification_in_app_title;
                pageName = "in_app.html";
                trackingType = "notification_in_app";
                break;
            case inAppPopup:
                titleId = R.string.how_to_send_these_notification_in_app_popup_title;
                pageName = "in_app_popup.html";
                trackingType = "notification_in_app_popup";
                break;
            case fullScreenInterstitial:
                titleId = R.string.how_to_send_these_notification_full_screen_interstitial_title;
                pageName = "full_screen_interstitial.html";
                trackingType = "notification_full_screen";
                break;
            case webAnnouncement:
                titleId = R.string.how_to_send_these_notification_web_announcement_title;
                pageName = "web_announcement.html";
                trackingType = "notification_web_announcement";
                break;
            case poll:
                titleId = R.string.how_to_send_these_notification_poll_title;
                pageName = "poll.html";
                trackingType = "notification_poll";
                break;
            case dataPushNotification:
                titleId = R.string.how_to_send_these_notification_data_push_title;
                pageName = "data_push_notification.html";
                trackingType = "notification_datapush";
                break;
            default:
                titleId = R.string.app_name;
                pageName = "";
                trackingType = "";
                break;
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(titleId);
        }

        final WebView webView = (WebView) findViewById(R.id.webView);
        webView.loadUrl("file:///android_asset/html/how_to_send_notification/" + pageName);

        AzmeTracker.startActivity(this, "how_to_send", trackingType);
    }
}

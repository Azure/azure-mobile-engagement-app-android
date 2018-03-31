// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package com.microsoft.azure.engagement.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.microsoft.azure.engagement.HowToSendNotificationActivity;
import com.microsoft.azure.engagement.HowToSendNotificationActivity.NotificationType;
import com.microsoft.azure.engagement.MainActivity;
import com.microsoft.azure.engagement.MainActivity.NavigationProvider;
import com.microsoft.azure.engagement.R;
import com.microsoft.azure.engagement.ReboundActivity;
import com.microsoft.azure.engagement.engagement.AzmeTracker;

public final class InAppNotificationsFragment
        extends Fragment
        implements OnClickListener, NavigationProvider {

    private View notificationOnlyButton;

    private View announcementButton;

    private View footer;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_in_app_notifications, container, false);

        notificationOnlyButton = view.findViewById(R.id.notificationOnlyButton);
        announcementButton = view.findViewById(R.id.announcementButton);
        footer = view.findViewById(R.id.footer);

        notificationOnlyButton.setOnClickListener(this);
        announcementButton.setOnClickListener(this);
        footer.setOnClickListener(this);

        AzmeTracker.startActivity(getActivity(), "notification_in_app");

        return view;
    }

    @Override
    public void onClick(View view) {
        if (view == notificationOnlyButton) {
            final Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(getString(R.string.deeplink_recent_product_updates)));
            ((MainActivity) getActivity()).showInAppNotification(getString(R.string.in_app_notifications_notification_title), getString(R.string.in_app_notifications_notification_message), intent);

            AzmeTracker.sendEvent(getActivity(), "display_in_app_notification_only");
        } else if (view == announcementButton) {
            final Intent intent = new Intent(getActivity(), ReboundActivity.class);
            intent.putExtra(ReboundActivity.ACTION_URL_EXTRA, getString(R.string.deeplink_recent_product_updates));
            ((MainActivity) getActivity()).showInAppNotification(getString(R.string.in_app_notifications_notification_title), getString(R.string.in_app_notifications_notification_message), intent);

            AzmeTracker.sendEvent(getActivity(), "display_in_app_announcement");
        } else if (view == footer) {
            final Intent intent = new Intent(getActivity(), HowToSendNotificationActivity.class);
            intent.putExtra(HowToSendNotificationActivity.NOTIFICATION_TYPE_EXTRA, NotificationType.inApp);
            startActivity(intent);
        }
    }

    @Override
    public int getMenuIdentifier() {
        return R.id.menu_in_app;
    }

    @Override
    public int getTitleIdentifier() {
        return R.string.menu_in_app_title;
    }
}

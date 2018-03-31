// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package com.microsoft.azure.engagement.fragment;

import android.content.Intent;
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
import com.microsoft.azure.engagement.PollLocalActivity;
import com.microsoft.azure.engagement.R;
import com.microsoft.azure.engagement.engagement.AzmeTracker;

public final class PollFragment
        extends Fragment
        implements OnClickListener, NavigationProvider {

    private View pollNotificationButton;

    private View footer;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_poll, container, false);

        pollNotificationButton = view.findViewById(R.id.pollNotificationButton);
        footer = view.findViewById(R.id.footer);

        pollNotificationButton.setOnClickListener(this);
        footer.setOnClickListener(this);

        AzmeTracker.startActivity(getActivity(), "notification_poll");

        return view;
    }

    @Override
    public void onClick(View view) {
        if (view == pollNotificationButton) {
            final Intent intent = new Intent(getActivity(), PollLocalActivity.class);
            ((MainActivity) getActivity()).showInAppNotification(getString(R.string.poll_notification_title), getString(R.string.poll_notification_message), intent);

            AzmeTracker.sendEvent(getActivity(), "display_poll");
        } else if (view == footer) {
            final Intent intent = new Intent(getActivity(), HowToSendNotificationActivity.class);
            intent.putExtra(HowToSendNotificationActivity.NOTIFICATION_TYPE_EXTRA, NotificationType.poll);
            startActivity(intent);
        }
    }

    @Override
    public int getMenuIdentifier() {
        return R.id.menu_poll_survey;
    }

    @Override
    public int getTitleIdentifier() {
        return R.string.menu_poll_survey_title;
    }
}

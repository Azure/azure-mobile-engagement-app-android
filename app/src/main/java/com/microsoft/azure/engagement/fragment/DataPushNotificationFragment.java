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
import com.microsoft.azure.engagement.MainActivity.NavigationProvider;
import com.microsoft.azure.engagement.ProductDiscountActivity;
import com.microsoft.azure.engagement.R;
import com.microsoft.azure.engagement.engagement.AzmeTracker;

public final class DataPushNotificationFragment
        extends Fragment
        implements OnClickListener, NavigationProvider {

    private View dataPushButton;
    private View footer;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_data_push_notification, container, false);

        dataPushButton = view.findViewById(R.id.dataPushButton);
        footer = view.findViewById(R.id.footer);
        dataPushButton.setOnClickListener(this);
        footer.setOnClickListener(this);

        AzmeTracker.startActivity(getActivity(), "notification_datapush");

        return view;
    }

    @Override
    public void onClick(View view) {
        if (view == dataPushButton) {
            final Intent intent = new Intent(getActivity(), ProductDiscountActivity.class);
            startActivity(intent);

            AzmeTracker.sendEvent(getActivity(), "launch_data_push");
        } else if (view == footer) {
            final Intent intent = new Intent(getActivity(), HowToSendNotificationActivity.class);
            intent.putExtra(HowToSendNotificationActivity.NOTIFICATION_TYPE_EXTRA, NotificationType.dataPushNotification);
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

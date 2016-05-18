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
import com.microsoft.azure.engagement.R;
import com.microsoft.azure.engagement.ReboundFullScreenInterstitialActivity;
import com.microsoft.azure.engagement.engagement.AzmeTracker;

public final class FullScreenInterstitialFragment
        extends Fragment
        implements OnClickListener, NavigationProvider {

    private View fullInterstitialButton;

    private View footer;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_full_screen_interstitial, container, false);

        fullInterstitialButton = view.findViewById(R.id.fullInterstitialButton);
        footer = view.findViewById(R.id.footer);
        fullInterstitialButton.setOnClickListener(this);
        footer.setOnClickListener(this);

        AzmeTracker.startActivity(getActivity(), "notification_full_interstitial");

        return view;
    }

    @Override
    public void onClick(View view) {
        if (view == fullInterstitialButton) {
            final Intent intent = new Intent(getActivity(), ReboundFullScreenInterstitialActivity.class);
            intent.putExtra(ReboundFullScreenInterstitialActivity.ACTION_URL_EXTRA, getString(R.string.deeplink_interstitial));
            startActivity(intent);

            AzmeTracker.sendEvent(getActivity(), "display_full_interstitial");
        } else if (view == footer) {
            final Intent intent = new Intent(getActivity(), HowToSendNotificationActivity.class);
            intent.putExtra(HowToSendNotificationActivity.NOTIFICATION_TYPE_EXTRA, NotificationType.fullScreenInterstitial);
            startActivity(intent);
        }
    }

    @Override
    public int getMenuIdentifier() {
        return R.id.menu_full_screen;
    }

    @Override
    public int getTitleIdentifier() {
        return R.string.menu_full_screen_title;
    }
}

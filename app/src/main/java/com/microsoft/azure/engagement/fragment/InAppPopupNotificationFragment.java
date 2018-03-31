// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package com.microsoft.azure.engagement.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog.Builder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.microsoft.azure.engagement.HowToSendNotificationActivity;
import com.microsoft.azure.engagement.HowToSendNotificationActivity.NotificationType;
import com.microsoft.azure.engagement.MainActivity.NavigationProvider;
import com.microsoft.azure.engagement.ProductActivity;
import com.microsoft.azure.engagement.R;
import com.microsoft.azure.engagement.engagement.AzmeTracker;

public final class InAppPopupNotificationFragment
        extends Fragment
        implements OnClickListener, NavigationProvider {

    private View popupButton;

    private View footer;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_in_app_popup_notification, container, false);

        popupButton = view.findViewById(R.id.popupButton);
        footer = view.findViewById(R.id.footer);
        popupButton.setOnClickListener(this);
        footer.setOnClickListener(this);

        AzmeTracker.startActivity(getActivity(), "notification_in_app_popup");

        return view;
    }

    @Override
    public void onClick(View view) {
        if (view == popupButton) {
            final Builder builder = new Builder(getContext());
            builder.setTitle(R.string.in_app_coupon_notification_dialog_title);
            builder.setMessage(R.string.in_app_coupon_notification_dialog_description);
            builder.setPositiveButton(R.string.positive_button, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    final Intent intent = new Intent(getActivity(), ProductActivity.class);
                    startActivity(intent);
                }
            });
            builder.setNegativeButton(R.string.negative_button, null);
            builder.show();

            AzmeTracker.sendEvent(getActivity(), "display_in_app_pop_up");
        } else if (view == footer) {
            final Intent intent = new Intent(getActivity(), HowToSendNotificationActivity.class);
            intent.putExtra(HowToSendNotificationActivity.NOTIFICATION_TYPE_EXTRA, NotificationType.inAppPopup);
            startActivity(intent);
        }
    }

    @Override
    public int getMenuIdentifier() {
        return R.id.menu_in_app_popup;
    }

    @Override
    public int getTitleIdentifier() {
        return R.string.menu_in_app_pop_up_title;
    }
}

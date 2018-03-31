// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package com.microsoft.azure.engagement.fragment;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.microsoft.azure.engagement.EngagementAgent;
import com.microsoft.azure.engagement.EngagementAgent.Callback;
import com.microsoft.azure.engagement.MainActivity.NavigationProvider;
import com.microsoft.azure.engagement.R;
import com.microsoft.azure.engagement.engagement.AzmeTracker;

public final class GetDeviceIdFragment
        extends Fragment
        implements View.OnClickListener, NavigationProvider {

    private static final String TAG = GetDeviceIdFragment.class.getSimpleName();
    private View shareButton;
    private View copyButton;
    private String deviceId;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_get_device_id, container, false);

        // getting the Device ID
        EngagementAgent.getInstance(getActivity()).getDeviceId(new Callback<String>() {
            @Override
            public void onResult(String deviceId) {
                GetDeviceIdFragment.this.deviceId = deviceId;
            }
        });

        shareButton = view.findViewById(R.id.shareButton);
        copyButton = view.findViewById(R.id.copyButton);

        shareButton.setOnClickListener(this);
        copyButton.setOnClickListener(this);

        AzmeTracker.startActivity(getActivity(), "get_device_id");

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.shareButton:
                shareDeviceID();
                break;

            case R.id.copyButton:
                copyDeviceID();
                break;
        }
    }

    @Override
    public int getMenuIdentifier() {
        return R.id.menu_get_device_id;
    }

    @Override
    public int getTitleIdentifier() {
        return R.string.menu_get_device_id_title;
    }


    /**
     * Method that shares the device id
     */
    private final void shareDeviceID() {
        if (deviceId != null) {
            final Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.get_the_device_id_share_object));
            intent.putExtra(Intent.EXTRA_TEXT, deviceId);
            startActivity(intent);

            Log.d(GetDeviceIdFragment.TAG, "Intent share started");
        } else {
            Log.w(GetDeviceIdFragment.TAG, "Device ID is null");
        }

        AzmeTracker.sendEvent(getActivity(), "share_device_id");
    }

    /**
     * Method that copies the device id
     */
    private final void copyDeviceID() {
        if (deviceId != null) {
            final ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Activity.CLIPBOARD_SERVICE);
            final ClipData clip = ClipData.newPlainText("label", deviceId);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(getActivity(), getString(R.string.get_the_device_id_copy_success_message, deviceId), Toast.LENGTH_LONG).show();

            Log.d(GetDeviceIdFragment.TAG, "String Device ID copied " + deviceId);
        } else {
            Log.w(GetDeviceIdFragment.TAG, "Device ID is null");
        }

        AzmeTracker.sendEvent(getActivity(), "copy_device_id");
    }
}
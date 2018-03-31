// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package com.microsoft.azure.engagement.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.microsoft.azure.engagement.MainActivity.NavigationProvider;
import com.microsoft.azure.engagement.R;
import com.microsoft.azure.engagement.engagement.AzmeTracker;
import com.microsoft.azure.engagement.utils.CustomTabActivityHelper;

public final class AboutFragment
        extends Fragment
        implements NavigationProvider {

    private static final String TAG = AboutFragment.class.getSimpleName();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_about, container, false);

        final WebView webView = (WebView) view.findViewById(R.id.webView);
        webView.loadUrl("file:///android_asset/html/about.html");
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                final String eventName;

                switch (url) {
                    case "file:///android_asset/html/smartnsoft":
                        eventName = null;
                        url = getString(R.string.smartnsoft_url);
                        break;
                    case "file:///android_asset/html/github":
                        eventName = "click_source_link";
                        url = getString(R.string.github_url);
                        break;
                    case "file:///android_asset/html/application_license":
                        eventName = "click_application_license_link";
                        url = getString(R.string.application_license_url);
                        break;
                    case "file:///android_asset/html/third_party_notices":
                        eventName = "click_3rd_party_notices_link";
                        url = getString(R.string.third_party_notices_url);
                        break;
                    default:
                        eventName = null;
                        break;
                }

                Log.d(AboutFragment.TAG, "String url clicked on: " + url);

                CustomTabActivityHelper.openCustomTab(getActivity(), Uri.parse(url), eventName, null, null);

                return true;
            }
        });

        AzmeTracker.startActivity(getActivity(), "about");

        return view;
    }

    @Override
    public int getMenuIdentifier() {
        return R.id.menu_about;
    }

    @Override
    public int getTitleIdentifier() {
        return R.string.menu_about_title;
    }
}

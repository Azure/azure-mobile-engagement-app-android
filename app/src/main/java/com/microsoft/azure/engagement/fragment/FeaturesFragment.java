// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package com.microsoft.azure.engagement.fragment;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.microsoft.azure.engagement.MainActivity.NavigationProvider;
import com.microsoft.azure.engagement.MainActivity.WebViewProvider;
import com.microsoft.azure.engagement.R;
import com.microsoft.azure.engagement.engagement.AzmeTracker;

public final class FeaturesFragment
        extends Fragment
        implements OnClickListener, NavigationProvider, WebViewProvider {

    private WebView webView;
    private View progressBar;
    private View errorContainer;
    private View retryButton;
    private boolean webViewError;

    @SuppressLint("SetJavaScriptEnabled")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_features, container, false);
        progressBar = view.findViewById(R.id.progressBar);
        errorContainer = view.findViewById(R.id.errorContainer);
        retryButton = view.findViewById(R.id.retryButton);
        retryButton.setOnClickListener(this);
        webView = (WebView) view.findViewById(R.id.webView);
        webView.setWebViewClient(new WebViewClient() {
            @TargetApi(android.os.Build.VERSION_CODES.M)
            @Override
            public void onReceivedError(WebView view, WebResourceRequest req, WebResourceError err) {
                onReceivedError(view, err.getErrorCode(), err.getDescription().toString(), req.getUrl().toString());
            }

            @SuppressWarnings("deprecation")
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                webViewError = true;
            }

            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                super.onReceivedHttpError(view, request, errorResponse);
                webViewError = true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressBar.setVisibility(View.GONE);
                webView.setVisibility(webViewError ? View.GONE : View.VISIBLE);
                errorContainer.setVisibility(webViewError ? View.VISIBLE : View.GONE);
            }
        });
        webView.getSettings().setJavaScriptEnabled(true);

        displayLoadingView();
        webView.loadUrl(getString(R.string.drawer_features_url));

        AzmeTracker.startActivity(getActivity(), "features");
        return view;
    }

    /**
     * Method that hides the webView and the errorContainer
     * and shows the loading progressBar
     */
    private final void displayLoadingView() {
        webViewError = false;

        progressBar.setVisibility(View.VISIBLE);
        webView.setVisibility(View.GONE);
        errorContainer.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View view) {
        if (view == retryButton) {
            displayLoadingView();
            webView.reload();
        }
    }

    @Override
    public int getMenuIdentifier() {
        return R.id.menu_features;
    }

    @Override
    public int getTitleIdentifier() {
        return R.string.menu_features_title;
    }

    @Override
    public boolean canGoBack() {
        return webView != null && webView.canGoBack();
    }

    @Override
    public void goBack() {
        webView.goBack();
    }
}

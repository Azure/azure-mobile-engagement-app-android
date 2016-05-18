// Copyright 2015 Google Inc. All Rights Reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.microsoft.azure.engagement.utils;

import java.util.List;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsClient;
import android.support.customtabs.CustomTabsIntent;
import android.support.customtabs.CustomTabsServiceConnection;
import android.support.customtabs.CustomTabsSession;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.microsoft.azure.engagement.R;
import com.microsoft.azure.engagement.engagement.AzmeTracker;
import com.microsoft.azure.engagement.utils.CustomTabsHelper.ServiceConnection;
import com.microsoft.azure.engagement.utils.CustomTabsHelper.ServiceConnectionCallback;

/**
 * This is a helper class to manage the connection to the Custom Tabs Service.
 */
public final class CustomTabActivityHelper
        implements ServiceConnectionCallback {

    private static final String TAG = CustomTabActivityHelper.class.getSimpleName();

    private CustomTabsSession customTabsSession;

    private CustomTabsClient client;

    private CustomTabsServiceConnection connection;

    private ConnectionCallback connectionCallback;

    /**
     * Opens the URL on a Custom Tab if possible. Otherwise fallsback to opening it on a WebView.
     *
     * @param activity   The host activity.
     * @param uri        The Uri to be opened.
     * @param eventName  The tracking eventName.
     * @param eventTitle The tracking eventTitle.
     * @param eventUrl   The tracking eventUrl.
     */
    public static final void openCustomTab(Activity activity, Uri uri, String eventName, String eventTitle,
                                           String eventUrl) {
        try {
            final String packageName = CustomTabsHelper.getPackageNameToUse(activity);
            final CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
            builder.setToolbarColor(ContextCompat.getColor(activity, R.color.customTabColor));
            builder.enableUrlBarHiding();
            builder.setCloseButtonIcon(BitmapFactory.decodeResource(activity.getResources(), R.drawable.ic_arrow_back));
            builder.setStartAnimations(activity, R.anim.slide_in_right, R.anim.slide_out_left);
            builder.setExitAnimations(activity, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            final CustomTabsIntent customTabsIntent = builder.build();
            customTabsIntent.intent.setPackage(packageName);
            customTabsIntent.launchUrl(activity, uri);

            AzmeTracker.sendEventForCustomTab(activity, eventName, eventTitle, eventUrl);
        } catch (Exception exception) {
            Log.w(CustomTabActivityHelper.TAG, "Cannot launch the uri '" + uri + "'");
        }
    }

    /**
     * Unbinds the Activity from the Custom Tabs Service.
     *
     * @param activity the activity that is connected to the service.
     */
    public final void unbindCustomTabsService(Activity activity) {
        if (connection == null) {
            return;
        }
        activity.unbindService(connection);
        client = null;
        customTabsSession = null;
        connection = null;
    }

    /**
     * Creates or retrieves an exiting CustomTabsSession.
     *
     * @return a CustomTabsSession.
     */
    private CustomTabsSession getSession() {
        if (client == null) {
            customTabsSession = null;
        } else if (customTabsSession == null) {
            customTabsSession = client.newSession(null);
        }
        return customTabsSession;
    }

    /**
     * Register a Callback to be called when connected or disconnected from the Custom Tabs Service.
     *
     * @param connectionCallback The connectionCallback
     */
    public final void setConnectionCallback(ConnectionCallback connectionCallback) {
        this.connectionCallback = connectionCallback;
    }

    /**
     * Binds the Activity to the Custom Tabs Service.
     *
     * @param activity the activity to be binded to the service.
     */
    public final void bindCustomTabsService(Activity activity) {
        if (client != null) {
            return;
        }

        final String packageName = CustomTabsHelper.getPackageNameToUse(activity);
        if (packageName == null) {
            return;
        }

        connection = new ServiceConnection(this);
        CustomTabsClient.bindCustomTabsService(activity, packageName, connection);
    }

    /**
     * @return true if call to mayLaunchUrl was accepted.
     * @see {@link CustomTabsSession#mayLaunchUrl(Uri, Bundle, List)}.
     */
    public final boolean mayLaunchUrl(Uri uri, Bundle extras, List<Bundle> otherLikelyBundles) {
        if (client == null) {
            return false;
        }

        CustomTabsSession session = getSession();

        return session != null && session.mayLaunchUrl(uri, extras, otherLikelyBundles);
    }

    @Override
    public void onServiceConnected(CustomTabsClient client) {
        this.client = client;
        this.client.warmup(0L);
        if (connectionCallback != null) {
            connectionCallback.onCustomTabsConnected();
        }
    }

    @Override
    public void onServiceDisconnected() {
        client = null;
        customTabsSession = null;
        if (connectionCallback != null) {
            connectionCallback.onCustomTabsDisconnected();
        }
    }

    /**
     * A Callback for when the service is connected or disconnected. Use those callbacks to
     * handle UI changes when the service is connected or disconnected.
     */
    public interface ConnectionCallback {

        /**
         * Called when the service is connected.
         */
        void onCustomTabsConnected();

        /**
         * Called when the service is disconnected.
         */
        void onCustomTabsDisconnected();
    }
}

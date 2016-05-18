// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package com.microsoft.azure.engagement.engagement;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.microsoft.azure.engagement.EngagementAgent;

public final class AzmeTracker {

    private static final String TAG = AzmeTracker.class.getSimpleName();

    /**
     * Method that starts an activity
     *
     * @param activity The activity to tag
     * @param name     The activity name
     */
    public static final void startActivity(@NonNull Activity activity, @NonNull String name) {
        Log.d(TAG, "name=" + name);
        EngagementAgent.getInstance(activity).startActivity(activity, name, null);
    }

    /**
     * Method that starts an activity
     *
     * @param activity The activity to tag
     * @param name     The activity name
     * @param type     The type
     */
    public static final void startActivity(@NonNull Activity activity, @NonNull String name, String type) {
        final Bundle bundle = new Bundle();
        if (type != null && type.isEmpty() == false) {
            bundle.putString("type", type);
        }

        Log.d(TAG, "name=" + name + " type=" + type);
        EngagementAgent.getInstance(activity).startActivity(activity, name, bundle);
    }

    /**
     * Method that sends an event
     *
     * @param activity The activity
     * @param name     The event name
     */
    public static final void sendEvent(@NonNull Activity activity, String name) {
        if (name != null) {
            Log.d(TAG, "name=" + name);
            EngagementAgent.getInstance(activity).sendEvent(name, null);
        }
    }

    /**
     * Method that sends an event coming from CustomTab
     *
     * @param activity The activity
     * @param name     The event name
     * @param title    The event title
     * @param url      The event url
     */
    public static final void sendEventForCustomTab(@NonNull Activity activity, String name, String title, String url) {
        if (name != null) {
            final Bundle bundle = new Bundle();
            if (title != null && title.isEmpty() == false) {
                bundle.putString("title", title);
            }

            if (url != null && url.isEmpty() == false) {
                bundle.putString("url", url);
            }

            Log.d(TAG, "name=" + name + " title=" + title + " url=" + url);
            EngagementAgent.getInstance(activity).sendEvent(name, bundle);
        }
    }

    /**
     * Method that starts a job
     *
     * @param context The context
     * @param name    The job name
     * @param title   The video title
     * @param url     The video url
     */
    public static final void startJob(@NonNull Context context, @NonNull String name, String title, String url) {
        final Bundle bundle = new Bundle();
        if (title != null && title.isEmpty() == false) {
            bundle.putString("title", title);
        }

        if (url != null && url.isEmpty() == false) {
            bundle.putString("url", url);
        }

        Log.d(TAG, "name=" + name + " title=" + title + " url=" + url);
        EngagementAgent.getInstance(context).startJob(name, bundle);
    }

    /**
     * Method that ends a job
     *
     * @param context The context
     * @param name    The job name
     */
    public static final void endJob(@NonNull Context context, @NonNull String name) {
        Log.d(TAG, "name=" + name);
        EngagementAgent.getInstance(context).endJob(name);
    }
}

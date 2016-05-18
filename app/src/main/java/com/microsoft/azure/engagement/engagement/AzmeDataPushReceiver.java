// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package com.microsoft.azure.engagement.engagement;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.microsoft.azure.engagement.reach.EngagementReachDataPushReceiver;

public final class AzmeDataPushReceiver
        extends EngagementReachDataPushReceiver {

    public static final String PROMOTION_DATA_PUSH_BODY_PREFERENCE_KEY = "promotionDataPushBodyPreferenceKey";

    @Override
    protected Boolean onDataPushStringReceived(Context context, String category, String body) {
        Log.d("tmp", "String data push message received: " + body);

        final SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        defaultSharedPreferences.edit().putString(AzmeDataPushReceiver.PROMOTION_DATA_PUSH_BODY_PREFERENCE_KEY, body).apply();

        return true;
    }

    @Override
    protected Boolean onDataPushBase64Received(Context context, String category, byte[] decodedBody, String encodedBody) {
        Log.d("tmp", "Base64 data push message received: " + encodedBody);
        // Do something useful with decodedBody like updating an image view
        return true;
    }
}

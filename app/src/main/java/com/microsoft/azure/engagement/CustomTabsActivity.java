// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package com.microsoft.azure.engagement;

import android.os.Bundle;

import com.microsoft.azure.engagement.utils.CustomTabActivityHelper;
import com.microsoft.azure.engagement.utils.CustomTabActivityHelper.ConnectionCallback;

public abstract class CustomTabsActivity
        extends AbstractAzmeActivity
        implements ConnectionCallback {

    private CustomTabActivityHelper customTabActivityHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        customTabActivityHelper = new CustomTabActivityHelper();
        customTabActivityHelper.setConnectionCallback(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        customTabActivityHelper.bindCustomTabsService(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        customTabActivityHelper.unbindCustomTabsService(this);
    }

    @Override
    public void onCustomTabsConnected() {
    }

    @Override
    public void onCustomTabsDisconnected() {
    }

    public final CustomTabActivityHelper getCustomTabActivityHelper() {
        return customTabActivityHelper;
    }
}

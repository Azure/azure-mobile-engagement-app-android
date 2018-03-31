// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package com.microsoft.azure.engagement;

import android.content.Intent;

import com.microsoft.azure.engagement.engagement.AzmeNotifier;
import com.microsoft.azure.engagement.reach.EngagementReachAgent;

/**
 * The entry point of the application.
 */
public final class AzmeApplication
        extends EngagementApplication {

    @Override
    public void onApplicationProcessCreate() {

        // Specify your Engagement connection string
        final EngagementConfiguration engagementConfiguration = new EngagementConfiguration();
        engagementConfiguration.setConnectionString("Endpoint=XXXXXXXXXXXXXXXXXXXXXXXXXXXX");
        EngagementAgent.getInstance(this).init(engagementConfiguration);

        final EngagementReachAgent reachAgent = EngagementReachAgent.getInstance(this);
        reachAgent.registerNotifier(new AzmeNotifier(this), Intent.CATEGORY_DEFAULT);
    }
}

// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package com.microsoft.azure.engagement;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.microsoft.azure.engagement.engagement.AzmeTracker;
import com.microsoft.azure.engagement.reach.activity.EngagementPollActivity;

public final class PollEngagementActivity
        extends EngagementPollActivity
        implements OnClickListener {

    private View closeButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AzmeTracker.startActivity(this, "poll_detail");

        closeButton = findViewById(R.id.exit);
        closeButton.setOnClickListener(this);
    }

    @Override
    protected void onAction() {
        super.onAction();
        final Intent intent = new Intent(PollEngagementActivity.this, PollFinishActivity.class);
        startActivity(intent);

        AzmeTracker.sendEvent(this, "submit_poll_answers");
    }

    @Override
    public void onClick(View view) {
        if (view == closeButton) {
            AzmeTracker.sendEvent(this, "cancel_poll_detail");

            finish();
        }
    }
}

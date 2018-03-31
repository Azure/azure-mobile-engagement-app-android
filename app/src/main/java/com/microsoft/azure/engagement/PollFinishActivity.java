// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package com.microsoft.azure.engagement;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.microsoft.azure.engagement.engagement.AzmeTracker;

public final class PollFinishActivity
        extends AbstractAzmeActivity
        implements OnClickListener {

    private View closeButton;

    @Override
    public int getLayoutResourceId() {
        return R.layout.activity_poll_finish;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        closeButton = findViewById(R.id.closeButton);
        closeButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == closeButton) {
            AzmeTracker.sendEvent(this, "close_poll_thanks");
            finish();
        }
    }
}

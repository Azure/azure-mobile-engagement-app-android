// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package com.microsoft.azure.engagement;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.microsoft.azure.engagement.engagement.AzmeTracker;

public final class PollLocalActivity
        extends AppCompatActivity
        implements OnClickListener, OnCheckedChangeListener {

    private RadioGroup firstRadioGroup;
    private RadioGroup secondRadioGroup;
    private View cancelButton;
    private View submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poll_local);

        firstRadioGroup = (RadioGroup) findViewById(R.id.firstRadioGroup);
        secondRadioGroup = (RadioGroup) findViewById(R.id.secondRadioGroup);
        cancelButton = findViewById(R.id.cancelButton);
        submitButton = findViewById(R.id.submitButton);

        firstRadioGroup.setOnCheckedChangeListener(this);
        secondRadioGroup.setOnCheckedChangeListener(this);

        cancelButton.setOnClickListener(this);
        submitButton.setOnClickListener(this);

        AzmeTracker.startActivity(this, "poll_detail");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View view) {
        if (view == cancelButton) {
            AzmeTracker.sendEvent(this, "cancel_poll_detail");

            finish();
        } else if (view == submitButton) {
            final Intent intent = new Intent(PollLocalActivity.this, PollFinishActivity.class);
            startActivity(intent);
            finish();

            AzmeTracker.sendEvent(this, "submit_poll_answers");
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
        submitButton.setEnabled(firstRadioGroup.getCheckedRadioButtonId() != -1 && secondRadioGroup.getCheckedRadioButtonId() != -1);
    }
}

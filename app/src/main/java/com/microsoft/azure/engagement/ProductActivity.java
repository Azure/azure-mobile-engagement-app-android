// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package com.microsoft.azure.engagement;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.microsoft.azure.engagement.engagement.AzmeTracker;

public final class ProductActivity
        extends AbstractAzmeActivity {

    @Override
    public int getLayoutResourceId() {
        return R.layout.activity_product;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final View discountView = findViewById(R.id.discountView);
        discountView.setVisibility(View.GONE);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ProductDiscountActivity.performAnimation(discountView, ObjectAnimator.ofFloat(discountView, "alpha", 0f, 1f), true);
            }
        }, 500);

        AzmeTracker.startActivity(this, "product_coupon");
    }
}

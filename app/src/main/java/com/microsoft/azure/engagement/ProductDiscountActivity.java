// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package com.microsoft.azure.engagement;

import java.text.NumberFormat;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.microsoft.azure.engagement.engagement.AzmeDataPushReceiver;
import com.microsoft.azure.engagement.engagement.AzmeTracker;

import org.json.JSONException;
import org.json.JSONObject;

public final class ProductDiscountActivity
        extends AbstractAzmeActivity
        implements OnClickListener, OnSharedPreferenceChangeListener {

    private static final String TAG = ProductDiscountActivity.class.getSimpleName();
    private final double priceValue = 899;
    private final int defaultDiscountInPercent = 15;
    private boolean discountApplied;
    private SharedPreferences sharedPreferences;
    private TextView prizeTextView;
    private TextView prizeDiscountTextView;
    private TextView discountTextView;
    private View applyDiscountButton;
    private View removeDiscountButton;
    private int discountRateInPercent = defaultDiscountInPercent;

    /**
     * Method that animates a view
     *
     * @param view           The view to animate
     * @param objectAnimator The objectAnimator to play
     * @param isVisible      The visibility of the view at the end of the animation
     */
    public static final void performAnimation(final View view, ObjectAnimator objectAnimator, final boolean isVisible) {
        view.setVisibility(View.VISIBLE);

        objectAnimator.setDuration(300);

        final AnimatorSet animatorSet = new AnimatorSet();

        animatorSet.play(objectAnimator);

        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                view.setVisibility(isVisible ? View.VISIBLE : View.INVISIBLE);
            }
        });
        animatorSet.start();
    }

    @Override
    public int getLayoutResourceId() {
        return R.layout.activity_product_discount;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        prizeTextView = (TextView) findViewById(R.id.prizeTextView);
        prizeDiscountTextView = (TextView) findViewById(R.id.prizeDiscountTextView);
        discountTextView = (TextView) findViewById(R.id.discountTextView);
        applyDiscountButton = findViewById(R.id.applyDiscountButton);
        removeDiscountButton = findViewById(R.id.removeDiscountButton);

        applyDiscountButton.setOnClickListener(this);
        removeDiscountButton.setOnClickListener(this);

        prizeTextView.setText(getPrice(priceValue));

        computePromotional();

        AzmeTracker.startActivity(this, "product_discount");
    }

    @Override
    protected void onStop() {
        super.onStop();
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == applyDiscountButton) {
            discountApplied = true;
        } else if (view == removeDiscountButton) {
            discountApplied = false;
            discountRateInPercent = defaultDiscountInPercent;
            sharedPreferences.edit().remove(AzmeDataPushReceiver.PROMOTION_DATA_PUSH_BODY_PREFERENCE_KEY).apply();
        }

        updatePriceLayout(view == removeDiscountButton);

        AzmeTracker.sendEvent(this, discountApplied ? "apply_discount" : "remove_discount");
    }

    /**
     * Method that updates the ui screen with the new product price
     *
     * @param fromRemoveButton Is true if the removeDiscountButton was clicked on
     */
    private final void updatePriceLayout(boolean fromRemoveButton) {
        if (fromRemoveButton == false) {
            final double finalPrice = priceValue * (100 - discountRateInPercent) / 100;
            prizeDiscountTextView.setText(getPrice(finalPrice));
            discountTextView.setText(getString(R.string.product_discount, discountRateInPercent, getString(R.string.percent_sign)));
        }
        // Start the animation
        ProductDiscountActivity.performAnimation(prizeDiscountTextView, discountApplied ? ObjectAnimator.ofFloat(prizeDiscountTextView, "alpha", 0f, 1f) : ObjectAnimator.ofFloat(prizeDiscountTextView, "alpha", 1f, 0f), discountApplied);
        ProductDiscountActivity.performAnimation(discountTextView, discountApplied ? ObjectAnimator.ofFloat(discountTextView, "alpha", 0f, 1f) : ObjectAnimator.ofFloat(discountTextView, "alpha", 1f, 0f), discountApplied);

        applyDiscountButton.setVisibility(discountApplied ? View.GONE : View.VISIBLE);
        removeDiscountButton.setVisibility(discountApplied ? View.VISIBLE : View.GONE);

        addOrRemoveStrikeTextView(prizeTextView, discountApplied);
    }

    /**
     * Method that adds or removes a strike from a TextView object
     *
     * @param textView The textView to manage
     */
    private final void addOrRemoveStrikeTextView(TextView textView, boolean toAdd) {
        textView.setText(textView.getText().toString(), TextView.BufferType.SPANNABLE);
        final Spannable spannable = (Spannable) textView.getText();

        if (toAdd == true) {
            // Add a StrikethroughSpan style
            final StrikethroughSpan strikethroughSpan = new StrikethroughSpan();
            spannable.setSpan(strikethroughSpan, 0, textView.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            // Remove only StrikethroughSpan style
            final Object spans[] = spannable.getSpans(0, textView.length(), Object.class);
            for (final Object span : spans) {
                if (span instanceof StrikethroughSpan == true) {
                    spannable.removeSpan(span);
                    return;
                }
            }
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String preferenceKey) {
        if (preferenceKey.equals(AzmeDataPushReceiver.PROMOTION_DATA_PUSH_BODY_PREFERENCE_KEY) == true) {
            computePromotional();
        }
    }

    /**
     * Method that updates the product prize from json
     */
    private final void computePromotional() {
        final String body = PreferenceManager.getDefaultSharedPreferences(this).getString(AzmeDataPushReceiver.PROMOTION_DATA_PUSH_BODY_PREFERENCE_KEY, null);
        if (body != null) {
            try {
                final JSONObject jsonObject = new JSONObject(body);
                discountApplied = jsonObject.getBoolean("isDiscountAvailable");
                discountRateInPercent = jsonObject.getInt("discountRateInPercent");
                if (discountRateInPercent < 1) {
                    discountApplied = false;
                } else if (discountRateInPercent > 100) {
                    discountRateInPercent = 100;
                }
                updatePriceLayout(false);
            } catch (JSONException exception) {
                Log.e(ProductDiscountActivity.TAG, "An occurs while parsing the json" + exception);
            }
        }
    }

    /**
     * Formats a specific price
     *
     * @param finalPrice The price to format
     * @return The price formatted
     */
    private final String getPrice(double finalPrice) {
        final NumberFormat formatter = NumberFormat.getCurrencyInstance();
        return formatter.format(finalPrice);
    }
}

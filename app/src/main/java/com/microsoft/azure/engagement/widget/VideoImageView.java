// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package com.microsoft.azure.engagement.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * This class allows to have an ImageView with 16/9 ratio
 */
public final class VideoImageView
        extends ImageView {

    public VideoImageView(Context context) {
        super(context);
    }

    public VideoImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VideoImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        final int height = (9 * parentWidth) / 16;

        setMeasuredDimension(parentWidth, height);
    }
}
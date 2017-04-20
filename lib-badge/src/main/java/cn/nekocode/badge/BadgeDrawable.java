/*
 * Copyright 2017. nekocode (nekocode.cn@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.nekocode.badge;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;

/**
 * @author nekocode (nekocode.cn@gmail.com)
 */
public class BadgeDrawable extends Drawable {
    public static final int TYPE_NUMBER = 1;
    public static final int TYPE_ONLY_ONE_TEXT = 1 << 1;
    public static final int TYPE_WITH_TWO_TEXT = 1 << 2;
    public static final int TYPE_WITH_TWO_TEXT_COMPLEMENTARY = 1 << 3;
    @IntDef({TYPE_NUMBER, TYPE_ONLY_ONE_TEXT, TYPE_WITH_TWO_TEXT, TYPE_WITH_TWO_TEXT_COMPLEMENTARY})
    public @interface  BadgeType {}

    private static class Config {
        private int badgeType = TYPE_NUMBER;
        private int number = 0;
        private String text1 = "";
        private String text2 = "";
        private float textSize = spToPixels(12);
        private int badgeColor = 0xffCC3333;
        private int textColor = 0xffFFFFFF;
        private Typeface typeface = Typeface.DEFAULT_BOLD;
        private float cornerRadius = dipToPixels(2);
        private float paddingLeft = dipToPixels(2);
        private float paddingTop = dipToPixels(2);
        private float paddingRight = dipToPixels(2);
        private float paddingBottom = dipToPixels(2);
        private float paddingCenter = dipToPixels(3);
        private int storkeWidth = (int) dipToPixels(1);
    }
    private Config config;

    private ShapeDrawable backgroundDrawable;
    private ShapeDrawable backgroundDrawableOfText2;
    private ShapeDrawable backgroundDrawableOfText1;
    private int badgeWidth;
    private int badgeHeight;
    private float[] outerR = new float[]{0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f};
    private float[] outerROfText1 = new float[]{0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f};
    private float[] outerROfText2 = new float[]{0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f};
    private Paint paint;
    private Paint.FontMetrics fontMetrics;
    private int text1Width, text2Width;

    public static class Builder {
        private Config config;

        public Builder() {
            config = new Config();
        }

        public Builder type(@BadgeType int type) {
            config.badgeType = type;
            return this;
        }

        public Builder number(int number) {
            config.number = number;
            return this;
        }

        public Builder text1(String text1) {
            config.text1 = text1;
            return this;
        }

        public Builder text2(String text2) {
            config.text2 = text2;
            return this;
        }

        public Builder textSize(float size) {
            config.textSize = size;
            return this;
        }

        public Builder badgeColor(int color) {
            config.badgeColor = color;
            return this;
        }

        public Builder textColor(int color) {
            config.textColor = color;
            return this;
        }

        public Builder typeFace(Typeface typeface) {
            config.typeface = typeface;
            return this;
        }

        public Builder cornerRadius(float radius) {
            config.cornerRadius = radius;
            return this;
        }

        public Builder padding(float l, float t, float r, float b, float c) {
            config.paddingLeft = l;
            config.paddingTop = t;
            config.paddingRight = r;
            config.paddingBottom = b;
            config.paddingCenter = c;
            return this;
        }

        public Builder strokeWidth(int width) {
            config.storkeWidth = width;
            return this;
        }

        public BadgeDrawable build() {
            return new BadgeDrawable(config);
        }
    }

    private BadgeDrawable(Config config) {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setTypeface(config.typeface);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setStyle(Paint.Style.FILL);
        paint.setAlpha(255);

        this.config = config;

        setCornerRadius(config.cornerRadius);
        RoundRectShape shape = new RoundRectShape(outerR, null, null);
        backgroundDrawable = new ShapeDrawable(shape);
        shape = new RoundRectShape(outerROfText1, null, null);
        backgroundDrawableOfText1 = new ShapeDrawable(shape);
        shape = new RoundRectShape(outerROfText2, null, null);
        backgroundDrawableOfText2 = new ShapeDrawable(shape);

        setTextSize(config.textSize);
        measureBadge();
    }

    public void setBadgeType(@BadgeType int type) {
        config.badgeType = type;

        measureBadge();
    }

    public void setNumber(int number) {
        config.number = number;
    }

    public int getNumber() {
        return config.number;
    }

    public void setText1(String text1) {
        config.text1 = text1;
        measureBadge();
    }

    public String getText1() {
        return config.text1;
    }

    public void setText2(String text2) {
        config.text2 = text2;
        measureBadge();
    }

    public String getText2() {
        return config.text2;
    }

    public void setTextSize(float textSize) {
        config.textSize = textSize;
        paint.setTextSize(textSize);
        fontMetrics = paint.getFontMetrics();

        measureBadge();
    }

    public float getTextSize() {
        return config.textSize;
    }

    public void setBadgeColor(int color) {
        config.badgeColor = color;
    }

    public int getBadgeColor() {
        return config.badgeColor;
    }

    public void setTextColor(int color) {
        config.textColor = color;
    }

    public int getTextColor() {
        return config.textColor;
    }

    public void setCornerRadius(float radius) {
        config.cornerRadius = radius;
        outerR[0] = outerR[1] = outerR[2] = outerR[3] =
                outerR[4] = outerR[5] = outerR[6] = outerR[7] = radius;

        outerROfText1[0] = outerROfText1[1] = outerROfText1[6] = outerROfText1[7] = radius;
        outerROfText1[2] = outerROfText1[3] = outerROfText1[4] = outerROfText1[5] = 0f;

        outerROfText2[0] = outerROfText2[1] = outerROfText2[6] = outerROfText2[7] = 0f;
        outerROfText2[2] = outerROfText2[3] = outerROfText2[4] = outerROfText2[5] = radius;
    }

    public void setPadding(float l, float t, float r, float b, float c) {
        config.paddingLeft = l;
        config.paddingTop = t;
        config.paddingRight = r;
        config.paddingBottom = b;
        config.paddingCenter = c;
        measureBadge();
    }

    public void setStrokeWidth(int width) {
        config.storkeWidth = width;
    }

    private void measureBadge() {
        badgeHeight = (int) (config.textSize + config.paddingTop + config.paddingBottom);

        switch (config.badgeType) {
            case TYPE_ONLY_ONE_TEXT:
                text1Width = (int) paint.measureText(config.text1);
                badgeWidth = (int) (text1Width + config.paddingLeft + config.paddingRight);

                setCornerRadius(config.cornerRadius);
                break;

            case TYPE_WITH_TWO_TEXT:
                text1Width = (int) paint.measureText(config.text1);
                text2Width = (int) paint.measureText(config.text2);
                badgeWidth = (int) (text1Width + text2Width +
                        config.paddingLeft + config.paddingRight + config.paddingCenter);

                setCornerRadius(config.cornerRadius);
                break;

            case TYPE_WITH_TWO_TEXT_COMPLEMENTARY:
                text1Width = (int) paint.measureText(config.text1);
                text2Width = (int) paint.measureText(config.text2);
                badgeWidth = (int) (text1Width + text2Width +
                        config.paddingLeft + config.paddingRight + config.paddingCenter);

                setCornerRadius(config.cornerRadius);
                break;

            default:
                badgeWidth = (int) (config.textSize + config.paddingLeft + config.paddingRight);
                setCornerRadius(badgeHeight);
        }

        int boundsWidth = getBounds().width();
        if (boundsWidth > 0) {
            // If the bounds has been set, adjust the badge size
            switch (config.badgeType) {
                case TYPE_ONLY_ONE_TEXT:
                    if(boundsWidth < badgeWidth) {
                        text1Width = (int) (boundsWidth - config.paddingLeft - config.paddingRight);
                        text1Width = text1Width > 0 ? text1Width : 0;

                        badgeWidth = boundsWidth;
                    }
                    break;

                case TYPE_WITH_TWO_TEXT:
                case TYPE_WITH_TWO_TEXT_COMPLEMENTARY:
                    if(boundsWidth < badgeWidth) {
                        if (boundsWidth < (text1Width + config.paddingLeft + config.paddingRight)) {
                            text1Width = (int) (boundsWidth - config.paddingLeft - config.paddingRight);
                            text1Width = text1Width > 0 ? text1Width : 0;
                            text2Width = 0;

                        } else {
                            text2Width = (int) (boundsWidth - text1Width -
                                    config.paddingLeft - config.paddingRight - config.paddingCenter);
                            text2Width = text2Width > 0 ? text2Width : 0;
                        }

                        badgeWidth = boundsWidth;
                    }
                    break;
            }
        }
    }

    @Override
    public void setBounds(int left, int top, int right, int bottom) {
        super.setBounds(left, top, right, bottom);
        measureBadge();
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        Rect bounds = getBounds();

        int marginTopAndBottom = (int) ((bounds.height() - badgeHeight) / 2f);
        int marginLeftAndRight = (int) ((bounds.width() - badgeWidth) / 2f);

        backgroundDrawable.setBounds(
                bounds.left + marginLeftAndRight,
                bounds.top + marginTopAndBottom,
                bounds.right - marginLeftAndRight,
                bounds.bottom - marginTopAndBottom);
        backgroundDrawable.getPaint().setColor(config.badgeColor);
        backgroundDrawable.draw(canvas);

        float textCx = bounds.centerX();
        float textCy = bounds.centerY() - (fontMetrics.bottom + fontMetrics.top) / 2f;

        switch (config.badgeType) {
            case TYPE_ONLY_ONE_TEXT:
                paint.setColor(config.textColor);
                canvas.drawText(
                        cutText(config.text1, text1Width),
                        textCx,
                        textCy,
                        paint);
                break;

            case TYPE_WITH_TWO_TEXT_COMPLEMENTARY:
                paint.setColor(config.textColor);
                canvas.drawText(
                        config.text1,
                        marginLeftAndRight + config.paddingLeft + text1Width / 2f,
                        textCy,
                        paint);

                backgroundDrawableOfText2.setBounds(
                        (int) (bounds.left + marginLeftAndRight + config.paddingLeft +
                                text1Width + config.paddingCenter / 2f),
                        bounds.top + marginTopAndBottom+ config.storkeWidth,
                        bounds.width() - marginLeftAndRight - config.storkeWidth,
                        bounds.bottom - marginTopAndBottom - config.storkeWidth);
                backgroundDrawableOfText2.getPaint().setColor(config.textColor);
                backgroundDrawableOfText2.draw(canvas);

                paint.setColor(config.badgeColor);
                canvas.drawText(
                        cutText(config.text2, text2Width),
                        bounds.width() - marginLeftAndRight - config.paddingRight - text2Width / 2f,
                        textCy,
                        paint);
                break;

            case TYPE_WITH_TWO_TEXT:
                backgroundDrawableOfText1.setBounds(
                        bounds.left + marginLeftAndRight + config.storkeWidth,
                        bounds.top + marginTopAndBottom+ config.storkeWidth,
                        (int) (bounds.left + marginLeftAndRight + config.paddingLeft +
                                text1Width + config.paddingCenter / 2f - config.storkeWidth / 2f),
                        bounds.bottom - marginTopAndBottom - config.storkeWidth);
                backgroundDrawableOfText1.getPaint().setColor(0xffFFFFFF);
                backgroundDrawableOfText1.draw(canvas);

                paint.setColor(config.badgeColor);
                canvas.drawText(
                        config.text1,
                        text1Width / 2f + marginLeftAndRight + config.paddingLeft,
                        textCy,
                        paint);

                backgroundDrawableOfText2.setBounds(
                        (int) (bounds.left + marginLeftAndRight + config.paddingLeft +
                                text1Width + config.paddingCenter / 2f + config.storkeWidth / 2f),
                        bounds.top + marginTopAndBottom + config.storkeWidth,
                        bounds.width() - marginLeftAndRight - config.storkeWidth,
                        bounds.bottom - marginTopAndBottom - config.storkeWidth);
                backgroundDrawableOfText2.getPaint().setColor(0xffFFFFFF);
                backgroundDrawableOfText2.draw(canvas);

                paint.setColor(config.badgeColor);
                canvas.drawText(
                        cutText(config.text2, text2Width),
                        bounds.width() - marginLeftAndRight - config.paddingRight - text2Width / 2f,
                        textCy,
                        paint);
                break;

            default:
                paint.setColor(config.textColor);
                canvas.drawText(
                        cutNumber(config.number, badgeWidth),
                        textCx,
                        textCy,
                        paint);
        }
    }

    @Override
    public int getIntrinsicWidth() {
        return badgeWidth;
    }

    @Override
    public int getIntrinsicHeight() {
        return badgeHeight;
    }

    @Override
    public void setAlpha(int alpha) {
        paint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        paint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    private String cutNumber(int number, int width) {
        String text = String.valueOf(number);
        if (paint.measureText(text) < width)
            return text;

        return "…";
    }

    private String cutText(String text, int width) {
        if (paint.measureText(text) <= width)
            return text;

        String suffix = "...";
        while(paint.measureText(text + suffix) > width) {
            if(text.length() > 0)
                text = text.substring(0, text.length() - 1);

            if(text.length() == 0) {
                suffix = suffix.substring(0, suffix.length() - 1);

                if(suffix.length() == 0) break;
            }
        }

        return text + suffix;
    }

    public SpannableString toSpannable() {
        final SpannableString spanStr = new SpannableString(" ");
        spanStr.setSpan(new ImageSpan(this, ImageSpan.ALIGN_BOTTOM), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        setBounds(0, 0, getIntrinsicWidth(), getIntrinsicHeight());

        return spanStr;
    }

    private static float dipToPixels(float dipValue) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return dipValue * scale + 0.5f;
    }

    private static float spToPixels(float spValue) {
        final float fontScale = Resources.getSystem().getDisplayMetrics().scaledDensity;
        return spValue * fontScale + 0.5f;
    }
}

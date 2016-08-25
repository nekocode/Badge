package cn.nekocode.badge;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;

/**
 * Created by nekocode on 16/5/28.
 */
public class BadgeDrawable extends Drawable {
    public static final int TYPE_NUMBER = 1;
    public static final int TYPE_ONLY_ONE_TEXT = 1 << 1;
    public static final int TYPE_WITH_TWO_TEXT = 1 << 2;
    public static final int TYPE_WITH_TWO_TEXT_COMPLEMENTARY = 1 << 3;
    private static final float DEFAULT_CORNER_RADIUS = dipToPixels(2);
    private static final float DEFAULT_TEXT_SIZE = spToPixels(12);
    private static final int DEFAULT_BADGE_COLOR = 0xffCC3333;
    private static final int DEFAULT_TEXT_COLOR = 0xffFFFFFF;

    private static class Config {
        private int badgeType = TYPE_NUMBER;
        private float cornerRadius;
        private int number = 0;
        private String text1 = "";
        private String text2 = "";
        private int badgeColor = DEFAULT_BADGE_COLOR;
        private int textColor = DEFAULT_TEXT_COLOR;
        private float textSize = DEFAULT_TEXT_SIZE;
        private Typeface typeface = Typeface.DEFAULT_BOLD;

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
    private boolean isAutoSetBounds = false;

    public static class Builder {
        private Config config;

        public Builder() {
            config = new Config();
        }

        public Builder type(int type) {
            config.badgeType = type;
            return this;
        }

        public Builder cornerRadius(float radius) {
            config.cornerRadius = radius;
            return this;
        }

        public Builder typeFace(Typeface typeface) {
            config.typeface = typeface;
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

        public Builder badgeColor(int color) {
            config.badgeColor = color;
            return this;
        }

        public Builder textColor(int color) {
            config.textColor = color;
            return this;
        }

        public Builder textSize(float size) {
            config.textSize = size;
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

    public void setBadgeType(int type) {
        config.badgeType = type;

        measureBadge();
    }

    public void setCornerRadius(float radius) {
        if (config.cornerRadius != radius) {
            config.cornerRadius = radius;
            outerR[0] = outerR[1] = outerR[2] = outerR[3] =
                    outerR[4] = outerR[5] = outerR[6] = outerR[7] = config.cornerRadius;

            outerROfText1[0] = outerROfText1[1] = outerROfText1[6] = outerROfText1[7] = config.cornerRadius;
            outerROfText1[2] = outerROfText1[3] = outerROfText1[4] = outerROfText1[5] = 0f;

            outerROfText2[0] = outerROfText2[1] = outerROfText2[6] = outerROfText2[7] = 0f;
            outerROfText2[2] = outerROfText2[3] = outerROfText2[4] = outerROfText2[5] = config.cornerRadius;
        }
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

    public void setTextSize(float textSize) {
        config.textSize = textSize;
        paint.setTextSize(textSize);
        fontMetrics = paint.getFontMetrics();

        measureBadge();
    }

    public float getTextSize() {
        return config.textSize;
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

    public void setAutoSetBounds(boolean autoSetBounds) {
        this.isAutoSetBounds = autoSetBounds;
    }

    private void measureBadge() {
        switch (config.badgeType) {
            case TYPE_ONLY_ONE_TEXT:
                text1Width = (int) paint.measureText(config.text1);
                badgeHeight = (int) (config.textSize * 1.4f);
                badgeWidth = (int) (text1Width + config.textSize * 0.4f);

                setCornerRadius(DEFAULT_CORNER_RADIUS);
                break;

            case TYPE_WITH_TWO_TEXT:
                text1Width = (int) paint.measureText(config.text1);
                text2Width = (int) paint.measureText(config.text2);
                badgeHeight = (int) (config.textSize * 1.4f);
                badgeWidth = (int) (text1Width + text2Width + config.textSize * 0.7f);

                setCornerRadius(DEFAULT_CORNER_RADIUS);
                break;

            case TYPE_WITH_TWO_TEXT_COMPLEMENTARY:
                text1Width = (int) paint.measureText(config.text1);
                text2Width = (int) paint.measureText(config.text2);
                badgeHeight = (int) (config.textSize * 1.4f);
                badgeWidth = (int) (text1Width + text2Width + config.textSize * 0.6f);

                setCornerRadius(DEFAULT_CORNER_RADIUS);
                break;

            default:
                badgeWidth = badgeHeight = (int) (config.textSize * 1.4f);
                setCornerRadius(badgeHeight);
        }

        if (isAutoSetBounds)
            setBounds(0, 0, badgeWidth, badgeHeight);
    }

    @Override
    public void setBounds(int left, int top, int right, int bottom) {
        super.setBounds(left, top, right, bottom);
        if (isAutoSetBounds)
            return;

        int boundsWidth = right - left;
        switch (config.badgeType) {
            case TYPE_ONLY_ONE_TEXT:
                if(!isAutoSetBounds && boundsWidth < badgeWidth) {
                    text1Width = (int) (boundsWidth - config.textSize * 0.4f);
                    text1Width = text1Width > 0 ? text1Width : 0;

                    badgeWidth = (int) (text1Width + config.textSize * 0.4f);
                }
                break;

            case TYPE_WITH_TWO_TEXT:
                if(!isAutoSetBounds && boundsWidth < badgeWidth) {
                    text2Width = (int) (boundsWidth - text1Width - config.textSize * 0.7f);
                    text2Width = text2Width > 0 ? text2Width : 0;

                    badgeWidth = (int) (text1Width + text2Width + config.textSize * 0.7f);
                }
                break;

            case TYPE_WITH_TWO_TEXT_COMPLEMENTARY:
                if(!isAutoSetBounds && boundsWidth < badgeWidth) {
                    text2Width = (int) (boundsWidth - text1Width - config.textSize * 0.6f);
                    text2Width = text2Width > 0 ? text2Width : 0;

                    badgeWidth = (int) (text1Width + text2Width + config.textSize * 0.6f);
                }
                break;
        }
    }

    @Override
    public void draw(Canvas canvas) {
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
        float textCy = bounds.centerY();
        float textCyOffset = (-fontMetrics.ascent) / 2f - dipToPixels(1);

        switch (config.badgeType) {
            case TYPE_ONLY_ONE_TEXT:
                paint.setColor(config.textColor);
                canvas.drawText(
                        cutText(config.text1, text1Width),
                        textCx,
                        textCy + textCyOffset,
                        paint);
                break;

            case TYPE_WITH_TWO_TEXT_COMPLEMENTARY:
                paint.setColor(config.textColor);
                canvas.drawText(
                        config.text1,
                        text1Width / 2f + marginLeftAndRight + config.textSize * 0.2f,
                        textCy + textCyOffset,
                        paint);

                int padding = (int) (config.textSize * 0.1f);
                backgroundDrawableOfText2.setBounds(
                        bounds.width() - marginLeftAndRight - text2Width - padding * 3,
                        bounds.top + marginTopAndBottom + padding,
                        bounds.width() - marginLeftAndRight - padding,
                        bounds.bottom - marginTopAndBottom - padding);
                backgroundDrawableOfText2.getPaint().setColor(config.textColor);
                backgroundDrawableOfText2.draw(canvas);

                paint.setColor(config.badgeColor);
                canvas.drawText(
                        cutText(config.text2, text2Width),
                        bounds.width() - marginLeftAndRight - text2Width / 2f - config.textSize * 0.2f,
                        textCy + textCyOffset,
                        paint);
                break;

            case TYPE_WITH_TWO_TEXT:
                padding = (int) (config.textSize * 0.1f);
                backgroundDrawableOfText1.setBounds(
                        bounds.left + marginLeftAndRight + padding,
                        bounds.top + marginTopAndBottom + padding,
                        bounds.left + marginLeftAndRight  + text1Width + padding * 3,
                        bounds.bottom - marginTopAndBottom - padding);
                backgroundDrawableOfText1.getPaint().setColor(0xffFFFFFF);
                backgroundDrawableOfText1.draw(canvas);

                paint.setColor(config.badgeColor);
                canvas.drawText(
                        config.text1,
                        text1Width / 2f + marginLeftAndRight + config.textSize * 0.2f,
                        textCy + textCyOffset,
                        paint);

                backgroundDrawableOfText2.setBounds(
//                        bounds.width() - marginLeftAndRight - text2Width - padding * 3,
                        bounds.left + marginLeftAndRight  + text1Width + padding * 4,
                        bounds.top + marginTopAndBottom + padding,
                        bounds.width() - marginLeftAndRight - padding,
                        bounds.bottom - marginTopAndBottom - padding);
                backgroundDrawableOfText2.getPaint().setColor(0xffFFFFFF);
                backgroundDrawableOfText2.draw(canvas);

                paint.setColor(config.badgeColor);
                canvas.drawText(
                        cutText(config.text2, text2Width),
                        bounds.width() - marginLeftAndRight - text2Width / 2f - config.textSize * 0.2f,
                        textCy + textCyOffset,
                        paint);
                break;

            default:
                paint.setColor(config.textColor);
                canvas.drawText(
                        cutNumber(config.number, badgeWidth),
                        textCx,
                        textCy + textCyOffset,
                        paint);
        }
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
        return 0;
    }

    private String cutNumber(int number, int width) {
        String text = String.valueOf(number);
        if (paint.measureText(text) < width)
            return text;

        return "...";
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
        isAutoSetBounds = true;
        setBounds(0, 0, badgeWidth, badgeHeight);

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

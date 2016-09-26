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
 * Created by nekocode on 16/5/28.
 */
public class BadgeDrawable extends Drawable {
    public static final int TYPE_NUMBER = 1;
    public static final int TYPE_ONLY_ONE_TEXT = 1 << 1;
    public static final int TYPE_WITH_TWO_TEXT = 1 << 2;
    public static final int TYPE_WITH_TWO_TEXT_COMPLEMENTARY = 1 << 3;
    @IntDef({TYPE_NUMBER, TYPE_ONLY_ONE_TEXT, TYPE_WITH_TWO_TEXT, TYPE_WITH_TWO_TEXT_COMPLEMENTARY})
    public @interface  BadgeType {}

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
    private Config _CONFIG;

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
    private boolean isNeedAutoSetBounds = false;

    public static class Builder {
        private Config config;

        public Builder() {
            config = new Config();
        }

        public Builder type(@BadgeType int type) {
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

        this._CONFIG = config;

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
        _CONFIG.badgeType = type;

        measureBadge();
    }

    public void setCornerRadius(float radius) {
        if (_CONFIG.cornerRadius != radius) {
            _CONFIG.cornerRadius = radius;
            outerR[0] = outerR[1] = outerR[2] = outerR[3] =
                    outerR[4] = outerR[5] = outerR[6] = outerR[7] = _CONFIG.cornerRadius;

            outerROfText1[0] = outerROfText1[1] = outerROfText1[6] = outerROfText1[7] = _CONFIG.cornerRadius;
            outerROfText1[2] = outerROfText1[3] = outerROfText1[4] = outerROfText1[5] = 0f;

            outerROfText2[0] = outerROfText2[1] = outerROfText2[6] = outerROfText2[7] = 0f;
            outerROfText2[2] = outerROfText2[3] = outerROfText2[4] = outerROfText2[5] = _CONFIG.cornerRadius;
        }
    }

    public void setBadgeColor(int color) {
        _CONFIG.badgeColor = color;
    }

    public int getBadgeColor() {
        return _CONFIG.badgeColor;
    }

    public void setTextColor(int color) {
        _CONFIG.textColor = color;
    }

    public int getTextColor() {
        return _CONFIG.textColor;
    }

    public void setTextSize(float textSize) {
        _CONFIG.textSize = textSize;
        paint.setTextSize(textSize);
        fontMetrics = paint.getFontMetrics();

        measureBadge();
    }

    public float getTextSize() {
        return _CONFIG.textSize;
    }

    public void setNumber(int number) {
        _CONFIG.number = number;
    }

    public int getNumber() {
        return _CONFIG.number;
    }

    public void setText1(String text1) {
        _CONFIG.text1 = text1;
        measureBadge();
    }

    public String getText1() {
        return _CONFIG.text1;
    }

    public void setText2(String text2) {
        _CONFIG.text2 = text2;
        measureBadge();
    }

    public String getText2() {
        return _CONFIG.text2;
    }

    public void setNeedAutoSetBounds(boolean needAutoSetBounds) {
        this.isNeedAutoSetBounds = needAutoSetBounds;
    }

    private void measureBadge() {
        switch (_CONFIG.badgeType) {
            case TYPE_ONLY_ONE_TEXT:
                text1Width = (int) paint.measureText(_CONFIG.text1);
                badgeHeight = (int) (_CONFIG.textSize * 1.4f);
                badgeWidth = (int) (text1Width + _CONFIG.textSize * 0.4f);

                setCornerRadius(DEFAULT_CORNER_RADIUS);
                break;

            case TYPE_WITH_TWO_TEXT:
                text1Width = (int) paint.measureText(_CONFIG.text1);
                text2Width = (int) paint.measureText(_CONFIG.text2);
                badgeHeight = (int) (_CONFIG.textSize * 1.4f);
                badgeWidth = (int) (text1Width + text2Width + _CONFIG.textSize * 0.7f);

                setCornerRadius(DEFAULT_CORNER_RADIUS);
                break;

            case TYPE_WITH_TWO_TEXT_COMPLEMENTARY:
                text1Width = (int) paint.measureText(_CONFIG.text1);
                text2Width = (int) paint.measureText(_CONFIG.text2);
                badgeHeight = (int) (_CONFIG.textSize * 1.4f);
                badgeWidth = (int) (text1Width + text2Width + _CONFIG.textSize * 0.6f);

                setCornerRadius(DEFAULT_CORNER_RADIUS);
                break;

            default:
                badgeWidth = badgeHeight = (int) (_CONFIG.textSize * 1.4f);
                setCornerRadius(badgeHeight);
        }

        if (isNeedAutoSetBounds)
            setBounds(0, 0, badgeWidth, badgeHeight);
    }

    @Override
    public void setBounds(int left, int top, int right, int bottom) {
        super.setBounds(left, top, right, bottom);
        if (isNeedAutoSetBounds)
            return;

        // If any view set the bounds of this drawable
        int boundsWidth = right - left;
        switch (_CONFIG.badgeType) {
            case TYPE_ONLY_ONE_TEXT:
                if(boundsWidth < badgeWidth) {
                    text1Width = (int) (boundsWidth - _CONFIG.textSize * 0.4f);
                    text1Width = text1Width > 0 ? text1Width : 0;

                    badgeWidth = (int) (text1Width + _CONFIG.textSize * 0.4f);
                }
                break;

            case TYPE_WITH_TWO_TEXT:
                if(boundsWidth < badgeWidth) {
                    text2Width = (int) (boundsWidth - text1Width - _CONFIG.textSize * 0.7f);
                    text2Width = text2Width > 0 ? text2Width : 0;

                    badgeWidth = (int) (text1Width + text2Width + _CONFIG.textSize * 0.7f);
                }
                break;

            case TYPE_WITH_TWO_TEXT_COMPLEMENTARY:
                if(boundsWidth < badgeWidth) {
                    text2Width = (int) (boundsWidth - text1Width - _CONFIG.textSize * 0.6f);
                    text2Width = text2Width > 0 ? text2Width : 0;

                    badgeWidth = (int) (text1Width + text2Width + _CONFIG.textSize * 0.6f);
                }
                break;
        }
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
        backgroundDrawable.getPaint().setColor(_CONFIG.badgeColor);
        backgroundDrawable.draw(canvas);

        float textCx = bounds.centerX();
        float textCy = bounds.centerY();
        float textCyOffset = (-fontMetrics.ascent) / 2f - dipToPixels(1);

        switch (_CONFIG.badgeType) {
            case TYPE_ONLY_ONE_TEXT:
                paint.setColor(_CONFIG.textColor);
                canvas.drawText(
                        cutText(_CONFIG.text1, text1Width),
                        textCx,
                        textCy + textCyOffset,
                        paint);
                break;

            case TYPE_WITH_TWO_TEXT_COMPLEMENTARY:
                paint.setColor(_CONFIG.textColor);
                canvas.drawText(
                        _CONFIG.text1,
                        text1Width / 2f + marginLeftAndRight + _CONFIG.textSize * 0.2f,
                        textCy + textCyOffset,
                        paint);

                int padding = (int) (_CONFIG.textSize * 0.1f);
                backgroundDrawableOfText2.setBounds(
                        bounds.width() - marginLeftAndRight - text2Width - padding * 3,
                        bounds.top + marginTopAndBottom + padding,
                        bounds.width() - marginLeftAndRight - padding,
                        bounds.bottom - marginTopAndBottom - padding);
                backgroundDrawableOfText2.getPaint().setColor(_CONFIG.textColor);
                backgroundDrawableOfText2.draw(canvas);

                paint.setColor(_CONFIG.badgeColor);
                canvas.drawText(
                        cutText(_CONFIG.text2, text2Width),
                        bounds.width() - marginLeftAndRight - text2Width / 2f - _CONFIG.textSize * 0.2f,
                        textCy + textCyOffset,
                        paint);
                break;

            case TYPE_WITH_TWO_TEXT:
                padding = (int) (_CONFIG.textSize * 0.1f);
                backgroundDrawableOfText1.setBounds(
                        bounds.left + marginLeftAndRight + padding,
                        bounds.top + marginTopAndBottom + padding,
                        bounds.left + marginLeftAndRight  + text1Width + padding * 3,
                        bounds.bottom - marginTopAndBottom - padding);
                backgroundDrawableOfText1.getPaint().setColor(0xffFFFFFF);
                backgroundDrawableOfText1.draw(canvas);

                paint.setColor(_CONFIG.badgeColor);
                canvas.drawText(
                        _CONFIG.text1,
                        text1Width / 2f + marginLeftAndRight + _CONFIG.textSize * 0.2f,
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

                paint.setColor(_CONFIG.badgeColor);
                canvas.drawText(
                        cutText(_CONFIG.text2, text2Width),
                        bounds.width() - marginLeftAndRight - text2Width / 2f - _CONFIG.textSize * 0.2f,
                        textCy + textCyOffset,
                        paint);
                break;

            default:
                paint.setColor(_CONFIG.textColor);
                canvas.drawText(
                        cutNumber(_CONFIG.number, badgeWidth),
                        textCx,
                        textCy + textCyOffset,
                        paint);
        }
    }

    @Override
    public int getIntrinsicWidth() {
        return isNeedAutoSetBounds ? badgeWidth : -1;
    }

    @Override
    public int getIntrinsicHeight() {
        return isNeedAutoSetBounds ? badgeHeight : -1;
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
        return PixelFormat.UNKNOWN;
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
        isNeedAutoSetBounds = true;
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

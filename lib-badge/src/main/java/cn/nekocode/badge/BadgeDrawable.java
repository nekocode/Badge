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
    private static final float DEFAULT_CORNER_RADIUS = dipToPixels(2);
    private static final float DEFAULT_TEXT_SIZE = spToPixels(12);
    private static final int DEFAULT_BADGE_COLOR = 0xffCC3333;
    private static final int DEFAULT_TEXT_COLOR = 0xffFFFFFF;

    private ShapeDrawable backgroundDrawable;
    private ShapeDrawable backgroundDrawableOfText2;
    private int badgeType;
    private int badgeWidth;
    private int badgeHeight;
    private float cornerRadius;
    private float[] outerR = new float[]{0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f};
    private float[] outerROfText2 = new float[]{0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f};

    private int number = 0;
    private String text1 = "";
    private String text2 = "";
    private int text1Width, text2Width;

    private float textSize;
    private int badgeColor;
    private int textColor;
    private Paint paint;
    private Paint.FontMetrics fontMetrics;

    private boolean willNotSetBounds = false;

    public BadgeDrawable() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setStyle(Paint.Style.FILL);
        paint.setAlpha(255);

        badgeType = TYPE_NUMBER;
        setCornerRadius(DEFAULT_CORNER_RADIUS);
        RoundRectShape shape = new RoundRectShape(outerR, null, null);
        backgroundDrawable = new ShapeDrawable(shape);
        shape = new RoundRectShape(outerROfText2, null, null);
        backgroundDrawableOfText2 = new ShapeDrawable(shape);

        setTextSize(DEFAULT_TEXT_SIZE);
        setBadgeColor(DEFAULT_BADGE_COLOR);
        setTextColor(DEFAULT_TEXT_COLOR);
    }

    public void setBadgeType(int type) {
        badgeType = type;

        measureBadge();
    }

    public void setCornerRadius(float radius) {
        if (cornerRadius != radius) {
            cornerRadius = radius;
            outerR[0] = outerR[1] = outerR[2] = outerR[3] =
                    outerR[4] = outerR[5] = outerR[6] = outerR[7] = cornerRadius;

            outerROfText2[0] = outerROfText2[1] = outerROfText2[6] = outerROfText2[7] = 0f;
            outerROfText2[2] = outerROfText2[3] = outerROfText2[4] = outerROfText2[5] = cornerRadius;
        }
    }

    public void setBadgeColor(int color) {
        badgeColor = color;
    }

    public void setTextColor(int color) {
        textColor = color;
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
        paint.setTextSize(textSize);
        fontMetrics = paint.getFontMetrics();

        measureBadge();
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public void setText1(String text1) {
        this.text1 = text1;
        measureBadge();
    }

    public void setText2(String text2) {
        this.text2 = text2;
        measureBadge();
    }

    private void measureBadge() {
        switch (badgeType) {
            case TYPE_ONLY_ONE_TEXT:
                badgeHeight = (int) (textSize * 1.4f);
                badgeWidth = (int) (paint.measureText(text1) + textSize * 0.4f);
                setCornerRadius(DEFAULT_CORNER_RADIUS);
                break;

            case TYPE_WITH_TWO_TEXT:
                badgeHeight = (int) (textSize * 1.4f);
                text1Width = (int) paint.measureText(text1);
                text2Width = (int) paint.measureText(text2);
                badgeWidth = (int) (text1Width + text2Width + textSize * 0.6f);
                setCornerRadius(DEFAULT_CORNER_RADIUS);
                break;

            default:
                badgeWidth = badgeHeight = (int) (textSize * 1.4f);
                setCornerRadius(badgeHeight);
        }

        if (willNotSetBounds)
            setBounds(0, 0, badgeWidth, badgeHeight);
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
        backgroundDrawable.getPaint().setColor(badgeColor);
        backgroundDrawable.draw(canvas);

        float textCx = bounds.centerX();
        float textCy = bounds.centerY();
        float textCyOffset = (-fontMetrics.ascent) / 2f - dipToPixels(1);

        switch (badgeType) {
            case TYPE_ONLY_ONE_TEXT:
                paint.setColor(textColor);
                canvas.drawText(
                        text1,
                        textCx,
                        textCy + textCyOffset,
                        paint);
                break;

            case TYPE_WITH_TWO_TEXT:
                paint.setColor(textColor);
                canvas.drawText(
                        text1,
                        text1Width / 2f + marginLeftAndRight + textSize * 0.2f,
                        textCy + textCyOffset,
                        paint);

                int padding = (int) (textSize * 0.1f);
                backgroundDrawableOfText2.setBounds(
                        bounds.width() - marginLeftAndRight - text2Width - padding * 3,
                        bounds.top + marginTopAndBottom + padding,
                        bounds.width() - marginLeftAndRight - padding,
                        bounds.bottom - marginTopAndBottom - padding);
                backgroundDrawableOfText2.getPaint().setColor(0xffFFFFFF);
                backgroundDrawableOfText2.draw(canvas);

                paint.setColor(badgeColor);
                canvas.drawText(
                        text2,
                        bounds.width() - marginLeftAndRight - text2Width / 2f - textSize * 0.2f,
                        textCy + textCyOffset,
                        paint);
                break;

            default:
                paint.setColor(textColor);
                canvas.drawText(
                        cutNumber(number, badgeWidth),
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

    public SpannableString toSpannable() {
        final SpannableString spanStr = new SpannableString(" ");
        spanStr.setSpan(new ImageSpan(this, ImageSpan.ALIGN_BOTTOM), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        willNotSetBounds = true;
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

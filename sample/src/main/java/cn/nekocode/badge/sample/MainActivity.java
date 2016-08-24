package cn.nekocode.badge.sample;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import cn.nekocode.badge.BadgeDrawable;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final TextView textView = (TextView) findViewById(R.id.tvHelloWorld);
        final ImageView imageView = (ImageView) findViewById(R.id.imageView);

        final BadgeDrawable drawable =
                new BadgeDrawable.Builder()
                        .type(BadgeDrawable.TYPE_NUMBER)
                        .number(9)
                        .build();

        final BadgeDrawable drawable2 =
                new BadgeDrawable.Builder()
                        .type(BadgeDrawable.TYPE_ONLY_ONE_TEXT)
                        .badgeColor(0xff336699)
                        .text1("VIP")
                        .typeFace(Typeface.createFromAsset(getAssets(),"fonts/code-bold.otf"))
                        .build();

        final BadgeDrawable drawable3 =
                new BadgeDrawable.Builder()
                        .type(BadgeDrawable.TYPE_WITH_TWO_TEXT)
                        .badgeColor(0xffCC9933)
                        .text1("LEVEL")
                        .text2("10")
                        .build();

        final BadgeDrawable drawable4 =
                new BadgeDrawable.Builder()
                        .type(BadgeDrawable.TYPE_NUMBER)
                        .number(999)
                        .badgeColor(0xff666666)
                        .textColor(0xffFFFF00)
                        .build();

        SpannableString spannableString =
                new SpannableString(TextUtils.concat(
                        "TextView ",
                        drawable.toSpannable(),
                        " ",
                        drawable2.toSpannable(),
                        " ",
                        drawable3.toSpannable(),
                        " ",
                        drawable4.toSpannable()
                ));

        if (textView != null) {
            textView.setText(spannableString);
        }

        if (imageView != null) {
            final BadgeDrawable drawable5 =
                    new BadgeDrawable.Builder()
                            .type(BadgeDrawable.TYPE_WITH_TWO_TEXT)
                            .badgeColor(0xff336633)
                            .textSize(sp2px(this, 14))
                            .text1("Author")
                            .text2("Nekocode")
                            .typeFace(Typeface.createFromAsset(getAssets(),"fonts/code-bold.otf"))
                            .build();

            imageView.setImageDrawable(drawable5);
        }
    }

    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }
}

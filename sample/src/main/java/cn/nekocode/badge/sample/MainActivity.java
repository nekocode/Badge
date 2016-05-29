package cn.nekocode.badge.sample;

import android.content.Context;
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
        final TextView textView = (TextView)findViewById(R.id.tvHelloWorld);
        final ImageView imageView = (ImageView)findViewById(R.id.imageView);

        final BadgeDrawable drawable = new BadgeDrawable();
        drawable.setBadgeType(BadgeDrawable.TYPE_NUMBER);
        drawable.setNumber(9);

        final BadgeDrawable drawable2 = new BadgeDrawable();
        drawable2.setBadgeType(BadgeDrawable.TYPE_ONLY_ONE_TEXT);
        drawable2.setBadgeColor(0xff336699);
        drawable2.setText1("VIP");

        final BadgeDrawable drawable3 = new BadgeDrawable();
        drawable3.setBadgeType(BadgeDrawable.TYPE_WITH_TWO_TEXT);
        drawable3.setBadgeColor(0xffCC9933);
        drawable3.setText1("LEVEL");
        drawable3.setText2("10");

        final BadgeDrawable drawable4 = new BadgeDrawable();
        drawable4.setBadgeType(BadgeDrawable.TYPE_NUMBER);
        drawable4.setNumber(999);
        drawable4.setBadgeColor(0xff666666);
        drawable4.setTextColor(0xffFFFF00);

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

        if(textView != null) {
            textView.setText(spannableString);
        }

        if(imageView != null) {
            final BadgeDrawable drawable5 = new BadgeDrawable();
            drawable5.setBadgeType(BadgeDrawable.TYPE_WITH_TWO_TEXT);
            drawable5.setTextSize(sp2px(this, 14));
            drawable5.setBadgeColor(0xff336633);
            drawable5.setText1("Author");
            drawable5.setText2("Nekocode");

            imageView.setImageDrawable(drawable5);
        }
    }

    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }
}

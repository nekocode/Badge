# Badge
[![Apache 2.0 License](https://img.shields.io/badge/license-Apache%202.0-blue.svg?style=flat)](http://www.apache.org/licenses/LICENSE-2.0.html) [![Release](https://img.shields.io/github/release/nekocode/Badge.svg?label=Jitpack)](https://jitpack.io/#nekocode/Badge)

### Preview
![preview](art/preview.png)

### Using with gradle
- Add the JitPack repository to your root build.gradle:
```gradle
repositories {
    maven { url "https://jitpack.io" }
}
```

- Add the dependency to your sub build.gradle:
```gradle
dependencies {
    compile 'com.github.nekocode:Badge:{lastest-version}'
}
```

### Usage

The above screenshot's example:

```java
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
```

The above `drawable4` BadgeDrawable has set a number that too large to show, in this case, it will be replaced with **"..."** for showing. And then you can use `toSpannable()` for converting the drawable to SpannableString without setting its drawing bounds. It has already took internal measure.

```java
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

textView.setText(spannableString);
```

You can also use the badge drawable for ImageView:

```
final BadgeDrawable drawable5 = new BadgeDrawable();
drawable5.setBadgeType(BadgeDrawable.TYPE_WITH_TWO_TEXT);
drawable5.setTextSize(sp2px(this, 14));
drawable5.setBadgeColor(0xff336633);
drawable5.setText1("Author");
drawable5.setText2("Nekocode");
```

package com.smartroad.util;

import android.content.res.ColorStateList;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Configures one "item_stat" card's label, icon, and icon-circle color. Shared by the Home
 * and Profile screens so their statistics cards always stay visually identical.
 *
 * Every card's icon circle inflates the same {@code bg_stat_circle} drawable resource, whose
 * GradientDrawable constant state is shared across instances by default - mutate() first so
 * tinting one card's circle can't bleed into the others.
 */
public final class StatCardHelper {

    private StatCardHelper() { }

    public static void configure(TextView label, ImageView icon, int labelRes, int iconRes, int tintColor) {
        label.setText(labelRes);
        icon.setImageResource(iconRes);
        icon.getBackground().mutate();
        icon.setBackgroundTintList(ColorStateList.valueOf(tintColor));
    }
}

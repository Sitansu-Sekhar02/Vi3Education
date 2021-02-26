package com.vi3.vi3education.adapter;

import androidx.cardview.widget.CardView;

public interface CardAdapter {
    public static final int MAX_ELEVATION_FACTOR = 8;

    float getBaseElevation();

    CardView getCardViewAt(int i);

    int getCount();
}

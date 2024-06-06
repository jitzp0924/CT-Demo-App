package com.jitendract.jitdemo;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class HomeScreen2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen2);

        // Find the outer LinearLayout containing the ImageView items
        LinearLayout imageLinearLayout = findViewById(R.id.image_linear_layout);

        // Calculate padding dynamically based on the screen width
        int screenWidth = getScreenWidth();
        int imageWidthDp = 50;
        int totalImageWidthPx = dpToPx(imageWidthDp * 4); // for 4 images in a row
        int totalPaddingPx = screenWidth - totalImageWidthPx; // remaining width to be used as padding
        int paddingPerItemPx = totalPaddingPx / 8; // total gaps are 4 for 4 items: 3 between items and 1 on each end

        // Set padding for each inner LinearLayout
        setPaddingBetweenChildren(imageLinearLayout, paddingPerItemPx);
    }

    // Method to get the screen width
    private int getScreenWidth() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    // Method to convert dp to pixels
    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    // Method to set padding for each inner LinearLayout
    private void setPaddingBetweenChildren(ViewGroup parentLayout, int paddingPx) {
        int childCount = parentLayout.getChildCount();

        for (int i = 0; i < childCount; i++) {
            ViewGroup row = (ViewGroup) parentLayout.getChildAt(i);

            // Iterate over each inner LinearLayout
            for (int j = 0; j < row.getChildCount(); j++) {
                LinearLayout innerChild = (LinearLayout) row.getChildAt(j);
                innerChild.setPadding(paddingPx, paddingPx, paddingPx, paddingPx);
            }
        }
    }
}

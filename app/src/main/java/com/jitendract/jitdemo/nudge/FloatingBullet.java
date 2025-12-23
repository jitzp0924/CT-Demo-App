package com.jitendract.jitdemo.nudge;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.CountDownTimer;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.*;
import android.widget.*;
import androidx.cardview.widget.CardView;
import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.jitendract.jitdemo.R;

import org.json.JSONObject;

import java.util.Locale;


public class FloatingBullet {

    private static View bulletView;
    private static CountDownTimer countDownTimer;
    private static View bulletContent;

    public static void show(Activity activity, JSONObject config) {
        try {
            boolean enabled = config.optBoolean("enabled", false);
            if (!enabled) return;

            FrameLayout rootView = activity.findViewById(android.R.id.content);
            if (bulletView != null) {
                rootView.removeView(bulletView);
                bulletView = null;
            }

            LayoutInflater inflater = LayoutInflater.from(activity);
            bulletView = inflater.inflate(R.layout.bullet_nudge, null);

            MaterialCardView bulletContainer = bulletView.findViewById(R.id.bullet_container);
            ImageView icon = bulletView.findViewById(R.id.bullet_icon);
            TextView title = bulletView.findViewById(R.id.bullet_title);
            TextView timer = bulletView.findViewById(R.id.bullet_timer);
            bulletContent = bulletView.findViewById(R.id.bullet_content);

            // Set content from config
            String titleText = config.optString("title", "Promo");
            long expiryTime = config.optLong("time", 0);
            String redirectUrl = config.optString("redirect_url", null);
            String bgColor = config.optString("bg_colour", "#FFFFFF");
            String iconUrl = config.optString("icon_url", null);
            String titleColor = config.optString("title_color", "#000000");
            String timerColor = config.optString("timer_color", "#888888");

            title.setText(titleText);
            title.setTextColor(Color.parseColor(titleColor));
            timer.setTextColor(Color.parseColor(timerColor));
            bulletContainer.setCardBackgroundColor(Color.parseColor(bgColor));

            if (iconUrl != null && !iconUrl.isEmpty()) {
                Glide.with(activity.getApplicationContext()).load(iconUrl).into(icon);
            }

            // Countdown logic
            long millisLeft = (expiryTime * 1000L) - System.currentTimeMillis();
            if (millisLeft > 0) {
                countDownTimer = new CountDownTimer(millisLeft, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        long seconds = millisUntilFinished / 1000;
                        long h = seconds / 3600;
                        long m = (seconds % 3600) / 60;
                        long s = seconds % 60;
                        timer.setText(String.format(Locale.getDefault(), "%02d:%02d:%02d", h, m, s));
                    }

                    @Override
                    public void onFinish() {
                        dismiss(rootView);
                    }
                };
                countDownTimer.start();
            } else {
                return;
            }

            // Redirect URL
            if (redirectUrl != null && !redirectUrl.isEmpty()) {
                bulletContainer.setOnClickListener(v -> {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(redirectUrl));
                    activity.startActivity(intent);
                });
            }

            // Position bottom right
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            params.gravity = Gravity.BOTTOM | Gravity.END;
            params.setMargins(dpToPx(activity, 20), dpToPx(activity, 20), dpToPx(activity, 20), dpToPx(activity, 30));
            rootView.addView(bulletView, params);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void dismiss(ViewGroup parent) {
        if (countDownTimer != null) countDownTimer.cancel();
        if (bulletView != null) parent.removeView(bulletView);
        bulletView = null;
    }

    public static void collapse() {
        if (bulletContent != null && bulletContent.getVisibility() != View.GONE) {
            bulletContent.setVisibility(View.GONE);
        }
    }

    public static void expand() {
        if (bulletContent != null && bulletContent.getVisibility() != View.VISIBLE) {
            bulletContent.setVisibility(View.VISIBLE);
        }
    }

    private static int dpToPx(Context context, int dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

}

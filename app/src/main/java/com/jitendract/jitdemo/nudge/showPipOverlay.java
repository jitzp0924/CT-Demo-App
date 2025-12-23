package com.jitendract.jitdemo.nudge;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.VideoView;

import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import com.bumptech.glide.Glide;
import com.jitendract.jitdemo.R;

import org.json.JSONException;
import org.json.JSONObject;

public class showPipOverlay implements DefaultLifecycleObserver {

    private static showPipOverlay instance;

    private View pipView;
    private VideoView pipVideo;
    private FrameLayout rootView;
    private Activity hostActivity;

    private boolean isDragging = false;

    private showPipOverlay() {}

    public static showPipOverlay getInstance() {
        if (instance == null) {
            synchronized (showPipOverlay.class) {
                if (instance == null) {
                    instance = new showPipOverlay();
                }
            }
        }
        return instance;
    }

    public void show(Activity activity, JSONObject config) {
        try {
            boolean enabled = config.optBoolean("enabled", false);
            if (!enabled) return;

            this.hostActivity = activity;
            this.rootView = activity.findViewById(android.R.id.content);

            if (pipView != null) {
                rootView.removeView(pipView);
                pipView = null;
            }

            String type = config.optString("type", "video");
            String assetUrl = config.getString("asset_url");
            String position = config.optString("position", "bottom_right");
            String redirectUrl = config.optString("redirect_url", null);
            boolean draggable = config.optBoolean("draggable", false);

            LayoutInflater inflater = LayoutInflater.from(activity);
            pipView = inflater.inflate(R.layout.pipvideo, null);

            FrameLayout pipContainer = pipView.findViewById(R.id.pip_container);
            pipVideo = pipView.findViewById(R.id.pip_video);
            ImageView pipImage = pipView.findViewById(R.id.pip_image);
            ImageButton closeBtn = pipView.findViewById(R.id.btn_close);

            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    dpToPx(activity, 120), dpToPx(activity, 200));
            params.setMargins(dpToPx(activity, 20), dpToPx(activity, 20), dpToPx(activity, 20), dpToPx(activity, 20));

            if ("bottom_left".equals(position)) {
                params.gravity = Gravity.BOTTOM | Gravity.START;
            } else {
                params.gravity = Gravity.BOTTOM | Gravity.END;
            }

            rootView.addView(pipView, params);

            // ✅ Fade-in animation
            pipView.setAlpha(0f);
            pipView.animate()
                    .alpha(1f)
                    .setDuration(400)
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .start();

            // Hide both media views initially
            pipVideo.setVisibility(View.GONE);
            pipImage.setVisibility(View.GONE);

            // Show based on type
            switch (type) {
                case "image":
                case "gif":
                    pipImage.setVisibility(View.VISIBLE);
                    Glide.with(activity.getApplicationContext())
                            .load(assetUrl)
                            .into(pipImage);
                    break;

                case "video":
                default:
                    pipVideo.setVisibility(View.VISIBLE);
                    pipVideo.setVideoURI(Uri.parse(assetUrl));
                    pipVideo.setOnPreparedListener(mp -> pipVideo.start());
                    // ✅ Loop video instead of dismissing
                    pipVideo.setOnCompletionListener(mp -> {
                        pipVideo.seekTo(0);
                        pipVideo.start();
                    });
                    break;
            }

            closeBtn.setOnClickListener(v -> dismiss());

            // ✅ Make draggable if allowed
            if (draggable) {
                pipView.setClickable(true);

                pipView.setOnTouchListener(new View.OnTouchListener() {

                    private int lastX, lastY;
                    private int deltaX, deltaY;
                    private final int screenWidth;
                    private final int screenHeight;

                    {
                        DisplayMetrics metrics = new DisplayMetrics();
                        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
                        screenWidth = metrics.widthPixels;
                        screenHeight = metrics.heightPixels;
                    }

                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) pipView.getLayoutParams();

                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                lastX = (int) event.getRawX();
                                lastY = (int) event.getRawY();
                                isDragging = false;
                                return true;

                            case MotionEvent.ACTION_MOVE:
                                int newX = (int) event.getRawX();
                                int newY = (int) event.getRawY();

                                deltaX = newX - lastX;
                                deltaY = newY - lastY;

                                int updatedLeft = layoutParams.leftMargin + deltaX;
                                int updatedTop = layoutParams.topMargin + deltaY;

                                // ✅ Clamp within screen bounds
                                layoutParams.leftMargin = Math.max(0, Math.min(screenWidth - pipView.getWidth(), updatedLeft));
                                layoutParams.topMargin = Math.max(0, Math.min(screenHeight - pipView.getHeight(), updatedTop));
                                layoutParams.rightMargin = 0;
                                layoutParams.bottomMargin = 0;
                                layoutParams.gravity = -1;

                                pipView.setLayoutParams(layoutParams);

                                lastX = newX;
                                lastY = newY;
                                isDragging = true;
                                return true;

                            case MotionEvent.ACTION_UP:
                                // ✅ Snap to nearest horizontal edge
                                int middle = screenWidth / 2;
                                if (layoutParams.leftMargin < middle) {
                                    layoutParams.leftMargin = dpToPx(activity, 16); // Snap to left
                                } else {
                                    layoutParams.leftMargin = screenWidth - pipView.getWidth() - dpToPx(activity, 16); // Snap to right
                                }

                                layoutParams.gravity = -1;
                                pipView.setLayoutParams(layoutParams);

                                // Reset drag flag shortly after

                                return true;
                        }
                        return false;
                    }
                });
            }

            // ✅ Click-to-redirect only if not dragging
            if (redirectUrl != null && !redirectUrl.isEmpty()) {
                pipContainer.setOnClickListener(v -> {
                    if (!isDragging) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(redirectUrl));
                        activity.startActivity(intent);
                    }
                });
            }

            // Attach lifecycle observer
            if (activity instanceof LifecycleOwner) {
                ((LifecycleOwner) activity).getLifecycle().addObserver(this);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void dismiss() {
        if (pipView != null && rootView != null) {
            rootView.removeView(pipView);
            Log.e("PIPNudge", "PIP dismiss() called.");
        }
        pipView = null;
        pipVideo = null;
        rootView = null;
        hostActivity = null;
    }

    @Override
    public void onPause(LifecycleOwner owner) {
        if (pipVideo != null && pipVideo.isPlaying()) {
            pipVideo.pause();
        }
    }

    @Override
    public void onResume(LifecycleOwner owner) {
        if (pipVideo != null && !pipVideo.isPlaying()) {
            pipVideo.start();
        }
    }

    @Override
    public void onDestroy(LifecycleOwner owner) {
        dismiss();
        instance = null;
    }

    private int dpToPx(Context context, int dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }
}



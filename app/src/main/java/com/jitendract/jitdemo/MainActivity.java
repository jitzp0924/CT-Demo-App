package com.jitendract.jitdemo;

import static java.lang.Boolean.TRUE;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.clevertap.android.sdk.CleverTapAPI;
import com.clevertap.android.sdk.pushnotification.CTPushNotificationListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements CTPushNotificationListener {

    private TextInputLayout idLayout, phoneLayout;
    private MaterialButton loginbtn;
    private CheckBox conditions, commsUpdate;
    private TextInputEditText identity, phone;
    private CleverTapAPI ct;
    private CleverTapUtils cleverTapUtils;

    private String userID, phoneNum;
    private boolean locationPermissionGranted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bindViews();

        ct = CleverTapAPI.getDefaultInstance(getApplicationContext());
        cleverTapUtils = CleverTapUtils.getInstance();

        if (ct != null) {
            ct.fetchVariables();
            applyPEConfig();
        }

        setupInputWatchers();
        setupSignInLink();
        setupLoginButton();
        setupLocationPermission();
        setupPushChannels();
    }

    // ── View binding ──────────────────────────────────────────────────────────

    private void bindViews() {
        idLayout    = findViewById(R.id.userTextL);
        phoneLayout = findViewById(R.id.numTextL);
        identity    = findViewById(R.id.identity);
        phone       = findViewById(R.id.phone);
        loginbtn    = findViewById(R.id.loginbtn);
        conditions  = findViewById(R.id.conditions);
        commsUpdate = findViewById(R.id.commsUpdate);
    }

    // ── PE Variable config ────────────────────────────────────────────────────

    private void applyPEConfig() {
        try {
            String appType = String.valueOf(ct.getVariableValue("appType"));
            Map<String, Object> loginScreen =
                    (Map<String, Object>) ct.getVariableValue("LoginScreen");

            // Top banner — only made VISIBLE inside the Glide success callback so
            // the 140dp slot never appears when there is no valid image to show.
            if (loginScreen != null) {
                Boolean   bannerActive = (Boolean) loginScreen.get("Active");
                String    bannerUrl    = (String)  loginScreen.get("Top BannerImage");
                ImageView topBanner    = findViewById(R.id.loginTopBanner);

                boolean hasValidUrl = bannerUrl != null
                        && !bannerUrl.isEmpty()
                        && !bannerUrl.equals("null"); // PE default is the string "null"

                if (Boolean.TRUE.equals(bannerActive) && hasValidUrl && topBanner != null) {
                    Glide.with(this)
                            .load(bannerUrl)
                            .skipMemoryCache(true)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .centerCrop()
                            .listener(new com.bumptech.glide.request.RequestListener<android.graphics.drawable.Drawable>() {
                                @Override
                                public boolean onResourceReady(android.graphics.drawable.Drawable r,
                                        Object model,
                                        com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable> t,
                                        com.bumptech.glide.load.DataSource s, boolean first) {
                                    topBanner.setVisibility(View.VISIBLE); // show only when image is ready
                                    return false;
                                }
                                @Override
                                public boolean onLoadFailed(com.bumptech.glide.load.engine.GlideException e,
                                        Object model,
                                        com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable> t,
                                        boolean first) {
                                    topBanner.setVisibility(View.GONE); // keep hidden on failure
                                    return false;
                                }
                            })
                            .into(topBanner);
                }
            }

            // Rapido variant — tint the login button yellow
            if ("rapido".equals(appType)) {
                loginbtn.setBackgroundTintList(
                        ColorStateList.valueOf(getResources().getColor(R.color.rapido_light_primary, getTheme())));
                loginbtn.setTextColor(getResources().getColor(R.color.black, getTheme()));
                TextView mainText = findViewById(R.id.mainText);
                if (mainText != null) {
                    mainText.setTextColor(getResources().getColor(R.color.rapido_light_primary, getTheme()));
                }
            }

        } catch (Exception e) {
            Log.e("MainActivity", "PE config error", e);
        }
    }

    // ── Input watchers ────────────────────────────────────────────────────────

    private void setupInputWatchers() {
        identity.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {}
            @Override public void afterTextChanged(Editable e) {}
            @Override public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                userID = s.toString().trim();
                if (!userID.isEmpty()) idLayout.setError(null);
            }
        });

        phone.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {}
            @Override public void onTextChanged(CharSequence s, int i, int i1, int i2) {}
            @Override public void afterTextChanged(Editable e) {
                if (e.length() == 10) {
                    phoneLayout.setError(null);
                    phoneNum = e.toString();
                } else {
                    phoneLayout.setError("Must be exactly 10 digits");
                }
            }
        });
    }

    // ── Navigation links ──────────────────────────────────────────────────────

    private void setupSignInLink() {
        TextView signInText = findViewById(R.id.signInText);
        if (signInText != null) {
            signInText.setOnClickListener(v ->
                    startActivity(new Intent(this, SignInPage.class)));
        }
    }

    // ── Login button ──────────────────────────────────────────────────────────

    private void setupLoginButton() {
        loginbtn.setOnClickListener(v -> {
            if (!validateInputs()) return;

            HashMap<String, Object> loginMap = new HashMap<>();
            loginMap.put("Identity", userID);
            loginMap.put("AppType", userID.contains("rapido") ? "rapido" : "basic");
            loginMap.put("Phone", phoneNum);
            loginMap.put("MSG-whatsapp", commsUpdate.isChecked());
            loginMap.put("T&C", conditions.isChecked());
            cleverTapUtils.login(loginMap, true);

            HashMap<String, Object> evtMap = new HashMap<>();
            evtMap.put("Screen", "Login");
            evtMap.put("Status", "Success");
            evtMap.put("ID", userID);
            cleverTapUtils.raiseEvent("Logged In", evtMap);

            getSharedPreferences("Login", MODE_PRIVATE).edit()
                    .putBoolean("LoggedIn", true)
                    .putString("Identity", userID)
                    .putString("Phone", phoneNum)
                    .putBoolean("locationPermissionGranted", locationPermissionGranted)
                    .apply();

            Intent di = HomeRouter.getHomeIntent(getApplicationContext());
            di.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(di);
        });
    }

    private boolean validateInputs() {
        boolean valid = true;
        if (userID == null || userID.isEmpty()) {
            idLayout.setError("Please enter your User ID");
            valid = false;
        }
        if (phoneNum == null) {
            phoneLayout.setError("Please enter a valid 10-digit number");
            valid = false;
        }
        if (!conditions.isChecked()) {
            Toast.makeText(this, "Please accept Terms & Conditions", Toast.LENGTH_SHORT).show();
            valid = false;
        }
        return valid;
    }

    // ── Permissions & Push ────────────────────────────────────────────────────

    private void setupLocationPermission() {
        ActivityResultLauncher<String[]> launcher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                result -> {
                    Boolean fine = result.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false);
                    locationPermissionGranted = Boolean.TRUE.equals(fine);
                });
        launcher.launch(new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        });
    }

    private void setupPushChannels() {
        if (Build.VERSION.SDK_INT >= 32 && ct != null) {
            ct.setCTPushNotificationListener(this);
        } else {
            CleverTapAPI.createNotificationChannel(getApplicationContext(),
                    "r2d2", "r2d2", "r2d2", NotificationManager.IMPORTANCE_MAX, true);
            CleverTapAPI.createNotificationChannel(getApplicationContext(),
                    "jiosound", "jiosound", "For Jio",
                    NotificationManager.IMPORTANCE_MAX, true, "jiosound.mp3");
        }
    }

    // ── CTPushNotificationListener ────────────────────────────────────────────

    @Override
    public void onNotificationClickedPayloadReceived(HashMap<String, Object> payload) {
        Log.d("MainActivity", "Push clicked: " + payload);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && ct != null) {
            ct.pushNotificationClickedEvent(intent.getExtras());
        }
    }
}

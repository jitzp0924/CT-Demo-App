package com.jitendract.jitdemo;

import android.Manifest;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.clevertap.android.sdk.CTInboxListener;
import com.clevertap.android.sdk.CTInboxStyleConfig;
import com.clevertap.android.sdk.CleverTapAPI;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.jitendract.jitdemo.CarouselModel.SliderData;
import com.jitendract.jitdemo.Reco.RecommendationCard;
import com.jitendract.jitdemo.homescreen.HomeSectionAdapter;
import com.jitendract.jitdemo.homescreen.HomeSection;
import com.jitendract.jitdemo.homescreen.PayBillItem;
import com.jitendract.jitdemo.homescreen.QuickLinkItem;
import com.jitendract.jitdemo.nudge.FloatingBullet;
import com.jitendract.jitdemo.nudge.showPipOverlay;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HomeScreenV2 extends AppCompatActivity implements CTInboxListener {

    private static final String TAG = "HomeScreenV2";
    private static final String PREF_LOGIN = "Login";
    private static final String PREF_APP = "MyPrefs";
    private static final String KEY_CUSTOM_INBOX = "custom_inbox_enabled";

    private CleverTapAPI ct;
    private CleverTapUtils cleverTapUtils;
    private String phoneNum, userId;
    private HashMap<String, Object> homeScreenEvt;
    private SharedPreferences loginPrefs, appPrefs;
    private FusedLocationProviderClient locationClient;
    private ActivityResultLauncher<String> permissionLauncher;

    private ImageView profileIcon, callIcon, searchIcon, notificationIcon, logoutIcon;
    private RecyclerView sectionsRecycler;
    private TextView greetingText;

    // -------------------------------------------------------------------------
    // Lifecycle
    // -------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen_v2);

        loginPrefs = getSharedPreferences(PREF_LOGIN, Context.MODE_PRIVATE);
        appPrefs = getSharedPreferences(PREF_APP, Context.MODE_PRIVATE);
        phoneNum = loginPrefs.getString("Phone", "NA");
        userId = loginPrefs.getString("Identity", "default");
        Log.e("HomeScreenV2","rendered from HomeScreen Version 2");

        ct = CleverTapAPI.getDefaultInstance(getApplicationContext());
        if (ct != null) {
            ct.setCTNotificationInboxListener(this);
            ct.initializeInbox();
            ct.fetchVariables();
        }

        cleverTapUtils = CleverTapUtils.getInstance();
        cleverTapUtils.initAdditionalInstance(this, "65R-654-5Z6Z", "456-256");

        HashMap<String, Object> identityMap = new HashMap<>();
        identityMap.put("Identity", userId);
        cleverTapUtils.login(identityMap, true);

        homeScreenEvt = new HashMap<>();
        homeScreenEvt.put("Phone", phoneNum);
        homeScreenEvt.put("UserId", userId);
        homeScreenEvt.put("Source", "MyJio");
        homeScreenEvt.put("Screen", "HomeScreen");

        bindViews();
        buildAndRenderSections();
        setupToolbarListeners();

        cleverTapUtils.raiseEvent("Home Screen", homeScreenEvt);

        locationClient = LocationServices.getFusedLocationProviderClient(this);
        setupPermissionLauncher();
        askNotificationPermission();
        fetchLocation();
        setupNudges();
    }

    // -------------------------------------------------------------------------
    // View binding
    // -------------------------------------------------------------------------

    private void bindViews() {
        sectionsRecycler = findViewById(R.id.home_sections_recycler);
        profileIcon = findViewById(R.id.v2_profile_icon);
        callIcon = findViewById(R.id.v2_call_icon);
        searchIcon = findViewById(R.id.v2_search_icon);
        notificationIcon = findViewById(R.id.v2_notification_icon);
        logoutIcon = findViewById(R.id.v2_logout_icon);
        greetingText = findViewById(R.id.v2_greeting);

        // Personalise greeting with the user's stored identity
        if (greetingText != null && userId != null && !userId.equals("default")) {
            greetingText.setText("Hello, " + userId + "!");
        }
    }

    // -------------------------------------------------------------------------
    // Section rendering
    // -------------------------------------------------------------------------

    private void buildAndRenderSections() {
        if (ct == null) return;

        Map<String, Object> homeScreen;
        try {
            homeScreen = (Map<String, Object>) ct.getVariableValue("HomeScreen");
            if (homeScreen == null) return;
        } catch (Exception e) {
            Log.e(TAG, "Failed to read HomeScreen variable", e);
            return;
        }

        applyToolbarConfig(homeScreen);
        loadProfileIcon(homeScreen);

        Map<String, SectionConfig> layoutConfig = parseHomeLayout(homeScreen);
        List<HomeSection> sections = buildSections(homeScreen, layoutConfig);

        HomeSectionAdapter adapter = new HomeSectionAdapter(this, sections,
                new HomeSectionAdapter.SectionInteractionListener() {
                    @Override
                    public void onQuickLinkClicked(QuickLinkItem item) {
                        handleQuickLinkClick(item);
                    }
                    @Override
                    public void onPayBillClicked(PayBillItem item) {
                        handlePayBillClick(item);
                    }
                });

        sectionsRecycler.setLayoutManager(new LinearLayoutManager(this));
        sectionsRecycler.setAdapter(adapter);
    }

    // -------------------------------------------------------------------------
    // HomeLayout parsing
    // -------------------------------------------------------------------------

    /**
     * Parses HomeScreen."Home Layout" into a keyed map of SectionConfig.
     *
     * Two formats are supported, matching the PEVariables hierarchy:
     *
     * 1. Map<String, Integer> — local default from PEVariables (order values only).
     *    Keys: "Recommended For You", "Quick Links", "Bill Pay", "Bottom Carousel"
     *
     * 2. JSON String — backend override with full control (order + visible + maxItems).
     *    Keys: "RecommendedForYou", "QuickLinks", "PayBills", "Carousel"
     *    Shape: { "QuickLinks": { "order": 2, "visible": true, "maxItems": 8 }, ... }
     */
    private Map<String, SectionConfig> parseHomeLayout(Map<String, Object> homeScreen) {
        try {
            Object raw = homeScreen.get("Home Layout");

            if (raw instanceof String) {
                // Backend override: full JSON string with order + visible + maxItems
                JSONObject layout = new JSONObject((String) raw);
                Map<String, SectionConfig> result = new LinkedHashMap<>();
                Iterator<String> keys = layout.keys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    JSONObject cfg = layout.getJSONObject(key);
                    result.put(key, new SectionConfig(
                            cfg.optInt("order", 99),
                            cfg.optBoolean("visible", true),
                            cfg.optInt("maxItems", -1)
                    ));
                }
                return result;

            } else if (raw instanceof Map) {
                // Local default from PEVariables: Map<String, Integer> order values only
                Map<String, Object> layoutMap = (Map<String, Object>) raw;
                Map<String, SectionConfig> result = new LinkedHashMap<>();
                for (Map.Entry<String, Object> entry : layoutMap.entrySet()) {
                    String normalizedKey = normalizeSectionKey(entry.getKey());
                    result.put(normalizedKey, new SectionConfig(toInt(entry.getValue(), 99), true, -1));
                }
                return result;
            }

        } catch (Exception e) {
            Log.e(TAG, "Home Layout parse error — using defaults", e);
        }
        return defaultLayout();
    }

    /**
     * Maps PEVariables section names (human-readable) to the switch-case keys
     * used in buildSections(). Only needed when reading from the local Map default.
     */
    private static String normalizeSectionKey(String peKey) {
        switch (peKey) {
            case "Recommended For You": return "RecommendedForYou";
            case "Quick Links":         return "QuickLinks";
            case "Bill Pay":            return "PayBills";
            case "Bottom Carousel":     return "Carousel";
            default:                    return peKey;
        }
    }

    private static Map<String, SectionConfig> defaultLayout() {
        Map<String, SectionConfig> d = new LinkedHashMap<>();
        d.put("RecommendedForYou", new SectionConfig(1, true, -1));
        d.put("QuickLinks",        new SectionConfig(2, true, 8));
        d.put("PayBills",          new SectionConfig(3, true, -1));
        d.put("Carousel",          new SectionConfig(4, true, -1));
        return d;
    }

    // -------------------------------------------------------------------------
    // Section builders
    // -------------------------------------------------------------------------

    private List<HomeSection> buildSections(Map<String, Object> homeScreen,
                                            Map<String, SectionConfig> layoutConfig) {
        List<HomeSection> sections = new ArrayList<>();
        for (Map.Entry<String, SectionConfig> entry : layoutConfig.entrySet()) {
            if (!entry.getValue().visible) continue;
            HomeSection section = null;
            switch (entry.getKey()) {
                case "RecommendedForYou": section = buildRecoSection(homeScreen, entry.getValue()); break;
                case "QuickLinks":        section = buildQuickLinksSection(homeScreen, entry.getValue()); break;
                case "PayBills":          section = buildPayBillsSection(homeScreen, entry.getValue()); break;
                case "Carousel":          section = buildCarouselSection(homeScreen, entry.getValue()); break;
            }
            if (section != null) sections.add(section);
        }
        Collections.sort(sections, (a, b) -> a.order - b.order);
        return sections;
    }

    private HomeSection buildRecoSection(Map<String, Object> homeScreen, SectionConfig cfg) {
        try {
            Map<String, Object> recoForU = (Map<String, Object>) homeScreen.get("RecommendedForU");
            if (recoForU == null) return null;
            int maxCards = toInt(recoForU.get("MaxCard"), 3);
            List<RecommendationCard> cards = new ArrayList<>();
            for (int i = 1; i <= maxCards; i++) {
                Map<String, Object> card = (Map<String, Object>) recoForU.get("Recommendation Card " + i);
                if (card == null) continue;
                cards.add(new RecommendationCard(
                        str(card.get("Card Text")),
                        str(card.get("Card Button")),
                        str(card.get("deeplink")),
                        str(card.get("icon"))
                ));
            }
            return new HomeSection("RecommendedForYou", cfg.order, true,
                    HomeSection.SectionType.RECO, new HomeSectionAdapter.RecoPayload(cards));
        } catch (Exception e) {
            Log.e(TAG, "Reco section error", e);
            return null;
        }
    }

    private HomeSection buildQuickLinksSection(Map<String, Object> homeScreen, SectionConfig cfg) {
        try {
            Map<String, Object> qlRaw = (Map<String, Object>) homeScreen.get("QuickLinks");
            if (qlRaw == null) return null;

            Map<String, Integer> orderMap = toIntMap(qlRaw);
            int maxItems = cfg.maxItems > 0 ? cfg.maxItems : orderMap.size();

            List<Map.Entry<String, Integer>> sorted = new ArrayList<>(orderMap.entrySet());
            Collections.sort(sorted, (a, b) -> a.getValue() - b.getValue());

            List<QuickLinkItem> items = new ArrayList<>();
            for (int i = 0; i < Math.min(sorted.size(), maxItems); i++) {
                QuickLinkItem item = QUICK_LINK_CATALOG.get(sorted.get(i).getKey());
                if (item != null) items.add(item);
            }

            // columns: 4 per row. PE can extend this via maxItems (e.g. maxItems=4 → 1 row, =8 → 2 rows).
            return new HomeSection("QuickLinks", cfg.order, true,
                    HomeSection.SectionType.QUICK_LINKS,
                    new HomeSectionAdapter.QuickLinksPayload(items, 4));
        } catch (Exception e) {
            Log.e(TAG, "QuickLinks section error", e);
            return null;
        }
    }

    private HomeSection buildPayBillsSection(Map<String, Object> homeScreen, SectionConfig cfg) {
        try {
            Map<String, Object> pbRaw = (Map<String, Object>) homeScreen.get("Pay Bills");
            if (pbRaw == null) return null;

            Map<String, Integer> orderMap = toIntMap(pbRaw);
            List<Map.Entry<String, Integer>> sorted = new ArrayList<>(orderMap.entrySet());
            Collections.sort(sorted, (a, b) -> a.getValue() - b.getValue());

            List<PayBillItem> items = new ArrayList<>();
            for (Map.Entry<String, Integer> entry : sorted) {
                PayBillItem item = PAY_BILL_CATALOG.get(entry.getKey());
                if (item != null) items.add(item);
            }

            return new HomeSection("PayBills", cfg.order, true,
                    HomeSection.SectionType.PAY_BILLS,
                    new HomeSectionAdapter.PayBillsPayload(items));
        } catch (Exception e) {
            Log.e(TAG, "PayBills section error", e);
            return null;
        }
    }

    private HomeSection buildCarouselSection(Map<String, Object> homeScreen, SectionConfig cfg) {
        try {
            Map<String, Object> sliderData = (Map<String, Object>) homeScreen.get("Bottom Carousel");
            if (sliderData == null) return null;

            int maxCards = toInt(sliderData.get("Max Cards"), 0);
            ArrayList<SliderData> slides = new ArrayList<>();
            HashMap<String, Object> slidermap = new HashMap<>();
            slidermap.put("eventName", "Bottom Carousel");

            for (int i = 1; i <= maxCards; i++) {
                try {
                    JSONObject card = new JSONObject((String) sliderData.get("Card" + i));
                    String imgUrl = card.getString("imageUrl");
                    slides.add(new SliderData(imgUrl));
                    slidermap.put(String.valueOf(i), imgUrl);
                } catch (Exception e) {
                    Log.e(TAG, "Carousel card " + i + " parse error", e);
                }
            }

            return new HomeSection("Carousel", cfg.order, true,
                    HomeSection.SectionType.CAROUSEL,
                    new HomeSectionAdapter.CarouselPayload(slides, slidermap, homeScreenEvt));
        } catch (Exception e) {
            Log.e(TAG, "Carousel section error", e);
            return null;
        }
    }

    // -------------------------------------------------------------------------
    // Toolbar helpers
    // -------------------------------------------------------------------------

    private void applyToolbarConfig(Map<String, Object> homeScreen) {
        Object searchFlag = homeScreen.get("SearchIcon");
        Object callFlagVal = homeScreen.get("Call");
        if (Boolean.TRUE.equals(searchFlag)) searchIcon.setVisibility(View.VISIBLE);
        if (Boolean.TRUE.equals(callFlagVal)) callIcon.setVisibility(View.VISIBLE);
    }

    private void loadProfileIcon(Map<String, Object> homeScreen) {
        try {
            JSONObject profileCfg = new JSONObject(str(homeScreen.get("Profile_Icon")));
            if (profileCfg.optString("enabled", "false").equalsIgnoreCase("true")) {
                String url = profileCfg.optString("profile_url", "");
                if (!url.isEmpty()) {
                    Glide.with(this).load(url).circleCrop().into(profileIcon);
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "Profile icon parse error", e);
        }
    }

    private void setupToolbarListeners() {
        profileIcon.setOnClickListener(v -> startActivity(new Intent(this, Settings.class)));

        logoutIcon.setOnClickListener(v -> {
            loginPrefs.edit().remove("LoggedIn").remove("Identity").apply();
            Intent i = new Intent(this, MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        });

        notificationIcon.setOnClickListener(v -> {
            if (appPrefs.getBoolean(KEY_CUSTOM_INBOX, false)) {
                startActivity(new Intent(this, CustomInboxActivity.class));
            } else if (cleverTapUtils.getDefaultInstance() != null) {
                ArrayList<String> tabs = new ArrayList<>();
                tabs.add("Promotions");
                tabs.add("Offers");
                CTInboxStyleConfig styleConfig = new CTInboxStyleConfig();
                styleConfig.setTabs(tabs);
                styleConfig.setNavBarTitle("MY INBOX");
                cleverTapUtils.getDefaultInstance().showAppInbox(styleConfig);
            }
        });
    }

    // -------------------------------------------------------------------------
    // Click handlers
    // -------------------------------------------------------------------------

    private void handleQuickLinkClick(QuickLinkItem item) {
        cleverTapUtils.raiseEvent("Quick Links", makeEvt(item.label), true);
        Intent intent;
        switch (item.destination) {
            case "webview": intent = new Intent(this, webview.class); break;
            case "fdHome":  intent = FDRouter.getFDIntent(this); break;
            default:        intent = new Intent(this, FDHome.class); break;
        }
        startActivity(intent);
    }

    private void handlePayBillClick(PayBillItem item) {
        cleverTapUtils.raiseEvent("Pay Bills", makeEvt(item.label), true);
        Intent intent = new Intent(this, MultiTaskPayBills.class);
        intent.putExtra("category", item.id);
        startActivity(intent);
    }

    private HashMap<String, Object> makeEvt(String label) {
        HashMap<String, Object> evt = new HashMap<>();
        evt.put("Action", "Click");
        evt.put("Label", label);
        evt.put("Screen", "Home Screen");
        return evt;
    }

    // -------------------------------------------------------------------------
    // Permissions, location, nudges
    // -------------------------------------------------------------------------

    private void setupPermissionLauncher() {
        permissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    HashMap<String, Object> evt = new HashMap<>();
                    evt.put("Type", "PUSH");
                    evt.put("Status", isGranted ? "Accepted" : "Denied");
                    cleverTapUtils.raiseEvent("Permission Request", evt);
                    if (isGranted) {
                        CleverTapAPI.createNotificationChannel(
                                getApplicationContext(), "JitDemo", "JitDemo", "JitDemo",
                                NotificationManager.IMPORTANCE_MAX, true);
                    }
                });
    }

    private void askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                        != PackageManager.PERMISSION_GRANTED) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
        }
    }

    private void fetchLocation() {
        if (!loginPrefs.getBoolean("locationPermissionGranted", true) || ct == null) return;
        try {
            locationClient.getLastLocation().addOnCompleteListener(this, task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    ct.setLocation(task.getResult());
                }
            });
        } catch (SecurityException e) {
            Log.e(TAG, "Location fetch error", e);
        }
    }

    private void setupNudges() {
        if (ct == null) return;
        try {
            String pipVideo = (String) ct.getVariableValue("PIPVideo");
            String nudgeBullet = (String) ct.getVariableValue("BulletNudge");
            if (pipVideo != null) showPipOverlay.getInstance().show(this, new JSONObject(pipVideo));
            if (nudgeBullet != null) FloatingBullet.show(this, new JSONObject(nudgeBullet));
        } catch (JSONException e) {
            Log.e(TAG, "Nudge config parse error", e);
        }
    }

    // -------------------------------------------------------------------------
    // CTInboxListener
    // -------------------------------------------------------------------------

    @Override public void inboxDidInitialize() {}
    @Override public void inboxMessagesDidUpdate() {}

    // -------------------------------------------------------------------------
    // Static helpers
    // -------------------------------------------------------------------------

    private static int toInt(Object val, int defaultVal) {
        return val instanceof Number ? ((Number) val).intValue() : defaultVal;
    }

    private static String str(Object val) {
        return val != null ? String.valueOf(val) : "";
    }

    private static Map<String, Integer> toIntMap(Map<String, Object> map) {
        Map<String, Integer> result = new HashMap<>();
        for (Map.Entry<String, Object> e : map.entrySet()) {
            if (e.getValue() instanceof Number) {
                result.put(e.getKey(), ((Number) e.getValue()).intValue());
            }
        }
        return result;
    }

    // -------------------------------------------------------------------------
    // Item catalogs — maps PE keys to display metadata
    // -------------------------------------------------------------------------

    private static final Map<String, QuickLinkItem> QUICK_LINK_CATALOG;
    private static final Map<String, PayBillItem> PAY_BILL_CATALOG;

    static {
        QUICK_LINK_CATALOG = new LinkedHashMap<>();
        QUICK_LINK_CATALOG.put("FDRD",         new QuickLinkItem("FDRD",         "FD/RD",          R.drawable.fd,               "webview"));
        QUICK_LINK_CATALOG.put("Investments",  new QuickLinkItem("Investments",  "Investments",    R.drawable.investment,       "fdHome"));
        QUICK_LINK_CATALOG.put("CreditCard",   new QuickLinkItem("CreditCard",   "Credit Card",    R.drawable.creditcard,       "fdHome"));
        QUICK_LINK_CATALOG.put("Loans",        new QuickLinkItem("Loans",        "Loans",          R.drawable.loans,            "fdHome"));
        QUICK_LINK_CATALOG.put("SendMoney",    new QuickLinkItem("SendMoney",    "Send Money",     R.drawable.sendmoney,        "fdHome"));
        QUICK_LINK_CATALOG.put("Services",     new QuickLinkItem("Services",     "Services",       R.drawable.services,         "fdHome"));
        QUICK_LINK_CATALOG.put("FixedReturns", new QuickLinkItem("FixedReturns", "Fixed Returns",  R.drawable.guaranteedreturns,"fdHome"));
        QUICK_LINK_CATALOG.put("BillPay",      new QuickLinkItem("BillPay",      "Bill Pay",       R.drawable.billpay,          "fdHome"));

        PAY_BILL_CATALOG = new LinkedHashMap<>();
        PAY_BILL_CATALOG.put("Fastag",     new PayBillItem("Fastag",     "FASTag",     R.drawable.fastag));
        PAY_BILL_CATALOG.put("Recharge",   new PayBillItem("Recharge",   "Recharge",   R.drawable.recharge));
        PAY_BILL_CATALOG.put("Electricity",new PayBillItem("Electricity","Electricity",R.drawable.electricity));
        PAY_BILL_CATALOG.put("PipedGas",   new PayBillItem("PipedGas",   "Piped Gas",  R.drawable.pipeline));
        PAY_BILL_CATALOG.put("DTH",        new PayBillItem("DTH",        "DTH",        R.drawable.dth));
        PAY_BILL_CATALOG.put("Broadband",  new PayBillItem("Broadband",  "Broadband",  R.drawable.broadband));
    }

    // -------------------------------------------------------------------------
    // Inner: SectionConfig
    // -------------------------------------------------------------------------

    private static final class SectionConfig {
        final int order;
        final boolean visible;
        final int maxItems;

        SectionConfig(int order, boolean visible, int maxItems) {
            this.order = order;
            this.visible = visible;
            this.maxItems = maxItems;
        }
    }
}

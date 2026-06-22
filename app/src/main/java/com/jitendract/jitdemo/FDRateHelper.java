package com.jitendract.jitdemo;

import android.content.Context;
import android.util.Log;
import com.clevertap.android.sdk.CleverTapAPI;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.Map;

/**
 * Single source of truth for FD interest rates across the entire FD journey.
 *
 * Rates are read from the "interest_rates" key inside the FDJourney PE Map.
 * Expected JSON format — a tiers array ordered by ascending max_months:
 *
 *   [
 *     {"max_months": 6,   "rate": 4.50},
 *     {"max_months": 18,  "rate": 5.00},
 *     {"max_months": 36,  "rate": 5.25},
 *     {"max_months": 999, "rate": 6.00}
 *   ]
 *
 * For a given tenure, the first tier whose max_months >= tenure is applied.
 * Falls back to hardcoded tiers if the PE variable is absent or unparseable.
 */
public final class FDRateHelper {

    private static final String TAG = "FDRateHelper";

    private FDRateHelper() {}

    // ── Public API ─────────────────────────────────────────────────────────────

    /**
     * Returns the applicable annual interest rate (%) for the given tenure.
     * Always returns a valid rate — never throws.
     */
    public static float getRate(Context context, int tenureMonths) {
        CleverTapAPI ct = CleverTapAPI.getDefaultInstance(context);
        if (ct != null) {
            try {
                Map<String, Object> fdJourney =
                        (Map<String, Object>) ct.getVariableValue("FDJourney");
                if (fdJourney != null) {
                    Object raw = fdJourney.get("interest_rates");
                    if (raw instanceof String) {
                        float rate = parseFromJson((String) raw, tenureMonths);
                        Log.d(TAG, "PE rate for " + tenureMonths + "m → " + rate + "%");
                        return rate;
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error reading interest_rates from PE — using defaults", e);
            }
        }
        return fallback(tenureMonths);
    }

    // ── Parsing ────────────────────────────────────────────────────────────────

    private static float parseFromJson(String json, int tenureMonths) throws Exception {
        JSONArray tiers = new JSONArray(json);
        for (int i = 0; i < tiers.length(); i++) {
            JSONObject tier = tiers.getJSONObject(i);
            if (tenureMonths <= tier.getInt("max_months")) {
                return (float) tier.getDouble("rate");
            }
        }
        // Tenure exceeds all defined tiers — use the last one as the ceiling
        return (float) tiers.getJSONObject(tiers.length() - 1).getDouble("rate");
    }

    // ── Fallback ───────────────────────────────────────────────────────────────

    /** Hardcoded fallback — mirrors the default tiers in PEVariables. */
    static float fallback(int tenureMonths) {
        if (tenureMonths <= 6)  return 4.50f;
        if (tenureMonths <= 18) return 5.00f;
        if (tenureMonths <= 36) return 5.25f;
        return 6.00f;
    }
}

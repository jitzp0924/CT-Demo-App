package com.jitendract.jitdemo;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.clevertap.android.sdk.CleverTapAPI;
import com.clevertap.android.sdk.CleverTapInstanceConfig;

import java.util.HashMap;

/**
 * Utility class to manage CleverTap SDK instances and provide a simplified interface for common operations.
 * This class supports both a default instance and an optional secondary instance.
 */
public class CleverTapUtils {

    private static CleverTapUtils sInstance;
    private final CleverTapAPI mDefaultInstance;
    private CleverTapAPI mAdditionalInstance;

    // --- Singleton Management ---

    /**
     * Private constructor to initialize the default CleverTap instance.
     */
    private CleverTapUtils(@NonNull Context applicationContext) {
        this.mDefaultInstance = CleverTapAPI.getDefaultInstance(applicationContext);
    }

    /**
     * Initializes the CleverTapUtils singleton. Should be called from the Application class.
     *
     * @param applicationContext The application context.
     */
    public static synchronized void init(@NonNull Context applicationContext) {
        if (sInstance == null) {
            sInstance = new CleverTapUtils(applicationContext.getApplicationContext());
        }
    }

    /**
     * Returns the singleton instance of CleverTapUtils.
     *
     * @return The CleverTapUtils instance.
     * @throws IllegalStateException If init() has not been called.
     */
    @NonNull
    public static CleverTapUtils getInstance() {
        if (sInstance == null) {
            throw new IllegalStateException("CleverTapUtils is not initialized. Call init(Context) first.");
        }
        return sInstance;
    }

    // --- Configuration & Setup ---

    /**
     * Initializes an additional CleverTap instance with the provided credentials.
     *
     * @param context   The context.
     * @param accountId The CleverTap Account ID.
     * @param token     The CleverTap Account Token.
     */
    public void initAdditionalInstance(@NonNull Context context, @NonNull String accountId, @NonNull String token) {
        CleverTapInstanceConfig config = CleverTapInstanceConfig.createInstance(context, accountId, token);
        this.mAdditionalInstance = CleverTapAPI.instanceWithConfig(context, config);
    }

    /**
     * Factory method to create a new CleverTap instance without storing it in the utility.
     *
     * @param context   The context.
     * @param accountId The CleverTap Account ID.
     * @param token     The CleverTap Account Token.
     * @return A configured CleverTapAPI instance.
     */
    @NonNull
    public CleverTapAPI createSecondaryInstance(@NonNull Context context, @NonNull String accountId, @NonNull String token) {
        CleverTapInstanceConfig config = CleverTapInstanceConfig.createInstance(context, accountId, token);
        return CleverTapAPI.instanceWithConfig(context, config);
    }

    // --- Getters ---

    /**
     * Returns the default CleverTap instance.
     */
    @Nullable
    public CleverTapAPI getDefaultInstance() {
        return mDefaultInstance;
    }

    /**
     * Returns the additional CleverTap instance, if initialized.
     */
    @Nullable
    public CleverTapAPI getAdditionalInstance() {
        return mAdditionalInstance;
    }

    // --- Tracking Methods ---

    /**
     * Logs a user login event.
     *
     * @param loginMap   The user profile properties.
     * @param sendToBoth Whether to send the event to both default and additional instances.
     */
    public void login(@NonNull HashMap<String, Object> loginMap, boolean sendToBoth) {
        if (mDefaultInstance != null) {
            mDefaultInstance.onUserLogin(loginMap);
        }

        if (sendToBoth && mAdditionalInstance != null) {
            mAdditionalInstance.onUserLogin(loginMap);
        }
    }

    /**
     * Logs a user login event to the default instance.
     *
     * @param loginMap The user profile properties.
     */
    public void login(@NonNull HashMap<String, Object> loginMap) {
        login(loginMap, false);
    }

    /**
     * Raises a custom event.
     *
     * @param eventName  The name of the event.
     * @param eventMap   The event properties.
     * @param sendToBoth Whether to send the event to both default and additional instances.
     */
    public void raiseEvent(@NonNull String eventName, @Nullable HashMap<String, Object> eventMap, boolean sendToBoth) {
        if (mDefaultInstance != null) {
            mDefaultInstance.pushEvent(eventName, eventMap);
        }

        if (sendToBoth && mAdditionalInstance != null) {
            mAdditionalInstance.pushEvent(eventName, eventMap);
        }
    }

    /**
     * Raises a custom event to the default instance.
     *
     * @param eventName The name of the event.
     * @param eventMap  The event properties.
     */
    public void raiseEvent(@NonNull String eventName, @Nullable HashMap<String, Object> eventMap) {
        raiseEvent(eventName, eventMap, false);
    }
}

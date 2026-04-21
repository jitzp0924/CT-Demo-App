package com.jitendract.jitdemo.homescreen;

public class QuickLinkItem {

    public final String id;
    public final String label;
    public final int iconRes;
    public final String destination; // "webview" or "fdHome"

    public QuickLinkItem(String id, String label, int iconRes, String destination) {
        this.id = id;
        this.label = label;
        this.iconRes = iconRes;
        this.destination = destination;
    }
}

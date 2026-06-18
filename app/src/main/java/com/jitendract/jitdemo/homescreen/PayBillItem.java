package com.jitendract.jitdemo.homescreen;

public class PayBillItem {

    public final String id;     // matches PE key, passed as "category" intent extra
    public final String label;
    public final int iconRes;

    public PayBillItem(String id, String label, int iconRes) {
        this.id = id;
        this.label = label;
        this.iconRes = iconRes;
    }
}

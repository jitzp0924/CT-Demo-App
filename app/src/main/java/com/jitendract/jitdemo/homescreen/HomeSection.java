package com.jitendract.jitdemo.homescreen;

public class HomeSection {

    public enum SectionType { RECO, QUICK_LINKS, PAY_BILLS, CAROUSEL }

    public final String sectionName;
    public final int order;
    public final boolean visible;
    public final SectionType type;
    public final Object data;

    public HomeSection(String sectionName, int order, boolean visible, SectionType type, Object data) {
        this.sectionName = sectionName;
        this.order = order;
        this.visible = visible;
        this.type = type;
        this.data = data;
    }
}

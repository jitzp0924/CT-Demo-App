package com.jitendract.jitdemo.Reco;

public class RecommendationCard {

    String title;
    String button;
    String deeplink;
    String icon;

    public RecommendationCard(String title, String button, String deeplink, String icon) {
        this.title = title;
        this.button = button;
        this.deeplink = deeplink;
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public String getButton() {
        return button;
    }

    public String getDeeplink() {
        return deeplink;
    }

    public String getIcon() {
        return icon;
    }
}
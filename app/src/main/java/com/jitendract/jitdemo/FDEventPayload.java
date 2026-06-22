package com.jitendract.jitdemo;

/**
 * Carries FD form data for CleverTap event properties.
 * Fields left null (not filled by the user) are excluded from the CT event payload.
 */
public final class FDEventPayload {

    public final String depositAmount;
    public final String tenure;
    public final String rateOfInterest;
    public final String nomineeName;
    public final String maturityAmount;

    private FDEventPayload(Builder b) {
        this.depositAmount  = b.depositAmount;
        this.tenure         = b.tenure;
        this.rateOfInterest = b.rateOfInterest;
        this.nomineeName    = nullIfEmpty(b.nomineeName);
        this.maturityAmount = b.maturityAmount;
    }

    private static String nullIfEmpty(String s) {
        return (s != null && !s.trim().isEmpty()) ? s.trim() : null;
    }

    public static final class Builder {
        String depositAmount, tenure, rateOfInterest, nomineeName, maturityAmount;

        public Builder amount(String v)   { depositAmount  = v; return this; }
        public Builder tenure(String v)   { tenure         = v; return this; }
        public Builder rate(String v)     { rateOfInterest = v; return this; }
        public Builder nominee(String v)  { nomineeName    = v; return this; }
        public Builder maturity(String v) { maturityAmount = v; return this; }

        public FDEventPayload build() { return new FDEventPayload(this); }
    }
}

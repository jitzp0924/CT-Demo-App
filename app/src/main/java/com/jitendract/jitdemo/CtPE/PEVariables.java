package com.jitendract.jitdemo.CtPE;

import android.content.Context;

import com.clevertap.android.sdk.variables.annotations.Variable;
import com.jitendract.jitdemo.R;

import java.util.HashMap;
import java.util.Map;

public class PEVariables {

    @Variable
    public Map<String, Object> LoginScreen;

    @Variable
    public Map<String,Object> HomeScreen;

    public Map<String,Object> RecommendedForU,J4U;
    public Map<String,Object> RecoCard1;
    public Map<String,Object> RecoCard2;
    public Map<String,Object> RecoCard3;
    public Map<String,Object> HomeSlider;
    public Map<String,Integer>quicklinks,PayBill,rapidoLink;
    @Variable
    public String appType;





    public PEVariables(Context applicationContext) {

        appType = "basic";

        LoginScreen = new HashMap<>();
        LoginScreen.put("Top BannerImage","null");
        LoginScreen.put("Active",false);

        RecoCard1 = new HashMap<>();
        RecoCard1.put("Card Text", applicationContext.getString(R.string.health_insurance_text));
        RecoCard1.put("Card Button", applicationContext.getString(R.string.buy_now));
        RecoCard1.put("deeplink", "https://jits-clever.github.io/TestWeb/");
        RecoCard1.put("icon","null");


        RecoCard2 = new HashMap<>();
        RecoCard2.put("Card Text", applicationContext.getString(R.string.peronal_insurance_text));
        RecoCard2.put("Card Button", applicationContext.getString(R.string.buy_now));
        RecoCard2.put("deeplink", "https://jits-clever.github.io/TestWeb/");
        RecoCard2.put("icon","null");

        RecoCard3 = new HashMap<>();
        RecoCard3.put("Card Text", applicationContext.getString(R.string.credit_card_text));
        RecoCard3.put("Card Button", applicationContext.getString(R.string.buy_now));
        RecoCard3.put("deeplink", "https://jits-clever.github.io/TestWeb/");
        RecoCard3.put("icon","null");

        RecommendedForU = new HashMap<>();
        RecommendedForU.put("MaxCard",3);
        RecommendedForU.put("Recommendation Card 1",RecoCard1);
        RecommendedForU.put("Recommendation Card 2",RecoCard2);
        RecommendedForU.put("Recommendation Card 3",RecoCard3);

        HomeSlider = new HashMap<>();
        HomeSlider.put("Max Cards",3);
        HomeSlider.put("Card1","{\"imageUrl\":\"https://shopifyctjt.s3.ap-south-1.amazonaws.com/axis_mweb_HV.jpg\",\"ServiceId\":\"false\",\"deeplink\":\"https://jits-clever.github.io/TestWeb/\"}");
        HomeSlider.put("Card2","{\"imageUrl\":\"https://shopifyctjt.s3.ap-south-1.amazonaws.com/Axis_T_C_Banner.avif\",\"ServiceId\":\"false\",\"deeplink\":\"https://jits-clever.github.io/TestWeb/\"}");
        HomeSlider.put("Card3","{\"imageUrl\":\"https://shopifyctjt.s3.ap-south-1.amazonaws.com/AxisBank_TnCBanner.jpg\",\"ServiceId\":\"false\",\"deeplink\":\"https://jits-clever.github.io/TestWeb/\"}");
        HomeSlider.put("Card4","{\"imageUrl\":\"https://shopifyctjt.s3.ap-south-1.amazonaws.com/Banking-Service-and-Facilities-of-HDFC-Bank.png\",\"ServiceId\":\"false\",\"deeplink\":\"https://jits-clever.github.io/TestWeb/\"}");
        HomeSlider.put("Card5","{\"imageUrl\":\"https://shopifyctjt.s3.ap-south-1.amazonaws.com/HDFC_T_C_Banner_beauty.jpg\",\"ServiceId\":\"false\",\"deeplink\":\"https://jits-clever.github.io/TestWeb/\"}");


        quicklinks = new HashMap<>();
        quicklinks.put("FDRD",1);
        quicklinks.put("Investments",2);
        quicklinks.put("CreditCard",3);
        quicklinks.put("Loans",4);
        quicklinks.put("SendMoney",5);
        quicklinks.put("Services",6);
        quicklinks.put("FixedReturns",7);
        quicklinks.put("BillPay",8);


        PayBill = new HashMap<>();
        PayBill.put("Fastag", 1);
        PayBill.put("Recharge", 2);
        PayBill.put("Electricity", 3);
        PayBill.put("PipedGas", 4);
        PayBill.put("DTH", 5);
        PayBill.put("Broadband", 6);


        rapidoLink = new HashMap<>();
        rapidoLink.put("icoBike",1);
        rapidoLink.put("icoCar",2);
        rapidoLink.put("icoAuto",3);
        rapidoLink.put("icoCycle",4);

        J4U = new HashMap<>();
        J4U.put("41","");
        J4U.put("42","");
        J4U.put("43","");
        J4U.put("44","");
        J4U.put("45","");
        J4U.put("46","");



        HomeScreen = new HashMap<>();
        HomeScreen.put("SearchIcon",true);
        HomeScreen.put("Call",false);
        HomeScreen.put("RecommendedForU",RecommendedForU);
        HomeScreen.put("Bottom Carousel",HomeSlider);
        HomeScreen.put("QuickLinks",quicklinks);
        HomeScreen.put("Pay Bills", PayBill);
        HomeScreen.put("Rapido Link", rapidoLink);
        HomeScreen.put("J4U", J4U);

//
//        R4U = new HashMap<>();
//        R4U.put("R4U.51","");
//        R4U.put("R4U.52","");
//        R4U.put("R4U.53","");
//        R4U.put("R4U.54","");
//        R4U.put("R4U.55","");
//        R4U.put("R4U.56","");

    }
}


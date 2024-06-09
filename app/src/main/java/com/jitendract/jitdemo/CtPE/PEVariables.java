package com.jitendract.jitdemo.CtPE;

import com.clevertap.android.sdk.variables.Var;
import com.clevertap.android.sdk.variables.annotations.Variable;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

public class PEVariables {

    @Variable
    public Map<String, Object> LoginScreen = new HashMap<String, Object>();





    @Variable
    public String img_url1 = "null";

    @Variable
    public String img_url4 = "null";

    @Variable
    public String img_url2 = "null";

    @Variable
    public String img_url3 = "null";

    @Variable
    public int position1 = 0;

    @Variable
    public int position2 = 0;

    @Variable
    public int position3 = 0;

    public PEVariables() {
        LoginScreen = new HashMap<>();
        LoginScreen.put("Top BannerImage","null");
        LoginScreen.put("Active",false);
    }
}


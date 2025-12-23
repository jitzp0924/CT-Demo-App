package com.jitendract.jitdemo.nudge;

import android.util.Log;

import androidx.annotation.NonNull;

import com.clevertap.android.sdk.inapp.customtemplates.CustomTemplateContext;
import com.clevertap.android.sdk.inapp.customtemplates.TemplatePresenter;

public class CustomTemplates implements TemplatePresenter {

    String templateName;
    @Override
    public void onClose(@NonNull CustomTemplateContext.TemplateContext templateContext) {
        Log.e("CustomCodeTemp", String.valueOf(templateContext));

    }

    @Override
    public void onPresent(@NonNull CustomTemplateContext.TemplateContext templateContext) {
        Log.e("CustomCodeTemp", String.valueOf(templateContext));
        templateName = templateContext.getTemplateName();
        switch (templateName){
            case "PIPVideoInApp":

        }

    }
}

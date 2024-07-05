package com.jitendract.jitdemo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.clevertap.android.sdk.CleverTapAPI;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.HashMap;

public class SignInPage extends AppCompatActivity {

    MaterialCardView signInButton;
    TextView loginText;
    CheckBox conditions, commsUpdate;
    TextInputEditText fullName, phone, email, referral;
    TextInputLayout idLayout, phoneLayout, emailLayout, referralLayout;
    CleveTapUtils cleveTapUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_page);

        cleveTapUtils=new CleveTapUtils(getApplicationContext());

        signInButton = findViewById(R.id.signInbtn);
        loginText = findViewById(R.id.LoginText);
        conditions = findViewById(R.id.conditions);
        commsUpdate = findViewById(R.id.commsUpdate);
        fullName = findViewById(R.id.fullName);
        phone = findViewById(R.id.phone);
        email = findViewById(R.id.email);
        referral = findViewById(R.id.referral);
        idLayout = findViewById(R.id.userTextL);
        phoneLayout = findViewById(R.id.numTextL);
        emailLayout = findViewById(R.id.emailTextL);
        referralLayout = findViewById(R.id.referalTextL);

        signInButton.setOnClickListener(v -> {
            String fullNameText = fullName.getText().toString().trim();
            String phoneNum = phone.getText().toString().trim();
            String emailText = email.getText().toString().trim();
            String referralCode = referral.getText().toString().trim();

            if (fullNameText.isEmpty() || phoneNum.isEmpty() || !conditions.isChecked()) {
                if (fullNameText.isEmpty()) {
                    idLayout.setErrorEnabled(true);
                    idLayout.setError("Please enter Full Name");
                } else {
                    idLayout.setErrorEnabled(false);
                    idLayout.setError(null);
                }

                if (phoneNum.isEmpty()) {
                    phoneLayout.setErrorEnabled(true);
                    phoneLayout.setError("Phone number must be 10 digits");
                } else if (phoneNum.length() != 10) {
                    phoneLayout.setErrorEnabled(true);
                    phoneLayout.setError("Phone number must be 10 digits");
                } else {
                    phoneLayout.setErrorEnabled(false);
                    phoneLayout.setError(null);
                }

                if (!conditions.isChecked()) {
                    Toast.makeText(this, "Please Agree to Terms & Conditions", Toast.LENGTH_LONG).show();
                }
            } else {
                HashMap<String, Object> signInData = new HashMap<>();

                //Identity in making
                String[] words = fullNameText.toLowerCase().split("\\s+");
                StringBuilder identityTextBuilder = new StringBuilder();
                for (int i = 0; i < words.length; i++) {
                    identityTextBuilder.append(words[i]);
                    if (i != words.length - 1) {
                        identityTextBuilder.append(".");
                    }
                    if(words.length == 1){
                        identityTextBuilder.append(".");
                    }
                }
                String identityText = identityTextBuilder.toString();

                signInData.put("Name", fullNameText);
                signInData.put("Identity", identityText);
                signInData.put("Phone", phoneNum);
                if(!emailText.isEmpty()) {
                    signInData.put("Email", emailText);
                }
                cleveTapUtils.login(signInData);

                if(!referralCode.isEmpty()){
                    HashMap<String,Object> propertydata = new HashMap<>();
                    propertydata.put("Referal Code",referralCode);
                    cleveTapUtils.clevertapDefaultInstance.pushProfile(propertydata);
                    cleveTapUtils.clevertapDefaultInstance.pushEvent("Referral Code Used",propertydata);
                }

                Intent intent = new Intent(SignInPage.this, HomeScreen2.class);
                startActivity(intent);
            }


        });

        loginText.setOnClickListener(view -> {
            Intent intent = new Intent(SignInPage.this, MainActivity.class);
            startActivity(intent);
        });
    }


}

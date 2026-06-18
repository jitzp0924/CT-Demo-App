package com.jitendract.jitdemo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.util.HashMap;

public class SignInPage extends AppCompatActivity {

    private MaterialButton signInButton;
    private CheckBox conditions, commsUpdate;
    private TextInputEditText fullName, phone, email, referral;
    private TextInputLayout idLayout, phoneLayout;
    private CleverTapUtils cleverTapUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_page);

        cleverTapUtils = CleverTapUtils.getInstance();

        bindViews();
        setupLoginLink();
        setupSignInButton();
    }

    // ── View binding ──────────────────────────────────────────────────────────

    private void bindViews() {
        signInButton  = findViewById(R.id.signInbtn);
        conditions    = findViewById(R.id.conditions);
        commsUpdate   = findViewById(R.id.commsUpdate);
        fullName      = findViewById(R.id.fullName);
        phone         = findViewById(R.id.phone);
        email         = findViewById(R.id.email);
        referral      = findViewById(R.id.referral);
        idLayout      = findViewById(R.id.userTextL);
        phoneLayout   = findViewById(R.id.numTextL);
    }

    // ── Navigation ────────────────────────────────────────────────────────────

    private void setupLoginLink() {
        TextView loginText = findViewById(R.id.LoginText);
        if (loginText != null) {
            loginText.setOnClickListener(v ->
                    startActivity(new Intent(this, MainActivity.class)));
        }
    }

    // ── Sign-up button ────────────────────────────────────────────────────────

    private void setupSignInButton() {
        signInButton.setOnClickListener(v -> {
            String nameText     = text(fullName);
            String phoneText    = text(phone);
            String emailText    = text(email);
            String referralCode = text(referral);

            if (!validateInputs(nameText, phoneText)) return;

            // Build CT identity from name  (e.g. "John Doe" → "john.doe")
            String[] words = nameText.toLowerCase().split("\\s+");
            StringBuilder id = new StringBuilder();
            for (int i = 0; i < words.length; i++) {
                id.append(words[i]);
                if (i < words.length - 1) id.append(".");
            }
            if (words.length == 1) id.append(".");

            HashMap<String, Object> signInData = new HashMap<>();
            signInData.put("Name",     nameText);
            signInData.put("Identity", id.toString());
            signInData.put("Phone",    phoneText);
            signInData.put("MSG-whatsapp", commsUpdate.isChecked());
            if (!emailText.isEmpty()) signInData.put("Email", emailText);
            cleverTapUtils.login(signInData);

            if (!referralCode.isEmpty()) {
                HashMap<String, Object> refMap = new HashMap<>();
                refMap.put("Referral Code", referralCode);
                cleverTapUtils.getDefaultInstance().pushProfile(refMap);
                cleverTapUtils.raiseEvent("Referral Code Used", refMap);
            }

            startActivity(HomeRouter.getHomeIntent(this));
        });
    }

    // ── Validation ────────────────────────────────────────────────────────────

    private boolean validateInputs(String nameText, String phoneText) {
        boolean valid = true;
        if (nameText.isEmpty()) {
            idLayout.setError("Please enter your full name");
            valid = false;
        } else {
            idLayout.setError(null);
        }
        if (phoneText.isEmpty() || phoneText.length() != 10) {
            phoneLayout.setError("Enter a valid 10-digit number");
            valid = false;
        } else {
            phoneLayout.setError(null);
        }
        if (!conditions.isChecked()) {
            Toast.makeText(this, "Please accept Terms & Conditions", Toast.LENGTH_SHORT).show();
            valid = false;
        }
        return valid;
    }

    private static String text(TextInputEditText field) {
        return field.getText() != null ? field.getText().toString().trim() : "";
    }
}

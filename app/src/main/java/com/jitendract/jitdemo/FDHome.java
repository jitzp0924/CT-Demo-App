package com.jitendract.jitdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class FDHome extends AppCompatActivity {

    private TextView investmentAmountDisplay, tenureDisplay;
    private SeekBar amountSeekBar, tenureSeekBar;
    private Spinner interestPayoutSpinner;
    private Button calculateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fdhome);

        investmentAmountDisplay = findViewById(R.id.investment_amount_display);
        tenureDisplay = findViewById(R.id.tenure_display);
        amountSeekBar = findViewById(R.id.amount_seekbar);
        tenureSeekBar = findViewById(R.id.tenure_seekbar);
        interestPayoutSpinner = findViewById(R.id.interest_payout_spinner);
        calculateButton = findViewById(R.id.calculate_button);

        amountSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                investmentAmountDisplay.setText("₹ " + String.format("%,d", progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        tenureSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tenureDisplay.setText(progress + " Months");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        calculateButton.setOnClickListener(v -> {
            String amount = investmentAmountDisplay.getText().toString().replace("₹ ", "").replace(",", "");
            String tenure = tenureDisplay.getText().toString().replace(" Months", "");
            String interestPayout = interestPayoutSpinner.getSelectedItem().toString();

            if (amount.isEmpty() || tenure.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else {
                // Calculate the fixed deposit
                Toast.makeText(this, "Calculation done", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
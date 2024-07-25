package com.jitendract.jitdemo;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.room.Room;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.datepicker.MaterialDatePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultiTaskPayBills extends AppCompatActivity {

    private static final String TAG = "MultiTaskPayBills";
    private EditText textField;
    private TextView gettingData;
    private TextView textView, datePicked, idTextField;
    private ImageView calendarIcon;
    private Calendar selectedCalendar;
    private final boolean isExpanded = false;
    private AppDatabase database;
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_task_pay_bills);

        // Initialize views
        TextView headerTitle = findViewById(R.id.header_title);
        gettingData = findViewById(R.id.gettingData);
        textView = findViewById(R.id.textView);
        idTextField = findViewById(R.id.id);
        calendarIcon = findViewById(R.id.calendar_icon);
        textField = findViewById(R.id.text_field);
        datePicked = findViewById(R.id.datePicked);
        MaterialCardView buttonAddToWallet = findViewById(R.id.button_add_to_wallet);
        MaterialCardView buttonPay = findViewById(R.id.button_pay);

        LinearLayout scrollViewContainer = findViewById(R.id.scrollViewContainer); // Assuming this is your ScrollView container

        // Initialize the database and executor service
        database = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "transactions-db").build();
        executorService = Executors.newSingleThreadExecutor();

        // Get category from intent
        String category = getIntent().getStringExtra("category");

        // Fetch transactions by category in the background
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                List<Transaction> transactions = database.transactionDao().getTransactionsByCategory(category);
                Collections.reverse(transactions);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for (Transaction transaction : transactions) {
                            // Inflate the transaction card layout
                            View cardView = getLayoutInflater().inflate(R.layout.item_transaction_card, null);

                            // Initialize views inside the card
                            TextView textAmount = cardView.findViewById(R.id.text_amount);
                            TextView textUserData = cardView.findViewById(R.id.text_user_data);
                            ImageView transIcon = cardView.findViewById(R.id.trans_icon);

                            // Set values based on transaction properties
                            textAmount.setText(String.format(Locale.getDefault(), "%.2f", transaction.getAmount()));

                            // Set card background color based on transaction status
                            if ("debit".equals(transaction.getTransactionType())) {
                                if ("success".equals(transaction.getStatus())) {
                                    transIcon.setImageResource(R.drawable.remove_money);
                                    textUserData.setText("Paid Successfully.");
                                } else {
                                    transIcon.setImageResource(R.drawable.failed_money);
                                    textUserData.setText("Transaction Failed.");
                                }
                                // Align the card to the right if it's a debit transaction
                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.WRAP_CONTENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT
                                );
                                params.gravity = Gravity.END;
                                params.setMargins(12,12,12,12);
                                cardView.setLayoutParams(params);
                            } else { // credit transaction
                                if ("success".equals(transaction.getStatus())) {
                                    transIcon.setImageResource(R.drawable.add_money);
                                    textUserData.setText("Added Successfully.");

                                } else {
                                    transIcon.setImageResource(R.drawable.failed_money);
                                    textUserData.setText("Transaction Failed.");
                                }
                                // Align the card to the left if it's a credit transaction
                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.WRAP_CONTENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT
                                );
                                params.gravity = Gravity.START;
                                params.setMargins(12,12,12,12);
                                cardView.setLayoutParams(params);
                            }

                            // Add the card to the ScrollView
                            scrollViewContainer.addView(cardView);
                        }
                    }
                });
            }
        });

        buttonPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String enteredText = textField.getText().toString();
                double amount = Double.parseDouble(enteredText);

                handlePaymentFromWallet(amount);
            }
        });

        // Set header title based on category (passed via intent)
        if (category != null) {
            headerTitle.setText(category);

            // Check category to show/hide request button
            if ("Fastag".equals(category)) {
                buttonAddToWallet.setVisibility(View.VISIBLE);
            } else {
                buttonAddToWallet.setVisibility(View.GONE);
            }
        }

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String dateKey = category + " Date";
        String storedDate = sharedPreferences.getString(dateKey, "");
        if (!storedDate.isEmpty()) {
            datePicked.setText(storedDate);
        }

        // Inside buttonAddToWallet OnClickListener
        buttonAddToWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String enteredText = textField.getText().toString();
                double amount = Double.parseDouble(enteredText);

                executorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        // Update Wallet
                        Wallet wallet = database.walletDao().getWallet();
                        if (wallet == null) {
                            wallet = new Wallet(amount);
                            database.walletDao().insert(wallet);
                        } else {
                            wallet.setAmount(wallet.getAmount() + amount);
                            database.walletDao().update(wallet);
                        }

                        // Save transaction to Room Database
                        String userData = gettingData.getText().toString(); // Assuming this contains the user data

                        long timestamp = System.currentTimeMillis();
                        Transaction transaction = new Transaction(category, amount, "credit", timestamp, "success", userData);
                        database.transactionDao().insert(transaction);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // Update UI or perform other operations
                                Toast.makeText(MultiTaskPayBills.this, "Amount added to wallet: " + amount, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        });

        // Retrieve user data from SharedPreferences based on category
        String dataKey;
        switch (category) {
            case "Fastag":
                dataKey = "fastagId";
                break;
            case "Recharge":
                dataKey = "phoneNo";
                break;
            case "Electricity":
                dataKey = "electricId";
                break;
            case "Piped Gas":
                dataKey = "gasId";
                break;
            case "DTH":
                dataKey = "dthId";
                break;
            case "Broadband":
                dataKey = "broadbandId";
                break;
            default:
                dataKey = "";
                break;
        }

        String userData = sharedPreferences.getString(dataKey, "");
        if (userData == null || userData.isEmpty()) {
            userData = "Set from Settings";
        }

        gettingData.setText(userData);

        // Handle calendar icon click to show Material Date Picker
        calendarIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create Material Date Picker
                MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
                builder.setTitleText("Select date");

                // Initialize with stored date if available, otherwise with today's date
                String storedDateStr = datePicked.getText().toString().trim();
                Calendar initialSelection = Calendar.getInstance();

                if (!storedDateStr.isEmpty()) {
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
                        sdf.setTimeZone(TimeZone.getTimeZone("IST")); // Set to Indian Standard Time

                        Date storedDate = sdf.parse(storedDateStr);
                        initialSelection.setTime(storedDate);
                    } catch (ParseException e) {
                        e.printStackTrace();
                        initialSelection = Calendar.getInstance();
                    }
                }

                builder.setSelection(initialSelection.getTimeInMillis());

                MaterialDatePicker<Long> materialDatePicker = builder.build();

                // Show Material Date Picker
                materialDatePicker.addOnPositiveButtonClickListener(selection -> {
                    // Format selected date
                    Calendar selectedCalendar = Calendar.getInstance();
                    selectedCalendar.setTimeInMillis(selection);

                    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
                    sdf.setTimeZone(TimeZone.getTimeZone("IST")); // Set to Indian Standard Time

                    String selectedDate = sdf.format(selectedCalendar.getTime());

                    // Update datePicked TextView
                    datePicked.setText(selectedDate);

                    // Save selected date to SharedPreferences
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(dateKey, selectedDate);
                    editor.apply();
                });

                materialDatePicker.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
            }
        });

    }

    private void handlePaymentFromWallet(double amount) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                Wallet wallet = database.walletDao().getWallet();
                String status;
                if (wallet == null || wallet.getAmount() < amount) {
                    status = "failure";
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MultiTaskPayBills.this, "Insufficient balance in wallet", Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    wallet.setAmount(wallet.getAmount() - amount);
                    database.walletDao().update(wallet);
                    status = "success";
                }

                String category = getIntent().getStringExtra("category");
                String userData = gettingData.getText().toString();
                long timestamp = System.currentTimeMillis();
                Transaction transaction = new Transaction(category, amount, "debit", timestamp, status, userData);
                database.transactionDao().insert(transaction);

                if ("success".equals(status)) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MultiTaskPayBills.this, "Payment of amount " + amount + " was successful", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}

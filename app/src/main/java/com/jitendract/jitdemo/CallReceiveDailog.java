package com.jitendract.jitdemo;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.clevertap.android.signedcall.exception.CallException;
import com.clevertap.android.signedcall.init.SignedCallAPI;
import com.clevertap.android.signedcall.interfaces.OutgoingCallResponse;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.jitendract.jitdemo.CTJTLoggger;

import org.json.JSONException;
import org.json.JSONObject;


public class CallReceiveDailog extends BottomSheetDialogFragment {


    TextInputEditText receiverId;
    SharedPreferences prefs;
    String callerID,UserId;
    MaterialCardView callbtn;

    @SuppressLint("ResourceType")
    public CallReceiveDailog() {
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Context context = getContext();
        callbtn = view.findViewById(R.id.callnow);
        receiverId = view.findViewById(R.id.receiverId);
        callerID="";
        prefs = context.getSharedPreferences("Login", MODE_PRIVATE);
        UserId = prefs.getString("Identity","default");
        JSONObject callOptions = new JSONObject();
        try {
            callOptions.put("remote_context",UserId.toUpperCase()+" is trying to reach you.");
        } catch (JSONException e) {
            Log.e("SignedCall","Error While Creating call options");
            throw new RuntimeException(e);
        }

        receiverId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                callerID = String.valueOf(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        callbtn.setOnClickListener(v ->{
            if (callerID != null) {

                Log.i("SignedCAll", "Receiver ID : "+callerID);
                OutgoingCallResponse outgoingCallResponseListener = new OutgoingCallResponse() {
                    @Override
                    public void onSuccess() {
                        //App is notified on the main thread when the call-request is accepted and being processed by the signalling channel
                    }

                    @Override
                    public void onFailure(CallException callException) {
                        //App is notified on the main thread when the call is failed
                        Log.d("SignedCall: ", "error code: " + callException.getErrorCode()
                                + "\n error message: " + callException.getMessage()
                                + "\n error explanation: " + callException.getExplanation());

                        if (callException.getErrorCode() == CallException.BadNetworkException.getErrorCode()) {
                            //Handle this error here
                        }
                    }
                };

                SignedCallAPI.getInstance().call(context.getApplicationContext(), callerID, "Dailing call to "+callerID.toUpperCase(), callOptions, outgoingCallResponseListener);

            }
            else {
                receiverId.setError("Please Provide identity of the person you are calling");
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_call_receive_dailog, container, false);
    }
}
package com.example.minhpq.paymentexample.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.minhpq.paymentexample.R;

/**
 * Activity for displaying the result.
 */

public class PaymentResultActivity extends Activity {

    private String result;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);
        result = getIntent().getStringExtra("Result");
        ((TextView) findViewById(R.id.verificationTextView)).setText(result);
    }
}
